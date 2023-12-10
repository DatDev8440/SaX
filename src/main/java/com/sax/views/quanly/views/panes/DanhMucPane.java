package com.sax.views.quanly.views.panes;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.sax.dtos.DanhMucDTO;
import com.sax.services.IDanhMucService;
import com.sax.services.impl.DanhMucService;
import com.sax.utils.ContextUtils;
import com.sax.utils.MsgBox;
import com.sax.utils.Session;
import com.sax.views.components.CustomTextArea;
import com.sax.views.components.ListPageNumber;
import com.sax.views.components.Loading;
import com.sax.views.components.Search;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.components.libraries.RoundPanel;
import com.sax.views.components.table.CustomHeaderTableCellRenderer;
import com.sax.views.components.table.CustomTableCellEditor;
import com.sax.views.components.table.CustomTableCellRender;
import com.sax.views.quanly.viewmodel.AbstractViewObject;
import com.sax.views.quanly.viewmodel.DanhMucViewObject;
import com.sax.views.quanly.viewmodel.SachViewObject;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTextField;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DanhMucPane extends JPanel {
    private JPanel danhMucPanel;
    private JPanel bg;
    private JXTable table;
    private JCheckBox cbkSelectedAll;
    private JButton btnDel;
    private JButton btnEdit;
    private JComboBox cboDanhMucCha;
    private JTextArea txtMoTa;
    private JTextField txtTen;
    private JPanel panelTT;
    private JButton btnSave;
    private Search timKiem;
    private JButton btnClear;
    private JPanel phanTrangPane;
    private JComboBox cboHienThi;
    private JList listPage;
    private IDanhMucService danhMucService = ContextUtils.getBean(DanhMucService.class);

    private Set<Integer> tempIdSet = new HashSet<>();
    private List<JCheckBox> listCbk = new ArrayList<>();
    private Loading loading = new Loading(this);

    private DefaultListModel listPageModel = new DefaultListModel();
    private Timer timer;

    public DanhMucPane() {
        initComponent();
        btnSave.addActionListener((e) -> save());
        btnDel.addActionListener((e) -> delete());
        btnClear.addActionListener((e) -> boLuaChon());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (table.getSelectedRow() >= 0) showItem();
            }
        });
        timKiem.txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                timer.restart();
            }
        });
        cbkSelectedAll.addActionListener((e) -> Session.chonTatCa(cbkSelectedAll, table, listCbk, tempIdSet));
    }

    private void boLuaChon() {
        table.clearSelection();
        fillDanhMucCha(-1);
    }

    public void initComponent() {
        panelTT.putClientProperty(FlatClientProperties.STYLE, "arc:10");
        ((DefaultTableModel) table.getModel()).setColumnIdentifiers(new String[]{"", "Id", "Tên danh mục", "Mô tả", "Tên danh mục cha"});
        fillDanhMucCha(-1);
        new Worker(0).execute();
        loading.setVisible(true);
        timer = new Timer(300, e -> {
            searchByKeyword();
            timer.stop();
        });
    }

    public void fillTable(List<AbstractViewObject> list) {
        Session.fillTable(list, table, cbkSelectedAll, tempIdSet, listCbk);
    }

    public void fillDanhMucCha(int id) {
        cboDanhMucCha.removeAllItems();
        cboDanhMucCha.addItem("-Không có danh mục-");
        if (id == -1)
            danhMucService.getAll().forEach(i -> cboDanhMucCha.addItem(i));
        else
            danhMucService.getAllDanhMucForUpdate(id).forEach(i -> cboDanhMucCha.addItem(i));
    }

    public void showItem() {
        Session.executorService.submit(() -> {
            int id = (int) table.getModel().getValueAt(table.getSelectedRow(), 1);
            DanhMucDTO dm = danhMucService.getById(id);
            txtTen.setText(String.valueOf(dm.getTenDanhMuc()));
            txtMoTa.setText(dm.getGhiChu());
            fillDanhMucCha(dm.getId());
            cboDanhMucCha.setSelectedItem(dm.getDanhMucCha());
            loading.dispose();
        });
        loading.setVisible(true);
    }

    public DanhMucDTO readForm() {
        String ten = txtTen.getText().trim();
        String mota = txtMoTa.getText().trim();
        DanhMucDTO dmCha = (cboDanhMucCha.getSelectedItem() instanceof String) ? null : (DanhMucDTO) cboDanhMucCha.getSelectedItem();
        return new DanhMucDTO(ten, dmCha, mota);
    }

    public void save() {
        DanhMucDTO dm = readForm();
        if (dm != null) {
            new javax.swing.SwingWorker<List<AbstractViewObject>, Integer>() {
                @Override
                protected List<AbstractViewObject> doInBackground() {
                    try {
                        if (table.getSelectedRow() >= 0) {
                            dm.setId((int) table.getModel().getValueAt(table.getSelectedRow(), 1));
                            danhMucService.update(dm);
                        } else danhMucService.insert(dm);
                    } catch (SQLServerException | InvalidDataAccessApiUsageException e) {
                        MsgBox.alert(DanhMucPane.this, e.getMessage());
                    }
                    return danhMucService.getAll().stream().map(i -> new DanhMucViewObject(i)).collect(Collectors.toList());
                }

                @Override
                protected void done() {
                    try {
                        fillTable(get());
                        fillDanhMucCha(-1);
                        loading.dispose();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }.execute();
            loading.setVisible(true);
        }
    }

    public void delete() {
        if (!tempIdSet.isEmpty()) {
            boolean check = MsgBox.confirm(this, "Bạn có muốn xoá " + tempIdSet.size() + " danh mục này không?");
            if (check) {
                new javax.swing.SwingWorker<List<AbstractViewObject>, Integer>() {
                    @Override
                    protected List<AbstractViewObject> doInBackground() {
                        try {
                            danhMucService.deleteAllDanhMucSach(tempIdSet);
                            tempIdSet.clear();
                        } catch (RuntimeException e) {
                            MsgBox.alert(DanhMucPane.this, e.getMessage());
                        }
                        return danhMucService.getAll().stream().map(i -> new DanhMucViewObject(i)).collect(Collectors.toList());
                    }

                    @Override
                    protected void done() {
                        try {
                            fillTable(get());
                            fillDanhMucCha(-1);
                            loading.dispose();
                            MsgBox.alert(DanhMucPane.this, "Xoá một danh mục thành công!");
                        } catch (InterruptedException | ExecutionException e) {
                            MsgBox.alert(DanhMucPane.this, "Danh mục đang chứa sản phẩm, bạn không thể xoá!");
                            loading.dispose();
                        }
                    }
                }.execute();
                loading.setVisible(true);
            }
        } else MsgBox.alert(this, "Vui lòng tick vào ít nhất một danh mục!");
    }

    public void searchByKeyword() {
        String keyword = timKiem.txtSearch.getText();
        if (!keyword.isEmpty()) {
            fillTable(danhMucService.searchByKeyword(keyword).stream().map(DanhMucViewObject::new).collect(Collectors.toList()));
            phanTrangPane.setVisible(false);
        } else {
            fillTable(danhMucService.getAll().stream().map(DanhMucViewObject::new).collect(Collectors.toList()));
            phanTrangPane.setVisible(true);
        }
    }

    private void createUIComponents() {
        danhMucPanel = this;
        bg = new RoundPanel(10);
        btnSave = new ButtonToolItem("add.svg", "add.svg");
        btnDel = new ButtonToolItem("trash-c.svg", "trash-c.svg");
        btnClear = new ButtonToolItem("clear-c.svg", "clear-c.svg");
        btnEdit = new ButtonToolItem("pencil.svg", "pencil.svg");
        txtMoTa = new CustomTextArea();

        listPage = new ListPageNumber();
    }

    class Worker extends SwingWorker<List<AbstractViewObject>, Integer> {
        int page;

        public Worker(int page) {
            this.page = page;
        }

        @Override
        protected List<AbstractViewObject> doInBackground() {
            return danhMucService.getAll().stream().map(DanhMucViewObject::new).collect(Collectors.toList());
        }

        @Override
        protected void done() {
            try {
                fillTable(get());
                loading.dispose();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


