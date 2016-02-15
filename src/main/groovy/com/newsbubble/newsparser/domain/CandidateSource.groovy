package com.newsbubble.newsparser.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.sql.Timestamp

@ToString(includePackage = false)
@EqualsAndHashCode(includes = [ "candidate", "source" ])
class CandidateSource {
    def CandidateSourceKey key
    def int count
    def Timestamp createdTs
}