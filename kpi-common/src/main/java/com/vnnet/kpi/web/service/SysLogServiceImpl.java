package com.vnnet.kpi.web.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.model.SysLog;
import com.vnnet.kpi.web.model.SysLogExample;
import com.vnnet.kpi.web.model.SysUser;
import com.vnnet.kpi.web.model.SysUserExample;
import com.vnnet.kpi.web.persistence.SysLogExMapper;
import com.vnnet.kpi.web.persistence.SysLogMapper;
import com.vnnet.kpi.web.utils.MybatisPageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysLogMapper sysLogMapper;
    @Autowired
    private SysLogExMapper sysLogExMapper;

    @Override
    public int save(SysLog record) {
        if (record.getId() == null || record.getId() == 0) {
            return sysLogMapper.insertSelective(record);
        }
        return sysLogMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int delete(SysLog record) {
        return sysLogMapper.deleteByPrimaryKey(record.getId());
    }

    @Override
    public int delete(List<SysLog> records) {
        List<Long> ids = new ArrayList<>();
        for (SysLog record : records) {
            ids.add(record.getId());
        }
        SysLogExample example = new SysLogExample();
        example.createCriteria().andIdIn(ids);
        sysLogMapper.deleteByExample(example);
        return 1;
    }

    @Override
    public SysLog findById(Long id) {
        return sysLogMapper.selectByPrimaryKey(id);
    }

    private SysLogExample createColumnFilter(PageRequest pageRequest) {
        SysLogExample example = new SysLogExample();
        SysLogExample.Criteria criteria = example.createCriteria();
        String p = MybatisPageHelper.getColumnFilterValue(pageRequest, "userName");
        if (p != null) {
            criteria.andUserNameEqualTo(p);
        }


        return example;
    }

    @Override
    public PageResult findPage(PageRequest pageRequest) {
        PageResult pageResult = MybatisPageHelper.findPage(pageRequest, sysLogMapper, "selectByExample", createColumnFilter(pageRequest));
        return pageResult;
    }

    @Override
    public PageResult findPageC2C(PageRequest pageRequest2, String userName) {
        String filterText = MybatisPageHelper.getColumnFilterValue(pageRequest2, "filterText");
        if (filterText == null)
            filterText = "";

        int pageNum = pageRequest2.getPageNum();
        int pageSize = pageRequest2.getPageSize();
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 0) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<SysLog> sysLogs = sysLogExMapper.selectByExampleNewC2C(filterText, userName);
        PageResult pageResult = MybatisPageHelper.getPageResult(pageRequest2, new PageInfo((List) sysLogs));
        return pageResult;
    }

    @Override
    public PageResult findPageKPI(PageRequest pageRequest2, String userName) {
        String filterText = MybatisPageHelper.getColumnFilterValue(pageRequest2, "filterText");
        if (filterText == null)
            filterText = "";

        int pageNum = pageRequest2.getPageNum();
        int pageSize = pageRequest2.getPageSize();
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 0) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<SysLog> sysLogs = sysLogExMapper.selectByExampleNewKPI(filterText, userName);
        PageResult pageResult = MybatisPageHelper.getPageResult(pageRequest2, new PageInfo((List) sysLogs));
        return pageResult;
    }

}
