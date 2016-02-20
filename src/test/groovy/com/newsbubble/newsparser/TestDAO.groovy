package com.newsbubble.newsparser

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
        sql.executeInsert("insert into article_summary(headlines, news_date, source, article_link, description, created_ts) values('headlines1', '2016-01-01', 'source1', 'http://link1', 'description1', ${new Timestamp(1)})")
        sql.executeInsert("insert into article_summary(headlines, news_date, source, article_link, description, created_ts) values('headlines2', '2016-01-02', 'source2', 'http://link2', 'description2', ${new Timestamp(2)})")
        sql.executeInsert("insert into article_summary(headlines, news_date, source, article_link, description, created_ts) values('headlines3', '2016-01-03', 'source3', 'http://link3', 'description3', ${new Timestamp(3)})")
        sql.executeInsert("insert into article_summary(headlines, news_date, source, article_link, description, created_ts) values('headlines4', '2016-01-04', 'source4', 'http://link4', 'description4', ${new Timestamp(4)})")

        assert dao.getArticleSummary(new Timestamp(0)).size() == 4

        def values = dao.getArticleSummary(new Timestamp(3))
        assert 1 == values.size()
        assert "headlines4" == values[0].headlines
        assert java.sql.Date.valueOf("2016-01-04") == values[0].newsDate
        assert "source4" == values[0].source
        assert "http://link4" == values[0].link
        assert "description4" == values[0].description
        assert new Timestamp(4) == values[0].createdTs
    }
}