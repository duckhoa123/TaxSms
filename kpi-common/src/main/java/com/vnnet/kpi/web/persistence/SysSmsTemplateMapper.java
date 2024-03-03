package com.vnnet.kpi.web.persistence;

import com.vnnet.kpi.web.model.SysSmsTemplate;
import com.vnnet.kpi.web.model.SysSmsTemplateExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysSmsTemplateMapper {
    long countByExample(SysSmsTemplateExample example);

    int deleteByExample(SysSmsTemplateExample example);

    int deleteByPrimaryKey(Integer templateId);

    int insert(SysSmsTemplate record);

    int insertSelective(SysSmsTemplate record);

    List<SysSmsTemplate> selectByExample(SysSmsTemplateExample example);

    SysSmsTemplate selectByPrimaryKey(Integer templateId);

    int updateByExampleSelective(@Param("record") SysSmsTemplate record, @Param("example") SysSmsTemplateExample example);

    int updateByExample(@Param("record") SysSmsTemplate record, @Param("example") SysSmsTemplateExample example);

    int updateByPrimaryKeySelective(SysSmsTemplate record);

    int updateByPrimaryKey(SysSmsTemplate record);
}