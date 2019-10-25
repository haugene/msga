package org.acestream.servermanager.resources

import org.acestream.servermanager.utils.CommandLineHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("openvpn")
@CrossOrigin(origins = ["*"])
class OpenVPNResource(
    @Autowired val cliHelper: CommandLineHelper
) {

    @GetMapping("start")
    fun startOpenvpn() {
        return cliHelper.startOpenvpn()
    }

    @GetMapping("stop")
    fun stopOpenvpn() {
        return cliHelper.stopOpenvpn()
    }

    @GetMapping("running")
    fun isOpenVpnRunning(): Map<String, Boolean> {
        return mapOf(Pair("running", cliHelper.openVpnIsRunning()))
    }

    @GetMapping("logs")
    fun logs(): String {
        return cliHelper.openvpnLogs()
    }
}