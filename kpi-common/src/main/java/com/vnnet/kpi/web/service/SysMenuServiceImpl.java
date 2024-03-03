package com.vnnet.kpi.web.service;

import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.config.Constants;
import com.vnnet.kpi.web.model.*;
import com.vnnet.kpi.web.persistence.*;
import com.vnnet.kpi.web.utils.MybatisPageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysMenuExMapper sysMenuExMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;


    @Override
    public int save(SysMenu record) {
        if (record.getId() == null || record.getId() == 0) {
            return sysMenuMapper.insertSelective(record);
        }
        if (record.getParentId() == null) {
            record.setParentId((long) 0);
        }
        return sysMenuMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int delete(SysMenu record) {
        return sysMenuMapper.deleteByPrimaryKey(record.getId());
    }

    @Override
    public int delete(List<SysMenu> records) {
        List<Long> ids = new ArrayList<>();
        for (SysMenu record : records) {
            ids.add(record.getId());
        }
        SysMenuExample example = new SysMenuExample();
        example.createCriteria().andIdIn(ids);
        sysMenuMapper.deleteByExample(example);
        return 1;
    }

    @Override
    public SysMenu findById(Long id) {
        return sysMenuMapper.selectByPrimaryKey(id);
    }

    private SysMenuExample createColumnFilter(PageRequest pageRequest) {
        SysMenuExample example = new SysMenuExample();
        SysMenuExample.Criteria criteria = example.createCriteria();
        return example;
    }

    @Override
    public PageResult findPage(PageRequest pageRequest) {
        PageResult pageResult = MybatisPageHelper.findPage(pageRequest, sysMenuMapper, "selectByExample", createColumnFilter(pageRequest));
        return pageResult;
    }

    @Override
    public List<SysMenu> findTree(String userName, int menuType) {
        List<SysMenu> sysMenus = new ArrayList<>();
        List<SysMenu> menus = findMenuByUser(userName);
        for (SysMenu menu : menus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                menu.setLevel(0);
                if (!exists(sysMenus, menu)) {
                    sysMenus.add(menu);
                }
            }
        }
        if (!sysMenus.isEmpty()) {
            sysMenus.sort(Comparator.comparing(SysMenu::getOrderNum));
        }

        findChildren(sysMenus, menus, menuType);
//		for(int i=0;i<sysMenus.size();i++)
//		{
//			System.out.println("findTree" + sysMenus.get(i));
//		}
        return sysMenus;
    }

    @Override
    public List<SysMenu> findByUser(String userName) {
        SysMenuExample sysMenuExample = new SysMenuExample();
        if (userName == null || "".equals(userName) || Constants.ADMIN.equalsIgnoreCase(userName)) {
            return sysMenuMapper.selectByExample(sysMenuExample);
        }
        return sysMenuExMapper.findByUserName(userName);
    }

    @Override
    public List<SysMenu> findMenuByUser(String userName) {
        if (userName == null || "".equals(userName) || Constants.ADMIN.equalsIgnoreCase(userName)) {
            SysMenuExample sysMenuExampleAdmin = new SysMenuExample();
            sysMenuExampleAdmin.createCriteria().andIdIsNotNull();
            return sysMenuMapper.selectByExample(sysMenuExampleAdmin);
        } else {
            SysMenuExample sysMenuExample = new SysMenuExample();
            SysUser sysUser = sysUserService.findByName(userName);

            SysUserRoleExample sysUserRoleExample = new SysUserRoleExample();
            sysUserRoleExample.createCriteria().andUserIdEqualTo(sysUser.getId());

            List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectByExample((sysUserRoleExample));

            List<Long> sysRoleUsers = new ArrayList<>();

            for (SysUserRole sysUserRole : sysUserRoles) {
                if (sysUserRole.getRoleId() != null || sysUserRole.getRoleId() != 0) {
                    sysRoleUsers.add(sysUserRole.getRoleId());
                }
            }

            SysRoleMenuExample sysRoleMenuExample = new SysRoleMenuExample();
            sysRoleMenuExample.createCriteria().andRoleIdIn(sysRoleUsers);

            List<SysRoleMenu> sysRoleMenus = sysRoleMenuMapper.selectByExample(sysRoleMenuExample);
            List<Long> menuIds = new ArrayList<>();
            for (SysRoleMenu sysRoleMenu : sysRoleMenus) {
                if (sysRoleMenu.getMenuId() != null || sysRoleMenu.getMenuId() != 0) {
                    menuIds.add(sysRoleMenu.getMenuId());
                }
            }

            sysMenuExample.createCriteria().andIdIn(menuIds).andDelFlagEqualTo((byte) 0);
            return sysMenuMapper.selectByExample(sysMenuExample);
        }
    }

    private void findChildren(List<SysMenu> SysMenus, List<SysMenu> menus, int menuType) {
        for (SysMenu SysMenu : SysMenus) {
            List<SysMenu> children = new ArrayList<>();
            for (SysMenu menu : menus) {
                if (menuType == 1 && menu.getType() == 2) {
                    // If it is the type that does not require a button, and the menu type is a button, directly filter it out
                    continue;
                }
                if (SysMenu.getId() != null && SysMenu.getId().equals(menu.getParentId())) {
                    menu.setParentName(SysMenu.getName());
                    menu.setLevel(SysMenu.getLevel() + 1);
                    if (!exists(children, menu)) {
                        children.add(menu);
                    }
                }
            }
            SysMenu.setChildren(children);
            children.sort(Comparator.comparing(com.vnnet.kpi.web.model.SysMenu::getOrderNum));
            findChildren(children, menus, menuType);
        }
    }

    private void findChildrenICRM(List<SysMenu> SysMenuParents, List<SysMenu> menualls, int menuType) {
        //System.out.println("findChildrenICRM" + SysMenus.size());
        for (SysMenu sysMenuParents : SysMenuParents) {
            List<SysMenu> children = new ArrayList<>();
            for (SysMenu menuall : menualls) {
                if (menuType == 1 && menuall.getType() == 2) {
                    // If it is the type that does not require a button, and the menu type is a button, directly filter it out
                    continue;
                }
                //System.out.println("1: " + SysMenu.getObjectIdCRM());
                //System.out.println("2: " + menu.getParentIdCRM());
                if (sysMenuParents.getObjectIdCRM() != null && sysMenuParents.getObjectIdCRM().equals(menuall.getParentIdCRM())) {
                    menuall.setParentName(sysMenuParents.getNameCRM());
                    //System.out.println("3: " + menu.getParentName());
                    menuall.setLevel(sysMenuParents.getLevel() + 1);
                    //System.out.println("4: " + menu.getLevel());
                    if (!existsICRM(children, menuall)) {
                        //System.out.println("5 here!");
                        children.add(menuall);
                        //System.out.println("children: " + children);
                    }
                }
            }
            sysMenuParents.setChildren(children);
            //children.sort(Comparator.comparing(com.vnnet.kpi.web.model.SysMenu::getOrderNum));
            findChildren(children, menualls, menuType);
        }
    }

    private boolean exists(List<SysMenu> sysMenus, SysMenu sysMenu) {
        boolean exist = false;
        for (SysMenu menu : sysMenus) {
            if (menu.getId().equals(sysMenu.getId())) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    private boolean existsICRM(List<SysMenu> sysMenus, SysMenu sysMenu) {
        boolean exist = false;
        for (SysMenu menu : sysMenus) {
            if (menu.getNameCRM().equals(sysMenu.getNameCRM())) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    private boolean existsLong(List<Long> sysRoles, Long sysRole) {
        boolean exist = false;
        for (Long role : sysRoles) {
            if (role.equals(sysRole)) {
                exist = true;
                break;
            }
        }
        return exist;
    }

}
