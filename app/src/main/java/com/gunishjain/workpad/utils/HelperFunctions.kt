package com.gunishjain.workpad.utils

fun isValidUsername(username: String): Boolean {
    val regex = Regex("^(?=.*[a-zA-Z])(?=.{3,20}$)(?!_)(?!.*__)[a-zA-Z0-9_]+(?<!_)$")
    return regex.matches(username)
}
