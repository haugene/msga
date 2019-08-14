package org.acestream.servermanager.domain

data class MetaInfoDto(
    val javaVersion: String,
    val openvpnVersion: String,
    val acestream: AcestreamInfo
)