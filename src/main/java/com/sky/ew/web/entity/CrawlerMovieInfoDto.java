package com.sky.ew.web.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 爬虫信息
 *
 * @author wangtianqi
 * @since 2022/3/18 15:28
 */

public class CrawlerMovieInfoDto {
    private String pageUrl;
    private String moveId;
    private String directorName;
    private String adaptorName;
    private String leaderName;
    private String kindName;
    private String areaCharName;
    private String languageName;
    private Date releaseTime;
    private String yearName;
    private int totalNumber;
    private String otherName;
    private BigDecimal scores;
    private String story;

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setMoveId(String moveId) {


        this.moveId = moveId;
    }

    public String getMoveId() {
        return moveId;
    }

    public void setDirectorName(String directorName) {


        this.directorName = directorName;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setAdaptorName(String adaptorName) {


        this.adaptorName = adaptorName;
    }

    public String getAdaptorName() {
        return adaptorName;
    }

    public void setLeaderName(String leaderName) {


        this.leaderName = leaderName;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setKindName(String kindName) {


        this.kindName = kindName;
    }

    public String getKindName() {
        return kindName;
    }

    public void setAreaCharName(String areaCharName) {


        this.areaCharName = areaCharName;
    }

    public String getAreaCharName() {
        return areaCharName;
    }

    public void setLanguageName(String languageName) {


        this.languageName = languageName;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setReleaseTime(Date releaseTime) {

        this.releaseTime = releaseTime;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setYearName(String yearName) {

        this.yearName = yearName;
    }

    public String getYearName() {
        return yearName;
    }

    public void setTotalNumber(int totalNumber) {

        this.totalNumber = totalNumber;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setOtherName(String otherName) {

        this.otherName = otherName;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setScores(BigDecimal scores) {

        this.scores = scores;
    }

    public BigDecimal getScores() {
        return scores;
    }

    public void setStory(String story) {

        this.story = story;
    }

    public String getStory() {
        return story;
    }
}
