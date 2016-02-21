package com.newsbubble.newsparser

import com.newsbubble.newsparser.domain.ArticleSummary
import com.newsbubble.newsparser.domain.CandidateDetails
import com.newsbubble.newsparser.domain.CandidateSourceKey
import com.newsbubble.newsparser.domain.CandidateSummary
import org.apache.log4j.Logger

import java.sql.Timestamp

class Analyser {

    def DAO dao

    def static Logger LOG = Logger.getLogger(Analyser.class)

    def Analyser() {
        dao = new DAO()
    }

    def void analyse() {
        LOG.info("Analysing...")

        Timestamp lastRun = "get last run"()

        def List<ArticleSummary> articles = dao.getArticleSummary(lastRun)
        def List details = processArticles(articles)

        List<CandidateDetails> candidateDetails = details[1]
        List<CandidateDetails> dbCandidateDetails = dao.getExistingCandidateDetails()
        candidateDetails.removeAll(dbCandidateDetails)
        dao.insertCandidateDetails(candidateDetails)

        List<CandidateSummary> newCandidateSummary = details[0]
        List<CandidateSummary> dbCandidateSummary = dao.getExistingCandidateSummary()
        mergeValues(newCandidateSummary, dbCandidateSummary)

        printCandidateSummaryDetails()

        LOG.info("Done processing")
    }

    def processArticles(List<ArticleSummary> articles) {
        def candidateDetails = []
        def Map<CandidateSourceKey, Integer> candidateCountMap = [:].withDefault { 0 }
        articles.each { ArticleSummary articleSummary ->
            def headlines = (articleSummary.headlines == null) ? "" : articleSummary.headlines.toLowerCase()
            def description = (articleSummary.description == null) ? "" : articleSummary.description.toLowerCase()
            Constants.CANDIDATES.each { String candidate ->
                if (headlines.contains(candidate) || description.contains(candidate)) {
                    def CandidateSourceKey candidateSourceKey = new CandidateSourceKey(candidate: candidate, source: articleSummary.source, newsDate: articleSummary.newsDate)
                    candidateCountMap[candidateSourceKey] = candidateCountMap[candidateSourceKey] + 1
                    candidateDetails += new CandidateDetails(
                            candidate: candidate,
                            articleId: articleSummary.id
                    )
                }
            }
        }

        def List<CandidateSummary> candidateSummaries = candidateCountMap.collect { key, value ->
            new CandidateSummary(candidate: key.candidate, source: key.source, newsDate: key.newsDate, count: value)
        }

        LOG.info("Raw candidate map: ${candidateCountMap.size()}")
        return [candidateSummaries, candidateDetails]
    }

    def void printCandidateSummaryDetails() {

        List<CandidateSummary> summary = dao.getExistingCandidateSummary()
        def Map<String, Integer> summaryMap = [:].withDefault { 0 }
        summary.each {
            summaryMap[it.candidate] = summaryMap[it.candidate] + it.count
        }

        summaryMap = summaryMap.sort{ it.value }
        def totalArticles = summaryMap.values().sum()
        LOG.info("Total articles processed $totalArticles")
        LOG.info("Current candidate summary details")
        summaryMap.each { candidate, sum ->
            LOG.info("Candidate: $candidate Count: $sum (${ new Double(sum / totalArticles * 100.0).round(2) }%)".padLeft(2))
        }
    }

    def void mergeValues(List<CandidateSummary> newCandidateSummary, List<CandidateSummary> dbCandidateSummary) {
        def updateValues = newCandidateSummary.findAll { dbCandidateSummary.contains(it) }
        def insertValues = newCandidateSummary.findAll { !dbCandidateSummary.contains(it) }

        LOG.info("Insert: ${insertValues.size()} Update: ${updateValues.size()}")

        dao.insertCandidateSummary(insertValues)
        dao.updateCandidateSummaryCount(updateValues)
    }

    def Timestamp "get last run"() {
        def Timestamp lastRun = dao.getLastRun()

        LOG.info("Last run: $lastRun")
        def currentTime = new Timestamp(System.currentTimeMillis())

        if (lastRun == null) {
            lastRun = new Timestamp(0)
            dao.insertLastRun(currentTime)
            dao.truncateCandidateSummary()
            LOG.info("First time processing, Truncated candidate_summary table")
        } else {
            LOG.info("Updating run_time")
            dao.updateLastRun(currentTime)
        }

        LOG.info("Processing since: $lastRun")
        lastRun
    }
}