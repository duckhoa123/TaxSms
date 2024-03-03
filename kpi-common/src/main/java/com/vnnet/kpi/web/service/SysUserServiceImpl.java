package com.vnnet.kpi.web.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.model.*;
import com.vnnet.kpi.web.persistence.*;
import com.vnnet.kpi.web.utils.MybatisPageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;


@Service
public class SysUserServiceImpl implements SysUserService {

    private static final Logger logger = LoggerFactory.getLogger(SysUserServiceImpl.class);
    @Autowired
    private SysUserTypeMapper sysUserTypeMapper;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysUserExMapper sysUserExMapper;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysUserLogingMapper sysUserLogingMapper;

    @Autowired
    private SysRoleService sysRoleService;


    @Transactional
    @Override
    public int save(SysUser record) throws Exception {
        int ret = 1;
        if (record.getId() == null || record.getId() == 0) {

            SysUserExample sysUserExample = new SysUserExample();
            sysUserExample.createCriteria().andNameEqualTo(record.getName());
            List<SysUser> users = sysUserMapper.selectByExample(sysUserExample);
            if (users != null && !users.isEmpty()) {
                throw new IOException("Username already exist");
            }
            // New users
            ret = sysUserMapper.insertSelective(record);

            if (ret <= 0) {
                logger.error("Failed to Insert new Item");
                return ret;
            }
        } else {
            ret = sysUserMapper.updateByPrimaryKeySelective(record);
            if (ret <= 0) {
                logger.error("Failed to update new Item");
                return ret;
            }
        }

        updateMore(record);

        return 1;
    }

    private void updateMore(SysUser sysUser) {
        SysUser user = sysUserMapper.selectByPrimaryKey(sysUser.getId());
        if (user != null) {
            SysUserRoleExample sysUserRoleExample = new SysUserRoleExample();
            sysUserRoleExample.createCriteria().andUserIdEqualTo(sysUser.getId());
            sysUserRoleMapper.deleteByExample(sysUserRoleExample);
            if (sysUser.getRoles() != null && !sysUser.getRoles().isEmpty()) {
                for (Long role : sysUser.getRoles()) {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setUserId(user.getId());
                    sysUserRole.setRoleId(role);
                    sysUserRoleMapper.insertSelective(sysUserRole);
                }
            }

        }
    }


    @Override
    public int update(SysUser record) {
        int ret = 0;
        try {
            ret = sysUserMapper.updateByPrimaryKeySelective(record);
        } catch (Exception e) {
            return 0;
        }
        if (ret <= 0) {
            return ret;
        }
        updateMore(record);
        return 1;
    }

    @Override
    public SysUser findByNameLogin(String name) {

        SysUser sysUser = sysUserExMapper.selectByLogin(name);

        return sysUser;
    }

    @Override
    public int updateStatus(SysUser record) {
        int ret = 0;
        try {
            ret = sysUserMapper.updateByPrimaryKey(record);
        } catch (Exception e) {
            if (e.getCause().toString().contains("ORA-00001")) {
                return -1;
            }
            return 0;
        }
        if (ret <= 0) {
            return ret;
        } else {
//			sysUserLogingMapper.insert(record);

        }
        return 1;
    }

    @Override
    public int delete(SysUser record) {
        return sysUserMapper.deleteByPrimaryKey(record.getId());
    }

