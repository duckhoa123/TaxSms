package com.vnnet.kpi.web.model;

import java.util.Date;

public class BaseMessagDt extends BaseModel {
    private String title;
    private Date schedulerDateTime;
    private String createBy;
    private Date createTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getSchedulerDateTime() {
        return schedulerDateTime;
    }

    public void setSchedulerDateTime(Date schedulerDateTime) {
        this.schedulerDateTime = schedulerDateTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
