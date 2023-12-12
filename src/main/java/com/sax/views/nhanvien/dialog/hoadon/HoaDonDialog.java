package com.sax.views.nhanvien.dialog.hoadon;

import com.sax.dtos.DonHangDTO;
import com.sax.utils.CurrencyConverter;
import com.sax.utils.DateUtils;
import com.sax.utils.ImageUtils;
import com.sax.views.components.libraries.ButtonToolItem;
import com.sax.views.nhanvien.dialog.hoadon.table.TableCustom;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.jdesktop.swingx.JXTable;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class HoaDonDialog extends JDialog {
    private JPanel contentPane;
    private JLabel lblSdt;
    private JLabel lblTenKH;
    private JXTable table;
    private JLabel lblTPT;
    private JLabel lblTienHang;
    private JLabel lblMNV;
    private JLabel lblSHD;
    private JPanel sp;
    private JLabel lblNgayTao;
    private JButton btnSubmit;
    private JButton btnClose;
    private JLabel lblChietKhau;
    private JLabel lblLogo;
    private JPanel hd;
    private JButton btnSave;
    private DefaultTableModel tableModel;

    public HoaDonDialog(Component parent, DonHangDTO donHangDTO, boolean check) throws FileNotFoundException {
        table = new JXTable();
        tableModel = (DefaultTableModel) table.getModel();

        lblTenKH.setText(donHangDTO.getKhach().getTenKhach().toUpperCase());
        lblSdt.setText("Số điện thoại: " + donHangDTO.getKhach().getSdt());

        lblMNV.setText("Nhân viên " + donHangDTO.getAccount().getId() + " " + donHangDTO.getAccount().getTenNhanVien());
        lblSHD.setText("Hoá đơn #" + donHangDTO.getId());

        table.setVisibleRowCount(donHangDTO.getChiTietDonHangs().size());
        String[] columnNames = {"Sản phẩm", "SL", "Giá bán", "Giá giảm", "Thành tiền"};
        tableModel.setDataVector(donHangDTO.getChiTietDonHangs().stream().map(i -> new Object[]{
                i.getSach().getTenSach(),
                i.getSoLuong(),
                CurrencyConverter.parseString(i.getGiaBan()),
                i.getGiaGiam() > 0 ? "-" + CurrencyConverter.parseString(i.getGiaGiam()) : CurrencyConverter.parseString(i.getGiaGiam()),
                CurrencyConverter.parseString((i.getGiaBan() - i.getGiaGiam()) * i.getSoLuong())
        }).toArray(Object[][]::new), columnNames);
        table.setFocusable(false);
        table.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        TableCustom.apply(scrollPane);
        table.packAll();

        table.setPreferredScrollableViewportSize(table.getPreferredSize());

        sp.add(scrollPane);

        lblNgayTao.setText("Ngày tạo: " + DateUtils.parseString(donHangDTO.getNgayTao()));
        lblTienHang.setText(CurrencyConverter.parseString(donHangDTO.getTienHang()));
        lblChietKhau.setText(donHangDTO.getChietKhau() > 0 ? "-" + CurrencyConverter.parseString(donHangDTO.getChietKhau()) : CurrencyConverter.parseString(donHangDTO.getChietKhau()));
        lblTPT.setText(CurrencyConverter.parseString(donHangDTO.getTongTien()));
        lblLogo.setIcon(new ImageIcon(ImageUtils.readImage("logo-com.png").getScaledInstance(40, 40, Image.SCALE_SMOOTH)));


        setContentPane(contentPane);
        setModal(true);
        pack();
        setLocationRelativeTo(parent);
        setFocusableWindowState(true);
        setAlwaysOnTop(true);

        btnSave.addActionListener((e) -> {
            BufferedImage hoadon = new BufferedImage(hd.getWidth(), hd.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2D = (Graphics2D) hoadon.getGraphics();
            hd.paint(g2D);
            g2D.translate(0, this.getHeight());
            hd.paint(g2D);
            ImageUtils.saveBufferImageToPdf(hoadon, "invoices/" + donHangDTO.getId() + ".pdf");
            dispose();
        });

        btnSubmit.addActionListener((e) -> {
            BufferedImage hoadon = new BufferedImage(hd.getWidth(), hd.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2D = (Graphics2D) hoadon.getGraphics();
            hd.paint(g2D);
            g2D.translate(0, this.getHeight());
            hd.paint(g2D);
            ImageUtils.saveBufferImageToRaster(hoadon, "invoices/" + donHangDTO.getId() + ".png");
            ImageUtils.saveBufferImageToPdf(hoadon, "invoices/" + donHangDTO.getId() + ".pdf");

            try {
                FileInputStream fileInputStream = new FileInputStream("images/invoices/" + donHangDTO.getId() + ".png");

                DocFlavor myFormat = DocFlavor.INPUT_STREAM.PNG;

                Doc myDoc = new SimpleDoc(fileInputStream, myFormat, null);
                PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
                aset.add(new Copies(1));
                aset.add(OrientationRequested.REVERSE_PORTRAIT);

                PrintService[] services = PrintServiceLookup.lookupPrintServices(myFormat, aset);
                if (services.length != 0) {
                    DocPrintJob printJob = services[0].createPrintJob();
                    try {
                        printJob.print(myDoc, aset);
                    } catch (PrintException pe) {
                        throw new RuntimeException(pe);
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            dispose();
        });

        btnClose.addActionListener((e) -> dispose());
    }

    private void createUIComponents() {
        btnSubmit = new ButtonToolItem("ctkm.svg", "ctkm.svg");
        btnClose = new ButtonToolItem("x-c.svg", "x-c.svg");
        btnSave = new ButtonToolItem("save-c.svg", "save-c.svg");
    }
}
