package com.vnnet.kpi.web.persistence;

import com.vnnet.kpi.web.model.SysUser;
import com.vnnet.kpi.web.model.SysUserExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserExMapper {
    SysUser selectByLogin(@Param("username") String username);

    List<SysUser> selectByExample(@Param("province_code") String province_code, @Param("district_code") String district_code, @Param("filter_text") String filter_text, @Param("filter_type") String filter_type);

    List<SysUser> selectByGroup(@Param("filter_text") String filter_text);
}
