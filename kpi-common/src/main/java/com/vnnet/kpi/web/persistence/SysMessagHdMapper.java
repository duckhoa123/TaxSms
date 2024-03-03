package com.vnnet.kpi.web.persistence;

import com.vnnet.kpi.web.model.SysMessagHd;
import com.vnnet.kpi.web.model.SysMessagHdExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysMessagHdMapper {
    long countByExample(SysMessagHdExample example);

    int deleteByExample(SysMessagHdExample example);

    int deleteByPrimaryKey(Long messHdId);

    int insert(SysMessagHd record);

    int insertSelective(SysMessagHd record);

    List<SysMessagHd> selectByExample(SysMessagHdExample example);

    SysMessagHd selectByPrimaryKey(Long messHdId);

    int updateByExampleSelective(@Param("record") SysMessagHd record, @Param("example") SysMessagHdExample example);

    int updateByExample(@Param("record") SysMessagHd record, @Param("example") SysMessagHdExample example);

    int updateByPrimaryKeySelective(SysMessagHd record);

    int updateByPrimaryKey(SysMessagHd record);
}