package org.acestream.servermanager.resources

import org.acestream.servermanager.domain.SettingsDto
import org.acestream.servermanager.services.SettingsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("settings")
@CrossOrigin(origins = ["*"])
class SettingsResource(
    @Autowired val settingsService: SettingsService
) {

    @GetMapping
    fun getSettings(): SettingsDto {
        return settingsService.getSettings()
    }

    @PutMapping
    fun updateSettings(@RequestBody settings: SettingsDto) {
        settingsService.update(settings)
    }

}