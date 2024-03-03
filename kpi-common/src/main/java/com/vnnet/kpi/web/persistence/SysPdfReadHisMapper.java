package com.vnnet.kpi.web.persistence;

import com.vnnet.kpi.web.model.SysPdfReadHis;
import com.vnnet.kpi.web.model.SysPdfReadHisExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysPdfReadHisMapper {
    long countByExample(SysPdfReadHisExample example);

    int deleteByExample(SysPdfReadHisExample example);

    int insert(SysPdfReadHis record);

    int insertSelective(SysPdfReadHis record);

    List<SysPdfReadHis> selectByExample(SysPdfReadHisExample example);

    int updateByExampleSelective(@Param("record") SysPdfReadHis record, @Param("example") SysPdfReadHisExample example);

    int updateByExample(@Param("record") SysPdfReadHis record, @Param("example") SysPdfReadHisExample example);
}