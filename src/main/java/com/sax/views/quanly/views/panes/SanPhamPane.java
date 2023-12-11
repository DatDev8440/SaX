package com.sax.views.quanly.views.panes;

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
import com.sax.views.quanly.viewmodel.AbstractViewObject;
import com.sax.views.quanly.viewmodel.SachViewObject;
import lombok.Getter;
import lombok.Setter;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXTable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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
    private final ISachService sachService = ContextUtils.getBean(SachService.class);
    private final Set tempIdSet = new HashSet();
    private final List<JCheckBox> listCbk = new ArrayList<>();
    private final Loading loading = new Loading(this);

    private final DefaultListModel listPageModel = new DefaultListModel();
    @Getter
    @Setter
    private int sizeValue = 14;
    @Getter
    @Setter
    private int pageValue = 1;
    @Getter
    @Setter
    private Pageable pageable = PageRequest.of(pageValue - 1, sizeValue);
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
        new Worker().execute();
        loading.setVisible(true);
        timer = new Timer(300, e -> {
            searchByKeyword();
            timer.stop();
        });
    }

    public void fillTable(List<AbstractViewObject> list) {
        Session.fillTable(list, table, cbkSelectedAll, tempIdSet, listCbk);
    }

    private void nhapHang() {
        if (table.getSelectedRow() >= 0) {
            NhapHangDialog nhapHangDialog = new NhapHangDialog();
            nhapHangDialog.parentPane = this;
            nhapHangDialog.id = (int) table.getValueAt(table.getSelectedRow(), 1);
            nhapHangDialog.setTitle(sachService.getById((int) table.getValueAt(table.getSelectedRow(), 1)).getTenSach());
            nhapHangDialog.fillTable();
            nhapHangDialog.setVisible(true);
            table.clearSelection();
        } else MsgBox.alert(this, "Vui lòng chọn một sản phẩm!");
    }

    private void add() {
        SachDialog sachDialog = new SachDialog();
        sachDialog.parentPane = this;
        sachDialog.lblTitle.setText("Thêm mới sách");
        sachDialog.setVisible(true);
    }

    private void update() {
        if (table.getSelectedRow() >= 0) {
            Session.executorService.submit(() -> {
                SachDialog sachDialog = new SachDialog();
                sachDialog.parentPane = this;
                sachDialog.id = (int) table.getValueAt(table.getSelectedRow(), 1);
                sachDialog.fillForm();
                loading.dispose();
                sachDialog.setVisible(true);
            });
            loading.setVisible(true);
        } else MsgBox.alert(this, "Vui lòng chọn một sản phẩm!");
    }

    private void delete() {
        if (!tempIdSet.isEmpty()) {
            boolean check = MsgBox.confirm(this, "Bạn có muốn xoá " + tempIdSet.size() + " sản phẩm này không?");
            if (check) {
                try {
                    sachService.deleteAll(tempIdSet);
                    cbkSelectedAll.setSelected(false);
                } catch (Exception e) {
                    MsgBox.alert(this, e.getMessage());
                }
                pageValue = sachService.getTotalPage(sizeValue) < pageValue ? sachService.getTotalPage(sizeValue) : pageValue;
                pageable = PageRequest.of(pageValue > 0 ? pageValue - 1 : pageValue, sizeValue);
                fillTable(sachService.getPage(pageable).stream().map(SachViewObject::new).collect(Collectors.toList()));
                fillListPage();
                loading.dispose();
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

    public void fillListPage() {
        Session.fillListPage(pageValue, listPageModel, sachService, sizeValue, listPage);
    }

    public void selectPageDisplay() {
        if (listPage.getSelectedValue() instanceof Integer) {
            pageValue = Integer.parseInt(listPage.getSelectedValue().toString());
            pageable = PageRequest.of(pageValue - 1, sizeValue);
            new Worker().execute();
            loading.setVisible(true);
        }
    }

    public void selectSizeDisplay() {
        sizeValue = Integer.parseInt(cboHienThi.getSelectedItem().toString());
        pageValue = 1;
        pageable = PageRequest.of(pageValue - 1, sizeValue);
        new Worker().execute();
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
        @Override
        protected List<AbstractViewObject> doInBackground() {
            return sachService.getPage(pageable).stream().map(SachViewObject::new).collect(Collectors.toList());
        }

        @Override
        protected void done() {
            try {
                fillTable(get());
                if (table.getRowCount() > 0) fillListPage();
                loading.dispose();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
