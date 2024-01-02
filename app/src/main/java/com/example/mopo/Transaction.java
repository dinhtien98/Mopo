package com.example.mopo;
import java.util.Date;

public class Transaction {
    private String loaiGiaoDich;
    private String taiKhoanDoiTac;
    private String soTien;
    private String ngayGio;

    // Empty constructor required for Firestore
    public Transaction() {
    }

    public Transaction(String loaiGiaoDich, String taiKhoanDoiTac, String soTien, String ngayGio) {
        this.loaiGiaoDich = loaiGiaoDich;
        this.taiKhoanDoiTac = taiKhoanDoiTac;
        this.soTien = soTien;
        this.ngayGio = ngayGio;
    }

    public String getLoaiGiaoDich() {
        return loaiGiaoDich;
    }

    public String getTaiKhoanDoiTac() {
        return taiKhoanDoiTac;
    }

    public String getSoTien() {
        return soTien;
    }

    public String getNgayGio() {
        return ngayGio;
    }
}
