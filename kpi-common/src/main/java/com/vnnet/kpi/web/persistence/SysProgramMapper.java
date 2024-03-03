package com.vnnet.kpi.web.persistence;

import com.vnnet.kpi.web.model.SysProgram;
import com.vnnet.kpi.web.model.SysProgramExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysProgramMapper {
    long countByExample(SysProgramExample example);

    int deleteByExample(SysProgramExample example);

    int deleteByPrimaryKey(Long programId);

    int insert(SysProgram record);

    int insertSelective(SysProgram record);

    List<SysProgram> selectByExample(SysProgramExample example);

    SysProgram selectByPrimaryKey(Long programId);

    int updateByExampleSelective(@Param("record") SysProgram record, @Param("example") SysProgramExample example);

    int updateByExample(@Param("record") SysProgram record, @Param("example") SysProgramExample example);

    int updateByPrimaryKeySelective(SysProgram record);

    int updateByPrimaryKey(SysProgram record);
}