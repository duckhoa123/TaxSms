package com.vnnet.kpi.web.persistence;

import com.vnnet.kpi.web.model.SysMessagDt;
import com.vnnet.kpi.web.model.SysMessagDtExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysMessagDtMapper {
    long countByExample(SysMessagDtExample example);

    int deleteByExample(SysMessagDtExample example);

    int deleteByPrimaryKey(Long messDtId);

    int insert(SysMessagDt record);

    int insertSelective(SysMessagDt record);

    List<SysMessagDt> selectByExample(SysMessagDtExample example);

    SysMessagDt selectByPrimaryKey(Long messDtId);

    int updateByExampleSelective(@Param("record") SysMessagDt record, @Param("example") SysMessagDtExample example);

    int updateByExample(@Param("record") SysMessagDt record, @Param("example") SysMessagDtExample example);

    int updateByPrimaryKeySelective(SysMessagDt record);

    int updateByPrimaryKey(SysMessagDt record);
}