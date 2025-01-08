package net.codinux.accounting.platform

expect object Platform {

    val type: PlatformType

}


val Platform.isAndroid: Boolean
    get() = this.type == PlatformType.Android

val Platform.isIOS: Boolean
    get() = this.type == PlatformType.iOS