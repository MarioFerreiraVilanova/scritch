package app.minimal.fasting

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform