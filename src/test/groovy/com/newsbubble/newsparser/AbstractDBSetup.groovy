package com.newsbubble.newsparser

import groovy.sql.Sql
import org.junit.After
import org.junit.Before

abstract class AbstractDBSetup {

    def Sql sql

    @Before def void before() {
        def url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
        def userId = "sa"
        def password =""
        def driver = "org.h2.Driver"
        System.setProperty("db.url", url)
        System.setProperty("db.user", userId)
        System.setProperty("db.password", password)
        System.setProperty("db.driver", driver)

        sql = Sql.newInstance(url, userId, password, driver)

        createTables()
    }

    @After def void after() {
        sql.execute("DROP ALL OBJECTS")
    }

    def void createTables() {
        ["tables/article_summary.table", "sql/inserts.sql"].each {
            def table = this.getClass().getClassLoader().getResourceAsStream(it).text
            table = table.replace("\n", " ")
            table = table.replace("\r", " ")
            sql.execute(table)
        }
    }
}