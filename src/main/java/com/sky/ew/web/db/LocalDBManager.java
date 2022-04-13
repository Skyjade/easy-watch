package com.sky.ew.web.db;

import com.sky.ew.web.entity.CrawlerMovieInfoDto;

/**
 * 本地数据库管理者
 *
 * @author wangtianqi
 * @since 2022/3/18 16:39
 */
public interface LocalDBManager {


    /**
     * 爬虫到本地数据库
     *
     * @param crawlerMovieInfoDto 爬虫信息
     */
    void crawlToLocalDB(CrawlerMovieInfoDto crawlerMovieInfoDto);


}
