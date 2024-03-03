package com.vnnet.kpi.console.sevice;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init();

    String store(MultipartFile file, String filename) throws Exception;

    String store2(MultipartFile file, String filename, String location) throws Exception;

    Path load(String filename);

    Path load(String filename, String location);

    void deleteFile(String fileName);

    Resource loadAsResource(String fileName);

    Path loadExcelTemplate(String filename);

    Path loadPdf(String filename);

}
