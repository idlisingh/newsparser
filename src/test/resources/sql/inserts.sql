insert into article_summary(id, headlines, news_date, source, article_link, description, created_ts) values(1, 'headlines1', '2016-01-02', 'source1', 'http://link1', 'description1', '2016-01-02 00:00:00');
insert into article_summary(id, headlines, news_date, source, article_link, description, created_ts) values(2, 'headlines2', '2016-01-03', 'source2', 'http://link2', 'description2', '2016-01-03 00:00:00');
insert into article_summary(id, headlines, news_date, source, article_link, description, created_ts) values(3, 'headlines3', '2016-01-04', 'source3', 'http://link3', 'description3', '2016-01-04 00:00:00');
insert into article_summary(id, headlines, news_date, source, article_link, description, created_ts) values(4, 'headlines4', '2016-01-05', 'source4', 'http://link4', 'description4', '2016-01-05 00:00:00');

insert into candidate_summary(news_date, candidate, source, count, created_ts, updated_ts) values ('2016-01-02', 'sanders', 'nbc', 10, '2016-01-02 00:00:00', '2016-01-02 12:00:00');
insert into candidate_summary(news_date, candidate, source, count, created_ts, updated_ts) values ('2016-01-02', 'sanders', 'bbc', 20, '2016-01-02 00:00:00', '2016-01-02 12:00:00');
insert into candidate_summary(news_date, candidate, source, count, created_ts, updated_ts) values ('2016-01-02', 'sanders', 'abc', 30, '2016-01-02 00:00:00', '2016-01-02 12:00:00');
insert into candidate_summary(news_date, candidate, source, count, created_ts, updated_ts) values ('2016-01-02', 'trump', 'nbc', 50, '2016-01-02 00:00:00', '2016-01-02 12:00:00');
insert into candidate_summary(news_date, candidate, source, count, created_ts, updated_ts) values ('2016-01-02', 'trump', 'bbc', 60, '2016-01-02 00:00:00', '2016-01-02 12:00:00');
insert into candidate_summary(news_date, candidate, source, count, created_ts, updated_ts) values ('2016-01-02', 'trump', 'fox', 70, '2016-01-02 00:00:00', '2016-01-02 12:00:00');

insert into candidate_summary(news_date, candidate, source, count, created_ts, updated_ts) values ('2016-01-03', 'sanders', 'nbc', 11, '2016-01-03 00:00:00', '2016-01-03 12:00:00');
insert into candidate_summary(news_date, candidate, source, count, created_ts, updated_ts) values ('2016-01-03', 'sanders', 'bbc', 22, '2016-01-03 00:00:00', '2016-01-03 12:00:00');
insert into candidate_summary(news_date, candidate, source, count, created_ts, updated_ts) values ('2016-01-03', 'sanders', 'abc', 33, '2016-01-03 00:00:00', '2016-01-03 12:00:00');
insert into candidate_summary(news_date, candidate, source, count, created_ts, updated_ts) values ('2016-01-03', 'trump', 'nbc', 50, '2016-01-03 00:00:00', '2016-01-03 12:00:00');
insert into candidate_summary(news_date, candidate, source, count, created_ts, updated_ts) values ('2016-01-03', 'trump', 'bbc', 60, '2016-01-03 00:00:00', '2016-01-03 12:00:00');
insert into candidate_summary(news_date, candidate, source, count, created_ts, updated_ts) values ('2016-01-03', 'trump', 'fox', 70, '2016-01-03 00:00:00', '2016-01-03 12:00:00');
insert into candidate_summary(news_date, candidate, source, count, created_ts, updated_ts) values ('2016-01-03', 'clinton', 'nbc', 55, '2016-01-03 00:00:00', '2016-01-03 12:00:00');
insert into candidate_summary(news_date, candidate, source, count, created_ts, updated_ts) values ('2016-01-03', 'clinton', 'bbc', 65, '2016-01-03 00:00:00', '2016-01-03 12:00:00');
insert into candidate_summary(news_date, candidate, source, count, created_ts, updated_ts) values ('2016-01-03', 'clinton', 'fox', 75, '2016-01-03 00:00:00', '2016-01-03 12:00:00');

insert into candidate_details(id, candidate, article_id, created_ts) values (1, 'sanders', 1, '2016-01-01 00:00:00');
insert into candidate_details(id, candidate, article_id, created_ts) values (2, 'sanders', 2, '2016-01-02 00:00:00');
insert into candidate_details(id, candidate, article_id, created_ts) values (3, 'sanders', 3, '2016-01-03 00:00:00');
insert into candidate_details(id, candidate, article_id, created_ts) values (4, 'sanders', 4, '2016-01-04 00:00:00');
insert into candidate_details(id, candidate, article_id, created_ts) values (5, 'sanders', 5, '2016-01-05 00:00:00');
