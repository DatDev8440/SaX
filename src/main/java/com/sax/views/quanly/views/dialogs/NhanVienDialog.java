package com.sax.views.quanly.views.dialogs;

import com.sax.dtos.AccountDTO;
import com.sax.services.IAccountService;
import com.sax.services.impl.AccountService;
import com.sax.utils.*;
import com.sax.views.components.Loading;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.quanly.viewmodel.NhanVienViewObject;
import com.sax.views.quanly.views.panes.NhanVienPane;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class NhanVienDialog extends JDialog {
    @Getter
    private JTextField txtName;
    @Getter
    private JTextField txtSdt;
    @Getter
    private JTextField txtEmail;
    @Getter
    private JButton btnSave;
    private JPanel contentPane;
    @Getter
    private JRadioButton rdoNu;
    @Getter
    private JRadioButton rdoNam;
    private JRadioButton rdoNhanVien;
    private JRadioButton rdoQuanLy;
    @Getter
    private JButton btnImg;
    private final IAccountService accountService = ContextUtils.getBean(AccountService.class);
    private JPanel pnImage;
    private String image;
    private JRadioButton rdoDL;
    private JRadioButton rdoDN;

    @Getter
    @Setter
    private JPanel panelTT;

    public JLabel lblTitle;
    private JTextField txtTK;
    @Getter
    private JPanel panelRole;
    public int id;
    public NhanVienPane parentPane;

    public NhanVienDialog() {
        initComponent();
        btnSave.addActionListener((e) -> update());
        btnImg.addActionListener(e -> image = ImageUtils.openImageFile(pnImage));
    }

    private void initComponent() {
        setContentPane(contentPane);
        setModal(true);
        pack();
        setLocationRelativeTo(parentPane);
    }

    public void fillForm() {
        if (id > 0) {
            AccountDTO accountDTO = accountService.getById(id);
            lblTitle.setText("Thông tin nhân viên " + accountDTO.getTenNhanVien());
            txtTK.setText(accountDTO.getUsername());
            txtEmail.setText(accountDTO.getEmail());
            txtName.setText(accountDTO.getTenNhanVien());
            txtSdt.setText(accountDTO.getSdt());
            if (accountDTO.isVaiTro()) rdoQuanLy.setSelected(true);
            else rdoNhanVien.setSelected(true);
            if (accountDTO.isGioiTinh()) rdoNam.setSelected(true);
            else rdoNu.setSelected(true);
            if (accountDTO.getTrangThai()) rdoDL.setSelected(true);
            else rdoDN.setSelected(true);
            image = "images/" + accountDTO.getAnh();
            pnImage.add(ImageUtils.getCircleImage(accountDTO.getAnh(), 200, 20, null, 0));
        }
    }

    private void update() {
        AccountDTO dto = readForm();
           if (dto != null) {
               try {
                   dto.setId(id);
                   accountService.update(dto);
                   if (Session.accountid.getId() == id) {
                       AccountDTO ac = accountService.getById(id);
                       Session.lblName.setText(ac.getTenNhanVien());
                       Session.avatar.removeAll();
                       Session.avatar.add(ImageUtils.getCircleImage(ac.getAnh(), 30, 20, null, 0));
                       Session.avatar.revalidate();
                   }
                   if (parentPane != null)
                       parentPane.fillTable(accountService.getPage(parentPane.getPageable()).stream().map(NhanVienViewObject::new).collect(Collectors.toList()));
                   Session.reload();
                   dispose();
               } catch (Exception ex) {
                   MsgBox.alert(this, "Có lỗi! " + ex.getMessage());
               }
           }
    }

    private AccountDTO readForm() {
        AccountDTO accountDTO = new AccountDTO();
        String ten = txtName.getText().trim();
        accountDTO.setTenNhanVien(ten);
        String sdt = txtSdt.getText().trim();
        try {
            accountDTO.setSdt(Integer.parseInt(sdt) + "");
        } catch (NumberFormatException e) {
            MsgBox.alert(this, "Số điện thoại phải là số!");
            return null;
        }
        String taiKhoan = txtTK.getText().trim();

        if (taiKhoan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tài khoản không được để trống!");
            return null;
        }
        if (taiKhoan.length() < 6) {
            JOptionPane.showMessageDialog(this, "Tài khoản phải it nhất 6 ký tự!");
            return null;
        }
        accountDTO.setUsername(taiKhoan);
        String email = txtEmail.getText().trim();
        accountDTO.setEmail(email);
        boolean gioiTinh = rdoNam.isSelected() ? true : false;
        accountDTO.setGioiTinh(gioiTinh);
        boolean vaiTro = rdoQuanLy.isSelected() ? true : false;
        accountDTO.setVaiTro(vaiTro);
        boolean trangThai = rdoDL.isSelected() ? true : false;
        accountDTO.setTrangThai(trangThai);
        accountDTO.setAnh(image);

        return accountDTO;
    }

    private void createUIComponents() {
        btnImg = new ButtonToolItem("image-c.svg", "image-c.svg");
    }
}
