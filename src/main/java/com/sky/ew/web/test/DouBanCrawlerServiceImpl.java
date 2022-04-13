package com.sky.ew.web.test;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.sky.ew.web.entity.CrawlerMovieInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 豆瓣爬虫处理逻辑
 *
 * @author wangtianqi
 * @since 2022/3/18 15:20
 */
@Slf4j
public class DouBanCrawlerServiceImpl {
    /**
     * 豆瓣搜索地址
     */
    private static final String SEARCH_URL = "https://www.douban.com/search?cat=1002&q=%s";
    /**
     * 请求用户代理
     */
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36";

    /**
     * 通过影片名称，检索影片信息
     *
     * @param clipName
     * @return
     */
    public List<CrawlerMovieInfoDto> getInfoListByClipName(String clipName) {
        clipName = dealClipName(clipName);
        List<CrawlerMovieInfoDto> resultList = new ArrayList<>();
        if (StringUtils.isEmpty(clipName)) {
            return null;
        }

        String searchUrl = String.format(SEARCH_URL, clipName);

        //获取页面对象
        Document document = getDocument(searchUrl);

        //找到结果列表
        Elements aEls = document.select("div.result-list").select("div.title").select("a");
        for (Element el : aEls) {
            String href = el.attr("href");
            CrawlerMovieInfoDto sigleVedioInfo = getSigleVedioInfo(href);
            if (sigleVedioInfo != null) {
                resultList.add(sigleVedioInfo);
            }
        }

        return resultList;
    }

    /**
     * 单个影片查询
     *
     * @param url
     */
    public CrawlerMovieInfoDto getSigleVedioInfo(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }

        Document doc = getDocument(url);
        url = doc.baseUri();
        CrawlerMovieInfoDto movieInfo = new CrawlerMovieInfoDto();
        movieInfo.setPageUrl(url);

        //从地址中提取豆瓣id
        String idPatten = "([1-9]\\d*)";
        String moveId = ReUtil.get(idPatten, url, 0);
        if (StringUtils.isNotEmpty(moveId)) {
            movieInfo.setMoveId(moveId);
        }

        Elements subject = doc.select("div#info");

        //导演
        Elements directs = subject.select("a[rel=\"v:directedBy\"]");
        String directName = directs.stream().map(o -> o.html().trim()).collect(Collectors.joining("|"));
        if (StringUtils.isNotEmpty(directName)) {
            movieInfo.setDirectorName(String.format("|%s|", directName.trim()));
        }

        Elements plElement = subject.select("span[class=\"pl\"]");
        //编剧
        Optional<Element> adaptorsOpt = plElement.stream().filter(o -> o.html().equals("编剧")).findFirst();
        if (adaptorsOpt.isPresent()) {
            Element adaptorEl = adaptorsOpt.get();
            Elements adaptors = adaptorEl.nextElementSibling().select("a");
            String adaptorName = adaptors.stream().map(o -> o.html().trim()).collect(Collectors.joining("|"));
            if (StringUtils.isNotEmpty(adaptorName)) {
                movieInfo.setAdaptorName(String.format("|%s|", adaptorName.trim()));
            }
        }

        //主演
        Optional<Element> leadOpt = plElement.stream().filter(o -> o.html().equals("主演")).findFirst();
        if (leadOpt.isPresent()) {
            Element leaderEl = leadOpt.get();
            Elements leaders = leaderEl.nextElementSibling().select("a[rel=\"v:starring\"]");
            String leaderName = leaders.stream().map(o -> o.html().trim()).collect(Collectors.joining("|"));
            if (StringUtils.isNotEmpty(leaderName)) {
                movieInfo.setLeaderName(String.format("|%s|", leaderName.trim()));
            }
        }

        //类型
        Elements kinds = subject.select("span[property=\"v:genre\"]");
        String kindName = kinds.stream().map(o -> o.html().trim()).collect(Collectors.joining("|"));
        if (StringUtils.isNotEmpty(kindName)) {
            movieInfo.setKindName(String.format("|%s|", kindName.trim()));
        }

        //制片国家/地区
        Optional<Element> areaOpt = plElement.stream().filter(o -> o.html().equals("制片国家/地区:")).findFirst();
        if (areaOpt.isPresent()) {
            Element areaEl = areaOpt.get();
            String areaName = areaEl.nextSibling().outerHtml();
            if (StringUtils.isNotEmpty(areaName)) {
                movieInfo.setAreaCharName(String.format("|%s|", areaName.trim()));
            }
        }

