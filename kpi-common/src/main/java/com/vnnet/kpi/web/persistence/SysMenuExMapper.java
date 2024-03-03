package com.vnnet.kpi.web.persistence;

import com.vnnet.kpi.web.model.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysMenuExMapper {
    List<SysMenu> findByUserName(@Param("userName") String userName);

    List<SysMenu> findRoleMenus(@Param("roleId") Long roleId);

    List<SysMenu> findRoleMenusC2C(@Param("roleId") Long roleId);

    List<SysMenu> findRoleMenusICRM(@Param("roleId") Long roleId);

    List<SysMenu> findRoleMenusNewProfile(@Param("name") String name);

    List<SysMenu> findRoleMenusC2CNew(@Param("name") String name);

}
