package com.vnnet.kpi.web.service;


import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.model.SysConfig;
import com.vnnet.kpi.web.model.SysProgram;

import java.util.List;

public interface SysProgramService {
    List<SysProgram> findByGroup(SysProgram sysProgram, String userName);

    int update(SysProgram sysProgram, String userName);

    int deleteBatch(long programId, String userName);
}
