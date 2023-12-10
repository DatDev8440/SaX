package com.sax.views.quanly.views.dialogs;

import com.sax.Application;
import com.sax.dtos.DanhMucDTO;
import com.sax.dtos.SachDTO;
import com.sax.services.IDanhMucService;
import com.sax.services.ISachService;
import com.sax.services.impl.DanhMucService;
import com.sax.services.impl.SachService;
import com.sax.utils.ContextUtils;
import com.sax.utils.CurrencyConverter;
import com.sax.utils.ImageUtils;
import com.sax.utils.MsgBox;
import com.sax.views.components.Loading;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.components.libraries.RoundPanel;
import com.sax.views.quanly.viewmodel.SachViewObject;
import com.sax.views.quanly.views.panes.SanPhamPane;
import org.springframework.data.domain.PageRequest;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SachDialog extends JDialog {
    private JPanel contentPane;
    private JTextField txtBarcode;
    private JButton btnSave;
    private JList<DanhMucDTO> danhMuc;
    private JComboBox cboNXB;
    private JFormattedTextField txtGiaBan;
    private JTextField txtTen;
    private JPanel panelImage;
    private JButton btnScan;
    private JButton btnChonAnh;
    private JTextField txtSL;
    private JPanel nhapTTPanel;
    private JPanel danhMucPanel;
    private JPanel thumbPanel;
    private Loading loading = new Loading(this);
    private ISachService sachService = ContextUtils.getBean(SachService.class);
    private IDanhMucService danhMucService = ContextUtils.getBean(DanhMucService.class);
    private DefaultListModel<DanhMucDTO> danhMucModel = new DefaultListModel<>();
    private String image;
    private JRadioButton rdoAn;
    private JRadioButton rdoHienThi;

    public JLabel lblTitle;
    public int id;
    public SanPhamPane parentPane;

    public SachDialog() {
        initComponent();

        btnSave.addActionListener((e) -> save());
        btnScan.addActionListener((e) -> scanQR());
        btnChonAnh.addActionListener(e -> image = ImageUtils.openImageFile(panelImage));
    }

    private void initComponent() {
        setContentPane(contentPane);
        setModal(true);
        pack();
        setLocationRelativeTo(Application.app);
        txtGiaBan.setFormatterFactory(CurrencyConverter.getVnCurrency());
        fillDanhMuc();
    }

    public void fillDanhMuc() {
        danhMucService.getAll().forEach(i -> danhMucModel.addElement(i));
        danhMuc.setModel(danhMucModel);
    }

    public void fillForm() {
        if (id > 0) {
            SachDTO sachDTO = sachService.getById(id);
            lblTitle.setText("Cập nhật sách " + sachDTO.getId() + " - " + sachDTO.getTenSach());
            txtBarcode.setText(sachDTO.getBarCode());
            txtTen.setText(sachDTO.getTenSach());
            txtGiaBan.setText(String.valueOf(sachDTO.getGiaBan()));
            txtSL.setText(String.valueOf(sachDTO.getSoLuong()));
            cboNXB.setSelectedItem(sachDTO.getNxb());
            if (sachDTO.getTrangThai()) rdoHienThi.setSelected(true);
            else rdoAn.setSelected(true);
            danhMuc.setSelectedIndices(sachDTO.getSetDanhMuc().stream().mapToInt(i -> danhMucModel.indexOf(i)).toArray());
            panelImage.add(ImageUtils.getCircleImage(sachDTO.getHinhAnh(), 220, 20, null, 0));
            image = "images/" + sachDTO.getHinhAnh();
            loading.dispose();
        }
    }

    private void save() {
        SachDTO dto = readForm();
        if (dto != null) {
            try {
                if (id > 0) {
                    dto.setId(id);
                    sachService.update(dto);
                } else {
                    sachService.insert(dto);
                    parentPane.setPageValue(sachService.getTotalPage(parentPane.getSizeValue()));
                    parentPane.setPageable(PageRequest.of(parentPane.getPageValue() - 1, parentPane.getSizeValue()));
                    parentPane.fillListPage();
                }
                parentPane.fillTable(sachService.getPage(parentPane.getPageable()).stream().map(SachViewObject::new).collect(Collectors.toList()));
                dispose();
            }  catch (Exception e) {
                MsgBox.alert(this,e.getMessage());
            }
        }
    }

    private SachDTO readForm() {
        SachDTO sachDTO = new SachDTO();

        String barcode = txtBarcode.getText().trim();
        sachDTO.setBarCode(barcode);

        String tenSach = txtTen.getText().trim();
        sachDTO.setTenSach(tenSach);

        String giaBan = txtGiaBan.getText().trim();
        try {
            sachDTO.setGiaBan(CurrencyConverter.parseLong(giaBan));
        } catch (NumberFormatException ex) {
            MsgBox.alert(this, "Giá bán phải là số!");
            return null;
        }

        String soLuong = txtSL.getText().trim();
        try {
            sachDTO.setSoLuong(Integer.parseInt(soLuong));
        } catch (NumberFormatException ex) {
            MsgBox.alert(this, "Số lượng phải là số!");
            return null;
        }

        String nxb = cboNXB.getSelectedItem().toString();
        sachDTO.setNxb(nxb);

        Set<DanhMucDTO> danhMucDTOS = new HashSet<>(danhMuc.getSelectedValuesList());
        sachDTO.setSetDanhMuc(danhMucDTOS);

        sachDTO.setHinhAnh(image);
        boolean trangThai = rdoHienThi.isSelected() ? true : false;
        sachDTO.setTrangThai(trangThai);
        return sachDTO;
    }

    private void scanQR() {
        new CameraDialog(txtBarcode).setVisible(true);
    }

    private void createUIComponents() {
        btnScan = new ButtonToolItem("barcode.svg", "barcode.svg");
        btnSave = new ButtonToolItem("", "");
        btnChonAnh = new ButtonToolItem("image-c.svg", "image-c.svg");
        nhapTTPanel = new RoundPanel(10);
        thumbPanel = new RoundPanel(10);
        danhMucPanel = new RoundPanel(10);
    }
}
