package com.sax.views.quanly.viewmodel;

import com.sax.dtos.CtkmDTO;
import lombok.Data;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class CtkmViewObject extends AbstractViewObject {
    public enum TrangThai {
        DANG_DIEN_RA, DA_KET_THUC, CHUA_BAT_DAU;
    }


    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private boolean kieuGiamGia;

    public CtkmViewObject(CtkmDTO ctkmDTO) {
        super(ctkmDTO.getId(), ctkmDTO.getTenSuKien());
        ngayBatDau = ctkmDTO.getNgayBatDau();
        ngayKetThuc = ctkmDTO.getNgayKetThuc();
        kieuGiamGia = ctkmDTO.isKieuGiamGia();
    }

    public String getTrangThai() {
        if (LocalDateTime.now().isAfter(ngayBatDau) && LocalDateTime.now().isBefore(ngayKetThuc)) return "Đang diễn ra";
        else if (LocalDateTime.now().isAfter(ngayKetThuc)) return "Đã kết thúc";
        return "Chưa bắt đầu";
    }

    @Override
    public Object[] toObject(JTable tbl, Set tempIdSet, List<JCheckBox> setCbk) {
        setCbk.add(checkBoxDelete);
        checkBoxDelete.addActionListener((e) -> {
            if (checkBoxDelete.isSelected()) tempIdSet.add(id);
            else tempIdSet.remove(id);
        });
        return new Object[]{checkBoxDelete, id, name, ngayBatDau, ngayKetThuc, kieuGiamGia ? "Phần trăm" : "Số tiền", getTrangThai()};
    }

    @Override
    public Object[] toObject() {
        return new Object[0];
    }
}