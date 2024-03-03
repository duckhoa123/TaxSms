package com.vnnet.kpi.web.persistence;

import com.vnnet.kpi.web.model.SysMessagDt;
import com.vnnet.kpi.web.model.SysMessagHd;
import com.vnnet.kpi.web.model.SysMessagHdExample;
import org.apache.ibatis.annotations.Param;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

public interface SysMessagExMapper {
    List<SysMessagHd> findPage(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("currDate") Date currDate, @Param("title") String title, @Param("userName") String userName, @Param("groupCode") String groupCode, @Param("programId") String programId);

    List<SysMessagDt> sendMessage();

    List<SysMessagHd> findTotalMessByDate(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("group") String group);

    List<SysMessagHd> findTop5Scheduler(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("group") String group);

    List<SysMessagHd> findListScheduler(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("group") String group);

    List<SysMessagHd> findListHistory(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("currDate") Date currDate, @Param("title") String title, @Param("taxCode") String taxtCode,
                                      @Param("corName") String corName, @Param("status") String status, @Param("isdn") String isdn, @Param("userName") String userName, @Param("groupProgram") String groupProgram, @Param("programId") String programId);

    List<SysMessagHd> loadReport(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("status") String status);
    List<SysMessagHd> loadDetailReport(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,@Param("groupCode") String groupCode, @Param("status") String status,@Param("programid") String programId);
    List<SysMessagHd> findTotalMessFromLoadDetailReport(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,@Param("groupCode") String groupCode,@Param("programid") String programId);
}
