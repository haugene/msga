package org.acestream.servermanager.routes

import org.acestream.servermanager.services.SettingsService
import org.apache.camel.spring.SpringRouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AcestreamNodeProxyRoute(
    @Autowired val settingsService: SettingsService
) : SpringRouteBuilder() {
    override fun configure() {
        from("servlet:node?servletName=AcestreamProxyServlet&matchOnUriPrefix=true")
            .routeId("acestream-node-proxy")
            .process {
                it.`in`.headers["acestream_server_port"] = settingsService.getSettings().aceServerPort
            }
            .toD("http://localhost:\${header.acestream_server_port}?bridgeEndpoint=true")
            .process { it.`in`.headers["Access-Control-Allow-Origin"] = "*" }
    }
}