package org.acestream.servermanager.routes

import org.apache.camel.spring.SpringRouteBuilder
import org.springframework.stereotype.Component

@Component
class AcestreamServerProxyRoute : SpringRouteBuilder() {
    override fun configure() {
        from("servlet:server?servletName=AcestreamProxyServlet&matchOnUriPrefix=true")
            .routeId("acestream-server-proxy")
            .toD("http://localhost:30000?bridgeEndpoint=true")

    }
}