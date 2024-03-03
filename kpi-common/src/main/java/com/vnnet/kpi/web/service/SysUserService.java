package com.vnnet.kpi.web.service;


import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.model.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface SysUserService {

    int save(SysUser record) throws Exception;

    int insert(SysUser record);

    int delete(SysUser record);

    int update(SysUser record);

    int updateStatus(SysUser record);

    SysUser findByNameLogin(String username);

    int delete(List<SysUser> records);

    SysUser findById(Long id);

    PageResult findPage(PageRequest pageRequest);

    List<SysUser> findUserGroup(String userName);

    SysUser findByName(String username);

    SysUser findByNameNew(String username);

    Set<String> findPermissions(String userName);

    List<SysUserRole> findUserRoles(Long userId);

    List<SysRole> findRoles(Long userId);

}
