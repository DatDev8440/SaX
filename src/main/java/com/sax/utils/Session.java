package com.sax.utils;

import com.sax.Application;
import com.sax.dtos.AccountDTO;
import com.sax.dtos.DonHangDTO;
import com.sax.services.ICrudServices;
import com.sax.views.LoginView;
import com.sax.views.components.table.CustomHeaderTableCellRenderer;
import com.sax.views.components.table.CustomTableCellEditor;
import com.sax.views.components.table.CustomTableCellRender;
import com.sax.views.nhanvien.cart.CartModel;
import com.sax.views.nhanvien.dialog.UserPopup;
import com.sax.views.nhanvien.doncho.DonChoViewObject;
import com.sax.views.quanly.viewmodel.AbstractViewObject;
import com.sax.views.quanly.viewmodel.DonHangViewObject;
import org.jdesktop.swingx.JXTable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.concurrent.ExecutorService;

public class Session {
    public static AccountDTO accountid;
    public static String otp;
    public static JLabel lblName;
    public static JPanel avatar;
    public static List<DonChoViewObject> listDonCho = new ArrayList<>();
    public static ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final String CONFIG_FILE_PATH = "config.yaml";

    public static void logout() {
        boolean check = MsgBox.confirm(Application.app, "Bạn có thực sự muốn đăng xuất không?");
        if (check) {
            Application.app.setContentPane(new LoginView());
            Application.app.pack();
            Application.app.setLocationRelativeTo(null);
            accountid = null;
            Cart.getCart().clear();
        }
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void chonTatCa(JCheckBox cbkSelectedAll, JTable table, List<JCheckBox> listCbk, Set tempIdSet) {
        if (cbkSelectedAll.isSelected()) {
            for (int i = 0; i < table.getRowCount(); i++) {
                listCbk.get(i).setSelected(true);
                tempIdSet.add((int) table.getValueAt(i, 1));
                table.repaint();
            }
        } else {
            for (int i = 0; i < table.getRowCount(); i++)
                listCbk.get(i).setSelected(false);
            tempIdSet.clear();
            table.repaint();
        }
    }

    public static void fillTable(List<AbstractViewObject> list, JXTable table, JCheckBox cbkSelectedAll, Set tempIdSet, List<JCheckBox> listCbk) {
        tempIdSet.clear();
        listCbk.clear();
        cbkSelectedAll.setSelected(false);
        ((DefaultTableModel) table.getModel()).setRowCount(0);
        list.forEach(i -> ((DefaultTableModel) table.getModel()).addRow(i.toObject(table, tempIdSet, listCbk)));
        table.setDefaultEditor(Object.class, null);
        table.getTableHeader().setDefaultRenderer(new CustomHeaderTableCellRenderer());
        table.getTableHeader().setEnabled(false);
        table.getTableHeader().setPreferredSize(new Dimension(-1, 28));
        table.getColumnModel().getColumn(0).setCellEditor(new CustomTableCellEditor(list));
        table.setDefaultRenderer(Object.class, new CustomTableCellRender(list));
        table.packAll();
        Application.app.pack();
        table.getColumns().forEach(i -> i.sizeWidthToFit());
        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void fillListPage(int value, DefaultListModel listPageModel, ICrudServices services, int amount, JList listPage) {
        listPageModel.clear();
        int totalPage = services.getTotalPage(amount);

        if (totalPage < 10) {
            for (int i = 1; i <= totalPage; i++) {
                listPageModel.addElement(i);
            }
        } else {
            if (value < 4) {
                for (int i = 0; i < 8; i++) {
                    listPageModel.addElement(i + 1);
                }
                listPageModel.addElement("...");
                listPageModel.addElement(totalPage);

            } else if (totalPage - value >= 4) {
                for (int i = value - 3; i < value; i++) {
                    listPageModel.addElement(i < 0 ? value + i : i);
                }
                for (int i = value; i <= value + 3; i++) {
                    listPageModel.addElement(i);
                }
                listPageModel.addElement("...");
                listPageModel.addElement(totalPage);
            } else {
                for (int i = totalPage - 8; i <= totalPage; i++) {
                    listPageModel.addElement(i);
                }
            }
        }

        listPage.setModel(listPageModel);
        listPage.setSelectedValue(value, false);
        listPage.repaint();
    }

    public static Map<String, String> getConfig() {
        Map<String, String> data = null;
        try {
            FileInputStream input = new FileInputStream(CONFIG_FILE_PATH);
            Yaml yaml = new Yaml();
            data = yaml.load(input);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            MsgBox.alert(null, "Lỗi đọc file config.yaml");
            System.exit(0);
        }
        return data;
    }

    public static boolean createDefaultConfigFile() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                writeDefaultConfig();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static String isConfigFileCreated() {
        File configFile = new File(CONFIG_FILE_PATH);
        return configFile.getAbsolutePath();
    }

    private static void writeDefaultConfig() throws IOException {
        Map<String, Object> defaultConfig = new HashMap<>();
        defaultConfig.put("server", "'localhost'");
        defaultConfig.put("password", "''");
        defaultConfig.put("databaseName", "''");
        defaultConfig.put("port", "''1433''");
        defaultConfig.put("username", "''sa''");

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setAllowReadOnlyProperties(true);

        Representer representer = new Representer();
        representer.addClassTag(defaultConfig.getClass(), Tag.MAP);

        Yaml yaml = new Yaml(representer, options);
        try (FileWriter writer = new FileWriter(CONFIG_FILE_PATH)) {
            yaml.dump(defaultConfig, writer);
        }
    }
    public static void reload(){
     try {
         ((JLayeredPane) Session.avatar.getComponent(0)).getComponent(0).addMouseListener(new MouseAdapter() {
             @Override
             public void mouseClicked(MouseEvent e) {
                 UserPopup userPopup = new UserPopup();
                 userPopup.setVisible(true);
             }
         });
     }catch (Exception e){

     }
    }
}
