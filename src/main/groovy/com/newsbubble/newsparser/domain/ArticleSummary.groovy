package com.newsbubble.newsparser.domain

import groovy.transform.ToString

import java.sql.Date
import java.sql.Timestamp

@ToString(includes = ["headlines"], includePackage = false)
class ArticleSummary {
    def String headlines
    def Date newsDate
    def String source
    def String link
    def String description
    def Timestamp createdTs
}