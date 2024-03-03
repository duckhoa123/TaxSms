package com.vnnet.kpi.web.service;

import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.model.SysConfig;
import com.vnnet.kpi.web.model.SysConfigExample;
import com.vnnet.kpi.web.persistence.SysConfigMapper;
import com.vnnet.kpi.web.utils.MybatisPageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class SysConfigServiceImpl implements SysConfigService {

    private static final Logger logger = LoggerFactory.getLogger(SysConfigServiceImpl.class);

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public int save(SysConfig record) {
        if (record.getId() == null || record.getId() == 0) {
            return sysConfigMapper.insertSelective(record);
        }
        return sysConfigMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int delete(SysConfig record) {
        return sysConfigMapper.deleteByPrimaryKey(record.getId());
    }

    @Override
    public int delete(List<SysConfig> records) {
        List<Long> ids = new ArrayList<>();
        for (SysConfig record : records) {
            ids.add(record.getId());
        }
        SysConfigExample example = new SysConfigExample();
        example.createCriteria().andIdIn(ids);
        sysConfigMapper.deleteByExample(example);
        return 1;
    }

    @Override
    public SysConfig findById(Long id) {
        return sysConfigMapper.selectByPrimaryKey(id);
    }

    @Override
    public SysConfig findByKey(String key) {
        SysConfigExample example = new SysConfigExample();
        example.createCriteria().andConfigKeyEqualTo(key);
        List<SysConfig> sysConfigs = sysConfigMapper.selectByExample(example);
        if (sysConfigs != null && !sysConfigs.isEmpty()) {
            return sysConfigs.get(0);
        }
        return null;
    }


    private SysConfigExample createColumnFilter(PageRequest pageRequest) {
        SysConfigExample example = new SysConfigExample();
        SysConfigExample.Criteria criteria = example.createCriteria();
        String p = MybatisPageHelper.getColumnFilterValue(pageRequest, "configKey");
        if (p != null) {
            criteria.andConfigKeyEqualTo(p);
        }
        return example;
    }

    @Override
    public PageResult selectPage(PageRequest pageRequest) {
        PageResult pageResult = MybatisPageHelper.findPage(pageRequest, sysConfigMapper, "selectByExample", createColumnFilter(pageRequest));
        return pageResult;
    }


    @Override
    public List<SysConfig> selectAll() {
        SysConfigExample sysConfigExample = new SysConfigExample();
        List<SysConfig> configs = sysConfigMapper.selectByExample(sysConfigExample);
        return configs;
    }

}
