package com.vnnet.kpi.web.persistence;

import com.vnnet.kpi.web.model.SysLogInfo;
import com.vnnet.kpi.web.model.SysMessagDt;
import com.vnnet.kpi.web.model.SysMessagHd;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SysLogInfoExMapper {
    List<SysLogInfo> findPage(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("operation") String operation, @Param("userName") String userName, @Param("userInput") String userInput);

}
