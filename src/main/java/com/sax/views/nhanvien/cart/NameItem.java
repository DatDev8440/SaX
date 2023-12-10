package com.sax.views.nhanvien.cart;

import com.sax.utils.ImageUtils;
import com.sax.utils.Session;
import org.jdesktop.swingx.JXTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NameItem extends JPanel {
    private JPanel p;
    private JXTextArea lblTen;
    private JPanel image;

    public NameItem(String icon, String name) {
        image.add(ImageUtils.getCircleImage(icon, 30, 20, null, 0));
        lblTen.setText(name);
        if (name.length() > 30) lblTen.setText(name.substring(0, 30));
    }

    private void createUIComponents() {
        p = this;
    }
}
