package com.newsbubble.newsparser

import com.newsbubble.newsparser.domain.ArticleSummary
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Spy

import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when
import static org.mockito.MockitoAnnotations.initMocks

class TestParser {

    @Mock DAO dao

    @Spy Parser parser

    @Before def void before() {
        initMocks(this)
        parser.dao = dao
    }

    @Test def void "test no articles from rss"() {
        when(dao.getDistinctArticleHeadlines()).thenReturn([])
        doReturn([]).when(parser).getRss("", "")

        parser.parser("", "")

        verify(dao).insertArticleSummary([])
    }

    @Test def void "test articles from rss but nothing in the db"() {
        List<ArticleSummary> summary = [
                new ArticleSummary(id: 1),
                new ArticleSummary(id: 2)
        ]

        when(dao.getDistinctArticleHeadlines()).thenReturn([])
        when(dao.getArticleSummaryTotalCount()).thenReturn(2L)
        doReturn(summary).when(parser).getRss("", "")

        parser.parser("", "")

        verify(dao).insertArticleSummary(summary)
        verify(dao).getArticleSummaryTotalCount()
    }

    @Test def void "test articles from rss and some entries in db"() {
        List<ArticleSummary> summary = [
                new ArticleSummary(id: 1, headlines: "headlines1"),
                new ArticleSummary(id: 2, headlines: "headlines2")
        ]

        when(dao.getDistinctArticleHeadlines()).thenReturn(["headlines2"])
        when(dao.getArticleSummaryTotalCount()).thenReturn(2L)
        doReturn(summary).when(parser).getRss("", "")

        parser.parser("", "")

        verify(dao).insertArticleSummary([summary[0]])
        verify(dao).getArticleSummaryTotalCount()
    }

    @Test def void "test articles from rss and all entries in db"() {
        List<ArticleSummary> summary = [
                new ArticleSummary(id: 1, headlines: "headlines1"),
                new ArticleSummary(id: 2, headlines: "headlines2")
        ]

        when(dao.getDistinctArticleHeadlines()).thenReturn(["headlines1", "headlines2"])
        when(dao.getArticleSummaryTotalCount()).thenReturn(2L)
        doReturn(summary).when(parser).getRss("", "")

        parser.parser("", "")

        verify(dao).insertArticleSummary([])
        verify(dao).getArticleSummaryTotalCount()
    }

    @Test def void "test parser called multiple times"() {
        List<ArticleSummary> summary = [
                new ArticleSummary(id: 1, headlines: "headlines1"),
                new ArticleSummary(id: 2, headlines: "headlines2")
        ]

        when(dao.getDistinctArticleHeadlines()).thenReturn([])
        doReturn(summary).when(parser).getRss("", "")

        parser.parser("", "")

        verify(dao).insertArticleSummary(summary)

        //  Second time around if the same values are returned then nothing should get inserted in the db
        when(dao.getDistinctArticleHeadlines()).thenReturn(["headlines1", "headlines2"])
        doReturn(summary).when(parser).getRss("", "")

        parser.parser("", "")

        verify(dao).insertArticleSummary([])
    }
}