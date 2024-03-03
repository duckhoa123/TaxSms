package com.vnnet.kpi.web.service;

import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.config.Constants;
import com.vnnet.kpi.web.model.*;
import com.vnnet.kpi.web.persistence.SysConfigMapper;
import com.vnnet.kpi.web.persistence.SysProgramMapper;
import com.vnnet.kpi.web.utils.DateTimeUtils;
import com.vnnet.kpi.web.utils.MybatisPageHelper;
import com.vnnet.kpi.web.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SysProgramServiceImpl implements SysProgramService {

    private static final Logger logger = LoggerFactory.getLogger(SysProgramServiceImpl.class);

    @Autowired
    private SysProgramMapper sysProgramMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysLogInfoService sysLogInfoService;
    @Autowired
    private SysRoleService sysRoleService;


    @Override
    public List<SysProgram> findByGroup(SysProgram sysProgram, String userName) {
        SysProgramExample example = new SysProgramExample();
        if (StringUtils.isBlank(sysProgram.getGroupCode()) || sysProgram.getGroupCode().equals("ALL")) {
            List<SysUserRole> sysUserRoles = sysRoleService.findByUserName(userName);
            if (sysUserRoles != null && sysUserRoles.size() > 0) {
                String groupProgram = new CommonService().getGroupProgramWithRole(sysUserRoles.get(0).getRoleId());
                if (groupProgram.equals("ALL"))
                    example.createCriteria().andDelFlagEqualTo(0);
                else
                    example.createCriteria().andDelFlagEqualTo(0).andGroupCodeEqualTo(groupProgram);
            } else
                return null;
        } else
            example.createCriteria().andDelFlagEqualTo(0).andGroupCodeEqualTo(sysProgram.getGroupCode());
        return sysProgramMapper.selectByExample(example);
    }

    @Override
    public int update(SysProgram sysProgram, String userName) {
        try {
            SysProgram sysProgram1 = sysProgramMapper.selectByPrimaryKey(sysProgram.getProgramId());
            if (sysProgram1 != null) {
                List<SysProgram> list = checkUpdateExist(sysProgram);
                if (list != null && list.size() > 0) {
                    sysLogInfoService.save("Program updated fail. Title: " + sysProgram1.getProgramName() + ". Error: Tên chương trình \"" + sysProgram.getProgramName() + "\" đã tồn tại", userName);
                    return 2;
                }

                String programName = "";
                String smsTemp = "";
                String programNameOld = sysProgram1.getProgramName();

                if (sysProgram.getSmsTemplate() == null)
                    sysProgram.setSmsTemplate("");

                if (!sysProgram.getProgramName().equals(sysProgram1.getProgramName()))
                    programName = sysProgram1.getProgramName() + " -> " + sysProgram.getProgramName();
                if (!sysProgram.getSmsTemplate().equals(sysProgram1.getSmsTemplate()))
                    smsTemp = sysProgram1.getSmsTemplate() + " -> " + sysProgram.getSmsTemplate();

                sysProgram1.setProgramName(sysProgram.getProgramName());
                sysProgram1.setSmsTemplate(sysProgram.getSmsTemplate());
                int result = sysProgramMapper.updateByPrimaryKeySelective(sysProgram1);
                if (result >= 1)
                    sysLogInfoService.save("Program updated successfully. Title: " + programNameOld + ". Tên chương trình: " + programName + ". Mẫu tin nhắn: " + smsTemp, userName);
                return result;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public int deleteBatch(long programId, String userName) {
        try {
            SysProgram sysProgram = sysProgramMapper.selectByPrimaryKey(programId);
            sysProgram.setDelFlag(1);
            int result = sysProgramMapper.updateByPrimaryKeySelective(sysProgram);
            if (result >= 1)
                sysLogInfoService.save("Delete program successfully. Title: " + sysProgram.getProgramName(), userName);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }


    private List<SysProgram> checkUpdateExist(SysProgram sysProgram) {
        SysProgramExample example = new SysProgramExample();
        example.createCriteria().andDelFlagEqualTo(0).andProgramNameLikeInsensitive(sysProgram.getProgramName()).andProgramIdNotEqualTo(sysProgram.getProgramId());
        return sysProgramMapper.selectByExample(example);
    }

}
