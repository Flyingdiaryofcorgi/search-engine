package com.search.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 搜索记录实体
 */
@Entity
@Table(name = "search_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 搜索关键词
     */
    @Column(nullable = false, length = 255)
    private String keyword;

    /**
     * 用户IP（用于统计，不记录个人身份）
     */
    @Column(length = 45)
    private String ip;

    /**
     * 搜索时间
     */
    @Column(nullable = false)
    private LocalDateTime searchTime;

    /**
     * 搜索来源（PC/Mobile）
     */
    @Column(length = 20)
    private String source;

    /**
     * 是否命中热点（用于回检）
     */
    @Column
    @Builder.Default
    private Boolean isHot = false;

    @PrePersist
    protected void onCreate() {
        if (searchTime == null) {
            searchTime = LocalDateTime.now();
        }
    }
}
