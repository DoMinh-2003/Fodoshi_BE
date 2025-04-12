package com.BE.service.implementServices;

import com.BE.enums.OrderStatus;
import com.BE.enums.ProductStatus;
import com.BE.model.entity.Order;
import com.BE.model.entity.Product;
import com.BE.model.response.dashboard.*;
import com.BE.repository.*;
import com.BE.utils.DateNowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ChronoField;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Locale;

@Service
public class AdminDashboardService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DateNowUtils dateNowUtils;

    public RevenueSummaryDTO getRevenueSummary() {
        RevenueSummaryDTO summary = new RevenueSummaryDTO();

        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDate today = LocalDate.now(zoneId);

        // Today's revenue
        BigDecimal todayRevenue = getRevenueForDay(today);
        summary.setTodayRevenue(todayRevenue);

        // This month's revenue
        BigDecimal monthRevenue = getRevenueForMonth(today.getMonthValue(), today.getYear());
        summary.setMonthRevenue(monthRevenue);

        // This year's revenue
        BigDecimal yearRevenue = getRevenueForYear(today.getYear());
        summary.setYearRevenue(yearRevenue);

        // Total revenue
        BigDecimal totalRevenue = getTotalRevenue();
        summary.setTotalRevenue(totalRevenue);

        return summary;
    }

    public RevenueByPeriodDTO getRevenueByPeriod(String period, LocalDate startDate, LocalDate endDate) {
        RevenueByPeriodDTO revenueData = new RevenueByPeriodDTO();
        revenueData.setPeriodType(period);

        Map<String, BigDecimal> revenueMap = new LinkedHashMap<>();
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDate today = LocalDate.now(zoneId);


        switch (period.toLowerCase()) {
            case "day":
                if (startDate == null) startDate = today.minusDays(30);
                if (endDate == null) endDate = today;

                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    String dateStr = date.format(DateTimeFormatter.ISO_DATE);
                    revenueMap.put(dateStr, getRevenueForDay(date));
                }
                break;

            case "week":
                if (startDate == null) startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                if (endDate == null) endDate = startDate.plusDays(6);

                // Get each day of the week with day name
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    String dayStr = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
                    revenueMap.put(dayStr, getRevenueForDay(date));
                }
                break;

            case "month":
                if (startDate == null) startDate = today.minusMonths(12).withDayOfMonth(1);
                if (endDate == null) endDate = today;

                // Get weekly data for months
                LocalDate current = startDate;
                while (!current.isAfter(endDate)) {
                    LocalDate monthStart = current.withDayOfMonth(1);
                    LocalDate monthEnd = current.withDayOfMonth(current.lengthOfMonth());

                    // Get weekly data for each month
                    for (int weekNum = 1; weekNum <= 5; weekNum++) {
                        LocalDate weekStart = monthStart.plusDays((weekNum - 1) * 7);
                        if (weekStart.getMonth() != monthStart.getMonth()) {
                            continue; // Skip if week extends to next month
                        }

                        LocalDate weekEnd = weekStart.plusDays(6);
                        if (weekEnd.isAfter(monthEnd)) {
                            weekEnd = monthEnd;
                        }

                        String key = monthStart.format(DateTimeFormatter.ofPattern("MMM")) + " Week " + weekNum;
                        BigDecimal weekRevenue = BigDecimal.ZERO;

                        for (LocalDate day = weekStart; !day.isAfter(weekEnd); day = day.plusDays(1)) {
                            weekRevenue = weekRevenue.add(getRevenueForDay(day));
                        }

                        revenueMap.put(key, weekRevenue);
                    }

                    current = current.plusMonths(1);
                }
                break;

            case "year":
                if (startDate == null) startDate = today.minusYears(5).withDayOfYear(1);
                if (endDate == null) endDate = today;

                // For year view, show monthly data
                LocalDate yearStart = startDate;
                while (!yearStart.isAfter(endDate)) {
                    for (int month = 1; month <= 12; month++) {
                        LocalDate monthDate = LocalDate.of(yearStart.getYear(), month, 1);
                        if (monthDate.isAfter(endDate)) {
                            break;
                        }

                        String monthStr = monthDate.format(DateTimeFormatter.ofPattern("yyyy-MMM"));
                        revenueMap.put(monthStr, getRevenueForMonth(month, monthDate.getYear()));
                    }

                    yearStart = yearStart.plusYears(1);
                }
                break;
        }

        revenueData.setRevenueData(revenueMap);
        return revenueData;
    }

    public ProductSummaryDTO getProductsSummary() {
        ProductSummaryDTO summary = new ProductSummaryDTO();

        // Total products
        long totalProducts = productRepository.count();
        summary.setTotalProducts(totalProducts);

        // Available products - Sản phẩm có sẵn là sản phẩm có trạng thái AVAILABLE và chưa bị xóa
        long availableProducts = productRepository.findAll().stream()
                .filter(product -> !product.isDeleted())
                .count();
        summary.setAvailableProducts(availableProducts);

        // Sold products - Sản phẩm đã bán là sản phẩm có isDeleted = true
        long soldProducts = productRepository.findAll().stream()
                .filter(product -> product.isDeleted())
                .count();

        summary.setSoldProducts(soldProducts);

        return summary;
    }

    public ProductsSoldDTO getProductsSold(String period, LocalDate date) {
        ProductsSoldDTO soldData = new ProductsSoldDTO();
        soldData.setPeriodType(period);

        LocalDate today = date != null ? date : LocalDate.now();

        switch (period.toLowerCase()) {
            case "day":
                int soldToday = getProductsSoldForDay(today);
                soldData.setCount(soldToday);
                soldData.setLabel("Products sold today");
                break;

            case "month":
                int soldThisMonth = getProductsSoldForMonth(today.getMonthValue(), today.getYear());
                soldData.setCount(soldThisMonth);
                soldData.setLabel("Products sold this month");
                break;

            case "year":
                int soldThisYear = getProductsSoldForYear(today.getYear());
                soldData.setCount(soldThisYear);
                soldData.setLabel("Products sold this year");
                break;
        }

        return soldData;
    }

    public int getCategoriesCount() {
        return categoryRepository.findAllByIsDeletedFalse().size();
    }

    public int getBrandsCount() {
        return brandRepository.findAllByIsDeletedFalse().size();
    }

    public CustomerSummaryDTO getCustomerSummary() {
        CustomerSummaryDTO summary = new CustomerSummaryDTO();

        // Đếm số khách hàng đã đăng ký (có tài khoản)
        int registeredCustomers = userRepository.findAll().size();
        summary.setTotalRegisteredCustomers(registeredCustomers);

        // Đếm số khách vãng lai (đơn hàng không có userId)
        int guestCustomers = (int) orderRepository.findAll().stream()
            .filter(order -> order.getUser() == null)
            .map(Order::getUser)
            .distinct()
            .count();
        summary.setTotalGuestCustomers(guestCustomers);

        // Tổng số khách hàng sẽ được tự động tính trong constructor của CustomerSummaryDTO

        return summary;
    }

    public DashboardOverviewDTO getDashboardOverview() {
        DashboardOverviewDTO overview = new DashboardOverviewDTO();

        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDate today = LocalDate.now(zoneId);


        // Set revenue summaries
        overview.setRevenueSummary(getRevenueSummary());

        // Set product summaries
        overview.setProductSummary(getProductsSummary());

        // Set today's sold products
        ProductsSoldDTO todaySold = new ProductsSoldDTO();
        todaySold.setPeriodType("day");
        todaySold.setCount(getProductsSoldForDay(today));
        todaySold.setLabel("Products sold today");
        overview.setTodaySoldProducts(todaySold);

        // Set month's sold products
        ProductsSoldDTO monthSold = new ProductsSoldDTO();
        monthSold.setPeriodType("month");
        monthSold.setCount(getProductsSoldForMonth(today.getMonthValue(), today.getYear()));
        monthSold.setLabel("Products sold this month");
        overview.setMonthSoldProducts(monthSold);

        // Set year's sold products
        ProductsSoldDTO yearSold = new ProductsSoldDTO();
        yearSold.setPeriodType("year");
        yearSold.setCount(getProductsSoldForYear(today.getYear()));
        yearSold.setLabel("Products sold this year");
        overview.setYearSoldProducts(yearSold);

        // Set category and brand counts
        overview.setCategoriesCount(getCategoriesCount());
        overview.setBrandsCount(getBrandsCount());

        // Set customer summary
        overview.setCustomerSummary(getCustomerSummary());

        return overview;
    }

    // Helper methods

    private BigDecimal getRevenueForDay(LocalDate day) {
        // Convert directly to LocalDateTime objects instead of formatting as strings
        LocalDateTime startDateTime = day.atStartOfDay();
        LocalDateTime endDateTime = day.atTime(LocalTime.MAX);

        // Pass LocalDateTime objects to the repository method
        // Chỉ tính doanh thu từ các đơn hàng đã thanh toán (PAID)
        List<Order> orders = orderRepository.findAllByCreatedAtBetweenAndStatus(
                startDateTime, endDateTime, OrderStatus.PAID);

        // Kiểm tra xem danh sách có rỗng không trước khi tính tổng
        if (orders.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return orders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal getRevenueForMonth(int month, int year) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());
        
        LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();
        LocalDateTime endOfMonth = lastDayOfMonth.atTime(LocalTime.MAX);
        
        // Pass LocalDateTime objects directly to the repository method
        List<Order> orders = orderRepository.findAllByCreatedAtBetweenAndStatus(
                startOfMonth, endOfMonth, OrderStatus.PAID);
        
        return orders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal getRevenueForYear(int year) {
        LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);
        LocalDate lastDayOfYear = firstDayOfYear.with(TemporalAdjusters.lastDayOfYear());
        
        LocalDateTime startOfYear = firstDayOfYear.atStartOfDay();
        LocalDateTime endOfYear = lastDayOfYear.atTime(LocalTime.MAX);
        
        // Pass LocalDateTime objects directly to the repository method
        List<Order> orders = orderRepository.findAllByCreatedAtBetweenAndStatus(
                startOfYear, endOfYear, OrderStatus.PAID);
        
        return orders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal getTotalRevenue() {
        // Query only filters by status, not by date
        List<Order> orders = orderRepository.findAllByStatus(OrderStatus.PAID);
        
        return orders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private int getProductsSoldForDay(LocalDate day) {
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.atTime(LocalTime.MAX);
        
        // Pass LocalDateTime objects directly to the repository method
        // Chỉ đếm các đơn hàng đã thanh toán (PAID)
        List<Order> orders = orderRepository.findAllByCreatedAtBetweenAndStatus(
                startOfDay, endOfDay, OrderStatus.PAID);
        
        // Kiểm tra xem danh sách có rỗng không trước khi tính tổng
        if (orders.isEmpty()) {
            return 0;
        }
        
        // Đếm số lượng sản phẩm (mỗi sản phẩm có số lượng là 1)
        return orders.stream()
                .mapToInt(order -> order.getOrderItems().size())
                .sum();
    }
    
    private int getProductsSoldForMonth(int month, int year) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());
        
        LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();
        LocalDateTime endOfMonth = lastDayOfMonth.atTime(LocalTime.MAX);
        
        // Pass LocalDateTime objects directly to the repository method
        List<Order> orders = orderRepository.findAllByCreatedAtBetweenAndStatus(
                startOfMonth, endOfMonth, OrderStatus.PAID);
        
        // Kiểm tra xem danh sách có rỗng không trước khi tính tổng
        if (orders.isEmpty()) {
            return 0;
        }
        
        // Đếm số lượng sản phẩm (mỗi sản phẩm có số lượng là 1)
        return orders.stream()
                .mapToInt(order -> order.getOrderItems().size())
                .sum();
    }
    
    private int getProductsSoldForYear(int year) {
        LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);
        LocalDate lastDayOfYear = firstDayOfYear.with(TemporalAdjusters.lastDayOfYear());
        
        LocalDateTime startOfYear = firstDayOfYear.atStartOfDay();
        LocalDateTime endOfYear = lastDayOfYear.atTime(LocalTime.MAX);
        
        // Pass LocalDateTime objects directly to the repository method
        List<Order> orders = orderRepository.findAllByCreatedAtBetweenAndStatus(
                startOfYear, endOfYear, OrderStatus.PAID);
        
        // Kiểm tra xem danh sách có rỗng không trước khi tính tổng
        if (orders.isEmpty()) {
            return 0;
        }
        
        // Đếm số lượng sản phẩm (mỗi sản phẩm có số lượng là 1)
        return orders.stream()
                .mapToInt(order -> order.getOrderItems().size())
                .sum();
    }

    public List<ProductResponseDTO> getRecentlySoldProducts(int limit) {
        // Lấy tất cả sản phẩm đã bán (isDeleted = true)
        List<Product> soldProducts = productRepository.findAllByIsDeleted(true);
        

        return soldProducts.stream()
                .filter(product -> !product.getOrderItems().isEmpty())
                .sorted((p1, p2) -> {
                    // Tìm orderItem mới nhất cho mỗi sản phẩm
                    LocalDateTime p1LatestOrder = p1.getOrderItems().stream()
                            .map(item -> item.getOrder().getCreatedAt())
                            .max(LocalDateTime::compareTo)
                            .orElse(LocalDateTime.MIN);
                    
                    LocalDateTime p2LatestOrder = p2.getOrderItems().stream()
                            .map(item -> item.getOrder().getCreatedAt())
                            .max(LocalDateTime::compareTo)
                            .orElse(LocalDateTime.MIN);
                    
                    // Sắp xếp ngược (mới nhất lên đầu)
                    return p2LatestOrder.compareTo(p1LatestOrder);
                })
                .limit(limit)
                .map(product -> {
                    // Chuyển sang DTO cho response
                    ProductResponseDTO dto = new ProductResponseDTO();
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setPrice(product.getSellingPrice());
                    dto.setSoldAt(product.getOrderItems().stream()
                            .map(item -> item.getOrder().getCreatedAt())
                            .max(LocalDateTime::compareTo)
                            .orElse(null));
                    
                    // Thêm các thông tin khác cần thiết
                    dto.setImage(product.getMainImage());
                    dto.setBrandName(product.getBrands().isEmpty() ? "" : product.getBrands().iterator().next().getName());
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
}