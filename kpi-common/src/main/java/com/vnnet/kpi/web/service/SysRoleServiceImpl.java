package com.vnnet.kpi.web.service;

import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.config.Constants;
import com.vnnet.kpi.web.model.*;
import com.vnnet.kpi.web.persistence.*;
import com.vnnet.kpi.web.utils.MybatisPageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysMenuExMapper sysMenuExMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public int save(SysRole record) {
        if (record.getId() == null || record.getId() == 0) {
            return sysRoleMapper.insertSelective(record);
        }
        return sysRoleMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int delete(SysRole record) {
        return sysRoleMapper.deleteByPrimaryKey(record.getId());
    }

    @Override
    public int delete(List<SysRole> records) {
        List<Long> ids = new ArrayList<>();
        for (SysRole record : records) {
            ids.add(record.getId());
        }
        SysRoleExample example = new SysRoleExample();
        example.createCriteria().andIdIn(ids);
        sysRoleMapper.deleteByExample(example);
        return 1;
    }

    @Override
    public SysRole findById(Long id) {
        return sysRoleMapper.selectByPrimaryKey(id);
    }

    private SysRoleExample createColumnFilter(PageRequest pageRequest, String userName) {
        SysRoleExample example = new SysRoleExample();
        SysRoleExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo((byte) 0);
        String p = MybatisPageHelper.getColumnFilterValue(pageRequest, "name");
        if (p != null) {
            criteria.andNameEqualTo(p);
        }
        return example;
    }

    @Override
    public PageResult findPage(PageRequest pageRequest, String userName) {
        PageResult pageResult = MybatisPageHelper.findPage(pageRequest, sysRoleMapper, "selectByExample", createColumnFilter(pageRequest, userName));
        return pageResult;
    }

    @Override
    public List<SysRole> findAll(String userName) {
        SysRoleExample sysRoleExample = new SysRoleExample();
        return sysRoleMapper.selectByExample(sysRoleExample);
    }

    @Override
    public List<SysMenu> findRoleMenus(Long roleId) {
        return sysMenuExMapper.findRoleMenus(roleId);
    }

    @Transactional
    @Override
    public int saveRoleMenus(List<SysRoleMenu> records) {
        if (records == null || records.isEmpty()) {
            return 1;
        }
        Long roleId = records.get(0).getRoleId();
        SysRoleMenuExample sysRoleMenuExample = new SysRoleMenuExample();
        sysRoleMenuExample.createCriteria().andRoleIdEqualTo(roleId);
        sysRoleMenuMapper.deleteByExample(sysRoleMenuExample);
        for (SysRoleMenu record : records) {
            sysRoleMenuMapper.insertSelective(record);
        }
        return 1;
    }

    @Override
    public List<SysRole> findByName(String name) {
        SysRoleExample sysRoleExample = new SysRoleExample();
        sysRoleExample.createCriteria().andNameEqualTo(name);
        return sysRoleMapper.selectByExample(sysRoleExample);
    }

    private boolean exists(List<Long> sysRoles, Long sysRole) {
        boolean exist = false;
        for (Long role : sysRoles) {
            if (role.equals(sysRole)) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    @Override
    public List<SysUserRole> findByUserName(String userName) {
        SysUserExample sysUserExample = new SysUserExample();
        sysUserExample.createCriteria().andNameEqualTo(userName);
        List<SysUser> sysUsers = sysUserMapper.selectByExample(sysUserExample);
        if (sysUsers == null || sysUsers.size() != 1)
            return null;
        SysUserRoleExample sysUserRoleExample = new SysUserRoleExample();
        sysUserRoleExample.createCriteria().andUserIdEqualTo(sysUsers.get(0).getId().longValue());
        return sysUserRoleMapper.selectByExample(sysUserRoleExample);
    }
}
