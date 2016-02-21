package com.newsbubble.newsparser

import com.newsbubble.newsparser.domain.ArticleSummary
import com.newsbubble.newsparser.domain.CandidateDetails
import com.newsbubble.newsparser.domain.CandidateSummary
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock

import java.sql.Timestamp

import static org.junit.Assert.fail
import static org.mockito.Matchers.any
import static org.mockito.Mockito.never
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when
import static org.mockito.MockitoAnnotations.initMocks

class TestAnalyser {

    @Mock DAO dao
    @Captor ArgumentCaptor<List<CandidateDetails>> insertCandidateDetailsCaptor
    @Captor ArgumentCaptor<List<CandidateSummary>> insertCandidateSummaryCaptor
    @Captor ArgumentCaptor<List<CandidateSummary>> updateCandidateSummaryCaptor

    def Analyser analyser

    @Before def void before() {
        initMocks(this)
        analyser = new Analyser(
                dao: dao
        )
    }

    @Test def void "test getLastRun first time run"() {
        when(dao.getLastRun()).thenReturn(null)

        def result = analyser.getLastRun()

        assert result == new Timestamp(0)

        verify(dao).insertLastRun(any(Timestamp.class))
        verify(dao, never()).updateLastRun(any(Timestamp.class))
        verify(dao).truncateCandidateSummary()
    }

    @Test def void "test getLastRun not first time run"() {
        def ts = new Timestamp(0)

        when(dao.getLastRun()).thenReturn(ts)

        def result = analyser.getLastRun()

        assert result == ts

        verify(dao).updateLastRun(any(Timestamp.class))
    }

    @Test def void "test nothing to process since last run"() {
        def ts = new Timestamp(0)

        when(dao.getLastRun()).thenReturn(ts)
        when(dao.getArticleSummary(ts)).thenReturn([])

        analyser.analyse()

        verify(dao).insertCandidateDetails([])
        verify(dao).insertCandidateSummary([])
        verify(dao).updateCandidateSummaryCount([])
    }

    @Test def void "test first time run"() {
        def ts = new Timestamp(0)

        def summaries = [
                new ArticleSummary(id: 1, source: "bbc", link: "link 1", headlines: "sanders 1", newsDate: java.sql.Date.valueOf("2016-01-01")),
                new ArticleSummary(id: 2, source: "bbc", link: "link 1", headlines: "trump 1", newsDate: java.sql.Date.valueOf("2016-01-01")),
                new ArticleSummary(id: 3, source: "bbc", link: "link 1", headlines: "random guy 1", newsDate: java.sql.Date.valueOf("2016-01-01")),
                new ArticleSummary(id: 4, source: "bbc", link: "link 1", headlines: "trump 2", newsDate: java.sql.Date.valueOf("2016-01-01"))
        ]

        when(dao.getLastRun()).thenReturn(ts)
        when(dao.getArticleSummary(ts)).thenReturn(summaries)

        analyser.analyse()

        verify(dao).insertCandidateDetails(insertCandidateDetailsCaptor.capture())
        verify(dao).insertCandidateSummary(insertCandidateSummaryCaptor.capture())
        verify(dao).updateCandidateSummaryCount(updateCandidateSummaryCaptor.capture())

        assert insertCandidateDetailsCaptor.allValues.flatten().size() == 3
        assert insertCandidateSummaryCaptor.allValues.flatten().size() == 2
        assert updateCandidateSummaryCaptor.allValues.flatten().size() == 0

        insertCandidateDetailsCaptor.allValues.flatten().each { CandidateDetails it ->
            if (it.candidate == "sanders") {
                assert it.articleId == 1
            } else if (it.candidate == "trump") {
                assert it.articleId in [2, 4]
            } else {
                fail("Unexpected value $it")
            }
        }

        insertCandidateSummaryCaptor.allValues.flatten().each { CandidateSummary it ->
            if (it.candidate == "sanders") {
                assert it.count == 1
            } else if (it.candidate == "trump") {
                assert it.count == 2
            } else {
                fail("Unexpected value $it")
            }
        }
    }

    @Test def void "test with values in db"() {
        def ts = new Timestamp(0)

        def articleSummaries = [
                new ArticleSummary(id: 1, source: "bbc", link: "link 1", headlines: "sanders 1", newsDate: java.sql.Date.valueOf("2016-01-01")),    //  update value
                new ArticleSummary(id: 2, source: "bbc", link: "link 1", headlines: "trump 1", newsDate: java.sql.Date.valueOf("2016-01-01")),      //  new entry due to missing in db
                new ArticleSummary(id: 3, source: "bbc", link: "link 1", headlines: "random guy 1", newsDate: java.sql.Date.valueOf("2016-01-01")), //  ignore
                new ArticleSummary(id: 4, source: "bbc", link: "link 1", headlines: "trump 2", newsDate: java.sql.Date.valueOf("2016-01-01")),      //  new entry due to missing in db
                new ArticleSummary(id: 5, source: "xyz", link: "link 1", headlines: "sanders 2", newsDate: java.sql.Date.valueOf("2016-01-01")),    //  new entry due to different source
                new ArticleSummary(id: 6, source: "bbc", link: "link 1", headlines: "sanders 2", newsDate: java.sql.Date.valueOf("2016-01-02")),    //  new entry due to different date

        ]

        def candidateSummaries = [
                new CandidateSummary(candidate: "sanders", source: "bbc", newsDate: java.sql.Date.valueOf("2016-01-01"), count: 10),
                new CandidateSummary(candidate: "cruz", source: "bbc", newsDate: java.sql.Date.valueOf("2016-01-01"), count: 10)
        ]

        when(dao.getLastRun()).thenReturn(ts)
        when(dao.getArticleSummary(ts)).thenReturn(articleSummaries)
        when(dao.getExistingCandidateSummary()).thenReturn(candidateSummaries)

        analyser.analyse()

        verify(dao).insertCandidateDetails(insertCandidateDetailsCaptor.capture())
        verify(dao).insertCandidateSummary(insertCandidateSummaryCaptor.capture())
        verify(dao).updateCandidateSummaryCount(updateCandidateSummaryCaptor.capture())

        assert insertCandidateDetailsCaptor.allValues.flatten().size() == 5
        assert insertCandidateSummaryCaptor.allValues.flatten().size() == 3
        assert updateCandidateSummaryCaptor.allValues.flatten().size() == 1

        insertCandidateSummaryCaptor.allValues.flatten().each { CandidateSummary it ->
            if (it.candidate == "trump") {
                assert it.count == 2
            } else if (it.candidate == "sanders" ){
                assert it.count == 1
                assert "${it.source}|${it.newsDate}".toString() in [ "xyz|2016-01-01", "bbc|2016-01-02" ]
            }
            else {
                fail("Unexpected value $it")
            }
        }

        updateCandidateSummaryCaptor.allValues.flatten().each { CandidateSummary it ->
            if (it.candidate == "sanders") {
                assert it.count == 11
                assert it.source == "bbc"
                assert it.newsDate == java.sql.Date.valueOf("2016-01-01")
            } else {
                fail("Unexpected value $it")
            }
        }
    }
}