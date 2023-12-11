package com.sax.views.quanly.views.dialogs;

import com.formdev.flatlaf.ui.FlatBorder;
import com.formdev.flatlaf.ui.FlatButtonBorder;
import com.sax.dtos.CtkmDTO;
import com.sax.dtos.CtkmSachDTO;
import com.sax.dtos.SachDTO;
import com.sax.services.ICtkmSachService;
import com.sax.services.ICtkmService;
import com.sax.services.ISachService;
import com.sax.services.impl.CtkmSachService;
import com.sax.services.impl.CtkmService;
import com.sax.services.impl.SachService;
import com.sax.utils.ContextUtils;
import com.sax.utils.CurrencyConverter;
import com.sax.utils.MsgBox;
import com.sax.utils.Session;
import com.sax.views.components.ListPageNumber;
import com.sax.views.components.Loading;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.components.libraries.RoundPanel;
import com.sax.views.components.table.CustomHeaderTableCellRenderer;
import com.sax.views.components.table.CustomTableCellEditor;
import com.sax.views.quanly.viewmodel.AbstractViewObject;
import com.sax.views.quanly.viewmodel.CtkmSachViewObject;
import com.sax.views.quanly.views.panes.KhuyenMaiPane;
import org.apache.commons.validator.Msg;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CtkmSachDialog extends JDialog {
    private JPanel contentPane;
    private JButton btnAdd;
    private JPanel bg;
    private JXTable table;
    private JLabel lblTitle;
    private JButton btnDel;
    private JButton btnSave;
    private JList listPage;
    private final List<CtkmSachDTO> listCtkmSach = new ArrayList<>();
    private final ICtkmService ctkmService = ContextUtils.getBean(CtkmService.class);
    private final ICtkmSachService ctkmSachService = ContextUtils.getBean(CtkmSachService.class);
    private final ISachService sachService = ContextUtils.getBean(SachService.class);
    private final Set tempIdSet = new HashSet();
    private final Loading loading = new Loading(this);
    private final FlatBorder flatBorder = new FlatButtonBorder() {
        @Override
        protected boolean isCellEditor(Component c) {
            return false;
        }
    };

    public CtkmDTO ctkmDTO;
    public KhuyenMaiPane khuyenMaiPane;


    public CtkmSachDialog() {
        btnAdd.addActionListener((e) -> {
            addToTable();
        });
        btnSave.addActionListener((e) -> save());
        btnDel.addActionListener((e) -> delete());
        initComponent();
    }

    private void initComponent() {
        setContentPane(contentPane);
        setModal(true);
        pack();
        setLocationRelativeTo(khuyenMaiPane);
        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void fillForm() {
        lblTitle.setText("Áp dụng sách cho sự kiện " + ctkmDTO.getTenSuKien());
        ((DefaultTableModel) table.getModel()).setColumnIdentifiers(new String[]{"", "STT", "Sản phẩm", "Giá trị giảm " + (ctkmDTO.isKieuGiamGia() ? "(%)" : "(vnd)")});
        fillTable(listCtkmSach.stream().map(CtkmSachViewObject::new).collect(Collectors.toList()));
    }

    private void addToTable() {
        tempIdSet.clear();
        CtkmSachDTO ctkmSachDTO = new CtkmSachDTO();
        SachDTO sachDTO = new SachDTO();
        ctkmSachDTO.setCtkm(ctkmDTO);
        ctkmSachDTO.setSach(sachDTO);
        ctkmSachDTO.setGiaTriGiam(100L);
        listCtkmSach.add(ctkmSachDTO);
        fillTable(listCtkmSach.stream().map(CtkmSachViewObject::new).collect(Collectors.toList()));
        table.getColumns().forEach(TableColumn::sizeWidthToFit);
    }

    private void fillTable(List<AbstractViewObject> list) {
        ((DefaultTableModel) table.getModel()).setRowCount(0);
        for (int i = 0; i < list.size(); i++) {
            CtkmSachViewObject obj = (CtkmSachViewObject) list.get(i);
            ((DefaultTableModel) table.getModel()).addRow(new Object[]{list.get(i).getCheckBoxDelete(), i + 1, obj.getSach(), obj.getGiaTriGiam()});
        }
        table.setDefaultEditor(Object.class, null);
        table.getTableHeader().setDefaultRenderer(new CustomHeaderTableCellRenderer());
        table.getTableHeader().setEnabled(false);
        table.getTableHeader().setPreferredSize(new Dimension(getWidth(), 28));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel p = new JPanel();
                p.setLayout(new GridLayout());
                p.setBackground(new Color(0, 0, 0, 0));
                if (column == 0) {
                    JCheckBox cbd = list.get(row).getCheckBoxDelete();
                    cbd.setHorizontalAlignment(SwingConstants.CENTER);
                    p.add(cbd);
                } else if (column == 2) {
                    JLabel l = new JLabel(listCtkmSach.get(row).getSach().toString());
                    l.setFont(new Font(".SF NS Text", 4, 13));
                    l.setHorizontalAlignment(SwingConstants.LEFT);
                    p.add(l);
                } else if (column == 3) {
                    JLabel l = new JLabel(listCtkmSach.get(row).getCtkm().isKieuGiamGia() ? listCtkmSach.get(row).getGiaTriGiam() + "%" : CurrencyConverter.parseString(listCtkmSach.get(row).getGiaTriGiam()));
                    l.setFont(new Font(".SF NS Text", 4, 13));
                    p.add(l);
                } else {
                    JLabel l = (value == null) ? new JLabel("") : new JLabel("  " + value + "  ");
                    l.setFont(new Font(".SF NS Text", 4, 13));
                    l.setHorizontalAlignment(SwingConstants.CENTER);
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
        });
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JPanel p = new JPanel();
                JCheckBox c = list.get(row).getCheckBoxDelete();
                c.addActionListener((e) -> tempIdSet.add(row));
                p.setLayout(new GridBagLayout());
                p.add(c);
                p.setBackground(Color.decode("#F6FAFF"));
                p.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.decode("#EA6C20")));
                return p;
            }
        });
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JComboBox jComboBox = new JComboBox();
                jComboBox.setBorder(flatBorder);

                Session.executorService.submit(() -> {
                    sachService.getAllSachNotInCTKM().stream().filter(i -> {
                        for (CtkmSachDTO s : listCtkmSach)
                            if (s.getSach() != null && s.getSach().getId() == i.getId()) return false;
                        return true;
                    }).forEach(i -> jComboBox.addItem(i));
                    loading.dispose();
                });
                loading.setVisible(true);
                jComboBox.addActionListener((e) -> {
                    listCtkmSach.get(row).setSach((SachDTO) jComboBox.getSelectedItem());
                    table.revalidate();
                    ((JXTable) table).packAll();
                });
                return jComboBox;
            }
        });
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JFormattedTextField jTextField = new JFormattedTextField(listCtkmSach.get(row).getGiaTriGiam());
                jTextField.setFormatterFactory(CurrencyConverter.getVnCurrency());
                jTextField.setBorder(flatBorder);
                jTextField.addActionListener((e) -> {
                            listCtkmSach.get(row).setGiaTriGiam(CurrencyConverter.parseLong(jTextField.getText().trim()));
                            table.getCellEditor().stopCellEditing();
                        }
                );
                return jTextField;
            }
        });
        table.packAll();
    }

    public void save() {
        if (table.getRowCount() > 0) {
            boolean check = true;

            if (ctkmDTO.isKieuGiamGia()) {
                for (CtkmSachDTO ctkmSachDTO : listCtkmSach) {
                    if (table.getRowCount() == 0) {
                        MsgBox.alert(this, "Vui lòng thêm sách!");
                        check = false;
                        break;
                    }
                    if (ctkmSachDTO.getSach().getId() == 0) {
                        MsgBox.alert(this, "Vui lòng chọn sách!");
                        check = false;
                        break;
                    }
                    if (ctkmSachDTO.getGiaTriGiam() == null) {
                        MsgBox.alert(this, "Nhập giá trị giảm của sản phẩm " + ctkmSachDTO.getSach().getTenSach());
                        check = false;
                        break;
                    }
                    if (ctkmSachDTO.getGiaTriGiam() > 100) {
                        MsgBox.alert(this, "Giá trị giảm của sản phẩm " + ctkmSachDTO.getSach().getTenSach() + " phải nhỏ hơn 100%");
                        check = false;
                        break;
                    }
                }
            }
            if (check) {
                try {
                    ctkmSachService.insetAll(listCtkmSach);
                    khuyenMaiPane.fillTableSP(ctkmSachService.getAll().stream().map(CtkmSachViewObject::new).collect(Collectors.toList()));
                    dispose();
                } catch (Exception e) {
                    MsgBox.alert(this, e.getMessage());
                }
            }
        } else MsgBox.alert(this, "Vui lòng thêm sách!");
    }

    private void delete() {
        if (!tempIdSet.isEmpty()) {
            boolean check = MsgBox.confirm(this, "Bạn có muốn xoá sản phẩm này không?");
            if (check) {
                try {
                    tempIdSet.stream()
                            .map(i -> listCtkmSach.get((int) i)).toList().forEach(i -> listCtkmSach.remove(i));
                    tempIdSet.clear();
                    fillTable(listCtkmSach.stream().map(CtkmSachViewObject::new).collect(Collectors.toList()));
                } catch (Exception e) {
                    MsgBox.alert(this, e.getMessage());
                }
            }
        }
    }

    private void createUIComponents() {
        bg = new RoundPanel(10);
        btnAdd = new ButtonToolItem("add.svg", "add.svg");
        btnDel = new ButtonToolItem("trash-c.svg", "trash-c.svg");

        listPage = new ListPageNumber();
    }
}
