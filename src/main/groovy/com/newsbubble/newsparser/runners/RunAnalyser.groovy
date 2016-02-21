package com.newsbubble.newsparser.runners

import com.newsbubble.newsparser.Analyser
import com.newsbubble.newsparser.DAO

def analyser = new Analyser()

analyser.dao = new DAO()

analyser.analyse()