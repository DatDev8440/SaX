package com.sax.views.quanly.viewmodel;

import com.sax.dtos.KhachHangDTO;
import com.sax.utils.DateUtils;
import lombok.Data;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class KhachHangViewObject extends AbstractViewObject {
    private String sdt;
    private int diemTichLuy;
    private LocalDateTime ngayThem;
    private boolean gioiTinh;

    public KhachHangViewObject(KhachHangDTO khachHangDTO) {
        super(khachHangDTO.getId(), khachHangDTO.getTenKhach());
        sdt = khachHangDTO.getSdt();
        diemTichLuy = khachHangDTO.getDiem();
        ngayThem = khachHangDTO.getNgayThem();
        gioiTinh = khachHangDTO.isGioiTinh();
    }

    public String getGioiTinh() {
        if (gioiTinh) return "Nam";
        if (!gioiTinh) return "Nữ";
        return "Không xác định";
    }

    @Override
    public Object[] toObject(JTable tbl, Set tempIdSet, List<JCheckBox> setCbk) {
        setCbk.add(checkBoxDelete);
        checkBoxDelete.addActionListener((e) -> {
            if (checkBoxDelete.isSelected()) tempIdSet.add(id);
            else tempIdSet.remove(id);
        });
        return new Object[]{checkBoxDelete, id, name, diemTichLuy, sdt, getGioiTinh(), DateUtils.parseString(ngayThem)};
    }
}