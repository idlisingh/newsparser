package com.newsbubble.newsparser

import com.newsbubble.newsparser.domain.ArticleSummary
import com.newsbubble.newsparser.domain.CandidateDetails
import com.newsbubble.newsparser.domain.CandidateSummary
import org.junit.Before
import org.junit.Test

import java.sql.Timestamp

import static org.junit.Assert.fail


class TestDAO extends AbstractDBSetup {

    def DAO dao

    @Before def void before() {
        super.before()
        dao = new DAO()
    }

    @Test def void "test getArticleSummary"() {
        assert dao.getArticleSummary(Timestamp.valueOf("2016-01-01 00:00:00")).size() == 4

        def values = dao.getArticleSummary(Timestamp.valueOf("2016-01-04 00:00:00"))
        assert 1 == values.size()
        assert 4 == values[0].id
        assert "headlines4" == values[0].headlines
        assert java.sql.Date.valueOf("2016-01-05") == values[0].newsDate
        assert "source4" == values[0].source
        assert "http://link4" == values[0].link
        assert "description4" == values[0].description
        assert Timestamp.valueOf("2016-01-05 00:00:00") == values[0].createdTs
    }

    @Test def void "test getDistinctArticleHeadlines"() {
        def headlines = dao.getDistinctArticleHeadlines()

        assert headlines.size() == 4

        assert headlines.sort() == [ "headlines1", "headlines2", "headlines3", "headlines4" ]
    }

    @Test def void "test getExistingCandidateSummary"() {
        def candidateSummaries = dao.getExistingCandidateSummary()

        assert 15 == candidateSummaries.size()
        def single = candidateSummaries.findAll { it.candidate == 'sanders' && it.source == 'nbc' && it.newsDate == java.sql.Date.valueOf("2016-01-02") }

        assert single.size() == 1

        assert single[0].count == 10
        assert single[0].createdTs == Timestamp.valueOf("2016-01-02 00:00:00")
        assert single[0].updatedTs == Timestamp.valueOf("2016-01-02 12:00:00")
    }

    @Test def void "test insertCandidateSummary"() {
        def currentTime = new Timestamp(System.currentTimeMillis() - 1000)
        truncateAllTables()
        def values = [
                new CandidateSummary(newsDate: java.sql.Date.valueOf("2016-02-01"), candidate: 'candidate1', source: 'source1', count: 1),
                new CandidateSummary(newsDate: java.sql.Date.valueOf("2016-02-02"), candidate: 'candidate2', source: 'source2', count: 2),
                new CandidateSummary(newsDate: java.sql.Date.valueOf("2016-02-03"), candidate: 'candidate3', source: 'source3', count: 3)
        ]
        dao.insertCandidateSummary(values)

        def results = dao.getExistingCandidateSummary()
        assert results.size() == 3

        results.each {
            if (it.newsDate == java.sql.Date.valueOf("2016-02-01")) {
                assert it.candidate == "candidate1"
                assert it.source == "source1"
                assert it.count == 1
            } else if (it.newsDate == java.sql.Date.valueOf("2016-02-02")) {
                assert it.candidate == "candidate2"
                assert it.source == "source2"
                assert it.count == 2
            } else if (it.newsDate == java.sql.Date.valueOf("2016-02-03")) {
                assert it.candidate == "candidate3"
                assert it.source == "source3"
                assert it.count == 3
            } else {
                fail("Unexpected value $it")
            }
            assert it.createdTs > currentTime
            assert it.updatedTs > currentTime
            assert it.createdTs == it.updatedTs
        }
    }

    @Test def void "test updateCandidateSummaryCount"() {
        truncateAllTables()
        def values = [
                new CandidateSummary(newsDate: java.sql.Date.valueOf("2016-02-01"), candidate: 'candidate1', source: 'source1', count: 1),
                new CandidateSummary(newsDate: java.sql.Date.valueOf("2016-02-02"), candidate: 'candidate2', source: 'source2', count: 2),
                new CandidateSummary(newsDate: java.sql.Date.valueOf("2016-02-03"), candidate: 'candidate3', source: 'source3', count: 3)
        ]
        dao.insertCandidateSummary(values)

        values.each {
            it.count = it.count * 10
        }

        dao.updateCandidateSummaryCount(values)

        def results = dao.getExistingCandidateSummary()

        results.collect{ it.count }.sort() == [10, 20, 30]
    }

    @Test def void "test getLastRun"() {
        assert dao.getLastRun() == Timestamp.valueOf("2016-01-01 00:00:00")
    }

    @Test def void "test insertLastRun"() {
        truncateAllTables()
        def timestamp = new Timestamp(13)
        dao.insertLastRun(timestamp)
        assert dao.getLastRun() == timestamp
    }

    @Test def void "test updateLastRun"() {
        def timestamp = new Timestamp(13)
        dao.updateLastRun(timestamp)
        assert dao.getLastRun() == timestamp
    }

    @Test def void "test truncateCandidateSummary"() {
        assert dao.getExistingCandidateSummary().size() > 0
        dao.truncateCandidateSummary()
        assert dao.getExistingCandidateSummary().size() == 0
    }

    @Test def void "test truncateCandidateDetails"() {
        assert dao.getExistingCandidateDetails().size() > 0
        dao.truncateCandidateDetails()
        assert dao.getExistingCandidateDetails().size() == 0
    }


    @Test def void "test getExistingCandidateDetails"() {
        def results = dao.getExistingCandidateDetails()

        assert results.size() == 5
        results.each {
            assert it.id in [1, 2, 3, 4, 5]
            assert it.id * 10 == it.articleId
            assert it.createdTs == Timestamp.valueOf("2016-01-0${it.id} 00:00:00")
            assert it.candidate == "sanders"
        }
    }

    @Test def void "test insertCandidateDetails"() {
        truncateAllTables()

        def values = [
                new CandidateDetails(candidate: "candidate 1", articleId: 200),
                new CandidateDetails(candidate: "candidate 2", articleId: 400),
                new CandidateDetails(candidate: "candidate 3", articleId: 600),
        ]

        dao.insertCandidateDetails(values)

        def results = dao.getExistingCandidateDetails()

        assert results.size() == 3

        results.each {
            assert it.candidate in ["candidate 1", "candidate 2", "candidate 3"]
            assert it.articleId in [200, 400, 600]
        }
    }

    @Test def void "test insertArticleSummary"() {
        truncateAllTables()

        def currentTime = new Timestamp(System.currentTimeMillis() - 1000)
        def values = (1..5).collect {
            new ArticleSummary(headlines: "headlines ${it}", source: "source ${it}", link: "link ${it}", newsDate: java.sql.Date.valueOf("2016-01-0${it}"))
        }

        dao.insertArticleSummary(values)

        def results = dao.getArticleSummary(currentTime)

        assert results.size() == 5

        results.sort{ it.createdTs }.eachWithIndex { ArticleSummary entry, int i ->
            i++
            assert entry.headlines == "headlines ${i}"
            assert entry.source == "source ${i}"
            assert entry.link == "link ${i}"
            assert entry.newsDate == java.sql.Date.valueOf("2016-01-0${i}")
        }
    }

    @Test def void "test getArticleSummaryTotalCount"() {
        assert dao.getArticleSummaryTotalCount() == 4
    }
}