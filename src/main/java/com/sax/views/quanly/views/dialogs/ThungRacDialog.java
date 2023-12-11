package com.sax.views.quanly.views.dialogs;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.sax.Application;
import com.sax.services.IDonHangService;
import com.sax.services.impl.DonHangService;
import com.sax.utils.ContextUtils;
import com.sax.utils.MsgBox;
import com.sax.utils.Session;
import com.sax.views.components.ListPageNumber;
import com.sax.views.components.Loading;
import com.sax.views.components.Search;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.components.libraries.RoundPanel;
import com.sax.views.quanly.viewmodel.AbstractViewObject;
import com.sax.views.quanly.viewmodel.DonHangViewObject;
import com.sax.views.quanly.views.panes.DonHangPane;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.validator.Msg;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXTable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ThungRacDialog extends JDialog{
    private JPanel bg;
    private JXTable table;
    private JPanel phanTrangPane;
    private JComboBox cboHienThi;
    private JList listPage;
    private Search timKiem;
    private JCheckBox cbkSelectedAll;
    private JButton btnDel;
    private JButton btnRestore;
    private JPanel contentPane;
    private final Set tempIdSet = new HashSet();
    private final List<JCheckBox> listCbk = new ArrayList<>();
    private final IDonHangService donHangService = ContextUtils.getBean(DonHangService.class);
    private Loading loading = new Loading(this);

    private DefaultListModel listPageModel = new DefaultListModel();
    private int sizeValue = 14;
    private int pageValue = 1;
    private Pageable pageable = PageRequest.of(pageValue - 1, sizeValue);
    private Timer timer;

    public ThungRacDialog() {
        initComponent();
        btnDel.addActionListener((e) -> delete());
    }

    private void initComponent() {
        setContentPane(contentPane);
        setModal(true);
        ((DefaultTableModel) table.getModel()).setColumnIdentifiers(new String[]{"", "Mã đơn hàng", "Tên khách hàng", "Nhân viên", "Tiền hàng", "Chiết khấu", "Tổng tiền", "Phương thức thanh toán", "Ngày tạo"});
        new Worker().execute();
        loading.setVisible(true);
        pack();
        setLocationRelativeTo(Application.app);
    }

    private void fillTable(List<AbstractViewObject> list)
    {
        Session.fillTable(list, table, cbkSelectedAll, tempIdSet, listCbk);
    }

    private void delete()
    {
        if (tempIdSet.size() > 0)
        {
            try {
                donHangService.deleteAll(tempIdSet);
                new Worker().execute();
                loading.setVisible(true);
            } catch (SQLServerException e) {
                MsgBox.alert(this, e.getMessage());
            }
        } else MsgBox.alert(this, "Vui lòng tick ít nhất một đơn hàng!");
    }

    private void restore()
    {
        if (tempIdSet.size() > 0)
        {
            try {
                donHangService.updateStatus(tempIdSet);
                new Worker().execute();
                loading.setVisible(true);
            } catch (SQLServerException e) {
                MsgBox.alert(this, e.getMessage());
            }
        } else MsgBox.alert(this, "Vui lòng tick ít nhất một đơn hàng!");
    }

    private void fillListPage() {
        Session.fillListPage(pageValue, listPageModel, donHangService, sizeValue, listPage);
    }

    private void selectPageDisplay() {
        if (listPage.getSelectedValue() instanceof Integer) {
            pageValue = Integer.parseInt(listPage.getSelectedValue().toString());
            pageable = PageRequest.of(pageValue - 1, sizeValue);
            new Worker().execute();
            loading.setVisible(true);
        }
    }

    private void selectSizeDisplay() {
        sizeValue = Integer.parseInt(cboHienThi.getSelectedItem().toString());
        pageValue = 1;
        pageable = PageRequest.of(pageValue - 1, sizeValue);
        new Worker().execute();
        loading.setVisible(true);
    }

    private void createUIComponents() {
        bg = new RoundPanel(10);
        btnDel = new ButtonToolItem("trash-c.svg", "trash-c.svg");
        btnRestore = new ButtonToolItem("pencil.svg", "pencil.svg");

        listPage = new ListPageNumber();
    }

    class Worker extends SwingWorker<List<AbstractViewObject>, Integer> {

        @Override
        protected List<AbstractViewObject> doInBackground() {
            return donHangService.getPageHidenInvoice(pageable).stream().map(DonHangViewObject::new).collect(Collectors.toList());
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
