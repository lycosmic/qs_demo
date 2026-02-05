package com.example.domain.model


/**
 * 夜晚行动结果
 */
data class NightActionResult(
    val hostMessage: String,      // 主持人总结的话术
    val actionDetails: List<String>
)