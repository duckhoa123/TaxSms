package com.vnnet.kpi.console.controller;

import com.vnnet.kpi.console.utils.MySecurityUtils;
import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.bean.HttpStatus;
import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.model.SysConfig;
import com.vnnet.kpi.web.service.SysConfigService;
import com.vnnet.kpi.web.service.SysLogInfoService;
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
@RequestMapping("log")
public class SysLogController {

    private static final Logger logger = LoggerFactory.getLogger(SysLogController.class);

    @Autowired
    private SysLogInfoService sysLogInfoService;

    @PostMapping(value = "/save")
    public HttpResult save(@RequestBody String operation) {
        String userName = MySecurityUtils.getUsername();
        if (StringUtils.isBlank(userName))
            return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "access denied");
        return HttpResult.ok(sysLogInfoService.save(operation, userName));
    }

    @PreAuthorize("hasAuthority('sys:log:view')")
    @PostMapping(value = "/findPage")
    public HttpResult findPage(@RequestBody PageRequest pageRequest) {
        return HttpResult.ok(sysLogInfoService.findPage(pageRequest, MySecurityUtils.getUsername()));
    }

}
