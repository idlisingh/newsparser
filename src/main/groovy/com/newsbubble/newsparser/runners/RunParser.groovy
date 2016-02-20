package com.newsbubble.newsparser.runners

import com.newsbubble.newsparser.Parser
import org.apache.log4j.Logger

def Logger LOG = Logger.getLogger(RunParser.class)

def parser = new Parser()

def sourceMap = [
    "huffingtonpost": "http://feeds.huffingtonpost.com/c/35496/f/677086/index.rss",
    "cnn": "http://rss.cnn.com/rss/cnn_allpolitics.rss",
    "fox": "http://feeds.foxnews.com/foxnews/politics",
    "aljazeera": "http://america.aljazeera.com/content/ajam/articles.rss",
    "abc": "http://feeds.abcnews.com/abcnews/politicsheadlines",
    "nbc": "http://www.nbcnewyork.com/news/politics/?rss=y",
    "bbc": "http://feeds.bbci.co.uk/news/politics/rss.xml?edition=us",
    "nytimes": "http://rss.nytimes.com/services/xml/rss/nyt/Politics.xml",
    "guardian": "http://www.theguardian.com/us-news/rss"
]

while(true) {
    LOG.info("Begin processing all sources.")
    sourceMap.each { source, rss ->
        parser.parser(source, rss)
    }
    LOG.info("Done processing all sources. Sleeping")
    Thread.sleep(60000)
    LOG.info("**************************************")
}