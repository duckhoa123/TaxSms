package com.vnnet.kpi.console.controller;

import com.vnnet.kpi.console.utils.MySecurityUtils;
import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.service.SysUserTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userType")
public class SysUserTypeController {
    private static final Logger logger = LoggerFactory.getLogger(com.vnnet.kpi.console.controller.SysUserTypeController.class);

    @Autowired
    private SysUserTypeService sysUserTypeService;

    //    @PreAuthorize("hasAuthority('sys:usertype:view')")
    @PostMapping(value = "/findByUser")
    public HttpResult findByUser() {
        String userName = MySecurityUtils.getUsername();
        if (userName == null || userName.isEmpty())
            return HttpResult.error(403, "access denied");
        return HttpResult.ok(sysUserTypeService.findByUser(userName));
    }

    @PostMapping(value = "/getListShowReport")
    public HttpResult getListShowReport() {
        String userName = MySecurityUtils.getUsername();
        if (userName == null || userName.isEmpty())
            return HttpResult.error(403, "access denied");
        return HttpResult.ok(sysUserTypeService.getListShowReport(userName));
    }
}
