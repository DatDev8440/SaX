package com.sax.views.nhanvien.dialog;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.sax.Application;
import com.sax.utils.MsgBox;
import com.sax.utils.Session;
import com.sax.views.LoginView;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.components.libraries.PanelShadow;
import com.sax.views.quanly.views.dialogs.NhanVienDialog;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserPopup extends JDialog {
    private JPanel main;
    private JButton btnLogout;
    private JButton btnThongTin;
    private JLabel lblTen;
    private JPanel contentPane;
    private JButton btnClose;
    private JButton buttonOK;
    private JButton buttonCancel;

    @Setter
    private JLabel lblTenView;

    public UserPopup() {
        btnThongTin.addActionListener((e) -> thongTinChiTiet());
        btnLogout.addActionListener((e) -> dangXuat());

        lblTen.setText(Session.accountid.getTenNhanVien());
        btnClose.addActionListener((e) -> dispose());
        main.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Color.decode("#a0a0a0"), 1, 10));
        setUndecorated(true);
        getRootPane().setBackground(new Color(0, 0, 0, 0));
        getRootPane().putClientProperty("Window.shadow", Boolean.FALSE);
        setBackground(new Color(0, 0, 0, 0));
        getRootPane().setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));

        setContentPane(contentPane);
        setModal(true);
        pack();
        int frameWidth = Application.app.getWidth(); // Lấy chiều rộng của JFrame
        int frameX = Application.app.getX(); // Lấy vị trí x của JFrame
        int frameY = Application.app.getY(); // Lấy vị trí y của JFrame

        int cornerX = frameX + frameWidth; // Tính toán vị trí x của góc trên bên phải
        int cornerY = frameY; // Vị trí y của góc trên bên phải giống với vị trí y của JFrame

        Point cornerPoint = new Point(cornerX - getWidth(), cornerY + 70);

        setLocation(cornerPoint);
    }

    private void thongTinChiTiet() {
        dispose();
        NhanVienDialog dialog = new NhanVienDialog();
        dialog.id = Session.accountid.getId();
        dialog.fillForm();
        dialog.getPanelRole().setVisible(false);
        dialog.setLblTenView(lblTenView);
        dialog.setLocationRelativeTo(Application.app);
        dialog.setVisible(true);
    }

    private void dangXuat() {
        dispose();
        boolean check = MsgBox.confirm(Application.app, "Bạn có thực sự muốn đăng xuất không?");
        if (check) {
            Session.accountid = null;
            Application.app.setContentPane(new LoginView(Application.app));
            Application.app.pack();
            Application.app.setLocationRelativeTo(null);
        }
    }

    private void createUIComponents() {
        contentPane = new PanelShadow(10);
        btnThongTin = new ButtonToolItem("info-c.svg", "info-c.svg");
        btnLogout = new ButtonToolItem("exit-c.svg", "exit-c.svg");
        btnClose = new ButtonToolItem("x-c.svg","x-c.svg");
    }
}
