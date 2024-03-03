package com.vnnet.kpi.web.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.config.Constants;
import com.vnnet.kpi.web.model.*;
import com.vnnet.kpi.web.persistence.SysProgramMapper;
import com.vnnet.kpi.web.persistence.SysSmsTemplateExMapper;
import com.vnnet.kpi.web.persistence.SysSmsTemplateMapper;
import com.vnnet.kpi.web.utils.DateTimeUtils;
import com.vnnet.kpi.web.utils.MybatisPageHelper;
import com.vnnet.kpi.web.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SysSmsTemplateServiceImpl implements SysSmsTemplateService {

    private static final Logger logger = LoggerFactory.getLogger(SysSmsTemplateServiceImpl.class);

    @Autowired
    private SysSmsTemplateExMapper sysSmsTemplateExMapper;
    @Autowired
    private SysSmsTemplateMapper sysSmsTemplateMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysLogInfoService sysLogInfoService;
    @Autowired
    private SysProgramMapper sysProgramMapper;
    @Autowired
    private SysRoleService sysRoleService;

    @Override
    public PageResult findPage(PageRequest pageRequest, String userName) {
        try {
            List<SysUserRole> sysUserRoles = sysRoleService.findByUserName(userName);
            if (sysUserRoles == null || sysUserRoles.size() == 0)
                return null;
            String group = new CommonService().getGroupProgramWithRole(sysUserRoles.get(0).getRoleId());

            String fromDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "fromDate");
            String toDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "toDate");
            String programId = MybatisPageHelper.getColumnFilterValue(pageRequest, "programId");

            int pageNum = pageRequest.getPageNum();
            int pageSize = pageRequest.getPageSize();
            if (pageNum < 1)
                pageNum = 1;
            if (pageSize < 0)
                pageSize = 10;

            if (!StringUtils.isBlank(fromDate) && !StringUtils.isBlank(toDate)) {
                Date from_date = DateTimeUtils.convertStringToDate(fromDate, "dd/MM/yyyy");
                Date to_date = DateTimeUtils.addDay(DateTimeUtils.convertStringToDate(toDate, "dd/MM/yyyy"), 1);
                PageHelper.startPage(pageNum, pageSize);
                List<SysSmsTemplate> sysSmsTemplates = sysSmsTemplateExMapper.findPage(from_date, to_date, group, programId);
                return MybatisPageHelper.getPageResult(pageRequest, new PageInfo(sysSmsTemplates));
            } else {
                PageHelper.startPage(pageNum, pageSize);
                List<SysSmsTemplate> sysSmsTemplates = sysSmsTemplateExMapper.findPageWithOutDate(group, programId);
                return MybatisPageHelper.getPageResult(pageRequest, new PageInfo(sysSmsTemplates));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
        return null;
    }

    @Override
    public int add(SysSmsTemplate sysSmsTemplate, String userName) {
        try {
            SysSmsTemplateExample example = new SysSmsTemplateExample();
            example.createCriteria().andProgramIdEqualTo(sysSmsTemplate.getProgramId()).andSmsTemplateLikeInsensitive(sysSmsTemplate.getSmsTemplate());
            List<SysSmsTemplate> list = sysSmsTemplateMapper.selectByExample(example);
            if (list != null && list.size() > 0)
                return 2;

            sysSmsTemplate.setDelFlag(0);
            int result = sysSmsTemplateMapper.insertSelective(sysSmsTemplate);
            if (result >= 1) {
                SysProgram sysProgram = sysProgramMapper.selectByPrimaryKey(sysSmsTemplate.getProgramId());
                sysLogInfoService.save("Sms template Updated successfully. Program name: " + sysProgram.getProgramName() + ". Sms template: " + sysSmsTemplate.getSmsTemplate(), userName);
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public int update(SysSmsTemplate sysSmsTemplate, String userName) {
        try {
            SysSmsTemplate smsTemplate = sysSmsTemplateMapper.selectByPrimaryKey(sysSmsTemplate.getTemplateId());
            if (smsTemplate != null) {
                List<SysSmsTemplate> list = checkUpdateExist(sysSmsTemplate);
                if (list != null && list.size() > 0)
                    return 2;

                String smsTemp = "";

                if (smsTemplate.getSmsTemplate() == null)
                    smsTemplate.setSmsTemplate("");

                if (!smsTemplate.getSmsTemplate().equals(sysSmsTemplate.getSmsTemplate()))
                    smsTemp = smsTemplate.getSmsTemplate() + " -> " + sysSmsTemplate.getSmsTemplate();

                smsTemplate.setSmsTemplate(sysSmsTemplate.getSmsTemplate());
                int result = sysSmsTemplateMapper.updateByPrimaryKeySelective(smsTemplate);
                if (result >= 1)
                    sysLogInfoService.save("Sms template Updated successfully. Program name: " + sysSmsTemplate.getProgramName() + ". Sms template: " + smsTemp, userName);
                return result;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public int deleteBatch(SysSmsTemplate sysSmsTemplate, String userName) {
        try {
            int result = sysSmsTemplateMapper.deleteByPrimaryKey(sysSmsTemplate.getTemplateId());
            if (result >= 1)
                sysLogInfoService.save("Delete sms template successfully. Sms template: " + sysSmsTemplate.getSmsTemplate(), userName);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<SysSmsTemplate> findByProgramId(Long id) {
        SysSmsTemplateExample example = new SysSmsTemplateExample();
        example.createCriteria().andProgramIdEqualTo(id).andDelFlagEqualTo(0);
        example.setOrderByClause(" CREATE_TIME DESC ");
        return sysSmsTemplateMapper.selectByExample(example);
    }

    private List<SysSmsTemplate> checkUpdateExist(SysSmsTemplate sysSmsTemplate) {
        SysSmsTemplateExample example = new SysSmsTemplateExample();
        example.createCriteria().andDelFlagEqualTo(0).andSmsTemplateLikeInsensitive(sysSmsTemplate.getSmsTemplate()).andTemplateIdNotEqualTo(sysSmsTemplate.getTemplateId());
        return sysSmsTemplateMapper.selectByExample(example);
    }

}
