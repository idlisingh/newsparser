package com.newsbubble.newsparser

import com.newsbubble.newsparser.domain.ArticleSummary
import com.newsbubble.newsparser.domain.CandidateSourceKey
import groovy.sql.Sql
import org.apache.log4j.Logger

import java.sql.Timestamp

class Analyser {

    def Sql sql

    def static Logger LOG = Logger.getLogger(Analyser.class)

    def Analyser() {
        def url = System.getProperty("db.url")
        def userId = System.getProperty("db.user")
        def password = System.getProperty("db.password")
        sql = Sql.newInstance(url, userId, password, "org.postgresql.Driver")
    }

    def void analyse() {
        LOG.info("Analysing...")

        Timestamp lastRun = "get last run"()

        Map<CandidateSourceKey, Integer> candidateCountMap = getLatestCandidateSummaries(lastRun)

        ArrayList<CandidateSourceKey> dbCandidateSourceValues = 'get existing candidate summaries'()

        mergeValues(candidateCountMap, dbCandidateSourceValues)

        printCandidateSummaryDetails()

        LOG.info("Done processing")
    }

    def void printCandidateSummaryDetails() {
        def Map<String, Integer> map = [:]
        sql.eachRow("select candidate, sum(count) as sum from candidate_summary group by candidate order by sum;") {
            map[it.candidate] = it.sum
        }
        def totalArticles = map.values().sum()
        LOG.info("Total articles processed $totalArticles")
        LOG.info("Current candidate summary details")
        map.each { candidate, sum ->
            LOG.info("Candidate: $candidate Count: $sum (${ new Double(sum / totalArticles * 100.0).round(2) }%)".padLeft(2))
        }
    }

    def Map<CandidateSourceKey, Integer> getLatestCandidateSummaries(Timestamp lastRun) {
        def List<ArticleSummary> articles = []
        sql.eachRow("select headlines, news_date, source, article_link, description, created_ts from article_summary where created_ts > ?", [lastRun]) {
            articles += new ArticleSummary(
                    headlines: it.headlines,
                    newsDate: it.news_date,
                    source: it.source,
                    link: it.article_link,
                    description: it.description,
                    createdTs: it.created_ts
            )
        }

        LOG.info("Number of articles processing: ${articles.size()}")

        def Map candidateCountMap = [:].withDefault { 0 }
        articles.each { ArticleSummary articleSummary ->
            def headlines = (articleSummary.headlines == null) ? "" : articleSummary.headlines.toLowerCase()
            def description = (articleSummary.description == null) ? "" : articleSummary.description.toLowerCase()
            Constants.CANDIDATES.each { String candidate ->
                if (headlines.contains(candidate) || description.contains(candidate)) {
                    def CandidateSourceKey candidateSourceKey = new CandidateSourceKey(candidate: candidate, source: articleSummary.source, newsDate: articleSummary.newsDate)
                    candidateCountMap[candidateSourceKey] = candidateCountMap[candidateSourceKey] + 1
                }
            }
        }

        LOG.info("Raw candidate map: ${candidateCountMap.size()}")
        candidateCountMap
    }

    def ArrayList<CandidateSourceKey> 'get existing candidate summaries'() {
        def List<CandidateSourceKey> dbCandidateSourceValues = []
        sql.eachRow("select news_date, candidate, source from candidate_summary") {
            dbCandidateSourceValues += new CandidateSourceKey(
                    candidate: it.candidate,
                    source: it.source,
                    newsDate: it.news_date
            )
        }
        LOG.info("Number of summaries from db: ${dbCandidateSourceValues.size()}")
        dbCandidateSourceValues
    }

    def void mergeValues(Map<CandidateSourceKey, Integer> candidateCountMap, dbCandidateSourceValues) {
        def updateValues = candidateCountMap.findAll { dbCandidateSourceValues.contains(it.key) }
        def insertValues = candidateCountMap.findAll { !dbCandidateSourceValues.contains(it.key) }

        LOG.info("Insert: ${insertValues.size()} Update: ${updateValues.size()}")

        insertValues.each { CandidateSourceKey key, value ->
            sql.execute("insert into candidate_summary(news_date, candidate, source, count) values(?, ?, ?, ?)",
                    [key.newsDate, key.candidate, key.source, value]
            )
        }

        updateValues.each { CandidateSourceKey key, value ->
            sql.execute("update candidate_summary set count = (count + ? ), updated_ts = current_timestamp where news_date = ? and candidate = ? and source = ?",
                    [value, key.newsDate, key.candidate, key.source]
            )
        }
    }

    def Timestamp "get last run"() {
        def Timestamp lastRun
        sql.eachRow("select run_time from last_run") {
            lastRun = it.run_time
        }

        LOG.info("Last run: $lastRun")
        def currentTime = new Timestamp(System.currentTimeMillis())

        if (lastRun == null) {
            lastRun = new Timestamp(0)
            sql.execute("insert into last_run(run_time) values (?)", currentTime)
            sql.execute("truncate table candidate_summary")
            LOG.info("First time processing, Truncated candidate_summary table")
        } else {
            LOG.info("Updating run_time")
            sql.execute("update last_run set run_time = ?", [currentTime])
        }

        LOG.info("Processing since: $lastRun")
        lastRun
    }
}