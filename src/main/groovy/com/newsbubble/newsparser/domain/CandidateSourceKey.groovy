package com.newsbubble.newsparser.domain

import groovy.transform.ToString

import java.sql.Date

@ToString(includePackage = false)
class CandidateSourceKey {
    def String candidate
    def String source
    def Date newsDate

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        CandidateSourceKey that = (CandidateSourceKey) o

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