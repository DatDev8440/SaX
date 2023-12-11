package com.sax.views.quanly.viewmodel;

import com.sax.dtos.DanhMucDTO;
import lombok.Data;

import javax.swing.*;
import java.util.List;
import java.util.Set;

@Data
public class DanhMucViewObject extends AbstractViewObject {
    private String moTa;
    private String tenDanhMucCha;

    public DanhMucViewObject(DanhMucDTO danhMucDTO) {
        super(danhMucDTO.getId(), danhMucDTO.getTenDanhMuc());
        moTa = danhMucDTO.getGhiChu();
        if (danhMucDTO.getDanhMucCha() != null)
            tenDanhMucCha = danhMucDTO.getDanhMucCha().getTenDanhMuc();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object[] toObject(JTable tbl, Set tempIdSet, List<JCheckBox> setCbk) {
        setCbk.add(checkBoxDelete);
        checkBoxDelete.addActionListener((e) -> {
            if (checkBoxDelete.isSelected()) tempIdSet.add(id);
            else tempIdSet.remove(id);
        });
        return new Object[]{checkBoxDelete, id, name, moTa, tenDanhMucCha};
    }
}
