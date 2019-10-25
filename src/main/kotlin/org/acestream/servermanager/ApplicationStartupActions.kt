package org.acestream.servermanager

import org.acestream.servermanager.domain.SettingsDto
import org.acestream.servermanager.services.SettingsService
import org.acestream.servermanager.utils.CommandLineHelper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class ApplicationStartupActions(
    @Autowired val settingsService: SettingsService,
    @Autowired val commandLineHelper: CommandLineHelper
): ApplicationListener<ApplicationReadyEvent> {

    companion object {
        private val logger = LoggerFactory.getLogger(ApplicationStartupActions::class.java)
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        val settings = loadSettings()
        if (settings.configureDnsServers) commandLineHelper.setDnsServers()
        if (settings.connectVpnOnStartup) commandLineHelper.startOpenvpn()
    }

    private fun loadSettings(): SettingsDto {
        if (settingsService.settingsFileExists()) {
            logger.info("Existing settings-file found, will be used.")
        } else {
            logger.info("No settings-file found, generating new with default values")
            settingsService.generateSettings()
        }
        return settingsService.getSettings()
    }
}