package org.acestream.servermanager.routes

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.spring.SpringRouteBuilder
import org.springframework.stereotype.Component
import java.net.URLEncoder

@Component
class RequestProxyRoute : SpringRouteBuilder() {
    override fun configure() {
        from("servlet:request?servletName=RequestProxyServlet")
            .routeId("request-proxy")
            .process(UrlProcessor())
            .toD("\${header.PROXY_TO_URL}?bridgeEndpoint=true")
            .process { it.`in`.headers["Access-Control-Allow-Origin"] = "*" }
    }
}

class UrlProcessor: Processor {
    override fun process(exchange: Exchange) {
        // Verify input
        if (!exchange.`in`.getHeader("url", String::class.java).startsWith(prefix = "http")){
            throw IllegalArgumentException("Proxy URL must start with \"http\"")
        }

        // URL-encode most of it, but keep protocol:// for Camel to know what's going on.
        val httpQuery = exchange.`in`.getHeader(Exchange.HTTP_QUERY, String::class.java).removePrefix("url=")
        val protocol = httpQuery.substringBefore("://")
        val hostPathAndQuery = httpQuery.substringAfter("://")
        val urlEncodedHostPathAndQuery = URLEncoder.encode(hostPathAndQuery, Charsets.UTF_8.name())

        // Set the header to be used in toD endpoint
        exchange.`in`.setHeader("PROXY_TO_URL", "$protocol://$urlEncodedHostPathAndQuery")
    }
}