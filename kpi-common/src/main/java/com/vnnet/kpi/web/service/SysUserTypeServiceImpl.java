package com.vnnet.kpi.web.service;

import com.vnnet.kpi.web.config.Constants;
import com.vnnet.kpi.web.model.SysUser;
import com.vnnet.kpi.web.model.SysUserExample;
import com.vnnet.kpi.web.model.SysUserType;
import com.vnnet.kpi.web.model.SysUserTypeExample;
import com.vnnet.kpi.web.persistence.SysUserMapper;
import com.vnnet.kpi.web.persistence.SysUserTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SysUserTypeServiceImpl implements SysUserTypeService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserTypeMapper sysUserTypeMapper;
    @Autowired
    private SysUserService sysUserService;

    @Override
    public List<SysUserType> findByUser(String userName) {
        SysUser sysUser = sysUserService.findByNameNew(userName);
        SysUserTypeExample sysUserTypeExample = new SysUserTypeExample();
        if (sysUser.getUserTypeId() == Constants.USER_TYPE_ADMIN)
            sysUserTypeExample.createCriteria().andDelFlagEqualTo((byte) 0);
        else
            sysUserTypeExample.createCriteria().andIdEqualTo(sysUser.getUserTypeId()).andDelFlagEqualTo((byte) 0);
        return sysUserTypeMapper.selectByExample(sysUserTypeExample);
    }

    @Override
    public List<SysUserType> getListShowReport(String userName) {
        SysUser sysUser = sysUserService.findByNameNew(userName);
        SysUserTypeExample example = new SysUserTypeExample();
        if (sysUser.getUserTypeId() == Constants.USER_TYPE_ADMIN)
            example.createCriteria().andDelFlagEqualTo((byte)0).andShowReportEqualTo(true);
        else
            example.createCriteria().andDelFlagEqualTo((byte)0).andShowReportEqualTo(true).andIdEqualTo(sysUser.getUserTypeId());
        example.setOrderByClause(" ID ASC ");
        return  sysUserTypeMapper.selectByExample(example);
    }

}
