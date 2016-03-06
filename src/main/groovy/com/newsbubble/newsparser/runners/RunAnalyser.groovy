package com.newsbubble.newsparser.runners

import com.newsbubble.newsparser.Analyser
import com.newsbubble.newsparser.DAO

def analyser = new Analyser()

def dao = new DAO()
dao.sql.execute("truncate table last_run")

analyser.dao = dao

analyser.analyse()