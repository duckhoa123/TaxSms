package com.vnnet.kpi.console.controller;

import com.vnnet.kpi.console.utils.MySecurityUtils;
import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.model.SysConfig;
import com.vnnet.kpi.web.model.SysProgram;
import com.vnnet.kpi.web.service.SysConfigService;
import com.vnnet.kpi.web.service.SysProgramService;
import com.vnnet.kpi.web.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/program")
public class SysProgramController {

    private static final Logger logger = LoggerFactory.getLogger(SysProgramController.class);

    @Autowired
    private SysProgramService sysProgramService;

    @PostMapping(value = "/findByGroup")
    public HttpResult findByGroup(@RequestBody SysProgram sysProgram) {
        String userName = MySecurityUtils.getUsername();
        if (StringUtils.isBlank(userName))
            return HttpResult.error(403, "access denied");
        return HttpResult.ok(sysProgramService.findByGroup(sysProgram, userName));
    }

    @PreAuthorize("hasAuthority('sys:program:edit')")
    @PostMapping(value = "/update")
    public HttpResult update(@RequestBody SysProgram sysProgram) {
        String userName = MySecurityUtils.getUsername();
        int result = sysProgramService.update(sysProgram, userName);
        if (result == 1)
            return HttpResult.ok("Program update successful");
        else if (result == 2)
            return HttpResult.error("Program name already exists");
        else
            return HttpResult.error("Failed to update program. Please try again ");
    }

    @PreAuthorize("hasAuthority('sys:program:delete')")
    @PostMapping(value = "/deleteBatch")
    public HttpResult deleteBatch(@RequestBody long id) {
        String userName = MySecurityUtils.getUsername();
        int result = sysProgramService.deleteBatch(id, userName);
        if (result >= 1)
            return HttpResult.ok("Program deleted successfully");
        else
            return HttpResult.error("Failed to delete program. Please try again");
    }

}
