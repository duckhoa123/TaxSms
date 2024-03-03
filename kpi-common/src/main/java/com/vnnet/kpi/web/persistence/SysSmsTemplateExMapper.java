package com.vnnet.kpi.web.persistence;

import com.vnnet.kpi.web.model.SysSmsTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SysSmsTemplateExMapper {
    List<SysSmsTemplate> findPage(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("group") String group, @Param("programId") String programId);

    List<SysSmsTemplate> findPageWithOutDate(@Param("group") String group, @Param("programId") String programId);

}
