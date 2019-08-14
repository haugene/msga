package org.acestream.servermanager.domain

data class SettingsDto(
    val dnsServers: List<String>,
    val connectVpnOnStartup: Boolean,
    val openvpnUsername: String,
    val openvpnPassword: String
)