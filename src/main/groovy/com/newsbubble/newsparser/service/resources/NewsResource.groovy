package com.newsbubble.newsparser.service.resources

import com.newsbubble.newsparser.DAO

import javax.ws.rs.GET
import javax.ws.rs.Path

@Path("/news")
class NewsResource {

    def DAO dao

    @GET
    @Path("/summary")
    def String newsSummary() {
        def summary = dao.getExistingCandidateSummary()
        def result = [:].withDefault { 0 }
        summary.each {
            result[it.candidate] = result[it.candidate] + it.count
        }
        def resultStr = ""
        result.each {
            resultStr += """{text: "${it.key}", weight: ${it.value}}, """
        }
        def html = this.getClass().getClassLoader().getResourceAsStream("index.html").text
        html.replace("REPLACEME", resultStr)
    }
}