package com.newsbubble.newsparser

import com.newsbubble.newsparser.domain.ArticleSummary
import com.newsbubble.newsparser.domain.CandidateDetails
import com.newsbubble.newsparser.domain.CandidateSummary
import groovy.sql.Sql
import org.apache.log4j.Logger

import java.sql.Timestamp

class DAO {
    def Sql sql

    def static Logger LOG = Logger.getLogger(DAO.class)

    def DAO() {
        def url = System.getProperty("db.url")
        def userId = System.getProperty("db.user")
        def password = System.getProperty("db.password")
        def driver = System.getProperty("db.driver")
        sql = Sql.newInstance(url, userId, password, driver)
    }

    def List<CandidateSummary> getExistingCandidateSummary() {
        def List<CandidateSummary> candidateSummaries = []
        sql.eachRow("select news_date, candidate, source, count, created_ts, updated_ts from candidate_summary") {
            candidateSummaries += new CandidateSummary(
                    candidate: it.candidate,
                    source: it.source,
                    newsDate: it.news_date,
                    count: it.count,
                    createdTs: it.created_ts,
                    updatedTs: it.updated_ts
            )
        }
        LOG.info("Number of summaries from db: ${candidateSummaries.size()}")
        candidateSummaries
    }

    def List<CandidateDetails> getExistingCandidateDetails() {
        def List<CandidateDetails> dbCandidates = []
        sql.eachRow("select id, candidate, article_id, created_ts from candidate_details") {
            dbCandidates += new CandidateDetails(
                    id: it.id,
                    candidate: it.candidate,
                    articleId: it.article_id,
                    createdTs: it.created_ts
            )
        }
        LOG.info("Number of candidate details from db: ${dbCandidates.size()}")
        dbCandidates
    }

    def List<ArticleSummary> getArticleSummary(Timestamp lastRun) {
        def List<ArticleSummary> articles = []
        sql.eachRow("select id, headlines, news_date, source, article_link, description, created_ts from article_summary where created_ts > ?", [lastRun]) {
            articles += new ArticleSummary(
                    id: it.id,
                    headlines: it.headlines,
                    newsDate: it.news_date,
                    source: it.source,
                    link: it.article_link,
                    description: it.description,
                    createdTs: it.created_ts
            )
        }
        LOG.info("Number of articles processing: ${articles.size()}")

        articles
    }

    def insertCandidateSummary(List<CandidateSummary> insertValues) {
        insertValues.each { CandidateSummary key ->
            sql.execute("insert into candidate_summary(news_date, candidate, source, count) values(?, ?, ?, ?)",
                    [key.newsDate, key.candidate, key.source, key.count]
            )
        }
    }

    def updateCandidateSummaryCount(List<CandidateSummary> updateValues) {
        updateValues.each { CandidateSummary key ->
            sql.execute("update candidate_summary set count = (count + ? ), updated_ts = current_timestamp where news_date = ? and candidate = ? and source = ?",
                    [key.count, key.newsDate, key.candidate, key.source]
            )
        }
    }

    def insertCandidateDetails(List<CandidateDetails> candidateDetails) {
        candidateDetails.each { detail ->
            sql.execute("insert into candidate_details(candidate, article_id) values(?, ?)", [detail.candidate, detail.articleId])
        }
    }

    def insertArticleSummary(List<ArticleSummary> articles) {
        articles.each { ArticleSummary it ->
            sql.execute("insert into article_summary(headlines, news_date, source, article_link, description) values(?, ?, ?, ?, ?)",
                    [it.headlines, it.newsDate, it.source, it.link, it.description])
        }
    }

    def List<String> getDistinctArticleHeadlines() {
        def List<String> dbHeadLines = []

        sql.eachRow("select distinct headlines from article_summary") {
            dbHeadLines += it.headlines
        }

        dbHeadLines
    }

    def Timestamp getLastRun() {
        def Timestamp lastRun
        sql.eachRow("select run_time from last_run") {
            lastRun = it.run_time
        }
        lastRun
    }

    def void insertLastRun(Timestamp currentTime) {
        sql.execute("insert into last_run(run_time) values (?)", currentTime)
    }

    def void updateLastRun(Timestamp currentTime) {
        sql.execute("update last_run set run_time = ?", [currentTime])
    }

    def void truncateCandidateSummary() {
        sql.execute("truncate table candidate_summary")
    }

    def void truncateCandidateDetails() {
        sql.execute("truncate table candidate_details")
    }

    def Long getArticleSummaryTotalCount() {
        def totalCount = 0
        sql.eachRow("select count(*) as count from article_summary") { totalCount = it.count }
        totalCount
    }
}