package org.acestream.servermanager.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.acestream.servermanager.domain.SettingsDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Service
class SettingsService {

    companion object {
        private val logger = LoggerFactory.getLogger(SettingsService::class.java)
    }

    @Value("\${msga.settings.file}")
    lateinit var settingsFileLocation: String

    fun getSettings(): SettingsDto = jacksonObjectMapper()
            .readValue(File(settingsFileLocation))

    fun settingsFileExists(): Boolean {
        logger.info("Looking for file $settingsFileLocation")
        val settingsFile = File(settingsFileLocation)
        return settingsFile.exists() && settingsFile.isFile
    }

    fun generateSettings() {
        logger.info("Settings will be written to: $settingsFileLocation")

        val settings = SettingsDto(
                configureDnsServers = true,
                dnsServers = listOf("8.8.8.8", "8.8.4.4"),
                connectVpnOnStartup = false,
                openvpnUsername = "",
                openvpnPassword = "",
                liveCacheSize = 200,
                fetchOpenPort = true,
                aceServerPort = 25000,
                liveBufferSeconds = 30,
                configureLocalNetworkRoutes = false,
                localNetworkRoutes = listOf("192.168.1.0/24")
        )

        writeSettings(settings)
    }

    private fun writeSettings(settings: SettingsDto) {
        val settingsFile = File(settingsFileLocation)
        avoidOverridingDirectories(settingsFile)
        settingsFile.writeText(
                jacksonObjectMapper()
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(settings)
        )
    }

    private fun avoidOverridingDirectories(settingsFile: File) {
        if (settingsFile.exists() && settingsFile.isDirectory) {
            throw IllegalArgumentException("Settings-file cannot be a directory (which already exists)")
        }
    }

    fun update(settings: SettingsDto) {
        logger.info("Updating settings")
        writeSettings(settings)
    }
}