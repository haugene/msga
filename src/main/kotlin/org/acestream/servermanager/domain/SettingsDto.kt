package org.acestream.servermanager.domain

data class SettingsDto(
    val configureDnsServers: Boolean,
    val dnsServers: List<String>,
    val connectVpnOnStartup: Boolean,
    val openvpnUsername: String,
    val openvpnPassword: String,
    val liveCacheSize: Int,
    val fetchOpenPort: Boolean,
    val aceServerPort: Int,
    val liveBufferSeconds: Int,
    val configureLocalNetworkRoutes: Boolean,
    val localNetworkRoutes: List<String>
)