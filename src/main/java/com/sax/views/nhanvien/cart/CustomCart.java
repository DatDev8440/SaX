package com.sax.views.nhanvien.cart;

import com.jgoodies.binding.adapter.AbstractTableAdapter;
import com.sax.views.components.libraries.RoundPanel;
import com.sax.views.components.table.CustomHeaderTableCellRenderer;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class CustomCart extends JXTable {
    private List<CartModel> list;

    public CustomCart(List<CartModel> list) {
        this.list = list;
        putClientProperty("TableHeader.separatorColor", "#000000");
        setModel(new DisplayCartTableAdapter(list, new String[]{"Tên sản phẩm", "Đơn giá", "SL", " "}));
    }

    public void initComponent() {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        getTableHeader().setEnabled(false);
        getTableHeader().setDefaultRenderer(new CustomHeaderTableCellRenderer());
        setDefaultRenderer(Object.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel p = new JPanel();
                if (value instanceof NameItem) p.add((NameItem) value);
                else if (column == 2) {
                    p.add(list.get(row).getSoLuong());
                    p.setBorder(new EmptyBorder(new Insets(3, 3, 3, 3)));
                } else if (column == 3) p.add(list.get(row).getXoa());
                else p.add(new JLabel(" " + value + " "));
                p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
                p.setAlignmentY(Component.CENTER_ALIGNMENT);
                return p;
            }
        });

        getColumn(2).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JPanel p = new JPanel();
                p.add(list.get(row).getSoLuong());
                p.setBorder(new EmptyBorder(new Insets(3, 3, 3, 3)));
                p.setBackground(Color.decode("#f5f5f5"));
                p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
                p.setAlignmentY(Component.CENTER_ALIGNMENT);
                return p;
            }
        });

        getColumn(3).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JPanel p = new JPanel();
                p.add(list.get(row).getXoa());
                p.setBackground(Color.decode("#f5f5f5"));
                p.setLayout(new GridLayout());
                p.setAlignmentY(Component.CENTER_ALIGNMENT);
                return p;
            }
        });
        packAll();
    }

    private class DisplayCartTableAdapter extends AbstractTableAdapter {
        public DisplayCartTableAdapter(List listModel, String[] columnNames) {
            super((ListModel) listModel, columnNames);
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            CartModel cart = (CartModel) getRow(rowIndex);
            if (columnIndex == 0) return new NameItem(cart.getIcon(), cart.getName());
            else if (columnIndex == 1) return cart.getDonGia();
            else if (columnIndex == 2) return cart.getSoLuong();
            return cart.getXoa();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 2 || columnIndex == 3) return true;
            return false;
        }
    }
}
