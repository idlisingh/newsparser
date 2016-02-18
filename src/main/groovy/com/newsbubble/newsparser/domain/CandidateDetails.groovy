package com.newsbubble.newsparser.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.sql.Timestamp

@ToString(includePackage = false)
@EqualsAndHashCode(includes = [ "candidate", "articleId" ])
class CandidateDetails {
    def Integer id
    def String candidate
    def Integer articleId
    def Timestamp createdTs
}