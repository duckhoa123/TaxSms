package com.vnnet.kpi.console.controller;

import com.vnnet.kpi.console.utils.MySecurityUtils;
import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.bean.HttpStatus;
import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.config.Constants;
import com.vnnet.kpi.web.model.SysConfig;
import com.vnnet.kpi.web.model.SysLogInfo;
import com.vnnet.kpi.web.model.SysMessagHd;
import com.vnnet.kpi.web.service.SysConfigService;
import com.vnnet.kpi.web.service.SysLogInfoService;
import com.vnnet.kpi.web.service.SysMessageService;
import com.vnnet.kpi.web.service.TaxDebtService;
import com.vnnet.kpi.web.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/message")
public class SysMessageController {

    private static final Logger logger = LoggerFactory.getLogger(SysMessageController.class);

    private String SMS_TEMPLATE = "sms_template.xlsx";
    private String SMS_DETAIL_TEMPLATE = "sms_detail_template.xlsx";
    private String REPORT_TEMPLATE = "report_template.xlsx";
    private String REPORT_GEN_TEMPLATE = "report_gen_template.xlsx";
    private String REPORT_DETAIL_TEMPLATE = "file_bao_cao_chi_tiet.xlsx";

    @Autowired
    private SysMessageService sysMessageService;
    @Autowired
    private TaxDebtService taxDebtService;
    @Autowired
    private SysLogInfoService sysLogInfoService;

    @PreAuthorize("hasAuthority('sys:sms:scheduler:add')")
    @PostMapping(value = "/save")
    public HttpResult save(@RequestPart(value = "file1", required = false) MultipartFile file1, @RequestPart(value = "file2", required = false) MultipartFile file2, @RequestPart(value = "file3", required = false) MultipartFile[] file3, @RequestPart("data") SysMessagHd sysMessagHd) {
        try {
            if (file1.isEmpty() || file2.isEmpty()) {
                return HttpResult.error("Invalid data");
            }
            Date d1 = new Date();
            Date d2 = sysMessagHd.getSchedulerDateTime();
            if (d2.before(d1))
                return HttpResult.error("Message schedule must be after the current time");

            HttpResult httpResult = taxDebtService.save(file1, file2, file3, sysMessagHd);
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

    @PreAuthorize("hasAuthority('sys:sms:list:view')")
    @PostMapping(value = "/findPage")
    public HttpResult findPage(@RequestBody PageRequest pageRequest) {
        String userName = MySecurityUtils.getUsername();
        return HttpResult.ok(sysMessageService.findPage(pageRequest, userName));
    }

    @PreAuthorize("hasAuthority('sys:sms:list:view')")
    @PostMapping(value = "/findPageDT")
    public HttpResult findPageDT(@RequestBody PageRequest pageRequest) {
        return HttpResult.ok(sysMessageService.findPageDT(pageRequest));
    }

    @PreAuthorize("hasAuthority('sys:sms:list:delete')")
    @PostMapping(value = "/deleteBatch")
    public HttpResult deleteBatch(@RequestBody SysMessagHd sysMessagHd) {
        int result = sysMessageService.delete(sysMessagHd, MySecurityUtils.getUsername());
        if (result == 1)
            return HttpResult.ok("Message schedule canceled successfully");
        else if (result == 2)
            return HttpResult.error("The message has already been scheduled, so it cannot be canceled");
        else
            return HttpResult.error("Failed to cancel scheduled message");
    }

    @PreAuthorize("hasAuthority('sys:sms:list:view')")
    @PostMapping(value = "/download")
    public ResponseEntity<Resource> download(@RequestBody PageRequest pageRequest) {
        try {
//            Path path = storageService.loadExcelTemplate(COVID_TEMPLATE);
            File file = new File(Constants.FILE_PATH + SMS_TEMPLATE);  //path.toFile()
            if (!file.exists())
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            ByteArrayInputStream byteArrayInputStream = sysMessageService.download(pageRequest, file);
            if (byteArrayInputStream == null)
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            InputStreamResource streamResource = new InputStreamResource(byteArrayInputStream);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + SMS_TEMPLATE)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(streamResource);
        } catch (Exception ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PostMapping(value = "/findTotalMessFromLoadDetailReport")
    public HttpResult findTotalMessFromLoadDetailReport(@RequestBody PageRequest pageRequest) {
        String userName = MySecurityUtils.getUsername();
        if (StringUtils.isBlank(userName))
            return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "access denied");
        return HttpResult.ok(sysMessageService.findTotalMessFromLoadDetailReport(pageRequest, userName));
    }

    @PostMapping(value = "/findTotalMessByDate")
    public HttpResult findTotalMessByDate(@RequestBody PageRequest pageRequest) {
        String userName = MySecurityUtils.getUsername();
        if (StringUtils.isBlank(userName))
            return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "access denied");
        return HttpResult.ok(sysMessageService.findTotalMessByDate(pageRequest, userName));
    }

    @PostMapping(value = "/findTop5Scheduler")
    public HttpResult findTop5Scheduler(@RequestBody PageRequest pageRequest) {
        String userName = MySecurityUtils.getUsername();
        if (StringUtils.isBlank(userName))
            return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "access denied");
        return HttpResult.ok(sysMessageService.findTop5Scheduler(pageRequest, userName));
    }

