package com.sax.views.quanly.viewmodel;

import com.sax.dtos.CtkmDTO;
import com.sax.utils.DateUtils;
import lombok.Data;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class CtkmViewObject extends AbstractViewObject {

    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private boolean kieuGiamGia;
    private String trangThai;

    public CtkmViewObject(CtkmDTO ctkmDTO) {
        super(ctkmDTO.getId(), ctkmDTO.getTenSuKien());
        ngayBatDau = ctkmDTO.getNgayBatDau();
        ngayKetThuc = ctkmDTO.getNgayKetThuc();
        kieuGiamGia = ctkmDTO.isKieuGiamGia();
        trangThai = ctkmDTO.getTrangThai();
    }

    @Override
    public Object[] toObject(JTable tbl, Set tempIdSet, List<JCheckBox> setCbk) {
        setCbk.add(checkBoxDelete);
        checkBoxDelete.addActionListener((e) -> {
            if (checkBoxDelete.isSelected()) tempIdSet.add(id);
            else tempIdSet.remove(id);
        });
        return new Object[]{checkBoxDelete, id, name, DateUtils.parseString(ngayBatDau), DateUtils.parseString(ngayKetThuc), kieuGiamGia ? "Phần trăm" : "Số tiền", trangThai};
    }
}