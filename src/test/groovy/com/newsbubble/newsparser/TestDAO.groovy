package com.newsbubble.newsparser

import org.junit.Before
import org.junit.Test


class TestDAO {

    def DAO dao

    @Before def void before() {
        System.setProperty("db.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
        System.setProperty("db.user", "sa")
        System.setProperty("db.password", "")
        System.setProperty("db.driver", "org.h2.Driver")

        dao = new DAO()
    }

    @Test def void "test empty"() {
        assert 1 == 1
    }
}