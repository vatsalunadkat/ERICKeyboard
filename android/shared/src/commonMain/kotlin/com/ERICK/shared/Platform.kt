package com.ERICK.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
