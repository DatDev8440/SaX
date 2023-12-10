package com.sax.entities;

import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "hoa_don", schema = "dbo", catalog = "SaX")
public class DonHang {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "tien_hang")
    private long tienHang = 0L;
    @Basic
    @Column(name = "chiet_khau")
    private long chietKhau = 0L;
    @Basic
    @Column(name = "tong_tien", nullable = true)
    private Long tongTien  = 0L;
    @Basic
    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;
    @Basic
    @Column(name = "id_khach", nullable = false,insertable = false,updatable = false)
    private int idKhach;
    @Basic
    @Column(name = "id_tai_khoan", nullable = false,insertable = false,updatable = false)
    private int idTaiKhoan;
    @Basic
    @Column(name = "phuong_thuc_thanh_toan", nullable = true)
    private Boolean pttt;
    @ManyToOne
    @JoinColumn(name = "id_khach", referencedColumnName = "id", nullable = false)
    private KhachHang khachHang;
    @ManyToOne
    @JoinColumn(name = "id_tai_khoan", referencedColumnName = "id", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "donHang")
    private Collection<ChiTietDonHang> chiTietDonHangs;
}
