package com.sax.views.quanly.viewmodel;

import com.sax.dtos.AccountDTO;
import com.sax.utils.DateUtils;
import com.sax.views.components.table.CellNameRender;
import lombok.Data;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class NhanVienViewObject extends AbstractViewObject {
    private String username;
    private String password;
    private String email;
    private String sdt;
    private LocalDateTime ngayThem;
    private boolean gioiTinh;
    private String hinhAnh;
    private boolean trangThai;
    private boolean vaiTro;

    public NhanVienViewObject(AccountDTO accountDTO) {
        super(accountDTO.getId(), accountDTO.getTenNhanVien());
        username = accountDTO.getUsername();
        password = accountDTO.getPassword();
        email = accountDTO.getEmail();
        ngayThem = accountDTO.getNgayDangKi();
        trangThai = accountDTO.getTrangThai();
        vaiTro = accountDTO.isVaiTro();
        gioiTinh = accountDTO.isGioiTinh();
        hinhAnh = accountDTO.getAnh();
        sdt = accountDTO.getSdt();
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
        return new Object[]{checkBoxDelete, id, new CellNameRender(tbl, hinhAnh, name), username, email, sdt, getGioiTinh(), vaiTro ? "Quản lý" : "Nhân viên", DateUtils.parseString(ngayThem), trangThai ? "Đang làm" : "Đã nghỉ"};
    }
}