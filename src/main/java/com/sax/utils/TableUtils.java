package com.sax.utils;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TableUtils {
    public static List<Object[]> convertTableModelToList(TableModel model) {
        List<Object[]> dataList = new ArrayList<>();
        int rowCount = model.getRowCount();
        int columnCount = model.getColumnCount();

        for (int row = 0; row < rowCount; row++) {
            Object[] rowData = new Object[columnCount];
            for (int column = 0; column < columnCount; column++) {
                rowData[column] = model.getValueAt(row, column);
            }
            dataList.add(rowData);
        }

        return dataList;
    }

    public static void sortDataListByColumn(List<Object[]> dataList, int column, int type) {
        Collections.sort(dataList, new Comparator<Object[]>() {
            @Override
            public int compare(Object[] row1, Object[] row2) {
                Object value1 = row1[column];
                Object value2 = row2[column];

                if (value1 == null && value2 == null) {
                    return 0;
                } else if (value1 == null) {
                    return -1;
                } else if (value2 == null) {
                    return 1;
                } else {
                    if (type == 0) return value1.toString().compareTo(value2.toString());
                    return value2.toString().compareTo(value1.toString());
                }
            }
        });
    }
}
