package com.search.controller;

import com.search.entity.SearchRecord;
import com.search.service.SearchRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SearchController {

    private final SearchRecordService searchRecordService;

    /**
     * 执行搜索（调用百度搜索）
     */
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "pc") String source,
            HttpServletRequest request) {
        
        String ip = getClientIp(request);
        log.info("搜索请求 - 关键词: {}, IP: {}, 来源: {}", keyword, ip, source);
        
        // 记录搜索
        searchRecordService.recordSearch(keyword, ip, source);
        
        // 返回搜索结果页面URL（跳转到百度）
        String searchUrl = "https://www.baidu.com/s?wd=" + keyword;
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "keyword", keyword,
            "searchUrl", searchUrl,
            "message", "搜索已记录，正在跳转到百度搜索..."
        ));
    }

    /**
     * 获取热门搜索
     */
    @GetMapping("/hot")
    public ResponseEntity<List<String>> getHotKeywords() {
        return ResponseEntity.ok(searchRecordService.getHotKeywords());
    }

    /**
     * 获取热门搜索（带次数）
     */
    @GetMapping("/hot/detail")
    public ResponseEntity<List<Map<String, Object>>> getHotKeywordsWithCount() {
        return ResponseEntity.ok(searchRecordService.getHotKeywordsWithCount());
    }

    /**
     * 获取搜索趋势
     */
    @GetMapping("/trend")
    public ResponseEntity<List<Map<String, Object>>> getSearchTrend(
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(searchRecordService.getSearchTrend(hours));
    }

    /**
     * 获取最近搜索记录
     */
    @GetMapping("/recent")
    public ResponseEntity<List<SearchRecord>> getRecentSearches() {
        return ResponseEntity.ok(searchRecordService.getRecentSearches());
    }

    /**
     * 检测新热点
     */
    @GetMapping("/hotspots")
    public ResponseEntity<List<Map<String, Object>>> detectHotspots() {
        return ResponseEntity.ok(searchRecordService.detectNewHotspots());
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "ok",
            "timestamp", LocalDateTime.now().toString(),
            "service", "search-engine"
        ));
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时，第一个IP为真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
