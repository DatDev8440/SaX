package com.sax.views.quanly.views;

import com.sax.Application;
import com.sax.services.*;
import com.sax.services.impl.*;
import com.sax.utils.ContextUtils;
import com.sax.utils.ImageUtils;
import com.sax.utils.MsgBox;
import com.sax.utils.Session;
import com.sax.views.LoginView;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.components.menu.CustomMenu;
import com.sax.views.quanly.viewmodel.*;
import com.sax.views.quanly.views.panes.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class QuanLyView extends JPanel {
    private JPanel p;
    private JLabel title;
    private JList menu;
    private JPanel panelMenu;
    private JLabel lblLogo;
    private JPanel content;
    private SanPhamPane sanPham;
    private DanhMucPane danhMuc;
    private NhanVienPane taiKhoan;
    private KhachHangPane khachHang;
    private DonHangPane donHang;
    private KhuyenMaiPane khuyenMai;
    private ThongKePane thongKe;
    private JButton btnLogout;
    private JPanel avatar;
    private CardLayout cardLayout;
    private ISachService sachService = ContextUtils.getBean(SachService.class);
    private IDanhMucService danhMucService = ContextUtils.getBean(DanhMucService.class);
    private IAccountService accountService = ContextUtils.getBean(AccountService.class);
    private IKhachHangService khachHangService = ContextUtils.getBean(KhachHangService.class);
    private IDonHangService donHangService = ContextUtils.getBean(DonHangService.class);
    private ICtkmService ctkmService = ContextUtils.getBean(CtkmService.class);

    private Pageable pageable = PageRequest.of(0, 14);

    public QuanLyView() {
        btnLogout.addActionListener((e) -> dangXuat());
        initComponent();
        addCustomEventListener();
    }

    private void initComponent() {
        title.setText("Quản lý sản phẩm");
        lblLogo.setIcon(new ImageIcon(ImageUtils.readImage("logo-com.png").getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        avatar.add(ImageUtils.getCircleImage(Session.accountid.getAnh(), 30, 20, null, 0));
    }

    private void addCustomEventListener() {
        MouseAdapter hover = new MouseAdapter() {
            private int i = 50;
            private Dimension d = new Dimension();
            private java.util.Timer tm1;
            private java.util.Timer tm2;

            @Override
            public void mouseEntered(MouseEvent e) {
                if (tm2 != null) tm2.cancel();
                if (i < 200) {
                    tm1 = new java.util.Timer();
                    tm1.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            d.setSize(i++, panelMenu.getHeight());
                            panelMenu.setPreferredSize(d);
                            panelMenu.revalidate();
                            if (i >= 200) tm1.cancel();
                        }
                    }, 0, 1);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (tm1 != null) tm1.cancel();
                tm2 = new java.util.Timer();
                tm2.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        d.setSize(i--, panelMenu.getHeight());
                        panelMenu.setPreferredSize(d);
                        panelMenu.revalidate();
                        if (i <= 50) tm2.cancel();
                    }
                }, 0, 1);
            }
        };
        panelMenu.addMouseListener(hover);

        menu.addMouseListener(hover);
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                content.removeAll();
                switch (menu.getSelectedIndex()) {
                    case 0 -> {
                        title.setText("Quản lý sản phẩm");
                        sanPham = new SanPhamPane();
                        content.add(sanPham);
                    }
                    case 1 -> {
                        title.setText("Quản lý danh mục");
                        danhMuc = new DanhMucPane();
                        content.add(danhMuc);
                    }
                    case 2 -> {
                        title.setText("Quản lý đơn hàng");
                        donHang = new DonHangPane();
                        content.add(donHang);
                    }
                    case 3 -> {
                        title.setText("Quản lý khuyến mại");
                        khuyenMai = new KhuyenMaiPane();
                        content.add(khuyenMai);
                    }
                    case 4 -> {
                        title.setText("Quản lý khách hàng");
                        khachHang = new KhachHangPane();
                        content.add(khachHang);
                    }
                    case 5 -> {
                        title.setText("Quản lý tài khoản nhân viên");
                        taiKhoan = new NhanVienPane();
                        content.add(taiKhoan);
                    }
                    case 6 -> {
                        title.setText("Quản lý thống kê");
                        thongKe = new ThongKePane();
                        content.add(thongKe);
                    }
                }
                content.revalidate();
            }
        });

        btnLogout.addMouseListener(hover);
    }

    private void dangXuat() {
        boolean check = MsgBox.confirm(this, "Bạn có thực sự muốn đăng xuất không?");
        if (check) {
            Session.accountid = null;
            Application.app.setContentPane(new LoginView(Application.app));
            Application.app.pack();
            Application.app.setLocationRelativeTo(null);
        }
    }

    private void createUIComponents() {
        p = this;
        menu = new CustomMenu();
        btnLogout = new ButtonToolItem("exit-c.svg", "exit-c.svg");
    }
}
