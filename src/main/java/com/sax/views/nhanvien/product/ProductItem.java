package com.sax.views.nhanvien.product;

import com.sax.dtos.SachDTO;
import com.sax.utils.AudioUtils;
import com.sax.utils.Cart;
import com.sax.utils.ImageUtils;
import com.sax.utils.Session;
import com.sax.views.nhanvien.NhanVienView;
import com.sax.views.nhanvien.cart.CartModel;
import com.sax.views.components.libraries.PictureBox;
import lombok.Getter;
import lombok.Setter;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductItem extends JPanel {
    private JTextArea lbItemName;
    private JLabel lbPrice;
    private JPanel p;
    @Getter
    private JButton btnAddToCart;
    private JLabel discount;
    private JPanel image;
    private JLabel lblSoLuong;
    private boolean selected = false;
    private SachDTO data;

    public ProductItem(JXTable table) {
        btnAddToCart.addActionListener((e) -> {
            Session.executorService.submit(() -> AudioUtils.playAudio("beep.wav"));
            Optional<CartModel> cartModel = Cart.getCart().stream().filter(i -> i.getId() == data.getId()).findFirst();
            if (cartModel.isEmpty())
                Cart.getCart().add(new CartModel(data));
            else {
                JSpinner s = cartModel.get().getSoLuong();
                s.setValue(s.getNextValue());
                table.repaint();
            }
            NhanVienView.nvv.tinhTien();
            table.packAll();
        });
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        if (selected) {
            g2.setColor(Color.decode("#EA6C20"));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    public void setData(SachDTO data) {
        this.data = data;
        String text = data.getTenSach();
        if (data.getTenSach().length() > 47) text = text.substring(0, 47) + "...";
        lbItemName.setText(text);
        lblSoLuong.setText("Số lượng: " + data.getSoLuong() + " cuốn");
        DecimalFormat df = new DecimalFormat("#,###đ");
        lbPrice.setText(df.format(data.getGiaBan() - data.getGiaGiam()).replace(",", "."));
        double per = (double) data.getGiaGiam() / (double) data.getGiaBan() * 100;
        if (per > 0) discount.setText("-" + Math.round(per) + "%");
        else discount.setVisible(false);
        image.add(ImageUtils.getCircleImage(data.getHinhAnh(), 215, 20, null, 0));
        btnAddToCart.setEnabled(data.getSoLuong() > 0 ? true : false);
    }

    private void createUIComponents() {
        p = this;
    }
}
