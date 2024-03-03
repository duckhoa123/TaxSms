package com.vnnet.kpi.console.controller;

import com.vnnet.kpi.console.utils.MySecurityUtils;
import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.model.SysMessagHd;
import com.vnnet.kpi.web.service.DeclarationService;
import com.vnnet.kpi.web.service.SysLogInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("declaration")
public class DeclarationController {

    private static final Logger logger = LoggerFactory.getLogger(DeclarationController.class);

    @Autowired
    private DeclarationService declarationService;
    @Autowired
    private SysLogInfoService sysLogInfoService;


    @PreAuthorize("hasAuthority('sys:sms:kekhai:add')")
    @PostMapping(value = "/add")
    public HttpResult add(@RequestPart(value = "file1", required = false) MultipartFile file1, @RequestPart(value = "file2", required = false) MultipartFile file2, @RequestPart(value = "file3", required = false) MultipartFile file3, @RequestPart("data") SysMessagHd sysMessagHd) {
        try {
            if (file1.isEmpty() || file2.isEmpty()) {
                return HttpResult.error("Invalid data");
            }
            HttpResult httpResult = declarationService.save(file1, file2, file3, sysMessagHd);
            if (httpResult.getCode() == 200)
                sysLogInfoService.save("Scheduled message created successfully. Title: " + sysMessagHd.getTitle(), MySecurityUtils.getUsername());
            else
                sysLogInfoService.save("Failed to schedule message. Title: " + sysMessagHd.getTitle() + ". Error: " + httpResult.getMsg(), MySecurityUtils.getUsername());
            return httpResult;
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpResult.error(ex.getMessage());
        }
    }
}
