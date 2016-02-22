package com.newsbubble.newsparser.service

import com.newsbubble.newsparser.DAO
import com.newsbubble.newsparser.service.resources.NewsResource
import com.yammer.dropwizard.Service
import com.yammer.dropwizard.config.Bootstrap
import com.yammer.dropwizard.config.Environment

class NewsService extends Service<NewsConfiguration>{

    public static void main(String[] args) {
        new NewsService().run(args)
    }

    @Override
    void initialize(Bootstrap<NewsConfiguration> bootstrap) {

    }

    @Override
    void run(NewsConfiguration newsConfiguration, Environment environment) throws Exception {
        def resource = new NewsResource()
        resource.dao = new DAO()
        environment.addResource(resource)
    }
}