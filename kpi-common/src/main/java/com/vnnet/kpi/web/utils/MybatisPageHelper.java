package com.vnnet.kpi.web.utils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vnnet.kpi.web.bean.ColumnFilter;
import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;

import java.util.List;
import java.util.Map;


public class MybatisPageHelper {

    public static final String findPage = "findPage";

    public static PageResult findPage(PageRequest pageRequest, Object mapper) {
        return findPage(pageRequest, mapper, findPage);
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    public static PageResult findPage(PageRequest pageRequest, Object mapper, String queryMethodName, Object... args) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 0) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Object result = ReflectionUtils.invoke(mapper, queryMethodName, args);
        return getPageResult(pageRequest, new PageInfo((List) result));
    }

    public static PageResult getPageResult(PageRequest pageRequest, PageInfo<?> pageInfo) {
        PageResult pageResult = new PageResult();
        pageResult.setPageNum(pageInfo.getPageNum());
        pageResult.setPageSize(pageInfo.getPageSize());
        pageResult.setTotalSize(pageInfo.getTotal());
        pageResult.setTotalPages(pageInfo.getPages());
        pageResult.setContent(pageInfo.getList());
        return pageResult;
    }

    public static String getColumnFilterValue(PageRequest pageRequest, String filterName) {
        String value = null;
        ColumnFilter columnFilter = pageRequest.getColumnFilter(filterName);
        if (columnFilter != null && columnFilter.getValue() != null && !columnFilter.getValue().isEmpty()) {
            value = columnFilter.getValue();
        }

        return value;
    }

    public static void addColumnFilterValue(PageRequest pageRequest, String filterName, String value) {
        Map<String, ColumnFilter> columnFilters = pageRequest.getColumnFilters();

        ColumnFilter columnFilter = new ColumnFilter();
        columnFilter.setName(filterName);
        columnFilter.setValue(value);
        columnFilters.put(filterName, columnFilter);
    }


}
