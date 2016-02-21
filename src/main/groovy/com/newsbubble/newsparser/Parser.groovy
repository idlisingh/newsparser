package com.newsbubble.newsparser

import com.newsbubble.newsparser.domain.ArticleSummary
import org.apache.log4j.Logger

import java.sql.Date
import java.text.SimpleDateFormat

class Parser {

    def static Logger LOG = Logger.getLogger(Parser.class)
    def static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z")

    def DAO dao

    def Parser() { }

    def void parser(String source, String rssLink) {

        LOG.info("Processing site: $source")

        List<ArticleSummary> articles = getRss(rssLink, source)

        LOG.debug("Parsed ${articles.size()} number of items from source")

        def List<String> dbHeadLines = dao.getDistinctArticleHeadlines()

        articles = articles.findAll{ !dbHeadLines.contains(it.headlines) }

        LOG.debug("Filtered article size: ${articles.size()}")

        dao.insertArticleSummary(articles)

        LOG.info("Done processing for $source. Inserted ${articles.size()} values")
    }

    def List<ArticleSummary> getRss(String rssLink, String source) {
        def rssFeed = rssLink.toURL().text

        def List<ArticleSummary> articles = []
        def rss = new XmlSlurper().parseText(rssFeed)
        rss.channel.item.each {
            def String description = it.description
            description = description.length() > 10000 ? description.substring(0, 9999) : description
            articles += new ArticleSummary(
                    source: source,
                    headlines: it.title,
                    link: it.link,
                    newsDate: new Date(DATE_FORMAT.parse(it.pubDate.toString()).time),
                    description: description
            )
        }
        articles
    }
}