package com.vnnet.kpi.web.model;

import java.util.Date;

public class BaseMessagHd extends BaseModel {

    private long smsTotal;
    private Date currDate;
    private long corTotal;
    private long sendSuccess;
    private long sendFail;
    private boolean createShortLink;
    private long corsuccess;
    private long corfail;
    private long corall;


    public long getSmsTotal() {
        return smsTotal;
    }

    public void setSmsTotal(long smsTotal) {
        this.smsTotal = smsTotal;
    }

    public Date getCurrDate() {
        return currDate;
    }

    public void setCurrDate(Date currDate) {
        this.currDate = currDate;
    }

    public long getCorTotal() {
        return corTotal;
    }

    public void setCorTotal(long corTotal) {
        this.corTotal = corTotal;
    }

    public long getSendSuccess() {
        return sendSuccess;
    }

    public void setSendSuccess(long sendSuccess) {
        this.sendSuccess = sendSuccess;
    }

    public long getSendFail() {
        return sendFail;
    }

    public void setSendFail(long sendFail) {
        this.sendFail = sendFail;
    }
    public long getCorsuccess() {
        return corsuccess;
    }

    public void setCorsuccess(long corsuccess) {
        this.corsuccess = corsuccess;
    }
    public long getCorfail() {
        return corfail;
    }

    public void setCorfail(long corfail) {
        this.corfail = corfail;
    }
    public long getCorall() {
        return corall;
    }

    public void setCorall(long corall) {
        this.corall = corall;
    }


    public boolean getCreateShortLink() {
        return createShortLink;
    }

    public void setCreateShortLink(boolean createShortLink) {
        this.createShortLink = createShortLink;
    }
}
