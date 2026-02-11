package com.search.repository;

import com.search.entity.SearchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SearchRecordRepository extends JpaRepository<SearchRecord, Long> {

    /**
     * 统计关键词搜索次数（按时间范围）
     */
    @Query("SELECT s.keyword, COUNT(s) as cnt " +
           "FROM SearchRecord s " +
           "WHERE s.searchTime >= :startTime " +
           "GROUP BY s.keyword " +
           "ORDER BY cnt DESC")
    List<Object[]> findTopKeywordsByTimeRange(@Param("startTime") LocalDateTime startTime);

    /**
     * 查找某关键词的最近搜索记录
     */
    List<SearchRecord> findByKeywordOrderBySearchTimeDesc(String keyword);

    /**
     * 统计某时间段内的总搜索次数
     */
    long countBySearchTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 查找最近的搜索记录
     */
    List<SearchRecord> findTop10ByOrderBySearchTimeDesc();
}
