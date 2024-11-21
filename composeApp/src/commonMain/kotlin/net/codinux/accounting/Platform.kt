package net.codinux.accounting

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform