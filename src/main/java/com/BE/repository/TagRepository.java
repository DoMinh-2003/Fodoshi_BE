package com.BE.repository;

import com.BE.model.entity.Tag;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag,Long> {
    List<Tag> findAllByProductsId(Long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM product_tag WHERE tag_id IN :tagIds", nativeQuery = true)
    void deleteTagAssociations(@Param("tagIds") List<Long> tagIds);

}
