package com.sax.views.quanly.views.dialogs;

import com.sax.dtos.AccountDTO;
import com.sax.services.IAccountService;
import com.sax.services.impl.AccountService;
import com.sax.utils.ContextUtils;
import com.sax.utils.MsgBox;
import com.sax.views.quanly.viewmodel.NhanVienViewObject;
import com.sax.views.quanly.views.panes.NhanVienPane;
import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;

import javax.swing.*;
import java.util.stream.Collectors;

public class TaiKhoanDialog extends JDialog {
    private JTextField txtTK;
    private JPasswordField txtMK1;
    private JLabel lblEmail;
    private JButton btnSave;
    private JPasswordField txtMK2;
    private JPanel contentPane;
    private IAccountService accountService = ContextUtils.getBean(AccountService.class);

    public JLabel lblTitle;
    public NhanVienPane parentPane;
    public int id;

    public TaiKhoanDialog() {
        initComponent();
        btnSave.addActionListener((e) -> save());
    }

    private void initComponent() {
        setContentPane(contentPane);
        setModal(true);
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
                } else {
                    accountService.createAccount(account);
                    parentPane.setPageValue(accountService.getTotalPage(parentPane.getSizeValue()));
                    parentPane.setPageable(PageRequest.of(parentPane.getPageValue() - 1, parentPane.getSizeValue()));
                    parentPane.fillListPage();
                }
                parentPane.fillTable(accountService.getPage(parentPane.getPageable()).stream().map(NhanVienViewObject::new).collect(Collectors.toList()));
                dispose();
            } catch (DataIntegrityViolationException e) {
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
        accountDTO.setPassword(mk1);
        return accountDTO;
    }
}
