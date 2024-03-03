package com.vnnet.kpi.web.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.config.Constants;
import com.vnnet.kpi.web.model.*;
import com.vnnet.kpi.web.persistence.SysLogExMapper;
import com.vnnet.kpi.web.persistence.SysLogInfoExMapper;
import com.vnnet.kpi.web.persistence.SysLogInfoMapper;
import com.vnnet.kpi.web.persistence.SysLogMapper;
import com.vnnet.kpi.web.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class SysLogInfoServiceImpl implements SysLogInfoService {

    @Autowired
    private SysLogInfoMapper sysLogInfoMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysLogInfoExMapper sysLogInfoExMapper;


    @Override
    public int save(String operation, String userName) {
        SysLogInfo sysLogInfo = new SysLogInfo();
        sysLogInfo.setUserName(userName);
        sysLogInfo.setOperation(operation);
        HttpServletRequest request = HttpUtils.getHttpServletRequest();
        sysLogInfo.setIp(IPUtils.getIpAddr(request));
        return sysLogInfoMapper.insertSelective(sysLogInfo);
    }

    @Override
    public PageResult findPage(PageRequest pageRequest, String userName) {
        try {
            SysUser sysUser = sysUserService.findByNameNew(userName);
            if (sysUser == null)
                return null;
            if (sysUser.getUserTypeId() == Constants.USER_TYPE_ADMIN)
                userName = "";

            String fromDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "fromDate");
            String toDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "toDate");
            String title = MybatisPageHelper.getColumnFilterValue(pageRequest, "title");
            String userInput = MybatisPageHelper.getColumnFilterValue(pageRequest, "userName");
            title = title == null ? "" : title;
            userInput = userInput == null ? "" : userInput;

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
                List<SysLogInfo> sysLogInfos = sysLogInfoExMapper.findPage(from_date, to_date, title, userName, userInput);
                return MybatisPageHelper.getPageResult(pageRequest, new PageInfo(sysLogInfos));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
