package com.vnnet.kpi.web.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.config.Constants;
import com.vnnet.kpi.web.model.SysLogInfo;
import com.vnnet.kpi.web.model.SysPdfReadHis;
import com.vnnet.kpi.web.model.SysUser;
import com.vnnet.kpi.web.persistence.SysLogInfoExMapper;
import com.vnnet.kpi.web.persistence.SysLogInfoMapper;
import com.vnnet.kpi.web.persistence.SysPdfReadHisMapper;
import com.vnnet.kpi.web.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


@Service
public class SysPdfReadHisServiceImpl implements SysPdfReadHisService {

    @Autowired
    private SysPdfReadHisMapper sysPdfReadHisMapper;

    @Override
    public int save(Long messHdId, String calledNumber, String fileName) {
        SysPdfReadHis sysPdfReadHis = new SysPdfReadHis();
        sysPdfReadHis.setMessHdId(messHdId);
        sysPdfReadHis.setCalledNumber(calledNumber);
        sysPdfReadHis.setFileName(fileName);
        sysPdfReadHis.setCreateTime(new Date());
        return sysPdfReadHisMapper.insertSelective(sysPdfReadHis);
    }

}
