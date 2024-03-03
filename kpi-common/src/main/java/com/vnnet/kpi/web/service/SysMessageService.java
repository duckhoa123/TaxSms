package com.vnnet.kpi.web.service;


import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.model.SysConfig;
import com.vnnet.kpi.web.model.SysMessagHd;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;

public interface SysMessageService {

    int update(SysMessagHd sysMessagHd, String userName);

    int delete(SysMessagHd sysMessagHd, String userName);

    int approve(SysMessagHd sysMessagHd, String userName);

    PageResult findPage(PageRequest pageRequest, String userName);

    PageResult findPageDT(PageRequest pageRequest);

    ByteArrayInputStream download(PageRequest pageRequest, File file);

    SysMessagHd findTotalMessByDate(PageRequest pageRequest, String userName);

    HashMap<String, List<Object>> findTop5Scheduler(PageRequest pageRequest, String userName);

    HashMap<String, List<Object>> findListScheduler(PageRequest pageRequest, String userName);

    PageResult findListHistory(PageRequest pageRequest, String userName);

    ByteArrayInputStream downloadHis(PageRequest pageRequest, String userName, File file);

    ByteArrayInputStream reportDownload(PageRequest pageRequest, File file, String userName);

    PageResult loadReport(PageRequest pageRequest, String userName);

    ByteArrayInputStream reportGenDownload(PageRequest pageRequest, File file, String userName);
    PageResult loadDetailReport(PageRequest pageRequest, String userName);
    ByteArrayInputStream reportDetailDownload(PageRequest pageRequest, File file, String userName);
    SysMessagHd findTotalMessFromLoadDetailReport(PageRequest pageRequest, String userName);
}
