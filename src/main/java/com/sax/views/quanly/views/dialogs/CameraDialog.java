package com.sax.views.quanly.views.dialogs;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.sax.Application;
import com.sax.dtos.SachDTO;
import com.sax.services.ISachService;
import com.sax.services.impl.SachService;
import com.sax.utils.AudioUtils;
import com.sax.utils.Cart;
import com.sax.utils.ContextUtils;
import com.sax.utils.ImageUtils;
import com.sax.views.nhanvien.NhanVienView;
import com.sax.views.nhanvien.cart.CartModel;
import lombok.Getter;
import org.jdesktop.swingx.JXTable;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class CameraDialog extends JDialog {
    private JPanel contentPane;
    private JLabel camera;

    private Thread thread;
    @Getter
    private Mat frame;
    @Getter
    private VideoCapture videoCapture;

    private ISachService sachService = ContextUtils.getBean(SachService.class);

    public CameraDialog(JTextField txtBarcode) {
        initComponent();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedImage bufferedImage;
                while (true) {
                    videoCapture.read(frame);
                    bufferedImage = ImageUtils.convertMatToBufferedImage(frame);

                    Image icon = bufferedImage.getScaledInstance(640, 360, Image.SCALE_SMOOTH);
                    ImageIcon imageIcon = new ImageIcon(icon);
                    camera.setIcon(imageIcon);
                    try {
                        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                        Reader reader = new MultiFormatReader();
                        Result result = reader.decode(bitmap);

                        if (result != null) {
                            txtBarcode.setText(result.getText());
                            frame.release();
                            videoCapture.release();
                            break;
                        }
                    } catch (NotFoundException | ChecksumException | IllegalArgumentException | FormatException e) {

                    }
                }
            }
        });
        thread.start();
        dispose();
    }

    public CameraDialog(JXTable table) {
        initComponent();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedImage bufferedImage;
                while (true) {
                    videoCapture.read(frame);
                    bufferedImage = ImageUtils.convertMatToBufferedImage(frame);

                    Image icon = bufferedImage.getScaledInstance(640, 360, Image.SCALE_SMOOTH);
                    ImageIcon imageIcon = new ImageIcon(icon);
                    camera.setIcon(imageIcon);
                    try {
                        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                        Reader reader = new MultiFormatReader();
                        Result result = reader.decode(bitmap);

                        if (result != null) {
                            SachDTO data = sachService.getByBarCode(result.getText());
                            Optional<CartModel> cartModel = Cart.getCart().stream().filter(i -> i.getId() == data.getId()).findFirst();
                            if (cartModel.isEmpty())
                                Cart.getCart().add(new CartModel(data));
                            else {
                                JSpinner s = cartModel.get().getSoLuong();
                                s.setValue(s.getNextValue());
                                table.repaint();
                            }
                            NhanVienView.nvv.tinhTien();
                            AudioUtils.playAudio("beep.wav");
                            table.packAll();
                            Thread.sleep(1000);
                        }
                    } catch (NotFoundException | ChecksumException | IllegalArgumentException | FormatException |
                             InterruptedException e) {

                    }
                }
            }
        });
        thread.start();
    }

    private void initComponent() {
        setContentPane(contentPane);
        setModal(true);
        setSize(new Dimension(640, 360));
        setLocationRelativeTo(Application.app);

        nu.pattern.OpenCV.loadLocally();
        videoCapture = new VideoCapture(0);
        frame = new Mat();
    }
}
