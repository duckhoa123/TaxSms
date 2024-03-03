package com.vnnet.kpi.web.service;

import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.model.SysLog;
import com.vnnet.kpi.web.model.SysLogInfo;

import java.util.List;

public interface SysLogInfoService {
    int save(String operation, String userName);

    PageResult findPage(PageRequest pageRequest, String userName);
}
