package com.sax.views.quanly.views.dialogs;

import com.sax.dtos.CtkmDTO;
import com.sax.services.ICtkmService;
import com.sax.services.impl.CtkmService;
import com.sax.utils.ContextUtils;
import com.sax.utils.MsgBox;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.quanly.viewmodel.CtkmViewObject;
import com.sax.views.quanly.views.panes.KhuyenMaiPane;
import org.jdesktop.swingx.JXDatePicker;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

public class CtkmDialog extends JDialog {
    private JPanel contentPane;
    private JTextField txtName;
    private JXDatePicker ngayKetThuc;
    private JXDatePicker ngayBatDau;
    private JButton btnSave;
    private JComboBox cboGioBD;
    private JComboBox cboPhutBD;
    private JComboBox cboGioKT;
    private JComboBox cboPhutKT;
    private JRadioButton rdoPhanTram;
    private JRadioButton rdoSoTien;
    private ICtkmService ctkmService = ContextUtils.getBean(CtkmService.class);

    public JLabel lblTitle;
    public int id;
    public KhuyenMaiPane parentPane;

    public CtkmDialog() {
        btnSave.addActionListener((e) -> save());
        initComponent();
    }

    private void initComponent() {
        setContentPane(contentPane);
        setModal(true);
        pack();
        setLocationRelativeTo(parentPane);
        cboGioBD.setSelectedItem(LocalDateTime.now().getHour() < 10 ? "0" + LocalDateTime.now().getHour() : LocalDateTime.now().getHour() + "");
        cboPhutBD.setSelectedItem(LocalDateTime.now().getMinute() < 10 ? "0" + LocalDateTime.now().getMinute() + 1 : LocalDateTime.now().getMinute() + 1 + "");
        ngayBatDau.setFormats("dd/MM/yyyy");
        ngayBatDau.setDate(new Date());
        ngayKetThuc.setFormats("dd/MM/yyyy");
    }

    public void fillForm() {
        if (id > 0) {
            CtkmDTO ctkmDTO = ctkmService.getById(id);
            lblTitle.setText("Cập nhật CTKM " + ctkmDTO.getId() + " - " + ctkmDTO.getTenSuKien());
            txtName.setText(ctkmDTO.getTenSuKien());
            String hourStartString = String.valueOf(ctkmDTO.getNgayBatDau().getHour());
            String minStartString = String.valueOf(ctkmDTO.getNgayBatDau().getMinute());
            String hourEndString = String.valueOf(ctkmDTO.getNgayKetThuc().getHour());
            String minEndString = String.valueOf(ctkmDTO.getNgayKetThuc().getMinute());
            cboGioBD.setSelectedItem(hourStartString.length() == 1 ? "0" + hourStartString : hourStartString);
            cboPhutBD.setSelectedItem(minStartString.length() == 1 ? "0" + minStartString : minStartString);
            ngayBatDau.setDate(Date.from(ctkmDTO.getNgayBatDau().atZone(ZoneId.systemDefault()).toInstant()));
            cboGioKT.setSelectedItem(hourEndString.length() == 1 ? "0" + hourEndString : hourEndString);
            cboPhutKT.setSelectedItem(minEndString.length() == 1 ? "0" + minEndString : minEndString);
            ngayKetThuc.setDate(Date.from(ctkmDTO.getNgayKetThuc().atZone(ZoneId.systemDefault()).toInstant()));
        }
    }

    private void save() {
        CtkmDTO dto = readForm();
        if (dto != null) {
            try {
                if (id > 0) {
                    dto.setId(id);
                    ctkmService.update(dto);
                } else {
                    ctkmService.insert(dto);
                    parentPane.setPageKMValue(ctkmService.getTotalPage(parentPane.getSizeValue()));
                    parentPane.setPageableKM(PageRequest.of(parentPane.getPageKMValue() - 1, parentPane.getSizeValue()));
                    parentPane.fillListPage();
                }
                parentPane.fillTableKM(ctkmService.getPage(parentPane.getPageableKM()).stream().map(CtkmViewObject::new).collect(Collectors.toList()));
                parentPane.fillCboCtkm();
                dispose();
            } catch (Exception ex) {
                MsgBox.alert(this, "Có lỗi! " + ex.getMessage());
            }
        }
    }

    private CtkmDTO readForm() {
        CtkmDTO ctkmDTO = new CtkmDTO();

        String ten = txtName.getText().trim();
        ctkmDTO.setTenSuKien(ten);

        if (ten.isEmpty()) {
            MsgBox.alert(this, "Tên sự kiên không được để trống!");
            return null;
        }

        String gioBD = cboGioBD.getSelectedItem().toString().trim();
        String phutBD = cboPhutBD.getSelectedItem().toString().trim();

        if (ngayBatDau.getDate() == null) {
            MsgBox.alert(this, "Vui lòng chọn ngày bắt đầu!");
            return null;
        }
        String batDauString = ngayBatDau.getDate().toString().replace("00:00:00", gioBD + ":" + phutBD + ":00");

        String gioKT = cboGioKT.getSelectedItem().toString();
        String phutKT = cboPhutKT.getSelectedItem().toString();

        if (ngayKetThuc.getDate() == null) {
            MsgBox.alert(this, "Vui lòng chọn ngày kết thúc!");
            return null;
        }
        String ketThucString = ngayKetThuc.getDate().toString().replace("00:00:00", gioKT + ":" + phutKT + ":00");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        LocalDateTime lichBD = LocalDateTime.parse(batDauString, formatter);
        LocalDateTime lichKT = LocalDateTime.parse(ketThucString, formatter);

        if (lichBD.compareTo(lichKT) > 1 || lichBD.compareTo(lichKT) == 0) {
            MsgBox.alert(this, "Thời gian kết thúc phải lớn hơn thời gian bắt đầu!");
            return null;
        }
        ctkmDTO.setNgayBatDau(lichBD);
        ctkmDTO.setNgayKetThuc(lichKT);

        boolean type = rdoPhanTram.isSelected() ? true : false;
        ctkmDTO.setKieuGiamGia(type);

        return ctkmDTO;
    }

    private void createUIComponents() {
        btnSave = new ButtonToolItem("", "");
    }
}
