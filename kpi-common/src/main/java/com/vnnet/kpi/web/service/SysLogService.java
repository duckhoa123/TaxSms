package com.vnnet.kpi.web.service;

import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.model.SysLog;

import java.util.List;

public interface SysLogService {
    int save(SysLog record);

    int delete(SysLog record);

    int delete(List<SysLog> records);

    SysLog findById(Long id);

    PageResult findPage(PageRequest pageRequest);

    PageResult findPageC2C(PageRequest pageRequest, String userName);

    PageResult findPageKPI(PageRequest pageRequest, String userName);
}
