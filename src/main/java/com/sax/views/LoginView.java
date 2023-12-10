package com.sax.views;

import com.sax.Application;
import com.sax.dtos.AccountDTO;
import com.sax.services.IAccountService;
import com.sax.utils.*;
import com.sax.views.components.Loading;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.components.libraries.CurvesPanel;
import com.sax.views.nhanvien.NhanVienView;
import com.sax.views.quanly.views.QuanLyView;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginView extends CurvesPanel {
    private JPanel p;
    private JPasswordField txtPass;
    private JCheckBox chkRemember;
    private JLabel lblForgot;
    private JButton btnLogin;
    private JTextField txtUsername;
    private AccountDTO accountDTO;
    private IAccountService accountService = ContextUtils.getBean(IAccountService.class);

    public LoginView() {
        super(0);

        KeyAdapter submit = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    readForm();
                }
            }
        };

        txtUsername.addKeyListener(submit);
        txtPass.addKeyListener(submit);
        btnLogin.addActionListener(e -> login());

        btnLogin.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });
        txtPass.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });
        lblForgot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openQuenMatKhau();
            }
        });
        try {
            AccountDTO accountDTO1 = AccountUtils.retrieve();
            txtUsername.setText(accountDTO1.getUsername());
            txtPass.setText(accountDTO1.getPassword());
            chkRemember.setSelected(true);
        } catch (IOException e) {
//            txtUsername.setText("");
//            txtPass.setText("");
        }
        initComponent();
    }

    private void initComponent() {
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkRemember.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));

    }

    private AccountDTO readForm() {
        accountDTO = new AccountDTO();
        accountDTO.setUsername(txtUsername.getText());
        accountDTO.setPassword(new String(txtPass.getPassword()));
        return accountDTO;
    }

    private void login() {
        Loading loading = new Loading(this);
        Session.executorService.submit(() -> {
            try {
                String pass = new String(txtPass.getPassword());
                if (!txtUsername.getText().trim().isEmpty() || !pass.trim().isEmpty()) {
                    AccountDTO dto;

                    dto = Session.isValidEmail(txtUsername.getText())
                            ? accountService.getByEmail(readForm().getUsername())
                            : accountService.getByUsername(readForm().getUsername());
                    if (HashUtils.checkPassword(readForm().getPassword(), dto.getPassword())) {
                        Session.accountid = dto;
                        if (Session.accountid.getTrangThai()) {
                            loading.dispose();
                            Application.app.setContentPane((dto.isVaiTro()) ? new QuanLyView() : new NhanVienView());
                            if (chkRemember.isSelected()) AccountUtils.remember(accountDTO);
                            else AccountUtils.deleteFile();
                        } else {
                            loading.dispose();
                            MsgBox.alert(this, "Tài khoản không được phép");
                        }
                        Application.app.pack();
                        Application.app.setLocationRelativeTo(null);
                    } else {
                        loading.dispose();
                        MsgBox.alert(this, "Sai mật khẩu!");
                    }
                } else {
                    loading.dispose();
                    MsgBox.alert(this, "Vui lòng nhập đủ username password");
                }
            } catch (InvalidDataAccessResourceUsageException e) {
                loading.dispose();
                MsgBox.alert(this, "Server không khả dụng");
            } catch (IllegalArgumentException ex) {
                loading.dispose();
                MsgBox.alert(this, "Tài khoản không tồn tại");
            }
            loading.dispose();
        });
        loading.setVisible(true);
    }

    private void openQuenMatKhau() {
        QuenMatKhauDialog quenMatKhauDialog = new QuenMatKhauDialog();
        quenMatKhauDialog.setVisible(true);
    }

    private void createUIComponents() {
        p = this;
        btnLogin = new ButtonToolItem("enter.svg", "enter.svg");
    }
}