    @Override
    public int insert(SysUser record) {
        int ret = 0;
        try {
            ret = sysUserMapper.insert(record);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        if (ret <= 0) {
            return ret;
        }

        updateMore(record);
        return 1;
    }

    @Override
    public int delete(List<SysUser> records) {

        for (SysUser record : records) {
            SysUser sysUser = sysUserService.findById(record.getId());
            sysUser.setDelFlag((byte) 1L);
            sysUser.setStatus((byte) 0);
            sysUserService.update(sysUser);
//			sysUserLogingMapper.insert(sysUser);
        }
//		SysUserExample example = new SysUserExample();
//		example.createCriteria().andIdIn(ids);
//		sysUserMapper.deleteByExample(example);
        return 1;
    }

    @Override
    public SysUser findById(Long id) {
        SysUser sysUser = sysUserMapper.selectByPrimaryKey(id);
        if (sysUser == null) {
            return null;
        }
        sysUser.setUserRoles(findUserRoles(sysUser.getId()));


        List<SysRole> roles = findRoles(sysUser.getId());
        if (roles != null && !roles.isEmpty()) {
            List<String> names = new ArrayList<>();
            List<Long> ids = new ArrayList<>();
            for (SysRole role : roles) {
                names.add(role.getRemark());
                ids.add(role.getId());
            }
            sysUser.setRoleNames(String.join(",", names));
            sysUser.setRoles(ids);
        }

        return sysUser;

    }

    @Override
    public SysUser findByName(String name) {
        SysUserExample sysUserExample = new SysUserExample();
        sysUserExample.createCriteria().andNameEqualTo(name);
        List<SysUser> users = sysUserMapper.selectByExample(sysUserExample);
        if (users == null || users.isEmpty()) {
            return null;
        }
        SysUser sysUser = users.get(0);


        sysUser.setUserRoles(findUserRoles(sysUser.getId()));
        List<SysRole> roles = findRoles(sysUser.getId());
        if (roles != null && !roles.isEmpty()) {
            List<String> names = new ArrayList<>();
            List<Long> ids = new ArrayList<>();
            for (SysRole role : roles) {
                names.add(role.getRemark());
                ids.add(role.getId());
            }
            sysUser.setRoleNames(String.join(",", names));
            sysUser.setRoles(ids);
        }

        return sysUser;
    }

    @Override
    public SysUser findByNameNew(String name) {
        SysUserExample sysUserExample = new SysUserExample();
        sysUserExample.createCriteria().andNameEqualTo(name).andDelFlagEqualTo((byte) 0);
        List<SysUser> users = sysUserMapper.selectByExample(sysUserExample);
        if (users == null || users.isEmpty()) {
            return null;
        }
        SysUser sysUser = users.get(0);
        return sysUser;
    }

    private SysUserExample createColumnFilter(PageRequest pageRequest) {
        String name = MybatisPageHelper.getColumnFilterValue(pageRequest, "name");
        String email = MybatisPageHelper.getColumnFilterValue(pageRequest, "email");
        String filterText = MybatisPageHelper.getColumnFilterValue(pageRequest, "filterText");
        String userName = pageRequest.getUserName();

        SysUserExample example = new SysUserExample();
        SysUserExample.Criteria criteria = example.or();
        criteria.andDelFlagEqualTo((byte) 0);

        SysUserExample sysUserExample = new SysUserExample();
        sysUserExample.createCriteria().andNameEqualTo(userName);
        SysUser sysUser = sysUserMapper.selectByExample(sysUserExample).get(0);
        if (name != null && !name.isEmpty()) {
            if (email != null && !email.isEmpty()) {
                criteria.andNameEqualTo(name).andEmailEqualTo(email);
            } else {
                criteria.andNameEqualTo(name);
            }
        }
        if (filterText != null && !filterText.isEmpty()) {
            example.or().andNameLike("%" + filterText + "%");
        }
        return example;
    }

    @Override
    public PageResult findPage(PageRequest pageRequest) {
        String filterText = MybatisPageHelper.getColumnFilterValue(pageRequest, "filterText");
        if (filterText == null)
            filterText = "";
        String filterType = MybatisPageHelper.getColumnFilterValue(pageRequest, "filterType");
        if (filterType == null)
            filterType = "";
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 0) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<SysUser> sysUsers = sysUserExMapper.selectByExample("", "", filterText, filterType);
        PageResult pageResult = MybatisPageHelper.getPageResult(pageRequest, new PageInfo((List) sysUsers));
        findUserRoles(pageResult);
        findRoles(pageResult);
        return pageResult;
    }

