package com.search.service;

import com.search.entity.SearchRecord;
import com.search.repository.SearchRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchRecordService {

    private final SearchRecordRepository searchRecordRepository;

    @Value("${search.hot-keywords.top-count:10}")
    private int topCount;

    @Value("${search.hot-keywords.days:7}")
    private int days;

    /**
     * 记录搜索
     */
    @Transactional
    public SearchRecord recordSearch(String keyword, String ip, String source) {
        SearchRecord record = SearchRecord.builder()
                .keyword(keyword.trim())
                .ip(ip)
                .source(source)
                .searchTime(LocalDateTime.now())
                .build();
        return searchRecordRepository.save(record);
    }

    /**
     * 获取热门搜索词
     */
    public List<String> getHotKeywords() {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        List<Object[]> results = searchRecordRepository.findTopKeywordsByTimeRange(startTime);
        
        return results.stream()
                .limit(topCount)
                .map(row -> (String) row[0])
                .collect(Collectors.toList());
    }

    /**
     * 获取热点分析（带搜索次数）
     */
    public List<Map<String, Object>> getHotKeywordsWithCount() {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        List<Object[]> results = searchRecordRepository.findTopKeywordsByTimeRange(startTime);
        
        return results.stream()
                .limit(topCount)
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("keyword", row[0]);
                    map.put("count", ((Number) row[1]).longValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取搜索趋势（按小时统计）
     */
    public List<Map<String, Object>> getSearchTrend(int hours) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(hours);
        List<SearchRecord> records = searchRecordRepository.findByKeywordOrderBySearchTimeDesc(""); // 获取所有
        
        // 按小时分组统计
        Map<Integer, Long> hourlyCount = new TreeMap<>();
        for (int i = 0; i < hours; i++) {
            hourlyCount.put(i, 0L);
        }
        
        for (SearchRecord record : records) {
            if (record.getSearchTime().isAfter(startTime)) {
                int hour = record.getSearchTime().getHour();
                hourlyCount.merge(hour, 1L, Long::sum);
            }
        }
        
        return hourlyCount.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("hour", entry.getKey());
                    map.put("count", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取最近搜索记录
     */
    public List<SearchRecord> getRecentSearches() {
        return searchRecordRepository.findTop10ByOrderBySearchTimeDesc();
    }

    /**
     * 检测是否为新热点（搜索量突然增加）
     */
    public List<Map<String, Object>> detectNewHotspots() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        LocalDateTime twoHoursAgo = now.minusHours(2);
        
        // 获取最近2小时的搜索记录
        List<SearchRecord> recentRecords = searchRecordRepository
                .findByKeywordOrderBySearchTimeDesc("");
        
        Map<String, Long> recentCount = new HashMap<>();
        Map<String, Long> previousCount = new HashMap<>();
        
        for (SearchRecord record : recentRecords) {
            if (record.getSearchTime().isAfter(oneHourAgo)) {
                recentCount.merge(record.getKeyword(), 1L, Long::sum);
            } else if (record.getSearchTime().isAfter(twoHoursAgo)) {
                previousCount.merge(record.getKeyword(), 1L, Long::sum);
            }
        }
        
        // 找出增长超过50%的关键词
        List<Map<String, Object>> hotspots = new ArrayList<>();
        for (String keyword : recentCount.keySet()) {
            long recent = recentCount.getOrDefault(keyword, 0L);
            long previous = previousCount.getOrDefault(keyword, 0L);
            if (previous > 0 && (recent - previous) * 100 / previous > 50) {
                Map<String, Object> map = new HashMap<>();
                map.put("keyword", keyword);
                map.put("recentCount", recent);
                map.put("previousCount", previous);
                map.put("growthRate", (recent - previous) * 100.0 / previous);
                hotspots.add(map);
            }
        }
        
        return hotspots.stream()
                .sorted((a, b) -> Double.compare(
                        (Double) b.get("growthRate"), 
                        (Double) a.get("growthRate")))
                .limit(10)
                .collect(Collectors.toList());
    }
}
