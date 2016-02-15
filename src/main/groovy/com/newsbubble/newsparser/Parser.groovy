package com.newsbubble.newsparser

import com.newsbubble.newsparser.domain.ArticleSummary
import groovy.sql.Sql
import org.apache.log4j.Logger

import java.text.SimpleDateFormat

class Parser {

    def Sql sql
    def List<String> dbheadlines = []

    def static Logger LOG = Logger.getLogger(Parser.class)

    Parser() {
        sql = Sql.newInstance("jdbc:postgresql://localhost:5432/news", "postgres", "abc123", "org.postgresql.Driver")

        sql.eachRow("select distinct headlines from article_summary") {
            dbheadlines += it.headlines
        }

    }

    def void parser(String source, String rssLink) {

        LOG.info("Processing site: $source")

        def rssFeed = rssLink.toURL().text

        def List<ArticleSummary> articles = []

        def format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z")

        def rss = new XmlSlurper().parseText(rssFeed)
        rss.channel.item.each {
            def String description = it.description
            description = description.length() > 10000 ? description.substring(0, 9999) : description
            articles += new ArticleSummary(
                    source: source,
                    headlines: it.title,
                    link: it.link,
                    newsDate: new java.sql.Date(format.parse(it.pubDate.toString()).time),
                    description: description
            )
        }

        LOG.debug("Parsed ${articles.size()} number of items from source")

        articles = articles.findAll{ !dbheadlines.contains(it.headlines) }

        LOG.debug("Filtered article size: ${articles.size()}")

        if (!articles.isEmpty()) {
            articles.each { ArticleSummary it ->
                sql.execute("insert into article_summary(headlines, news_date, source, article_link, description) values(?, ?, ?, ?, ?)",
                        [it.headlines, it.newsDate, source, it.link, it.description])
            }
        }

        LOG.info("Done processing for $source. Inserted ${articles.size()} values")
    }
}