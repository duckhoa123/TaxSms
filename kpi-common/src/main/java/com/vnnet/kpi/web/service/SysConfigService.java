package com.vnnet.kpi.web.service;


import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.model.SysConfig;

import java.util.List;

public interface SysConfigService {
    int save(SysConfig record);

    int delete(SysConfig record);

    int delete(List<SysConfig> records);

    SysConfig findById(Long id);

    SysConfig findByKey(String key);

    PageResult selectPage(PageRequest pageRequest);

    List<SysConfig> selectAll();

}