    @Override
    public List<SysUser> findUserGroup(String userName) {
        List<SysUser> sysUsers1 = sysUserExMapper.selectByGroup("");
        return sysUsers1;
    }

    private void findUserRoles(PageResult pageResult) {
        List<?> content = pageResult.getContent();
        for (Object object : content) {
            SysUser sysUser = (SysUser) object;
            List<SysUserRole> userRoles = findUserRoles(sysUser.getId());
            sysUser.setUserRoles(userRoles);
        }
    }

    private void findRoles(PageResult pageResult) {
        List<?> content = pageResult.getContent();
        for (Object object : content) {
            SysUser sysUser = (SysUser) object;
            SysUserTypeExample sysUserTypeExample = new SysUserTypeExample();
            sysUserTypeExample.createCriteria().andIdEqualTo(sysUser.getUserTypeId());
            sysUser.setUserTypeName(sysUserTypeMapper.selectByExample(sysUserTypeExample).get(0).getName());
            SysUserRoleExample sysUserRoleExample = new SysUserRoleExample();
            sysUserRoleExample.createCriteria().andUserIdEqualTo(sysUser.getId());
            List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectByExample(sysUserRoleExample);
//			if(sysUser.getEndDate()!=null){
//				sysUser.setStatus(0L);
//			}
            if (sysUserRoles == null || sysUserRoles.isEmpty()) {
                sysUser.setRoles(new ArrayList<>());
                sysUser.setRoleNames("");
            } else {
                List<Long> ids = new ArrayList<>();
                for (SysUserRole sysUserRole : sysUserRoles) {
                    ids.add(sysUserRole.getRoleId());
                }
                SysRoleExample sysRoleExample = new SysRoleExample();
                sysRoleExample.createCriteria().andIdIn(ids);

                List<SysRole> sysRoles = sysRoleMapper.selectByExample(sysRoleExample);

                if (sysRoles == null || sysRoles.isEmpty()) {
                    sysUser.setRoles(new ArrayList<>());
                    sysUser.setRoleNames("");
                } else {
                    List<String> names = new ArrayList<>();
                    List<Long> roleIds = new ArrayList<>();
                    for (SysRole role : sysRoles) {
                        names.add(role.getRemark());
                        roleIds.add(role.getId());
                    }
                    sysUser.setRoles(roleIds);
                    sysUser.setRoleNames(String.join(",", names));
                }
            }
        }
    }

    @Override
    public Set<String> findPermissions(String userName) {
        Set<String> perms = new HashSet<>();
        List<SysMenu> sysMenus = sysMenuService.findByUser(userName);
        for (SysMenu sysMenu : sysMenus) {
            if (sysMenu.getPerms() != null && !"".equals(sysMenu.getPerms())) {
                perms.add(sysMenu.getPerms());
            }
        }
        return perms;
    }

    @Override
    public List<SysUserRole> findUserRoles(Long userId) {
        SysUserRoleExample sysUserRoleExample = new SysUserRoleExample();
        sysUserRoleExample.createCriteria().andUserIdEqualTo(userId);
        return sysUserRoleMapper.selectByExample(sysUserRoleExample);
    }

    @Override
    public List<SysRole> findRoles(Long userId) {
        SysUserRoleExample sysUserRoleExample = new SysUserRoleExample();
        sysUserRoleExample.createCriteria().andUserIdEqualTo(userId);
        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectByExample(sysUserRoleExample);

        if (sysUserRoles == null || sysUserRoles.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> ids = new ArrayList<>();
        for (SysUserRole sysUserRole : sysUserRoles) {
            ids.add(sysUserRole.getRoleId());
        }
        SysRoleExample sysRoleExample = new SysRoleExample();
        sysRoleExample.createCriteria().andIdIn(ids);
        return sysRoleMapper.selectByExample(sysRoleExample);
    }

}
