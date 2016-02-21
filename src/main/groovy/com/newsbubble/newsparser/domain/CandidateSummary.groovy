package com.newsbubble.newsparser.domain

import groovy.transform.ToString

import java.sql.Date
import java.sql.Timestamp

@ToString(includePackage = false)
class CandidateSummary {
    def String candidate
    def String source
    def Date newsDate
    def Integer count = 0
    def Timestamp createdTs
    def Timestamp updatedTs

    def CandidateSourceKey getKey() {
        new CandidateSourceKey(candidate: this.candidate, source: this.source, newsDate: this.newsDate)
    }
    
    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        CandidateSummary that = (CandidateSummary) o

        if (candidate != that.candidate) return false
        if (newsDate != that.newsDate) return false
        if (source != that.source) return false

        return true
    }

    int hashCode() {
        int result
        result = candidate.hashCode()
        result = 31 * result + source.hashCode()
        result = 31 * result + newsDate.hashCode()
        return result
    }
}