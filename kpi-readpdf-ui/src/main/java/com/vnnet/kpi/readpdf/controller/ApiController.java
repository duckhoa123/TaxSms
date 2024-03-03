package com.vnnet.kpi.readpdf.controller;


import com.vnnet.kpi.readpdf.model.FormData;
import com.vnnet.kpi.readpdf.service.SendSmsService;
import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.config.Constants;
import com.vnnet.kpi.web.service.SysPdfReadHisService;
import com.vnnet.kpi.web.utils.JwtTokenForPdfFile;
import com.vnnet.kpi.web.utils.StringUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ApiController {

    @Autowired
    private SysPdfReadHisService sysPdfReadHisService;


    @RequestMapping(value = { "/view/sendOtp", }, method = RequestMethod.POST)
    public HttpResult sendOtp(Model model, @RequestBody FormData formData) {
        try {
            Claims claims = JwtTokenForPdfFile.getClaimsFromToken(formData.getToken());
            if(claims != null) {
                String otp = StringUtils.random(6);
                String isdn = claims.get("calledNumber").toString();
                Long messHdId = ((Number) claims.get("messHdId")).longValue();
                String pdfFile = claims.get("pdfFile").toString();
                String content = "Ma OTP xac thuc giao dich cua quy khach la:" + otp;
                int result = new SendSmsService().sendSMS(isdn, content);
                if(result == 200) {
                    claims.put("otp", StringUtils.random(6));
                    String token = JwtTokenForPdfFile.generateToken(messHdId, isdn, pdfFile, otp);
                    formData.setToken(token);
                    return HttpResult.ok(formData);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return HttpResult.error();
    }

    @RequestMapping(value = "/view/pdf", method = RequestMethod.POST)
    public ResponseEntity<?> downloadPdf(@RequestBody FormData formData) {
        try {
            Claims claims = JwtTokenForPdfFile.getClaimsFromToken(formData.getToken());
            if(claims == null)
                return new ResponseEntity<>(HttpResult.error("Có lỗi xảy ra. Vui lòng thử lại sau."),  HttpStatus.BAD_REQUEST);

            if(!claims.get("otp").toString().equals(formData.getOtp()))
                return new ResponseEntity<>(HttpResult.error("Mã OTP không đúng. Vui lòng thử lại"),  HttpStatus.INTERNAL_SERVER_ERROR);

            String pdfFile = claims.get("pdfFile").toString();
            String calledNumber = claims.get("calledNumber").toString();
            Long messHdId = ((Number) claims.get("messHdId")).longValue();
            sysPdfReadHisService.save(messHdId, calledNumber, pdfFile);
            return loadFile(pdfFile);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpResult.error("Có lỗi xảy ra. Vui lòng thử lại sau."),  HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private ResponseEntity<Resource> loadFile(String filename) {
        try {
            String contentType = filename.endsWith(".jpg") ? "image/jpeg" : "application/pdf";
            String folder = filename.substring(0, 6);
            String file_name = filename.substring(7);
            Path path = Paths.get(Constants.FILE_PDF_PATH + folder + "\\" + file_name);
            File file = path.toFile();
            if (!file.exists())
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);

            try {
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                        .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                        .body(resource);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
