package com.vnnet.kpi.web.model;

import java.io.Serializable;
import java.util.Date;

public class SysPdfReadHis extends BasePdfReadHis implements Serializable {
    private Long messHdId;

    private String calledNumber;

    private String fileName;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Long getMessHdId() {
        return messHdId;
    }

    public void setMessHdId(Long messHdId) {
        this.messHdId = messHdId;
    }

    public String getCalledNumber() {
        return calledNumber;
    }

    public void setCalledNumber(String calledNumber) {
        this.calledNumber = calledNumber == null ? null : calledNumber.trim();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", messHdId=").append(messHdId);
        sb.append(", calledNumber=").append(calledNumber);
        sb.append(", fileName=").append(fileName);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}