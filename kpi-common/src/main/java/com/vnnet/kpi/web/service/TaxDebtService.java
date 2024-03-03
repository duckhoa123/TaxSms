package com.vnnet.kpi.web.service;


import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.model.SysMessagHd;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;

public interface TaxDebtService {
    HttpResult save(MultipartFile file1, MultipartFile file2, MultipartFile[] file3, SysMessagHd sysMessagHd);
}
