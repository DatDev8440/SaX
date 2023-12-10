package com.sax.views.components.table;

import com.sax.dtos.AbstractDTO;
import com.sax.views.quanly.viewmodel.AbstractViewObject;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class CustomTableCellRender extends DefaultTableCellRenderer {
    private List<AbstractViewObject> list;

    public CustomTableCellRender(List<AbstractViewObject> list) {
        this.list = list;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout());
        p.setBackground(new Color(0, 0, 0, 0));
        if (column == 0) {
            JCheckBox cbd = list.get(row).getCheckBoxDelete();
            cbd.setHorizontalAlignment(SwingConstants.CENTER);
            p.add(cbd);
        } else if (value instanceof CellNameRender) p.add((CellNameRender) value);
        else if (value instanceof List) {
            List<String> list = (List<String>) value;
            String text = "";
            for (int i = 0; i < list.size(); i++) {
                if (i == list.size() - 1) text = text + list.get(i);
                else text = text + list.get(i) + ", ";
            }
            JLabel l = new JLabel(text);
            l.setFont(new Font(".SF NS Text", 4, 13));
            l.setForeground(Color.decode("#727272"));
            p.add(l);
        } else {
            JLabel l = (value == null) ? new JLabel("") : new JLabel(value + "  ");
            l.setFont(new Font(".SF NS Text", 4, 13));
            l.setForeground(Color.decode("#727272"));
            p.add(l);
        }
        if (list.get(row).getCheckBoxDelete().isSelected()) p.setBackground(Color.decode("#F6FAFF"));
        if (isSelected) {
            if (column == 0) p.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.decode("#EA6C20")));
            else if (column == table.getColumnCount() - 1)
                p.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.decode("#EA6C20")));
            else p.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.decode("#EA6C20")));
        }
        return p;
    }
}
