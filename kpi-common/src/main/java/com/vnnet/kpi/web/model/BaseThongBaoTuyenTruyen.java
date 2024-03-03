package com.vnnet.kpi.web.model;

import java.io.Serializable;

public class BaseThongBaoTuyenTruyen extends BaseModel implements Serializable {
    private String maSoThue;//tax number
    private String nguoiNopThue;//tax payer
    private String soCVHoi;//number of question
    private String ngayHoi;//day of question
    private String soCVTraLoi;//number of answer
    private String ngayTraLoi;//day of answer
    private String soGiamDoc;//director number
    private String soKeToan;//accountant number
    private int rowIndex;
    private String error;

    public String getMaSoThue() {
        return maSoThue;
    }

    public void setMaSoThue(String maSoThue) {
        this.maSoThue = maSoThue;
    }

    public String getNguoiNopThue() {
        return nguoiNopThue;
    }

    public void setNguoiNopThue(String nguoiNopThue) {
        this.nguoiNopThue = nguoiNopThue;
    }

    public String getSoCVHoi() {
        return soCVHoi;
    }

    public void setSoCVHoi(String soCVHoi) {
        this.soCVHoi = soCVHoi;
    }

    public String getNgayHoi() {
        return ngayHoi;
    }

    public void setNgayHoi(String ngayHoi) {
        this.ngayHoi = ngayHoi;
    }

    public String getSoCVTraLoi() {
        return soCVTraLoi;
    }

    public void setSoCVTraLoi(String soCVTraLoi) {
        this.soCVTraLoi = soCVTraLoi;
    }

    public String getNgayTraLoi() {
        return ngayTraLoi;
    }

    public void setNgayTraLoi(String ngayTraLoi) {
        this.ngayTraLoi = ngayTraLoi;
    }

    public String getSoGiamDoc() {
        return soGiamDoc;
    }

    public void setSoGiamDoc(String soGiamDoc) {
        this.soGiamDoc = soGiamDoc;
    }

    public String getSoKeToan() {
        return soKeToan;
    }

    public void setSoKeToan(String soKeToan) {
        this.soKeToan = soKeToan;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "BaseThongBaoTuyenTruyen{" +
                "maSoThue='" + maSoThue + '\'' +
                ", nguoiNopThue='" + nguoiNopThue + '\'' +
                ", soCVHoi='" + soCVHoi + '\'' +
                ", ngayHoi='" + ngayHoi + '\'' +
                ", soCVTraLoi='" + soCVTraLoi + '\'' +
                ", ngayTraLoi='" + ngayTraLoi + '\'' +
                ", soGiamDoc='" + soGiamDoc + '\'' +
                ", soKeToan='" + soKeToan + '\'' +
                ", rowIndex=" + rowIndex +
                ", error='" + error + '\'' +
                '}';
    }
}
