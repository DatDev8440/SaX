package com.sax.views.quanly.viewmodel;

import com.sax.dtos.AccountDTO;
import com.sax.dtos.DonHangDTO;
import com.sax.utils.CurrencyConverter;
import com.sax.utils.DateUtils;
import lombok.Data;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class DonHangViewObject extends AbstractViewObject {
    private AccountDTO maNV;
    private String tongTien;
    private LocalDateTime ngayTao;
    private boolean pttt;
    private String tienHang;
    private String chietKhau;

    public DonHangViewObject(DonHangDTO donHangDTO) {
        super(donHangDTO.getId(), donHangDTO.getKhach().getTenKhach());
        maNV = donHangDTO.getAccount();
        tongTien = CurrencyConverter.parseString(donHangDTO.getTongTien());
        tienHang = CurrencyConverter.parseString(donHangDTO.getTienHang());
        chietKhau = CurrencyConverter.parseString(donHangDTO.getChietKhau());
        ngayTao = donHangDTO.getNgayTao();
        pttt = donHangDTO.getPttt();
    }

    @Override
    public Object[] toObject(JTable tbl, Set tempIdSet, List<JCheckBox> setCbk) {
        setCbk.add(checkBoxDelete);
        checkBoxDelete.addActionListener((e) -> {
            if (checkBoxDelete.isSelected()) tempIdSet.add(id);
            else tempIdSet.remove(id);
        });
        return new Object[]{checkBoxDelete, id, name, maNV.getId() + " " + maNV.getTenNhanVien(), tienHang, chietKhau, tongTien, pttt ? "Tiền mặt" : "Chuyển khoản", DateUtils.parseString(ngayTao)};
    }
    
    public Object[] toObject()
    {
        return new Object[] {checkBoxDelete, id, name, tienHang, chietKhau, tongTien, pttt ? "Tiền mặt" : "Chuyển khoản"};
    }
}