    @PostMapping(value = "/findListScheduler")
    public HttpResult findListScheduler(@RequestBody PageRequest pageRequest) {
        String userName = MySecurityUtils.getUsername();
        if (StringUtils.isBlank(userName))
            return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "access denied");
        return HttpResult.ok(sysMessageService.findListScheduler(pageRequest, userName));
    }

    //    @PreAuthorize("hasAuthority('sys:sms:history:view')")
    @PostMapping(value = "/findListHistory")
    public HttpResult findListHistory(@RequestBody PageRequest pageRequest) {
        String userName = MySecurityUtils.getUsername();
        return HttpResult.ok(sysMessageService.findListHistory(pageRequest, userName));
    }

    @PreAuthorize("hasAuthority('sys:sms:history:view')")
    @PostMapping(value = "/downloadHis")
    public ResponseEntity<Resource> downloadHis(@RequestBody PageRequest pageRequest) {
        try {
//            Path path = storageService.loadExcelTemplate(COVID_TEMPLATE);
            File file = new File(Constants.FILE_PATH + SMS_DETAIL_TEMPLATE);  //path.toFile()
            if (!file.exists())
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            ByteArrayInputStream byteArrayInputStream = sysMessageService.downloadHis(pageRequest, MySecurityUtils.getUsername(), file);
            if (byteArrayInputStream == null)
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            InputStreamResource streamResource = new InputStreamResource(byteArrayInputStream);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + SMS_DETAIL_TEMPLATE)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(streamResource);
        } catch (Exception ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasAuthority('sys:sms:list:view')")
    @RequestMapping(value = "/downloadExcel", method = RequestMethod.POST)
    public ResponseEntity<Resource> downloadExcel(@RequestBody String filename) {
        try {
            Path path = Paths.get(Constants.FILE_PATH + filename);
            File file = path.toFile();
            if (!file.exists())
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);

            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(org.springframework.http.MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasAuthority('sys:sms:list:view')")
    @RequestMapping(value = "/downloadZip", method = RequestMethod.POST)
    public ResponseEntity<Resource> downloadZip(@RequestBody String filename) {
        try {
            Path path = Paths.get(Constants.FILE_PATH + filename);
            File file = path.toFile();
            if (!file.exists())
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);

            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(org.springframework.http.MediaType.parseMediaType("application/zip"))
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasAuthority('sys:sms:list:edit')")
    @PostMapping(value = "/edit")
    public HttpResult edit(@RequestBody SysMessagHd sysMessagHd) {
        int result = sysMessageService.update(sysMessagHd, MySecurityUtils.getUsername());
        if (result == 1)
            return HttpResult.ok("Message schedule updated successfully");
        else if (result == 2)
            return HttpResult.error("Message schedule is earlier than the current time");
        else
            return HttpResult.error("Failed to update message schedule");
    }

    @PreAuthorize("hasAuthority('sys:sms:list:view')")
    @PostMapping(value = "/reportDownload")
    public ResponseEntity<Resource> reportDownload(@RequestBody PageRequest pageRequest) {
        try {
            /*File file = new File(Constants.FILE_PATH + REPORT_TEMPLATE);*/
            File file = new File("C:\\Users\\61451\\Downloads\\Mbf5_CucThue\\Mbf5_CucThue\\File\\report_template.xlsx");

            if (!file.exists())
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            ByteArrayInputStream byteArrayInputStream = sysMessageService.reportDownload(pageRequest, file, MySecurityUtils.getUsername());
            if (byteArrayInputStream == null)
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            InputStreamResource streamResource = new InputStreamResource(byteArrayInputStream);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + REPORT_TEMPLATE)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(streamResource);
        } catch (Exception ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasAuthority('sys:sms:list:approve')")
    @PostMapping(value = "/approve")
    public HttpResult approve(@RequestBody SysMessagHd sysMessagHd) {
        int result = sysMessageService.approve(sysMessagHd, MySecurityUtils.getUsername());
        if (result == 1)
            return HttpResult.ok("Message schedule approved successfully");
        else if (result == 2)
            return HttpResult.error("The message has already been scheduled, so it cannot be approved");
        else
            return HttpResult.error("Failed to approve message schedule");
    }
    /*@PostMapping(value = "/loadDetailReport")
    public HttpResult loadDetailReport(@RequestBody PageRequest pageRequest) {
        String userName = MySecurityUtils.getUsername();
        return HttpResult.ok(sysMessageService.loadDetailReport(pageRequest, userName));
    }*/

   /* @GetMapping(value = "/DetailreportDownload ")
    public ResponseEntity<Resource> DetailreportDownload(@RequestBody PageRequest pageRequest) {
        try {
            File file = new File(Constants.FILE_PATH + REPORT_DETAIL_TEMPLATE);
            if (!file.exists())
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            ByteArrayInputStream byteArrayInputStream = sysMessageService.reportDetailDownload(pageRequest, file, MySecurityUtils.getUsername());
            if (byteArrayInputStream == null)
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            InputStreamResource streamResource = new InputStreamResource(byteArrayInputStream);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + REPORT_DETAIL_TEMPLATE)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(streamResource);
        } catch (Exception ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }*/

    // @PreAuthorize("hasAuthority('sys:report:view')")
    @PostMapping(value = "/loadReport")
    public HttpResult loadReport(@RequestBody PageRequest pageRequest) {
        String userName = MySecurityUtils.getUsername();
        return HttpResult.ok(sysMessageService.loadReport(pageRequest, userName));

    }

    @PostMapping(value = "/loadDetailReport")
    public HttpResult loadDetailReport(@RequestBody PageRequest pageRequest) {
        String userName = MySecurityUtils.getUsername();
        return HttpResult.ok(sysMessageService.loadDetailReport(pageRequest, userName));
    }

   // @PreAuthorize("hasAuthority('sys:report:view')")
    @PostMapping(value = "/reportGenDownload")
   /* public ResponseEntity<Resource> reportGenDownload(@RequestBody PageRequest pageRequest) {
        try {
            File file = new File(Constants.FILE_PATH + REPORT_GEN_TEMPLATE);
            if (!file.exists())
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            ByteArrayInputStream byteArrayInputStream = sysMessageService.reportGenDownload(pageRequest, file, MySecurityUtils.getUsername());
            if (byteArrayInputStream == null)
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            InputStreamResource streamResource = new InputStreamResource(byteArrayInputStream);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + REPORT_TEMPLATE)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(streamResource);
        } catch (Exception ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }*/
    public ResponseEntity<Resource> DetailreportDownload(@RequestBody PageRequest pageRequest) {
       try {
           // File file = new File(Constants.FILE_PATH + REPORT_DETAIL_TEMPLATE);
            File file = new File("C:\\Users\\61451\\Downloads\\Mbf5_CucThue\\Mbf5_CucThue\\File\\file_bao_cao_chi_tiet.xlsx");
            if (!file.exists())
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            ByteArrayInputStream byteArrayInputStream = sysMessageService.reportDetailDownload(pageRequest, file, MySecurityUtils.getUsername());
            if (byteArrayInputStream == null)
                return ResponseEntity.status(org.springframework.http.HttpStatus.MULTI_STATUS).body(null);
            InputStreamResource streamResource = new InputStreamResource(byteArrayInputStream);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "file_bao_cao_chi_tiet.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(streamResource);
        } catch (Exception ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
       /* File file = new File(Constants.FILE_PATH + REPORT_DETAIL_TEMPLATE);*/


    }