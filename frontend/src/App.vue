<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 主搜索区域 -->
    <div class="bg-white">
      <div class="max-w-4xl mx-auto px-4 py-20">
        <!-- Logo -->
        <div class="text-center mb-8">
          <h1 class="text-5xl font-bold text-gray-800 mb-2">搜索</h1>
          <p class="text-gray-500">记录每一次搜索，发现热点趋势</p>
        </div>

        <!-- 搜索框 -->
        <div class="relative mb-6">
          <div class="flex items-center bg-white rounded-full border-2 border-gray-200 hover:border-baidu-blue transition-colors p-1">
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="输入搜索内容..."
              class="flex-1 h-12 px-6 text-lg outline-none rounded-l-full"
              @keyup.enter="handleSearch"
              @focus="showSuggestions = true"
            >
            <button
              @click="handleSearch"
              class="h-12 px-8 bg-baidu-blue text-white text-lg rounded-full hover:bg-blue-600 transition-colors"
            >
              百度搜索
            </button>
          </div>

          <!-- 搜索建议 -->
          <div
            v-if="showSuggestions && suggestions.length > 0"
            class="absolute top-full left-0 right-0 bg-white rounded-lg shadow-lg mt-1 z-10 overflow-hidden"
          >
            <div
              v-for="(item, index) in suggestions"
              :key="index"
              class="suggestion-item flex justify-between items-center"
              @click="selectSuggestion(item)"
            >
              <span>{{ item }}</span>
              <span class="text-gray-400 text-sm">搜索</span>
            </div>
          </div>
        </div>

        <!-- 热门搜索 -->
        <div class="flex flex-wrap gap-2 justify-center">
          <span class="text-gray-500 mr-2">热门：</span>
          <span
            v-for="(keyword, index) in hotKeywords"
            :key="index"
            class="hot-tag"
            @click="searchKeyword = keyword"
          >
            {{ keyword }}
          </span>
        </div>
      </div>
    </div>

    <!-- 数据统计区域 -->
    <div class="max-w-6xl mx-auto px-4 py-12">
      <!-- 统计卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div class="bg-white rounded-lg shadow p-6 text-center">
          <div class="text-3xl font-bold text-baidu-blue">{{ stats.todayCount }}</div>
          <div class="text-gray-500 mt-1">今日搜索</div>
        </div>
        <div class="bg-white rounded-lg shadow p-6 text-center">
          <div class="text-3xl font-bold text-baidu-green">{{ stats.totalCount }}</div>
          <div class="text-gray-500 mt-1">总搜索量</div>
        </div>
        <div class="bg-white rounded-lg shadow p-6 text-center">
          <div class="text-3xl font-bold text-baidu-red">{{ stats.hotCount }}</div>
          <div class="text-gray-500 mt-1">热点检测</div>
        </div>
        <div class="bg-white rounded-lg shadow p-6 text-center">
          <div class="text-3xl font-bold text-purple-500">{{ stats.trendData.length }}</div>
          <div class="text-gray-500 mt-1">趋势时段</div>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- 热门搜索词 -->
        <div class="bg-white rounded-lg shadow p-6">
          <h3 class="text-lg font-semibold mb-4">🔥 热门搜索词</h3>
          <div class="space-y-2">
            <div
              v-for="(item, index) in hotKeywordsDetail"
              :key="index"
              class="flex items-center justify-between p-2 hover:bg-gray-50 rounded"
            >
              <div class="flex items-center">
                <span
                  class="w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold mr-3"
                  :class="index < 3 ? 'bg-baidu-blue text-white' : 'bg-gray-200 text-gray-600'"
                >
                  {{ index + 1 }}
                </span>
                <span>{{ item.keyword }}</span>
              </div>
              <span class="text-gray-500">{{ item.count }} 次</span>
            </div>
          </div>
        </div>

        <!-- 搜索趋势 -->
        <div class="bg-white rounded-lg shadow p-6">
          <h3 class="text-lg font-semibold mb-4">📈 搜索趋势（24小时）</h3>
          <div id="trendChart" class="h-64"></div>
        </div>
      </div>

      <!-- 实时热点 -->
      <div class="mt-6 bg-white rounded-lg shadow p-6">
        <h3 class="text-lg font-semibold mb-4">⚡ 实时热点检测</h3>
        <div v-if="hotspots.length === 0" class="text-center text-gray-500 py-8">
          暂无新热点，保持监控...
        </div>
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div
            v-for="(item, index) in hotspots"
            :key="index"
            class="border rounded-lg p-4 hover:shadow-md transition-shadow"
          >
            <div class="flex items-center justify-between mb-2">
              <span class="font-semibold">{{ item.keyword }}</span>
              <span class="text-red-500 text-sm">↑ {{ item.growthRate.toFixed(1) }}%</span>
            </div>
            <div class="text-sm text-gray-500">
              近1小时: {{ item.recentCount }} 次 | 前1小时: {{ item.previousCount }} 次
            </div>
          </div>
        </div>
      </div>

      <!-- 最近搜索 -->
      <div class="mt-6 bg-white rounded-lg shadow p-6">
        <h3 class="text-lg font-semibold mb-4">🕒 最近搜索</h3>
        <div class="flex flex-wrap gap-2">
          <span
            v-for="(record, index) in recentSearches"
            :key="index"
            class="px-3 py-1 bg-gray-100 rounded-full text-sm cursor-pointer hover:bg-baidu-blue hover:text-white transition-colors"
            @click="searchKeyword = record.keyword"
          >
            {{ record.keyword }}
          </span>
        </div>
      </div>
    </div>

    <!-- 底部 -->
    <div class="bg-gray-100 py-6 mt-8">
      <div class="max-w-6xl mx-auto px-4 text-center text-gray-500 text-sm">
        <p>© 2024 搜索统计系统 | 基于 Vue + Spring Boot + MySQL</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

