package com.newsbubble.newsparser

import com.sun.tools.corba.se.idl.constExpr.Times
import org.junit.Before
import org.junit.Test

import java.sql.Timestamp


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
}