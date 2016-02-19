package com.newsbubble.newsparser

import com.newsbubble.newsparser.domain.ArticleSummary
import org.apache.log4j.Logger

import java.text.SimpleDateFormat

class Parser {

    def DAO dao

    def List<String> dbHeadLines

    def static Logger LOG = Logger.getLogger(Parser.class)

    Parser() {
        dao = new DAO()

        dbHeadLines = dao.getDistinctArticleHeadlines()
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

        articles = articles.findAll{ !dbHeadLines.contains(it.headlines) }

        LOG.debug("Filtered article size: ${articles.size()}")

        dao.insertArticleSummary(articles)

        LOG.info("Done processing for $source. Inserted ${articles.size()} values")
    }
}