const searchKeyword = ref('')
const showSuggestions = ref(false)
const suggestions = ref([])

const hotKeywords = ref([])
const hotKeywordsDetail = ref([])
const recentSearches = ref([])
const hotspots = ref([])
const trendData = ref([])

const stats = ref({
  todayCount: 0,
  totalCount: 0,
  hotCount: 0,
  trendData: []
})

// 监听输入，显示建议
watch(searchKeyword, (val) => {
  if (val && hotKeywords.value.includes(val)) {
    suggestions.value = hotKeywords.value.filter(k => 
      k.toLowerCase().includes(val.toLowerCase())
    ).slice(0, 5)
  } else {
    suggestions.value = []
  }
})

// 选择建议
const selectSuggestion = (keyword) => {
  searchKeyword.value = keyword
  showSuggestions.value = false
  handleSearch()
}

// 执行搜索
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) return
  
  try {
    await axios.get('/api/search', {
      params: {
        keyword: searchKeyword.value,
        source: 'web'
      }
    })
    
    // 跳转到百度搜索
    window.open(`https://www.baidu.com/s?wd=${encodeURIComponent(searchKeyword.value)}`, '_blank')
    
    // 刷新数据
    await loadData()
    showSuggestions.value = false
  } catch (error) {
    console.error('搜索失败:', error)
  }
}

// 加载数据
const loadData = async () => {
  try {
    const [hotRes, detailRes, recentRes, trendRes, hotspotsRes] = await Promise.all([
      axios.get('/api/search/hot'),
      axios.get('/api/search/hot/detail'),
      axios.get('/api/search/recent'),
      axios.get('/api/search/trend', { params: { hours: 24 } }),
      axios.get('/api/search/hotspots')
    ])
    
    hotKeywords.value = hotRes.data
    hotKeywordsDetail.value = detailRes.data
    recentSearches.value = recentRes.data
    hotspots.value = hotspotsRes.data
    trendData.value = trendRes.data
    
    // 更新统计
    stats.value.todayCount = hotKeywordsDetail.value.reduce((sum, item) => sum + item.count, 0)
    stats.value.totalCount = stats.value.todayCount * 7 // 估算
    stats.value.hotCount = hotspots.value.length
    stats.value.trendData = trendRes.data
    
    // 更新建议
    suggestions.value = hotKeywords.value.slice(0, 5)
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

// 初始化图表
const initChart = () => {
  const chartDom = document.getElementById('trendChart')
  if (!chartDom) return
  
  const chart = echarts.init(chartDom)
  
  const hours = trendData.value.map(item => `${item.hour}:00`)
  const counts = trendData.value.map(item => item.count)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: hours
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: counts,
      type: 'line',
      smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(78, 110, 242, 0.3)' },
          { offset: 1, color: 'rgba(78, 110, 242, 0)' }
        ])
      },
      itemStyle: {
        color: '#4E6EF2'
      }
    }]
  }
  
  chart.setOption(option)
}

onMounted(async () => {
  await loadData()
  nextTick(() => {
    initChart()
  })
})
</script>
