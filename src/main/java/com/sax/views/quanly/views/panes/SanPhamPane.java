package com.sax.views.quanly.views.panes;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.sax.dtos.SachDTO;
import com.sax.services.ISachService;
import com.sax.services.impl.SachService;
import com.sax.utils.ContextUtils;
import com.sax.utils.MsgBox;
import com.sax.utils.Session;
import com.sax.views.components.ListPageNumber;
import com.sax.views.components.Loading;
import com.sax.views.components.Search;
import com.sax.views.quanly.views.dialogs.NhapHangDialog;
import com.sax.views.quanly.views.dialogs.SachDialog;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.components.libraries.RoundPanel;
import com.sax.views.components.table.CustomHeaderTableCellRenderer;
import com.sax.views.components.table.CustomTableCellEditor;
import com.sax.views.components.table.CustomTableCellRender;
import com.sax.views.quanly.viewmodel.AbstractViewObject;
import com.sax.views.quanly.viewmodel.SachViewObject;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXTable;
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

public class SanPhamPane extends JPanel {
    private JXTable table;
    private JPanel bg;
    private JPanel sanPhamPanel;
    private JButton btnAdd;
    private JButton btnDel;
    private JButton btnEdit;
    private JCheckBox cbkSelectedAll;
    private JButton importExcel;
    private JButton exportExcel;
    private JButton btnNhapHang;
    private JPanel phanTrangPane;
    private JList listPage;
    private JComboBox cboHienThi;
    private Search timKiem;
    private ISachService sachService = ContextUtils.getBean(SachService.class);
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Set tempIdSet = new HashSet();
    private List<JCheckBox> listCbk = new ArrayList<>();
    private Loading loading = new Loading();

    private DefaultListModel listPageModel = new DefaultListModel();
    private int size = 14;
    private Pageable pageable = PageRequest.of(0, 14);
    private Timer timer;

    public SanPhamPane() {
        initComponent();
        btnAdd.addActionListener((e) -> add());
        btnEdit.addActionListener((e) -> update());
        btnDel.addActionListener((e) -> delete());
        importExcel.addActionListener((e) -> importExcel());
        btnNhapHang.addActionListener(e -> nhapHang());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) update();
            }
        });
        listPage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectPageDisplay();
            }
        });
        cboHienThi.addActionListener((e) -> selectSizeDisplay());
        timKiem.txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                timer.restart();
            }
        });
        cbkSelectedAll.addActionListener((e) -> Session.chonTatCa(cbkSelectedAll, table, listCbk, tempIdSet));
    }

    private void importExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showSaveDialog(null);
    }

    public void initComponent() {
        ((DefaultTableModel) table.getModel()).setColumnIdentifiers(new String[]{"", "Mã sách", "Barcode", "Tên sách", "Số lượng", "Giá bán", "Danh mục", "Ngày thêm", "Trạng thái"});
        new Worker(0).execute();
        loading.setVisible(true);
        timer = new Timer(300, e -> {
            searchByKeyword();
            timer.stop();
        });
    }

    public void fillTable(List<AbstractViewObject> list) {
        Session.fillTable(list, table, cbkSelectedAll, executorService, tempIdSet, listCbk);
    }

    private void nhapHang() {
        if (table.getSelectedRow() >= 0) {
            NhapHangDialog nhapHangDialog = new NhapHangDialog();
            nhapHangDialog.parentPane = this;
            nhapHangDialog.id = (int) table.getValueAt(table.getSelectedRow(), 1);
            nhapHangDialog.fillTable();
            nhapHangDialog.setVisible(true);
            table.clearSelection();
        } else MsgBox.alert(this, "Vui lòng chọn một sản phẩm!");
    }

    private void add() {
        SachDialog sachDialog = new SachDialog();
        sachDialog.parentPane = this;
        sachDialog.lblTitle.setText("Thêm mới sách");
        sachDialog.pageable = (listPageModel.getSize() > 0) ? PageRequest.of(listPageModel.getSize() - 1, 14) : PageRequest.of(listPageModel.getSize(), 14);
        sachDialog.setVisible(true);
        table.clearSelection();
    }

    private void update() {
        if (table.getSelectedRow() >= 0) {
            executorService.submit(() -> {
                SachDialog sachDialog = new SachDialog();
                sachDialog.parentPane = this;
                sachDialog.id = (int) table.getValueAt(table.getSelectedRow(), 1);
                sachDialog.pageable = pageable;
                sachDialog.fillForm();
                loading.dispose();
                sachDialog.setVisible(true);
                table.clearSelection();
            });
            loading.setVisible(true);
        } else MsgBox.alert(this, "Vui lòng chọn một sản phẩm!");
    }

    private void delete() {
        if (!tempIdSet.isEmpty()) {
            boolean check = MsgBox.confirm(null, "Bạn có muốn xoá " + tempIdSet.size() + " sản phẩm này không?");
            if (check) {
                try {
                    sachService.deleteAll(tempIdSet);
                    cbkSelectedAll.setSelected(false);
                    fillTable(sachService.getPage(pageable).stream().map(SachViewObject::new).collect(Collectors.toList()));
                    fillListPage(pageable.getPageNumber());
                    loading.dispose();
                } catch (Exception e) {
                    MsgBox.alert(this, e.getMessage());
                }
            }
        } else MsgBox.alert(this, "Vui lòng tick vào ít nhất một sản phẩm!");
    }

    public void searchByKeyword() {
        String keyword = timKiem.txtSearch.getText();
        if (!keyword.isEmpty()) {
            fillTable(sachService.searchByKeyword(keyword).stream().map(SachViewObject::new).collect(Collectors.toList()));
            phanTrangPane.setVisible(false);
        } else {
            fillTable(sachService.getAll().stream().map(SachViewObject::new).collect(Collectors.toList()));
            phanTrangPane.setVisible(true);
        }
    }

    public void fillListPage(int value) {
        Session.fillListPage(value, listPageModel, sachService, pageable, listPage);
    }

    public void selectPageDisplay() {
        if (listPage.getSelectedValue() instanceof Integer) {
            int page = Integer.parseInt(listPage.getSelectedValue().toString()) - 1;
            pageable = PageRequest.of(page, size);
            new Worker(page).execute();
            loading.setVisible(true);
        }
    }

    public void selectSizeDisplay() {
        size = Integer.parseInt(cboHienThi.getSelectedItem().toString());
        pageable = PageRequest.of(0, size);
        new Worker(pageable.getPageNumber()).execute();
        loading.setVisible(true);
    }

    private void createUIComponents() {
        sanPhamPanel = this;
        bg = new RoundPanel(10);
        btnAdd = new ButtonToolItem("add.svg", "add.svg");
        btnDel = new ButtonToolItem("trash-c.svg", "trash-c.svg");
        btnEdit = new ButtonToolItem("pencil.svg", "pencil.svg");
        importExcel = new ButtonToolItem("excel-c.svg", "excel-c.svg");
        exportExcel = new ButtonToolItem("excel-c.svg", "excel-c.svg");
        btnNhapHang = new ButtonToolItem("product-c.svg", "product-c.svg");

        listPage = new ListPageNumber();
    }

    class Worker extends SwingWorker<List<AbstractViewObject>, Integer> {
        int page;

        public Worker(int page) {
            this.page = page;
        }

        @Override
        protected List<AbstractViewObject> doInBackground() {
            return sachService.getPage(pageable).stream().map(SachViewObject::new).collect(Collectors.toList());
        }

        @Override
        protected void done() {
            try {
                fillTable(get());
                fillListPage(page);
                loading.dispose();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}