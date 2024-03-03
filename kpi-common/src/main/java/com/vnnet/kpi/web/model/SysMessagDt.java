package com.vnnet.kpi.web.model;

import java.io.Serializable;

public class SysMessagDt extends BaseMessagDt implements Serializable {
    private Long messDtId;

    private Long messHdId;

    private String taxCode;

    private String corName;

    private String calledNumber;

    private String smsContent;

    private Byte status;

    private String sendResult;

    private static final long serialVersionUID = 1L;

    public Long getMessDtId() {
        return messDtId;
    }

    public void setMessDtId(Long messDtId) {
        this.messDtId = messDtId;
    }

    public Long getMessHdId() {
        return messHdId;
    }

    public void setMessHdId(Long messHdId) {
        this.messHdId = messHdId;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode == null ? null : taxCode.trim();
    }

    public String getCorName() {
        return corName;
    }

    public void setCorName(String corName) {
        this.corName = corName == null ? null : corName.trim();
    }

    public String getCalledNumber() {
        return calledNumber;
    }

    public void setCalledNumber(String calledNumber) {
        this.calledNumber = calledNumber == null ? null : calledNumber.trim();
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent == null ? null : smsContent.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getSendResult() {
        return sendResult;
    }

    public void setSendResult(String sendResult) {
        this.sendResult = sendResult == null ? null : sendResult.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", messDtId=").append(messDtId);
        sb.append(", messHdId=").append(messHdId);
        sb.append(", taxCode=").append(taxCode);
        sb.append(", corName=").append(corName);
        sb.append(", calledNumber=").append(calledNumber);
        sb.append(", smsContent=").append(smsContent);
        sb.append(", status=").append(status);
        sb.append(", sendResult=").append(sendResult);
        sb.append("]");
        return sb.toString();
    }
}