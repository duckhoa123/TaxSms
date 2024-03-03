package com.vnnet.kpi.console.controller;

import com.vnnet.kpi.console.sevice.StorageService;
import com.vnnet.kpi.web.config.Constants;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


@CrossOrigin("*")
@RestController
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(com.vnnet.kpi.console.controller.UploadController.class);

    @Autowired
    private StorageService storageService;

    @RequestMapping(value = "/downloadExcel/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadExcel(@PathVariable String filename) {
        try {
            Path path = Paths.get(Constants.FILE_PATH + filename);
            File file = path.toFile();
            if (!file.exists())
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);

            try {
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                        .contentType(org.springframework.http.MediaType.parseMediaType("application/vnd.ms-excel"))
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

//    @RequestMapping(value = "/pdf/{filename:.+}", method = RequestMethod.GET)
//    public ResponseEntity<Resource> downloadPdf(@PathVariable String filename) {
//        try {
//            String folder = filename.substring(0,6);
//            String file_name = filename.substring(7);
//            Path path = Paths.get(Constants.FILE_PDF_PATH + folder + "\\" + file_name);
//            File file = path.toFile();
//            if(!file.exists())
//                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//
//            try{
//                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
//                return ResponseEntity.ok()
//                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
//                        .contentType(org.springframework.http.MediaType.parseMediaType("application/pdf"))
//                        .body(resource);
//            }
//            catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    @RequestMapping(value = "/pdf/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadPdf(@PathVariable String filename) {
        try {
            String folder = filename.substring(0, 6);
            String file_name = filename.substring(7);
            Path path = Paths.get(Constants.FILE_PDF_PATH + folder + "\\" + file_name);
            File file = path.toFile();
            if (!file.exists())
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);

            try {
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                        .contentType(org.springframework.http.MediaType.parseMediaType("application/pdf"))
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

    @RequestMapping(value = "/image/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadImage(@PathVariable String filename) {
        try {
            String folder = filename.substring(0, 6);
            String file_name = filename.substring(7);
            Path path = Paths.get(Constants.FILE_PDF_PATH + folder + "\\" + file_name);
            File file = path.toFile();
            if (!file.exists())
                return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);

            try {
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                        .contentType(org.springframework.http.MediaType.parseMediaType("image/jpeg"))
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
