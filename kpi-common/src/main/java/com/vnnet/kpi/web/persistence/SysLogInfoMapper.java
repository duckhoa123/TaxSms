package com.vnnet.kpi.web.persistence;

import com.vnnet.kpi.web.model.SysLogInfo;
import com.vnnet.kpi.web.model.SysLogInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysLogInfoMapper {
    long countByExample(SysLogInfoExample example);

    int deleteByExample(SysLogInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SysLogInfo record);

    int insertSelective(SysLogInfo record);

    List<SysLogInfo> selectByExample(SysLogInfoExample example);

    SysLogInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SysLogInfo record, @Param("example") SysLogInfoExample example);

    int updateByExample(@Param("record") SysLogInfo record, @Param("example") SysLogInfoExample example);

    int updateByPrimaryKeySelective(SysLogInfo record);

    int updateByPrimaryKey(SysLogInfo record);
}