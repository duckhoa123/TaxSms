package com.vnnet.kpi.web.model;

import java.io.Serializable;
import java.util.Date;

public class BaseThongBaoNoThue extends BaseModel implements Serializable {
    private String maSoThue;//tax number
    private String nguoiNopThue;//tax payer
    private String soThongBao;//announcement number
    private String ngayBanHanh;//approve day
    private long soTienChuaNop;//Amount not yet paid
    private long soTienQuaHan;//Amount overdue
    private String soGiamDoc;//director number
    private String soKeToan;//accountant number
    private String soETax;//eTax number
    private String soQuyetDinh;//decision number
    private String tuNgay;//from date
    private String denNgay;//to date
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

    public String getSoThongBao() {
        return soThongBao;
    }

    public void setSoThongBao(String soThongBao) {
        this.soThongBao = soThongBao;
    }

    public String getNgayBanHanh() {
        return ngayBanHanh;
    }

    public void setNgayBanHanh(String ngayBanHanh) {
        this.ngayBanHanh = ngayBanHanh;
    }

    public long getSoTienChuaNop() {
        return soTienChuaNop;
    }

    public void setSoTienChuaNop(long soTienChuaNop) {
        this.soTienChuaNop = soTienChuaNop;
    }

    public long getSoTienQuaHan() {
        return soTienQuaHan;
    }

    public void setSoTienQuaHan(long soTienQuaHan) {
        this.soTienQuaHan = soTienQuaHan;
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

    public String getSoETax() {
        return soETax;
    }

    public void setSoETax(String soETax) {
        this.soETax = soETax;
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

    public String getSoQuyetDinh() {
        return soQuyetDinh;
    }

    public void setSoQuyetDinh(String soQuyetDinh) {
        this.soQuyetDinh = soQuyetDinh;
    }

    public String getTuNgay() {
        return tuNgay;
    }

    public void setTuNgay(String tuNgay) {
        this.tuNgay = tuNgay;
    }

    public String getDenNgay() {
        return denNgay;
    }

    public void setDenNgay(String denNgay) {
        this.denNgay = denNgay;
    }

    @Override
    public String toString() {
        return "BaseThongBaoNoThue{" +
                "maSoThue='" + maSoThue + '\'' +
                ", nguoiNopThue='" + nguoiNopThue + '\'' +
                ", soThongBao='" + soThongBao + '\'' +
                ", ngayBanHanh='" + ngayBanHanh + '\'' +
                ", soTienChuaNop=" + soTienChuaNop +
                ", soTienQuaHan=" + soTienQuaHan +
                ", soGiamDoc='" + soGiamDoc + '\'' +
                ", soKeToan='" + soKeToan + '\'' +
                ", soETax='" + soETax + '\'' +
                ", rowIndex=" + rowIndex +
                ", error='" + error + '\'' +
                '}';
    }
}
