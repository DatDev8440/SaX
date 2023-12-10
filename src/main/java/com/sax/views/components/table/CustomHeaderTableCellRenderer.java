package com.sax.views.components.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomHeaderTableCellRenderer extends DefaultTableCellRenderer {
    private JLabel l;

    public CustomHeaderTableCellRenderer() {
        setHorizontalAlignment(JLabel.LEADING);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(247, 247, 247)));
        l = new JLabel();
        l.setBackground(new Color(0, 0, 0, 0));
        l.setText(value + "  ");
        l.setFont(new Font(".SF NS Text", 1, 13));
        l.setForeground(Color.decode("#727272"));
        l.setVerticalAlignment(SwingConstants.TOP);
        if (column == 0) l.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(l);
        return p;
    }
}
