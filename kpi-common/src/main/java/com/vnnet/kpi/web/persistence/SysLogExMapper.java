package com.vnnet.kpi.web.persistence;

import com.vnnet.kpi.web.model.SysLog;
import com.vnnet.kpi.web.model.SysLogExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysLogExMapper {
//    long countByExample(SysLogExample example);
//
//    int deleteByExample(SysLogExample example);
//
//    int deleteByPrimaryKey(Long id);
//
//    int insert(SysLog record);
//
//    int insertSelective(SysLog record);

    List<SysLog> selectByExampleNewC2C(@Param("filterText") String filterText, @Param("userName") String userName);

    List<SysLog> selectByExampleNewKPI(@Param("filterText") String filterText, @Param("userName") String userName);

//    SysLog selectByPrimaryKey(Long id);
//
//    int updateByExampleSelective(@Param("record") SysLog record, @Param("example") SysLogExample example);
//
//    int updateByExample(@Param("record") SysLog record, @Param("example") SysLogExample example);
//
//    int updateByPrimaryKeySelective(SysLog record);
//
//    int updateByPrimaryKey(SysLog record);
}