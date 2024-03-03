package com.vnnet.kpi.web.service;

import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;

public interface SysPdfReadHisService {
    int save(Long messHdId, String calledNumber, String fileName);

}
