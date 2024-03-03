package com.vnnet.kpi.web.model;

import java.io.Serializable;
import java.util.Date;

public class SysMessagHd extends BaseMessagHd implements Serializable {
    private Long messHdId;

    private String title;

    private Date schedulerDateTime;

    private String smsTemplate;

    private String createBy;

    private Date createTime;

    private String lastUpdateBy;

    private Date lastUpdateTime;

    private Long programId;

    private String detailFile;

    private String contactFile;

    private Byte delFlag;

    private String delReason;
    private Date schedulerDetailDateTime;

    private String pdfFile;
    private String taxCode;
    private String corName;
    private String status;
    private String sdt1;
    private String sdt2;
    private Long stt;

    private static final long serialVersionUID = 1L;

    public Long getMessHdId() {
        return messHdId;
    }

    public void setMessHdId(Long messHdId) {
        this.messHdId = messHdId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public Date getSchedulerDateTime() {
        return schedulerDateTime;
    }

    public void setSchedulerDateTime(Date schedulerDateTime) {
        this.schedulerDateTime = schedulerDateTime;
    }

    public String getSmsTemplate() {
        return smsTemplate;
    }

    public void setSmsTemplate(String smsTemplate) {
        this.smsTemplate = smsTemplate == null ? null : smsTemplate.trim();
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy == null ? null : lastUpdateBy.trim();
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public String getDetailFile() {
        return detailFile;
    }

    public void setDetailFile(String detailFile) {
        this.detailFile = detailFile == null ? null : detailFile.trim();
    }

    public String getContactFile() {
        return contactFile;
    }

    public void setContactFile(String contactFile) {
        this.contactFile = contactFile == null ? null : contactFile.trim();
    }

    public Byte getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Byte delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelReason() {
        return delReason;
    }

    public void setDelReason(String delReason) {
        this.delReason = delReason == null ? null : delReason.trim();
    }

    public String getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(String pdfFile) {
        this.pdfFile = pdfFile == null ? null : pdfFile.trim();
    }
    public Date getSchedulerDetailDateTime() {
        return schedulerDetailDateTime;
    }

    public void setSchedulerDetailDateTime(Date schedulerDetailDateTime) {
        this.schedulerDetailDateTime = schedulerDetailDateTime;
    }
    public  String getTaxcode(){return taxCode;}
    public void setTaxCode(String  taxCode) {
        this.taxCode = taxCode;
    }
    public  String getCorname(){return corName;}
    public void setCorname(String  corName) {
        this.corName = corName;
    }
    public  String getStatus(){return status;}
    public void status(String  status) {
        this.status = status;
    }
    public  String getsdt1(){return sdt1;}
    public void setsdt1(String  sdt1) {
        this.sdt1 = sdt1;
    }
    public  Long getstt(){return stt;}
    public void setstt(Long  stt) {
        this.stt = stt;
    }
    public  String getsdt2(){return sdt2;}
    public void setsdt2(String  sdt2) {
        this.sdt2 = sdt2;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", messHdId=").append(messHdId);
        sb.append(", title=").append(title);
        sb.append(", schedulerDateTime=").append(schedulerDateTime);
        sb.append(", schedulerDetailDateTime=").append(schedulerDetailDateTime);
        sb.append(", smsTemplate=").append(smsTemplate);
        sb.append(", createBy=").append(createBy);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateBy=").append(lastUpdateBy);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append(", programId=").append(programId);
        sb.append(", detailFile=").append(detailFile);
        sb.append(", contactFile=").append(contactFile);
        sb.append(", delFlag=").append(delFlag);
        sb.append(", delReason=").append(delReason);
        sb.append(", pdfFile=").append(pdfFile);
        sb.append(", taxCode=").append(taxCode);
        sb.append(", corName=").append(corName);
        sb.append(", status=").append(status);
        sb.append(", sdt1=").append(sdt1);
        sb.append(", sdt2=").append(sdt2);
        sb.append(", stt=").append(stt);
        sb.append("]");
        return sb.toString();
    }
}