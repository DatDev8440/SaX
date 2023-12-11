package com.sax.views.nhanvien.dialog;

import com.sax.Application;
import com.sax.services.IDonHangService;
import com.sax.services.impl.DonHangService;
import com.sax.utils.ContextUtils;
import com.sax.views.quanly.viewmodel.DonHangViewObject;
import com.sax.views.quanly.views.panes.DonHangPane;
import org.springframework.data.domain.PageRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.stream.Collectors;

public class DonHangDialog extends JDialog {
    private JPanel contentPane;
    private DonHangPane donHang;

    public DonHangDialog() {
        donHang.getToolPane().setVisible(false);
        donHang.getBtnTrash().setVisible(false);
        setContentPane(contentPane);
        setModal(true);
        pack();
        setLocationRelativeTo(Application.app);
    }
}
