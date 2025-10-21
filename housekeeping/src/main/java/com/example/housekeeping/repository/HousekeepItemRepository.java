package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface HousekeepItemRepository extends JpaRepository<HousekeepItem, Long> {

    List<HousekeepItem> findByItemTypeOrderByIdAsc(String itemType);

    Optional<HousekeepItem> findByIdAndItemType(Long id, String itemType);

    @Query("""
        SELECT i FROM HousekeepItem i
        WHERE i.itemType = :itemType AND (
            LOWER(COALESCE(i.title, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(CAST(COALESCE(i.content, '') AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(i.tag, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(i.author, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(i.serviceName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        ORDER BY i.id ASC
        """)
    List<HousekeepItem> searchByItemTypeAndKeyword(@Param("itemType") String itemType,
                                                   @Param("keyword") String keyword);

    List<HousekeepItem> findByItemTypeAndIdIn(String itemType, Collection<Long> ids);
}
