package com.yimiao100.sale.bean

/**
 * Created by Michel on 2017/6/21.
 */
data class Ad(
        var id: Int,
        var activityCode: String,
        var activityName: String,
        var imagePath: String,
        var imageUrl: String,
        var pageJumpUrl: String,
        var isPageJump: Int,
        var isPublish: Int,
        var publishAt: Long,
        var expiredAt: Long,
        var createdAt: Long,
        var updatedAt: Long
)