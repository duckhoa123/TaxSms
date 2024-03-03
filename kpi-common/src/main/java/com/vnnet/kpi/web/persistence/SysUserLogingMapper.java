package com.vnnet.kpi.web.persistence;

import com.vnnet.kpi.web.model.SysUser;
import com.vnnet.kpi.web.model.SysUserLoging;
import com.vnnet.kpi.web.model.SysUserLogingExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserLogingMapper {
    long countByExample(SysUserLogingExample example);

    int deleteByExample(SysUserLogingExample example);

    int deleteByPrimaryKey(Short mainId);

    int insert(SysUser record);

    int insertSelective(SysUserLoging record);

    List<SysUserLoging> selectByExample(SysUserLogingExample example);

    SysUserLoging selectByPrimaryKey(Short mainId);

    int updateByExampleSelective(@Param("record") SysUserLoging record, @Param("example") SysUserLogingExample example);

    int updateByExample(@Param("record") SysUserLoging record, @Param("example") SysUserLogingExample example);

    int updateByPrimaryKeySelective(SysUserLoging record);

    int updateByPrimaryKey(SysUserLoging record);
}