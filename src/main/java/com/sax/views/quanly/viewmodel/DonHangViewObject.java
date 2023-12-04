package com.sax.views.quanly.viewmodel;

import com.sax.dtos.DonHangDTO;
import lombok.Data;

import javax.swing.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@Data
public class DonHangViewObject extends AbstractViewObject {
    private int maNV;
    private String tongTien;
    private LocalDateTime ngayTao;
    private boolean pttt;

    public DonHangViewObject(DonHangDTO donHangDTO) {
        super(donHangDTO.getId(), donHangDTO.getKhach().getTenKhach());
        maNV = donHangDTO.getAccount().getId();
        tongTien = new DecimalFormat("#,###").format(donHangDTO.getTongTien()).replace(",", ".") + "đ";
        ngayTao = donHangDTO.getNgayTao();
        pttt = donHangDTO.getPttt();
    }

    @Override
    public Object[] toObject(ExecutorService executorService, JTable tbl, Set tempIdSet, List<JCheckBox> setCbk) {
        setCbk.add(checkBoxDelete);
        checkBoxDelete.addActionListener((e) -> {
            if (checkBoxDelete.isSelected()) tempIdSet.add(id);
            else tempIdSet.remove(id);
        });
        return new Object[]{checkBoxDelete, id, name, maNV, tongTien, pttt ? "Tiền mặt" : "Chuyển khoản", ngayTao};
    }
}