package com.vnnet.kpi.web.service;


import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.model.SysProgram;
import com.vnnet.kpi.web.model.SysSmsTemplate;

import java.util.List;

public interface SysSmsTemplateService {
    PageResult findPage(PageRequest pageRequest, String userName);

    int add(SysSmsTemplate sysSmsTemplate, String userName);

    int update(SysSmsTemplate sysSmsTemplate, String userName);

    int deleteBatch(SysSmsTemplate sysSmsTemplate, String userName);

    List<SysSmsTemplate> findByProgramId(Long id);
}
