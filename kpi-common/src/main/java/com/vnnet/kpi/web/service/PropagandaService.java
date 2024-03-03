package com.vnnet.kpi.web.service;


import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.model.SysMessagHd;
import org.springframework.web.multipart.MultipartFile;

public interface PropagandaService {
    HttpResult save(MultipartFile file1, MultipartFile file2, MultipartFile[] file3, SysMessagHd sysMessagHd);
}
