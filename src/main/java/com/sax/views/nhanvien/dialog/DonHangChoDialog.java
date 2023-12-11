package com.sax.views.nhanvien.dialog;

import com.sax.Application;
import com.sax.dtos.DonHangDTO;
import com.sax.utils.Cart;
import com.sax.utils.Session;
import com.sax.views.components.ListPageNumber;
import com.sax.views.components.Loading;
import com.sax.views.components.Search;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.components.libraries.RoundPanel;
import com.sax.views.components.table.CellNameRender;
import com.sax.views.components.table.CustomHeaderTableCellRenderer;
import com.sax.views.components.table.CustomTableCellEditor;
import com.sax.views.components.table.CustomTableCellRender;
import com.sax.views.nhanvien.NhanVienView;
import com.sax.views.nhanvien.doncho.DonChoViewObject;
import com.sax.views.quanly.viewmodel.AbstractViewObject;
import com.sax.views.quanly.viewmodel.DonHangViewObject;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DonHangChoDialog extends JDialog {
    private JXTable table;
    private JPanel donHangChoPane;
    private JPanel bg;
    private JPanel phanTrangPane;
    private JComboBox cboHienThi;
    private JList listPage;
    private Search timKiem;
    private JButton btnAdd;
    private JButton btnDel;
    private JButton btnEdit;
    private Set tempIdSet = new HashSet();
    private List<JCheckBox> listCbk = new ArrayList<>();

    public DonHangChoDialog() {
        initComponent();
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    sentDonTamToDonHang();
                }
            }
        });
        btnDel.addActionListener((e) -> delete());
    }

    private void initComponent() {
        setContentPane(donHangChoPane);
        setModal(true);

        ((DefaultTableModel) table.getModel()).setColumnIdentifiers(new String[]{"ID", "Tên khách hàng", "Số sản phẩm", "Tiền hàng", "Chiết khấu"});
        fillTable(Session.listDonCho);
    }

    private void fillTable(List<DonChoViewObject> list) {
        ((DefaultTableModel) table.getModel()).setRowCount(0);
        list.forEach(i -> ((DefaultTableModel) table.getModel()).addRow(i.toObject()));
        table.setDefaultEditor(Object.class, null);
        table.getTableHeader().setDefaultRenderer(new CustomHeaderTableCellRenderer());
        table.getTableHeader().setEnabled(false);
        table.getTableHeader().setPreferredSize(new Dimension(-1, 28));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel p = new JPanel();
                p.setLayout(new GridLayout());
                p.setBackground(new Color(0, 0, 0, 0));
                if (value instanceof List) {
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
                if (isSelected) {
                    if (column == 0) p.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.decode("#EA6C20")));
                    else if (column == table.getColumnCount() - 1)
                        p.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.decode("#EA6C20")));
                    else p.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.decode("#EA6C20")));
                }
                return p;
            }
        });
        table.packAll();
        pack();
        setLocationRelativeTo(Application.app);
        table.getColumns().forEach(TableColumn::sizeWidthToFit);
        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void sentDonTamToDonHang() {
        if (table.getSelectedRow() >= 0) {
            int id = (int) table.getValueAt(table.getSelectedRow(), 0);
            NhanVienView.nvv.sentDonTamToDonHang(id);
            dispose();
        }
    }

    private void delete() {
        Session.listDonCho.remove(table.getSelectedRow());
        fillTable(Session.listDonCho);
    }

    private void createUIComponents() {
        bg = new RoundPanel(10);
        btnAdd = new ButtonToolItem("add.svg", "add.svg");
        btnDel = new ButtonToolItem("trash-c.svg", "trash-c.svg");
        btnEdit = new ButtonToolItem("pencil.svg", "pencil.svg");

        listPage = new ListPageNumber();
    }
}
