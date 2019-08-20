package org.acestream.servermanager.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.acestream.servermanager.domain.AcestreamInfo
import org.acestream.servermanager.domain.SettingsDto
import org.acestream.servermanager.services.SettingsService
import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream
import java.io.File
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.core.MediaType

@Service
class CommandLineHelper(
    @Autowired val settingsService: SettingsService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(CommandLineHelper::class.java)
    }

    fun startAcestreamServer(): String {
        val logFile = File("/var/log/acestream/server1.log")

        if (settings().fetchOpenPort) {
            try {
                settingsService.update(settings().copy(aceServerPort = openPort()))
            } catch (e: Exception) {
                logger.warn("Couldn't get an open port from PIA, falling back to value from settings.json", e)
            }
        }


        val process = ProcessExecutor()
            .command(
                "/opt/acestream/acestreamengine",
                "--client-console",
                "--live-cache-type memory",
                "--live-cache-size ${settings().liveCacheSize * 1024 * 1024}",
                "--port=${settings().aceServerPort}",
                "--live-buffer ${settings().liveBufferSeconds}",
                "--http-port=30000"
            )
            .redirectOutput(logFile.outputStream())
            .start()

        logger.info("AceStream is alive: ${process.process.isAlive}")
        return "OK"
    }

    fun getJavaVersion(): String {
        val regex = """openjdk version \"(.*)\"""".toRegex()

        val result = ProcessExecutor().command("java", "-version")
            .readOutput(true).execute()
            .outputUTF8()

        return regex.find(result)!!.groupValues[1]
    }

    fun getOpenVpnVersion(): String {
        val regex = """OpenVPN\s+(\d+\.\d+\.\d+)\s+x86_64""".toRegex()

        val result = ProcessExecutor().command("openvpn", "--version")
            .readOutput(true).execute()
            .outputUTF8()

        return regex.find(result)!!.groupValues[1]
    }

    fun getAcestreamVersion(): AcestreamInfo {
        val regex = """version:\s+(\d+\.\d+\.\d+)\s+revision:\s+(\d+)""".toRegex()

        val result = ProcessExecutor().command("/opt/acestream/acestreamengine", "-v")
            .readOutput(true).execute()
            .outputUTF8()

        val (version, revision) = regex.find(result)!!.destructured
        return AcestreamInfo(version, revision)
    }

    fun startOpenvpn() {
        if (!tunDeviceExists()) {
            logger.info("No TUN device exists, creating one")
            createTunDevice()
        }

        setUsernameAndPassword()

        logger.info("Starting OpenVPN")
        ProcessExecutor()
            .command(
                "openvpn",
                "--writepid", "/opt/openvpn/pid.txt",
                "--log", "/opt/openvpn/client.log",
                //"--log-append", "/opt/openvpn/client.log", // Append instead of replace on each run
                "--script-security", "2",
                "--down", "/opt/openvpn/killAcestream.sh",
                "--config", "/opt/openvpn/Berlin.ovpn",
                "--daemon"
            )
            .redirectOutput(Slf4jStream.of(logger).asWarn())
            .exitValueNormal()
            .execute()
    }

    private fun setUsernameAndPassword() {
        val settings = settingsService.getSettings()
        File("/opt/openvpn/credentials.txt")
            .writeText(settings.openvpnUsername + "\n" + settings.openvpnPassword)
    }

    private fun tunDeviceExists() = File("/dev/net/tun").exists()

    private fun createTunDevice() {
        ProcessExecutor().command("mkdir", "-p", "/dev/net")
            .redirectOutput(Slf4jStream.of(logger).asWarn())
            .exitValueNormal()
            .execute()
        ProcessExecutor().command("mknod", "/dev/net/tun", "c", "10", "200")
            .redirectOutput(Slf4jStream.of(logger).asWarn())
            .exitValueNormal()
            .execute()
        ProcessExecutor().command("chmod", "0666", "/dev/net/tun")
            .redirectOutput(Slf4jStream.of(logger).asWarn())
            .exitValueNormal()
            .execute()
    }

    fun testDns() {
        ProcessExecutor()
            .command(
                "dig",
                "google.com",
                "+short", // Only IP in response
                "+time=1" // Set quicker timeout
            )
            .exitValueNormal()
            .execute()
    }

    fun setDnsServers() {
        logger.info("Setting DNS records")
        File("/etc/resolv.conf")
            .writeText(
                settingsService.getSettings()
                    .dnsServers.joinToString(separator = "\n", transform = { "nameserver $it" })
            )
    }

    fun publicIp(): String? {
        return ClientBuilder.newClient()
            .target("http://ipecho.net")
            .path("plain")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get(String::class.java)
    }

    fun openPort(): Int {
        val piaClientId = RandomStringUtils.randomAlphanumeric(60)
        val response = ClientBuilder.newClient()
            .target("http://209.222.18.222:2000")
            .queryParam("client_id", piaClientId)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get(String::class.java)

        val port = ObjectMapper().readValue<JsonNode>(response).get("port").asText()
        logger.info("Got port $port from PIA")

        return port.toInt()
    }

    fun openvpnLogs(): String {
        return File("/opt/openvpn/client.log").readText()
    }

    fun settings(): SettingsDto = settingsService.getSettings()
}