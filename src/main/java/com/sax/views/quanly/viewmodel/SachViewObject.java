package com.sax.views.quanly.viewmodel;

import com.sax.dtos.SachDTO;
import com.sax.utils.DateUtils;
import com.sax.views.components.table.CellNameRender;
import lombok.Data;

import javax.swing.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class SachViewObject extends AbstractViewObject {
    private String barcode;
    private String hinhAnh;
    private int soLuong;
    private String giaBan;
    private List<String> danhMucList;
    private LocalDateTime ngayThem;
    private boolean trangThai;

    public SachViewObject(SachDTO sachDTO) {
        super(sachDTO.getId(), sachDTO.getTenSach());
        DecimalFormat dm = new DecimalFormat("#,###");
        barcode = sachDTO.getBarCode();
        hinhAnh = sachDTO.getHinhAnh();
        soLuong = sachDTO.getSoLuong();
        giaBan = dm.format(sachDTO.getGiaBan()).replace(",", ".") + "đ";
        danhMucList = sachDTO.getSetDanhMuc().stream().map(i -> i.getTenDanhMuc()).toList();
        ngayThem = sachDTO.getNgayThem();
        trangThai = sachDTO.getTrangThai();
    }

    @Override
    public Object[] toObject(JTable tbl, Set tempIdSet, List<JCheckBox> listCbk) {
        listCbk.add(checkBoxDelete);
        checkBoxDelete.addActionListener((e) -> {
            if (checkBoxDelete.isSelected()) tempIdSet.add(id);
            else tempIdSet.remove(id);
        });
        return new Object[]{checkBoxDelete, id, barcode, new CellNameRender(tbl, hinhAnh, name), soLuong, giaBan, danhMucList, DateUtils.parseString(ngayThem), trangThai ? "Hiển thị" : "Ẩn"};
    }

}