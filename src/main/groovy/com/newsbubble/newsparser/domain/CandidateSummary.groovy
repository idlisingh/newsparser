package com.newsbubble.newsparser.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.sql.Date
import java.sql.Timestamp

@ToString(includePackage = false)
@EqualsAndHashCode(includes = [ "candidate", "source", "newsDate" ])
class CandidateSummary {
    def String candidate
    def String source
    def Date newsDate
    def Integer count = 0
    def Timestamp createdTs
    def Timestamp updatedTs
}