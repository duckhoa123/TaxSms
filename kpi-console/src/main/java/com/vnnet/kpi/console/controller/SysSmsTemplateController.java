package com.vnnet.kpi.console.controller;

import com.vnnet.kpi.console.utils.MySecurityUtils;
import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.model.SysProgram;
import com.vnnet.kpi.web.model.SysSmsTemplate;
import com.vnnet.kpi.web.service.SysProgramService;
import com.vnnet.kpi.web.service.SysSmsTemplateService;
import com.vnnet.kpi.web.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("smstemp")
public class SysSmsTemplateController {

    private static final Logger logger = LoggerFactory.getLogger(SysSmsTemplateController.class);

    @Autowired
    private SysSmsTemplateService sysSmsTemplateService;

    @PreAuthorize("hasAuthority('sys:smstemp:view')")
    @PostMapping(value = "/findPage")
    public HttpResult findPage(@RequestBody PageRequest pageRequest) {
        String userName = MySecurityUtils.getUsername();
        return HttpResult.ok(sysSmsTemplateService.findPage(pageRequest, userName));
    }

    @PreAuthorize("hasAuthority('sys:smstemp:add')")
    @PostMapping(value = "/add")
    public HttpResult add(@RequestBody SysSmsTemplate sysSmsTemplate) {
        String userName = MySecurityUtils.getUsername();
        int result = sysSmsTemplateService.add(sysSmsTemplate, userName);
        if (result == 1)
            return HttpResult.ok("New message template added successfully");
        else if (result == 2)
            return HttpResult.error("Message template already exists");
        else
            return HttpResult.error("Failed to update message template. Please try again");
    }

    @PreAuthorize("hasAuthority('sys:smstemp:edit')")
    @PostMapping(value = "/update")
    public HttpResult update(@RequestBody SysSmsTemplate sysSmsTemplate) {
        String userName = MySecurityUtils.getUsername();
        int result = sysSmsTemplateService.update(sysSmsTemplate, userName);
        if (result == 1)
            return HttpResult.ok("Message template updated successfully");
        else if (result == 2)
            return HttpResult.error("Message template already exists");
        else
            return HttpResult.error("Failed to update message template. Please try again ");
    }

    @PreAuthorize("hasAuthority('sys:smstemp:delete')")
    @PostMapping(value = "/deleteBatch")
    public HttpResult deleteBatch(@RequestBody SysSmsTemplate sysSmsTemplate) {
        String userName = MySecurityUtils.getUsername();
        int result = sysSmsTemplateService.deleteBatch(sysSmsTemplate, userName);
        if (result >= 1)
            return HttpResult.ok("Message template deleted successfully");
        else
            return HttpResult.error("Failed to delete message template. Please try again");
    }

    @PreAuthorize("hasAuthority('sys:smstemp:view')")
    @PostMapping(value = "/findByProgramId")
    public HttpResult findByProgramId(@RequestBody Long programId) {
        return HttpResult.ok(sysSmsTemplateService.findByProgramId(programId));
    }
}