        //语言
        Optional<Element> languageOpt = plElement.stream().filter(o -> o.html().equals("语言:")).findFirst();
        if (languageOpt.isPresent()) {
            Element languageEl = languageOpt.get();
            String languageName = languageEl.nextSibling().outerHtml();
            if (StringUtils.isNotEmpty(languageName)) {
                movieInfo.setLanguageName(String.format("|%s|", languageName.trim()));
            }
        }

        //首播时间
        Elements releaseTimeEl = subject.select("span[property=\"v:initialReleaseDate\"]");
        String releaseStr = releaseTimeEl.html();
        if (StringUtils.isNotEmpty(releaseStr)) {
            //提取 年月日
            String patten = "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))";
            String s = ReUtil.get(patten, releaseStr, 0);
            if (StringUtils.isNotEmpty(s)) {
                try {
                    movieInfo.setReleaseTime(DatePattern.NORM_DATE_FORMAT.parse(s));
                    //年份
                    movieInfo.setYearName(DateUtil.year(movieInfo.getReleaseTime()) + "");
                } catch (ParseException e) {
                    log.error("豆瓣爬虫格式化日期报错", e);
                }
            }
        }

        //集数 totalNumber
        Optional<Element> totalNumberOpt = plElement.stream().filter(o -> o.html().equals("集数:")).findFirst();
        if (totalNumberOpt.isPresent()) {
            Element totalNumberEl = totalNumberOpt.get();
            String totalNumber = totalNumberEl.nextSibling().outerHtml();
            if (StringUtils.isNotEmpty(totalNumber)) {
                try {
                    movieInfo.setTotalNumber(Integer.parseInt(totalNumber.trim()));
                } catch (NumberFormatException e) {
                    movieInfo.setTotalNumber(0);
                }
            }
        }

        //别名
        Optional<Element> otherNameOpt = plElement.stream().filter(o -> o.html().equals("又名:")).findFirst();
        if (otherNameOpt.isPresent()) {
            Element otherNameEl = otherNameOpt.get();
            String otherName = otherNameEl.nextSibling().outerHtml();
            if (StringUtils.isNotEmpty(otherName)) {
                movieInfo.setOtherName(otherName.trim());
            }
        }

        //豆瓣评分
        Elements scores = doc.select("div#interest_sectl").select("strong[property=\"v:average\"]");
        String scoreStr = scores.html();
        if (StringUtils.isNotEmpty(scoreStr)) {
            movieInfo.setScores(new BigDecimal(scoreStr));
        }

        //简介
        Elements storyEl = doc.select("span[property=\"v:summary\"]");
        String story = storyEl.html();
        if (StringUtils.isNotEmpty(story)) {
            movieInfo.setStory(story.trim());
        }
        return movieInfo;
    }

    /**
     * 获得页面对象包装
     *
     * @param url
     * @return
     * @throws IOException
     */
    private static Document getDocument(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(5000)
                    .get();
        } catch (IOException e) {
            throw new RuntimeException("爬取失败,url=" + url);
        }
        return doc;
    }

    /**
     * 处理片名
     *
     * @param clipName
     * @return
     */
    private static String dealClipName(String clipName) {
        if (StringUtils.isEmpty(clipName)) {
            return clipName;
        }

        String repStr = "1$|湖南卫视版$|卫视版$|预告片$|片花花絮$|TV版$|片花$|未删减版$|收录版$|国语版$|英语版$|典藏版$" +
                "|版权版$|动画版$|电影版$|动漫版$|纯享版$|终极版$|高清版$|剧场版$|精华版$|日文版$|3D$|3D版$" +
                "|[0-9]{1,4}版$|20[0-9-]{1,9}$|\\(.*?\\)$|（.*?\\)$|\\(.*?）$|（.*?）$";
        clipName = clipName.replaceAll(repStr, "").replace("-", "").trim();
        clipName = clipName.replace(":", "：")
                .replace("第1季", "第一季")
                .replace("第2季", "第二季")
                .replace("第3季", "第三季")
                .replace("第4季", "第四季")
                .replace("第5季", "第五季")
                .replace("第6季", "第六季")
                .replace("第7季", "第七季")
                .replace("第8季", "第八季")
                .replace("第9季", "第九季");

        return clipName.trim();
    }

    @Test
    public void test() {
//        String url = "https://movie.douban.com/subject/25853071/";
//        List<CrawlerMovieInfoDto> list = getInfoListByClipName("庆余年");
//        System.out.println(JSON.toJSONString(list));
//        List<CrawlerMovieInfoDto> list2 = getInfoListByClipName("新蝙蝠侠");
//        System.out.println(JSON.toJSONString(list2));
        //白夜追凶
        List<CrawlerMovieInfoDto> list3 = getInfoListByClipName("奇异博士");
        System.out.println(JSON.toJSONString(list3.get(0)));

    }
}
