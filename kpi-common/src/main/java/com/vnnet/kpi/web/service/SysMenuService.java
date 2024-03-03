package com.vnnet.kpi.web.service;

import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.model.SysMenu;

import java.util.List;

public interface SysMenuService {

    int save(SysMenu record);

    int delete(SysMenu record);

    int delete(List<SysMenu> records);

    SysMenu findById(Long id);

    PageResult findPage(PageRequest pageRequest);

    List<SysMenu> findTree(String userName, int menuType);


    List<SysMenu> findByUser(String userName);

    List<SysMenu> findMenuByUser(String userName);


}
