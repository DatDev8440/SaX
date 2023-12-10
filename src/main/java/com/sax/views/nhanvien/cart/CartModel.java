package com.sax.views.nhanvien.cart;

import com.formdev.flatlaf.ui.FlatBorder;
import com.formdev.flatlaf.ui.FlatButtonBorder;
import com.sax.dtos.SachDTO;
import com.sax.utils.AudioUtils;
import com.sax.utils.Cart;
import com.sax.utils.Session;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.nhanvien.NhanVienView;
import lombok.Getter;
import lombok.Setter;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import java.awt.*;

@Getter
@Setter
public class CartModel {
    private int id;
    private String icon;
    private String name;
    private CustomSpinner soLuong;
    private long donGia;
    private long giaBan;
    private JButton xoa;

    public CartModel(SachDTO sachDTO) {
        id = sachDTO.getId();
        icon = sachDTO.getHinhAnh();
        name = sachDTO.getTenSach();
        donGia = sachDTO.getGiaBan() - sachDTO.getGiaGiam();
        giaBan = sachDTO.getGiaBan();
        soLuong = new CustomSpinner();
        xoa = new ButtonToolItem("x-c.svg", "x-c.svg");

        FlatBorder flatBorder = new FlatButtonBorder() {
            @Override
            protected boolean isCellEditor(Component c) {
                return false;
            }
        };
//
        soLuong.setBorder(flatBorder);
        soLuong.setCursor(new Cursor(Cursor.HAND_CURSOR));
        soLuong.addChangeListener((e) -> {
            Session.executorService.submit(() -> AudioUtils.playAudio("beep.wav"));
            NhanVienView.nvv.tinhTien();
        });

        xoa.setBorder(flatBorder);
        xoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        xoa.addActionListener((e) -> {
            Session.executorService.submit(() -> AudioUtils.playAudio("beep.wav"));
            Cart.getCart().remove(this);
            NhanVienView.nvv.tinhTien();
        });
    }
}
