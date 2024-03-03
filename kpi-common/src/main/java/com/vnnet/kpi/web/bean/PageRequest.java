package com.vnnet.kpi.web.bean;

import java.util.HashMap;
import java.util.Map;

public class PageRequest {

    private int pageNum = 1;

    private int pageSize = 10;

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private Map<String, ColumnFilter> columnFilters = new HashMap<String, ColumnFilter>();

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Map<String, ColumnFilter> getColumnFilters() {
        return columnFilters;
    }

    public void setColumnFilters(Map<String, ColumnFilter> columnFilters) {
        this.columnFilters = columnFilters;
    }

    public ColumnFilter getColumnFilter(String name) {
        return columnFilters.get(name);
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "PageRequest{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", userName='" + userName + '\'' +
                ", columnFilters=" + columnFilters +
                ", title='" + title + '\'' +
                '}';
    }
}
