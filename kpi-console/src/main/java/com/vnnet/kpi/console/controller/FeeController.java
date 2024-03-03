package com.vnnet.kpi.console.controller;

import com.vnnet.kpi.console.utils.MySecurityUtils;
import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.model.SysMessagHd;
import com.vnnet.kpi.web.service.DeclarationService;
import com.vnnet.kpi.web.service.FeeService;
import com.vnnet.kpi.web.service.SysLogInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("fee")
public class FeeController {
    @Autowired
    private FeeService feeService;
    @Autowired
    private SysLogInfoService sysLogInfoService;


    @PreAuthorize("hasAuthority('sys:sms:fee:add')")
    @PostMapping(value = "/add")
    public HttpResult add(@RequestPart(value = "file1", required = false) MultipartFile file1, @RequestPart(value = "file2", required = false) MultipartFile file2, @RequestPart("data") SysMessagHd sysMessagHd) {
        try {
            if (file1.isEmpty() || file2.isEmpty()) {
                return HttpResult.error("Invalid data");
            }
            HttpResult httpResult = feeService.save(file1, file2, sysMessagHd);
            if (httpResult.getCode() == 200)
                sysLogInfoService.save("Scheduled message created successfully. Title: " + sysMessagHd.getTitle(), MySecurityUtils.getUsername());
            else
                sysLogInfoService.save("Scheduled message created successfully. Title: " + sysMessagHd.getTitle() + ". Error: " + httpResult.getMsg(), MySecurityUtils.getUsername());
            return httpResult;
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpResult.error(ex.getMessage());
        }
    }
}
