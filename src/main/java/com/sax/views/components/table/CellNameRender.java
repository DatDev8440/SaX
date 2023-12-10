package com.sax.views.components.table;


import com.sax.utils.ImageUtils;
import com.sax.utils.Session;

import javax.swing.*;

public class CellNameRender extends JPanel {
    private JPanel content;
    private JLabel cellName;
    private JPanel avt;

    public CellNameRender(JTable tbl, String url, String name) {
        cellName.setText(name);
        Session.executorService.submit(() -> {
            avt.add(ImageUtils.getCircleImage(url, 26, 20,null,0));
            tbl.repaint();
        });
    }

    private void createUIComponents() {
        content = this;
    }
}