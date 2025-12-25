package com.gunishjain.workpad.ui.home

import com.gunishjain.workpad.domain.model.Page

data class HomeUiState(
    val username: String? = null,
    val userId: String? = null,
    val pages: List<Page> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val error: String ? = null,
    val isPrivateListCollapsed: Boolean = false,
    val isParentPageExpanded: MutableMap<String, Boolean> = mutableMapOf()
)
