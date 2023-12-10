package com.sax.views.quanly.views.panes;

import com.sax.dtos.DonHangDTO;
import com.sax.services.IDonHangChiTetService;
import com.sax.services.IDonHangService;
import com.sax.services.impl.DonHangChiTietService;
import com.sax.services.impl.DonHangService;
import com.sax.utils.ContextUtils;
import com.sax.utils.MsgBox;
import com.sax.utils.Session;
import com.sax.views.components.ListPageNumber;
import com.sax.views.components.Loading;
import com.sax.views.components.Search;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.components.libraries.RoundPanel;
import com.sax.views.nhanvien.dialog.hoadon.HoaDonDialog;
import com.sax.views.quanly.viewmodel.AbstractViewObject;
import com.sax.views.quanly.viewmodel.DonHangViewObject;
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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DonHangPane extends JPanel {
    private JXTable table;
    private JPanel bg;
    private JPanel donHangPanel;
    private JButton btnAdd;
    private JButton btnDel;
    private JButton btnEdit;
    private JCheckBox cbkSelectedAll;
    private Search timKiem;
    private JPanel phanTrangPane;
    private JComboBox cboHienThi;
    private JList listPage;
    private IDonHangService donHangService = ContextUtils.getBean(DonHangService.class);
    private IDonHangChiTetService donHangChiTetService = ContextUtils.getBean(DonHangChiTietService.class);
    private Set tempIdSet = new HashSet();
    private List<JCheckBox> listCbk = new ArrayList<>();
    private Loading loading = new Loading(this);

    private DefaultListModel listPageModel = new DefaultListModel();
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

    public DonHangPane() {
        initComponent();
        btnEdit.addActionListener((e) -> update());
        btnDel.addActionListener((e) -> delete());
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

    public void initComponent() {
        ((DefaultTableModel) table.getModel()).setColumnIdentifiers(new String[]{"", "Mã đơn hàng", "Tên khách hàng", "Nhân viên", "Tiền hàng", "Chiết khấu", "Tổng tiền", "Phương thức thanh toán", "Ngày tạo"});

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

    private void update() {
        if (table.getSelectedRow() >= 0) {
            Session.executorService.submit(() -> {
                try {
                    DonHangDTO donHangDTO = donHangService.getById((int) table.getValueAt(table.getSelectedRow(), 1));
                    donHangDTO.setChiTietDonHangs(donHangChiTetService.getAllByDonHang(donHangDTO));
                    HoaDonDialog hoaDonDialog = new HoaDonDialog(this, donHangDTO, false);
                    loading.dispose();
                    hoaDonDialog.setVisible(true);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            loading.setVisible(true);
        } else MsgBox.alert(this, "Vui lòng chọn một đơn hàng!");
    }

    private void delete() {
        if (!tempIdSet.isEmpty()) {
            boolean check = MsgBox.confirm(this, "Bạn có muốn xoá " + tempIdSet.size() + " đơn hàng này không?");
            if (check) {
                try {
                    donHangService.deleteAll(tempIdSet);
                    tempIdSet.clear();
                } catch (Exception e) {
                    MsgBox.alert(this, e.getMessage());
                }
                fillTable(donHangService.getAll().stream().map(i -> new DonHangViewObject(i)).collect(Collectors.toList()));
            }
        }
        MsgBox.alert(this, "Vui lòng tick vào ít nhất một đơn hàng!");
    }

    public void searchByKeyword() {
        String keyword = timKiem.txtSearch.getText();
        if (!keyword.isEmpty()) {
            fillTable(donHangService.searchByKeyword(keyword).stream().map(DonHangViewObject::new).collect(Collectors.toList()));
            phanTrangPane.setVisible(false);
        } else {
            fillTable(donHangService.getAll().stream().map(DonHangViewObject::new).collect(Collectors.toList()));
            phanTrangPane.setVisible(true);
        }
    }

    public void fillListPage() {
        Session.fillListPage(pageValue, listPageModel, donHangService, sizeValue, listPage);
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
        donHangPanel = this;
        bg = new RoundPanel(10);
        btnAdd = new ButtonToolItem("add.svg", "add.svg");
        btnDel = new ButtonToolItem("trash-c.svg", "trash-c.svg");
        btnEdit = new ButtonToolItem("pencil.svg", "pencil.svg");

        listPage = new ListPageNumber();
    }

    class Worker extends SwingWorker<List<AbstractViewObject>, Integer> {

        @Override
        protected List<AbstractViewObject> doInBackground() {
            return donHangService.getPage(pageable).stream().map(DonHangViewObject::new).collect(Collectors.toList());
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
