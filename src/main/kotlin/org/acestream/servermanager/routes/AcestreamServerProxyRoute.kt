package org.acestream.servermanager.routes

import org.apache.camel.spring.SpringRouteBuilder
import org.springframework.stereotype.Component

@Component
class AcestreamServerProxyRoute : SpringRouteBuilder() {
    override fun configure() {
        from("servlet:server?servletName=AcestreamProxyServlet&matchOnUriPrefix=true")
            .routeId("acestream-server-proxy")
            .to("http://localhost:30000?bridgeEndpoint=true")
            .process { it.`in`.headers["Access-Control-Allow-Origin"] = "*" }
    }
}