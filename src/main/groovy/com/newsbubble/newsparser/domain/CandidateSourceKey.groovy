package com.newsbubble.newsparser.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.sql.Date

@ToString(includePackage = false)
@EqualsAndHashCode(includes = [ "candidate", "source" ])
class CandidateSourceKey {
    def String candidate
    def String source
    def Date newsDate
}