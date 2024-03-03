package com.vnnet.kpi.web.model;

import java.io.Serializable;

public class BaseThongBaoKeKhai extends BaseModel implements Serializable {
    private String maSoThue;//tax number
    private String nguoiNopThue;//taxpayer
    private String tieuMuc;//subsection
    private long soTien;//amount
    private String ngayNop;//tax filling day
    private String soGiamDoc;//director phone number
    private String soKeToan;//accountant phone number
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

    public String getTieuMuc() {
        return tieuMuc;
    }

    public void setTieuMuc(String tieuMuc) {
        this.tieuMuc = tieuMuc;
    }

    public long getSoTien() {
        return soTien;
    }

    public void setSoTien(long soTien) {
        this.soTien = soTien;
    }

    public String getNgayNop() {
        return ngayNop;
    }

    public void setNgayNop(String ngayNop) {
        this.ngayNop = ngayNop;
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
        return "BaseThongBaoKeKhai{" +
                "maSoThue='" + maSoThue + '\'' +
                ", nguoiNopThue='" + nguoiNopThue + '\'' +
                ", tieuMuc=" + tieuMuc +
                ", soTien=" + soTien +
                ", ngayNop='" + ngayNop + '\'' +
                ", soGiamDoc='" + soGiamDoc + '\'' +
                ", soKeToan='" + soKeToan + '\'' +
                ", rowIndex=" + rowIndex +
                ", error='" + error + '\'' +
                '}';
    }
}
