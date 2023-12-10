package com.sax.views.nhanvien.doncho;

import com.sax.dtos.KhachHangDTO;
import com.sax.views.nhanvien.cart.CartModel;
import lombok.Data;

import javax.swing.*;
import java.util.List;

@Data
public class DonChoViewObject {
    private int id;
    private KhachHangDTO khachHang;
    private List<CartModel> listCart;
    private String tienHang;
    private boolean diem;

    public DonChoViewObject() {
    }

    public DonChoViewObject(int id, KhachHangDTO tenKhachHang, List<CartModel> listCart, String tienHang, boolean diem) {
        this.id = id;
        this.khachHang = tenKhachHang;
        this.listCart = listCart;
        this.tienHang = tienHang;
        this.diem = diem;
    }

    public Object[] toObject() {
        return new Object[]{id, khachHang, listCart.size() + " sản phẩm", tienHang, diem ? "Có" : "Không"};
    }
}
