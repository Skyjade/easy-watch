package com.sky.ew.web.crawler;

import com.sky.ew.web.entity.CrawlerMovieInfoDto;
import com.sky.ew.web.entity.CrawlerOrigin;

import java.util.concurrent.TimeUnit;


/**
 * EasyWatch爬虫接口
 *
 * @author wangtianqi
 * @since 2022/3/18 16:39
 */
public interface EasyWatchCrawler {

    /**
     * @param origin 数据源
     * @return 影片信息
     */
    CrawlerMovieInfoDto crawl(CrawlerOrigin origin);

    /**
     * 自动切换数据源
     *
     * @param count 超时数量
     * @param unit  超时单位
     * @return 源
     */
    CrawlerOrigin autoSwitchOrigin(Integer count, TimeUnit unit);


}
