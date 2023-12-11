package com.sax.views.quanly.views.dialogs;

import com.sax.utils.Session;
import com.sax.views.components.ListPageNumber;
import com.sax.views.components.Search;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.components.libraries.RoundPanel;
import com.sax.views.quanly.viewmodel.AbstractViewObject;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThungRacDialog {
    private JPanel bg;
    private JXTable table;
    private JPanel phanTrangPane;
    private JComboBox cboHienThi;
    private JList listPage;
    private Search timKiem;
    private JCheckBox cbkSelectedAll;
    private JButton btnDel;
    private JButton btnRestore;
    private JPanel thungRacPane;
    private final Set tempIdSet = new HashSet();
    private final List<JCheckBox> listCbk = new ArrayList<>();

    public ThungRacDialog() {
    }

    public void fillTable(List<AbstractViewObject> list)
    {
        Session.fillTable(list, table, cbkSelectedAll, tempIdSet, listCbk);
    }

    private void createUIComponents() {
        bg = new RoundPanel(10);
        btnDel = new ButtonToolItem("trash-c.svg", "trash-c.svg");
        btnRestore = new ButtonToolItem("pencil.svg", "pencil.svg");

        listPage = new ListPageNumber();
    }
}
