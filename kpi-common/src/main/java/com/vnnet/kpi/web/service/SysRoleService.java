package com.vnnet.kpi.web.service;

import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.model.SysMenu;
import com.vnnet.kpi.web.model.SysRole;
import com.vnnet.kpi.web.model.SysRoleMenu;
import com.vnnet.kpi.web.model.SysUserRole;

import java.util.List;

public interface SysRoleService {

    int save(SysRole record);

    int delete(SysRole record);

    int delete(List<SysRole> records);

    SysRole findById(Long id);

    PageResult findPage(PageRequest pageRequest, String userName);

    List<SysRole> findAll(String userName);

    List<SysMenu> findRoleMenus(Long roleId);

    int saveRoleMenus(List<SysRoleMenu> records);

    List<SysRole> findByName(String name);

    List<SysUserRole> findByUserName(String name);
}
