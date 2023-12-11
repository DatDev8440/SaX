package com.sax.views.nhanvien.dialog;

import com.sax.dtos.AccountDTO;
import com.sax.services.IAccountService;
import com.sax.services.impl.AccountService;
import com.sax.utils.ContextUtils;
import com.sax.utils.HashUtils;
import com.sax.utils.MsgBox;
import com.sax.utils.Session;
import com.sax.views.quanly.viewmodel.NhanVienViewObject;
import com.sax.views.quanly.views.panes.NhanVienPane;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;

import javax.swing.*;
import java.util.stream.Collectors;

public class TaiKhoanDialog extends JDialog {
    private JPanel contentPane;
    private JLabel lblTitle;
    private JButton btnSave;
    private JTextField txtTK;
    private JPasswordField txtMK2;
    private JPasswordField txtMK1;
    private JLabel lblEmail;
    private JPasswordField txtOldPass;
    private JButton buttonOK;
    public NhanVienPane parentPane;
    private IAccountService accountService = ContextUtils.getBean(AccountService.class);
    private int id = Session.accountid.getId();



    public TaiKhoanDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        initComponent();

    }
    private void initComponent() {
        setContentPane(contentPane);
        setModal(true);
        btnSave.addActionListener((e) -> save());
        fillForm();
        pack();
        setLocationRelativeTo(parentPane);
    }
    public void fillForm() {
        AccountDTO accountDTO = accountService.getById(id);
        txtTK.setText(accountDTO.getUsername());
        pack();
    }

    private void save() {
        AccountDTO account = readForm();
        if (account != null) {
            try {
                if (id > 0) {
                    account.setId(id);
                    accountService.updateUsernamePassword(account);
                }
                dispose();
            } catch (Exception e) {
                MsgBox.alert(parentPane, "Có lỗi: " + e.getMessage());
            }
        }
    }

    private AccountDTO readForm() {
        AccountDTO accountDTO = new AccountDTO();

        String mk1 = new String(txtMK1.getText().trim());
        if (mk1.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống!");
            return null;
        }

        String mk2 = new String(txtMK2.getText().trim());
        if (mk2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập lại mật khẩu không được để trống!");
            return null;
        }
        if (!mk1.equals(mk2)) {
            JOptionPane.showMessageDialog(this, "Nhập lại mật khẩu không khớp với mật khẩu!");
            return null;
        }
        String oldPass = new String(txtOldPass.getText().trim());
        if (!HashUtils.checkPassword(oldPass, Session.accountid.getPassword())) {
            JOptionPane.showMessageDialog(this, "Không trùng mật khẩu cũ!");
            return null;
        }

        accountDTO.setPassword(mk1);
        return accountDTO;
    }
}
