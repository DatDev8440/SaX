package com.sax.views.quanly.views.panes;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatBorder;
import com.formdev.flatlaf.ui.FlatButtonBorder;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.sax.dtos.CtkmDTO;
import com.sax.dtos.CtkmSachDTO;
import com.sax.services.ICtkmSachService;
import com.sax.services.ICtkmService;
import com.sax.services.impl.CtkmSachService;
import com.sax.services.impl.CtkmService;
import com.sax.utils.*;
import com.sax.views.components.ListPageNumber;
import com.sax.views.components.Loading;
import com.sax.views.components.Search;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.components.libraries.RoundPanel;
import com.sax.views.quanly.viewmodel.AbstractViewObject;
import com.sax.views.quanly.viewmodel.CtkmSachViewObject;
import com.sax.views.quanly.viewmodel.CtkmViewObject;
import com.sax.views.quanly.views.dialogs.CtkmDialog;
import com.sax.views.quanly.views.dialogs.CtkmSachDialog;
import lombok.Getter;
import lombok.Setter;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXFormattedTextField;
import org.jdesktop.swingx.JXTable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class KhuyenMaiPane extends JPanel {
    private JXTable tableCTKM;
    private JXTable tableSP;
    private JPanel bg;
    private JPanel bg2;
    private JScrollPane kmScroll;
    private JButton btnAdd;
    private JButton btnDel;
    private JButton btnEdit;
    private JCheckBox cbkSelectedAllCTKM;
    private JPanel phanTrangPane;
    private JList listPageKM;
    private JComboBox cboHienThi;
    private Search timKiemCTKM;
    private Search timKiemSP;
    private JPanel khuyenMaiPane;
    private JComboBox cboCTKM;
    private JButton btnAddSachTo;
    private JCheckBox cbkSelectedAllSP;
    private JButton btnXoaSP;
    private JButton btnSuaSP;
    private JButton btnBoLuaChon;
    private JLabel lblTitleSach;
    private JComboBox cboSapXepKM;
    private JComboBox cboSXTieuChi;
    private final String[] tenCotKM = new String[]{"", "Mã sự kiện", "Tên sự kiện", "Ngày bắt đầu", "Ngày kết thúc", "Giảm theo", "Trạng thái"};
    private final String[] tenCotSP = new String[]{"", "Id", "Tên sách", "Giá bán", "Tên sự kiện", "Ngày bắt đầu", "Ngày kết thúc", "Giá trị giảm", "Trạng thái"};
    private ICtkmSachService ctkmSachService = ContextUtils.getBean(ICtkmSachService.class);
    private ICtkmService ctkmService = ContextUtils.getBean(CtkmService.class);
    private Set tempIdSetCTKM = new HashSet();
    private Set tempIdSetSP = new HashSet();
    private List<JCheckBox> listCbkCTKM = new ArrayList<>();
    private List<JCheckBox> listCbkSP = new ArrayList<>();
    private Loading loading = new Loading(this);
    private List<CtkmDTO> tempCtkmListDTO = new ArrayList<>();

    private final DefaultListModel listPageModelKM = new DefaultListModel();
    @Getter
    @Setter
    private int pageKMValue = 1;
    @Getter
    @Setter
    private int sizeValue = 14;
    @Getter
    @Setter
    private Pageable pageableKM = PageRequest.of(pageKMValue - 1, sizeValue);
    private Timer timerKM;
    private Timer timerSP;

    public KhuyenMaiPane() {
        initComponent();
        btnAdd.addActionListener((e) -> addKM());
        btnEdit.addActionListener((e) -> updateKM());
        btnDel.addActionListener((e) -> deleteKM());
        cbkSelectedAllCTKM.addActionListener((e) -> Session.chonTatCa(cbkSelectedAllCTKM, tableCTKM, listCbkCTKM, tempIdSetCTKM));
        tableCTKM.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                locSPCtkm();
            }
        });
        listPageKM.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectPageDisplay();
            }
        });
        cboHienThi.addActionListener((e) -> selectSizeDisplay());
        timKiemCTKM.txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchByKeywordKM();
            }
        });

        btnAddSachTo.addActionListener((e) -> addSP());
        btnSuaSP.addActionListener((e) -> updateSP());
        btnXoaSP.addActionListener((e) -> deleteSP());
        btnBoLuaChon.addActionListener((e) -> boLuaChon());
        tableSP.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) updateSP();
            }
        });
        cbkSelectedAllSP.addActionListener((e) -> Session.chonTatCa(cbkSelectedAllSP, tableSP, listCbkSP, tempIdSetSP));
        timKiemSP.txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                timerSP.restart();
            }
        });
        cboSapXepKM.addActionListener((e) -> sapXepKM());
        cboSXTieuChi.addActionListener((e) -> sapXepKM());
    }

    public void initComponent() {
        kmScroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "track:#F2F2F2");
        ((DefaultTableModel) tableCTKM.getModel()).setColumnIdentifiers(tenCotKM);
        ((DefaultTableModel) tableSP.getModel()).setColumnIdentifiers(tenCotSP);
        ((DefaultComboBoxModel) cboSapXepKM.getModel()).addAll(Arrays.stream(tenCotKM).filter(i -> !i.equals("")).collect(Collectors.toList()));
        cboSapXepKM.setSelectedIndex(3);
        cboSXTieuChi.setSelectedIndex(1);
        new WorkerKM().execute();
        loading.setVisible(true);
        new WorkerSP(-1).execute();
        loading.setVisible(true);
        timerKM = new Timer(300, e -> {
            searchByKeywordKM();
            timerKM.stop();
        });
        timerSP = new Timer(300, e -> {
            searchByKeywordSP();
            timerSP.stop();
        });
        fillCboCtkm();
    }

    public void fillTableKM(List<AbstractViewObject> list) {
        Session.fillTable(list, tableCTKM, cbkSelectedAllCTKM, tempIdSetCTKM, listCbkCTKM);

    }

    private void addKM() {
        CtkmDialog ctkmDialog = new CtkmDialog();
        ctkmDialog.parentPane = this;
        ctkmDialog.lblTitle.setText("Thêm mới chương trình khuyến mại");
        ctkmDialog.setVisible(true);
        tableCTKM.clearSelection();
    }

    private void updateKM() {
        if (tableCTKM.getSelectedRow() >= 0) {
            if (tableCTKM.getValueAt(tableCTKM.getSelectedRow(), 6).toString().equals("Đang diễn ra")) {
                MsgBox.alert(this, "Sự kiện đang diễn ra, bạn không thể chỉnh sửa!");
                return;
            }
            if (tableCTKM.getValueAt(tableCTKM.getSelectedRow(), 6).toString().equals("Đã kết thúc")) {
                MsgBox.alert(this, "Sự kiện đã kết thúc, bạn không thể chỉnh sửa!");
                return;
            }
            Session.executorService.submit(() -> {
                CtkmDialog ctkmDialog = new CtkmDialog();
                ctkmDialog.parentPane = this;
                ctkmDialog.id = (int) tableCTKM.getValueAt(tableCTKM.getSelectedRow(), 1);
                ctkmDialog.fillForm();
                loading.dispose();
                ctkmDialog.setVisible(true);
                tableCTKM.clearSelection();
            });
            loading.setVisible(true);
        } else MsgBox.alert(this, "Vui lòng chọn một chương trình khuyến mại!");
    }

    private void deleteKM() {
        if (!tempIdSetCTKM.isEmpty()) {
            boolean check = MsgBox.confirm(this, "Bạn có muốn xoá " + tempIdSetCTKM.size() + " chương trình khuyến mại này không?");
            if (check) {
                try {
                    ctkmService.deleteAll(tempIdSetCTKM);
                    cbkSelectedAllCTKM.setSelected(false);
                } catch (Exception e) {
                    MsgBox.alert(this, e.getMessage());
                }
                pageKMValue = ctkmService.getTotalPage(sizeValue) < pageKMValue ? ctkmService.getTotalPage(sizeValue) : pageKMValue;
                pageableKM = PageRequest.of(pageKMValue > 0 ? pageKMValue - 1 : pageKMValue, sizeValue);
                fillTableKM(ctkmService.getPage(pageableKM).stream().map(CtkmViewObject::new).collect(Collectors.toList()));
                fillListPage();
                loading.dispose();
            }
        } else MsgBox.alert(this, "Vui lòng tick vào ít nhất một chương trình khuyến mại!");
    }

    private void searchByKeywordKM() {
        String keyword = timKiemCTKM.txtSearch.getText();
        if (!keyword.isEmpty()) {
            fillTableKM(ctkmService.searchByKeyword(keyword).stream().map(CtkmViewObject::new).collect(Collectors.toList()));
            phanTrangPane.setVisible(false);
        } else {
            new WorkerKM().execute();
            loading.setVisible(true);
            new WorkerSP(-1).execute();
            loading.setVisible(true);
            lblTitleSach.setText("Sách được áp dụng trong tất cả CTKM");
            phanTrangPane.setVisible(true);
        }
    }

    private void sapXepKM() {
        Comparator<CtkmDTO> idComparator = Comparator.comparingInt(CtkmDTO::getId);
        Comparator<CtkmDTO> tenSuKienComparator = Comparator.comparing(CtkmDTO::getTenSuKien);
        Comparator<CtkmDTO> ngayBatDauComparator = Comparator.comparing(CtkmDTO::getNgayBatDau);
        Comparator<CtkmDTO> ngayKetThucComparator = Comparator.comparing(CtkmDTO::getNgayKetThuc);
        Comparator<CtkmDTO> kieuGiamGiaComparator = Comparator.comparing((CtkmDTO c) -> c.isKieuGiamGia() ? "Phần trăm" : "Số tiền cố định");
        Comparator<CtkmDTO> trangThaiComparator = Comparator.comparing(CtkmDTO::getTrangThai);
        Comparator<CtkmDTO> selectedComparator;
        switch (cboSapXepKM.getSelectedIndex()) {
            case 0:
                selectedComparator = cboSXTieuChi.getSelectedIndex() == 0 ? idComparator : idComparator.reversed();
                break;
            case 1:
                selectedComparator = cboSXTieuChi.getSelectedIndex() == 0 ? tenSuKienComparator : tenSuKienComparator.reversed();
                break;
            case 2:
                selectedComparator = cboSXTieuChi.getSelectedIndex() == 0 ? ngayBatDauComparator : ngayBatDauComparator.reversed();
                break;
            case 3:
                selectedComparator = cboSXTieuChi.getSelectedIndex() == 0 ? ngayKetThucComparator : ngayKetThucComparator.reversed();
                break;
            case 4:
                selectedComparator = cboSXTieuChi.getSelectedIndex() == 0 ? kieuGiamGiaComparator : kieuGiamGiaComparator.reversed();
                break;
            case 5:
                selectedComparator = cboSXTieuChi.getSelectedIndex() == 0 ? trangThaiComparator : trangThaiComparator.reversed();
                break;
            default:
                throw new IllegalArgumentException("Không tìm có lựa chọn!");
        }

        tempCtkmListDTO.sort(selectedComparator);
        fillTableKM(tempCtkmListDTO.stream().map(CtkmViewObject::new).collect(Collectors.toList()));
    }

    public void fillListPage() {
        Session.fillListPage(pageKMValue, listPageModelKM, ctkmService, sizeValue, listPageKM);
    }

    public void selectPageDisplay() {
        if (listPageKM.getSelectedValue() instanceof Integer) {
            pageKMValue = Integer.parseInt(listPageKM.getSelectedValue().toString());
            pageableKM = PageRequest.of(pageKMValue - 1, sizeValue);
            new WorkerKM().execute();
            loading.setVisible(true);
        }
    }

    public void selectSizeDisplay() {
        sizeValue = Integer.parseInt(cboHienThi.getSelectedItem().toString());
        pageKMValue = 1;
        pageableKM = PageRequest.of(pageKMValue - 1, sizeValue);
        new WorkerKM().execute();
        loading.setVisible(true);
    }

    //Table CTKM_Sach
    public void fillCboCtkm() {
        Session.executorService.submit(() -> {
            cboCTKM.addItem("-Tất cả-");
            ctkmService.getAll().stream().filter(i -> ctkmSachService.getAllSachInCtkm(i).size() > 0).forEach(i -> cboCTKM.addItem(i));
        });
    }

    public void fillTableSP(List<AbstractViewObject> list) {
        Session.fillTable(list, tableSP, cbkSelectedAllSP, tempIdSetSP, listCbkSP);
    }

    public void addSP() {
        if (tableCTKM.getSelectedRow() >= 0) {
            if (tableCTKM.getValueAt(tableCTKM.getSelectedRow(), 6).toString().equals("Đã kết thúc")) {
                MsgBox.alert(this, "Sự kiện đã kết thúc, bạn không thể thêm sản phẩm!");
                return;
            }
            if (!tableCTKM.getValueAt(tableCTKM.getSelectedRow(), 6).toString().equals("Đã kết thúc")) {
                CtkmSachDialog ctkmSachDialog = new CtkmSachDialog();
                ctkmSachDialog.khuyenMaiPane = this;
                ctkmSachDialog.ctkmDTO = ctkmService.getById((Integer) tableCTKM.getValueAt(tableCTKM.getSelectedRow(), 1));
                ctkmSachDialog.fillForm();
                ctkmSachDialog.setVisible(true);
            } else MsgBox.alert(this, "Sự kiện đã kết thúc!");
        } else MsgBox.alert(this, "Vui lòng chọn 1 sự kiện khuyến mại!");
    }

    private void updateSP() {
        if (tableSP.getSelectedRow() >= 0) {
            if (tableSP.getValueAt(tableSP.getSelectedRow(), 8).toString().equals("Đã kết thúc")) {
                MsgBox.alert(this, "Sự kiện đã kết thúc, bạn không thể chỉnh sửa!");
                return;
            }
            TableCellEditor editor = new DefaultCellEditor(new JCheckBox()) {
                private JXFormattedTextField textField = new JXFormattedTextField();
                private final FlatBorder flatBorder = new FlatButtonBorder() {
                    @Override
                    protected boolean isCellEditor(Component c) {
                        return false;
                    }
                };

                @Override
                public boolean isCellEditable(EventObject anEvent) {
                    return true;
                }

                @Override
                public Object getCellEditorValue() {
                    return textField.getText();
                }

                @Override
                public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                    textField.setText(value.toString());
                    textField.setBorder(flatBorder);
                    textField.addActionListener((e) -> {
                        long prevValue = CurrencyConverter.parseLong(value.toString());
                        long nextValue = CurrencyConverter.parseLong(textField.getText());
                        if (table.getCellEditor() != null && prevValue != nextValue) {
                            boolean check = MsgBox.confirm(KhuyenMaiPane.this, "Bạn có muốn cập nhật giá trị giảm không?");
                            if (check) {
                                CtkmSachDTO ctkmSachDTO = ctkmSachService.getById((int) table.getValueAt(row, 1));
                                ctkmSachDTO.setGiaTriGiam(nextValue);
                                try {
                                    ctkmSachService.update(ctkmSachDTO);
                                    textField.setText(ctkmSachDTO.getCtkm().isKieuGiamGia() ? nextValue + "%" : CurrencyConverter.parseString(nextValue));
                                    table.getCellEditor().stopCellEditing();
                                } catch (Exception ex) {
                                    textField.setText(value.toString());
                                    MsgBox.alert(KhuyenMaiPane.this, ex.getMessage());
                                    table.getCellEditor().stopCellEditing();
                                }
                            } else textField.setText(value.toString());
                        }
                    });
                    return textField;
                }
            };
            tableSP.getColumnModel().getColumn(7).setCellEditor(editor);

            tableSP.editCellAt(tableSP.getSelectedRow(), 7);
        } else MsgBox.alert(this, "Vui lòng chọn một sản phẩm trong chương trình khuyến mại!");
    }

    private void deleteSP() {
        if (!tempIdSetSP.isEmpty()) {
            boolean check = MsgBox.confirm(this, "Bạn có muốn xoá " + tempIdSetSP.size() + " sản phẩm trong chương trình khuyến mại này không?");
            if (check) {
                Session.executorService.submit(() -> {
                    try {
                        ctkmSachService.deleteAll(tempIdSetSP);
                        tableSP.clearSelection();
                        tempIdSetSP.clear();
                        fillTableSP(ctkmSachService.getAll().stream().map(CtkmSachViewObject::new).collect(Collectors.toList()));
                    } catch (Exception e) {
                        MsgBox.alert(this, e.getMessage());
                    }
                });
            }
        } else MsgBox.alert(this, "Vui lòng tick vào ít nhất một sản phẩm trong chương trình khuyến mại!");
    }

    public void locSPCtkm() {
        if (tableCTKM.getSelectedRow() >= 0) {
            new WorkerSP((int) tableCTKM.getValueAt(tableCTKM.getSelectedRow(), 1)).execute();
            loading.setVisible(true);
            lblTitleSach.setText("Sách được áp dụng trong " + tableCTKM.getValueAt(tableCTKM.getSelectedRow(), 2).toString());
        } else {
            new WorkerSP(-1).execute();
            loading.setVisible(true);
            lblTitleSach.setText("Sách được áp dụng trong tất cả CTKM");
        }
    }

    public void boLuaChon() {
        tableCTKM.clearSelection();
        locSPCtkm();
    }

    public void searchByKeywordSP() {
        String keyword = timKiemSP.txtSearch.getText();
        if (!keyword.isEmpty())
            fillTableSP(ctkmSachService.searchByKeyword(keyword).stream().map(CtkmSachViewObject::new).collect(Collectors.toList()));
        else {
            new WorkerSP((int) tableCTKM.getValueAt(tableCTKM.getSelectedRow(), 1)).execute();
            loading.setVisible(true);
        }
    }

    //ListPageSp
    //...

    private void createUIComponents() {
        khuyenMaiPane = this;
        bg = new RoundPanel(10);
        bg2 = new RoundPanel(10);
        btnAdd = new ButtonToolItem("add.svg", "add.svg");
        btnAddSachTo = new ButtonToolItem("add-c.svg", "add-c.svg");
        btnDel = new ButtonToolItem("trash-c.svg", "trash-c.svg");
        btnEdit = new ButtonToolItem("pencil.svg", "pencil.svg");
        btnXoaSP = new ButtonToolItem("trash-c.svg", "trash-c.svg");
        btnSuaSP = new ButtonToolItem("pencil.svg", "pencil.svg");
        btnBoLuaChon = new ButtonToolItem("clear-c.svg", "clear-c.svg");

        listPageKM = new ListPageNumber();
    }

    class WorkerKM extends SwingWorker<List<AbstractViewObject>, Integer> {
        @Override
        protected List<AbstractViewObject> doInBackground() {
            tempCtkmListDTO = ctkmService.getPage(pageableKM);
            return tempCtkmListDTO.stream().map(CtkmViewObject::new).collect(Collectors.toList());
        }

        @Override
        protected void done() {
            try {
                fillTableKM(get());
                if (tableCTKM.getRowCount() > 0) fillListPage();
                sapXepKM();
                loading.dispose();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class WorkerSP extends SwingWorker<List<AbstractViewObject>, Integer> {
        int id;

        public WorkerSP(int id) {
            this.id = id;
        }

        @Override
        protected List<AbstractViewObject> doInBackground() {
            if (id >= 0)
                return ctkmSachService.getAllSachByIdCtkm(id).stream().map(CtkmSachViewObject::new).collect(Collectors.toList());
            return ctkmSachService.getAll().stream().map(CtkmSachViewObject::new).collect(Collectors.toList());
        }

        @Override
        protected void done() {
            try {
                fillTableSP(get());
                loading.dispose();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
