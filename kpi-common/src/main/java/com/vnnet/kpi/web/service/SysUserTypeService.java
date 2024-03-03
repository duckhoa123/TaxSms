package com.vnnet.kpi.web.service;

import com.vnnet.kpi.web.model.SysUserType;

import java.util.List;

public interface SysUserTypeService {

    List<SysUserType> findByUser(String userName);
    List<SysUserType> getListShowReport(String userName);
}
