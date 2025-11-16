package com.gunishjain.workpad.domain.model

data class PageNode(
    val page: Page,
    val children: List<PageNode>,
    val depth: Int
)