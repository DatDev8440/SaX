package com.sax.views.quanly.viewmodel;

import lombok.Data;

import javax.swing.*;
import java.util.List;
import java.util.Set;

@Data
public abstract class AbstractViewObject {
    protected JCheckBox checkBoxDelete;
    protected int id;
    protected String name;

    public AbstractViewObject() {
    }

    public AbstractViewObject(int id, String name) {
        checkBoxDelete = new JCheckBox();
        this.id = id;
        this.name = name;
    }


    public abstract Object[] toObject(JTable tbl, Set tempIdSet, List<JCheckBox> setCbk);
}
