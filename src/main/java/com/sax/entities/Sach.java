package com.sax.entities;

import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "sach", schema = "dbo", catalog = "SaX")
public class Sach {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "ten_sach", nullable = false,columnDefinition = "nvarchar(255)")
    private String tenSach;
    @Basic
    @Column(name = "bar_code", nullable = true,columnDefinition = "varchar(13)")
    private String barCode;
    @Basic
    @Column(name = "gia_ban", nullable = false)
    private long giaBan;
    @Basic
    @Column(name = "nxb", nullable = false,columnDefinition = "nvarchar(128)")
    private String nxb;
    @Basic
    @Column(name = "so_luong", nullable = false)
    private int soLuong;
    @Basic
    @Column(name = "ngay_them", nullable = false)
    private LocalDateTime ngayThem;
    @Basic
    @Column(name = "ngay_sua", nullable = true)
    private LocalDateTime ngaySua;
    @Basic
    @Column(name = "trang_thai", nullable = false)
    private boolean trangThai;
    @Basic
    @Column(name = "hinh_anh", nullable = true, length = 2000)
    private String hinhAnh;

    @ManyToMany
    @JoinTable(
            name = "sach_danh_muc",
            joinColumns = { @JoinColumn(name = "id_sach") },
            inverseJoinColumns = { @JoinColumn(name = "id_danh_muc") }
    )
    Set<DanhMuc> setDanhMuc = new HashSet<>();
    @OneToMany(mappedBy = "sach")
    private List<CtkmSach> CtkmSach;
    @OneToMany(mappedBy = "sach")
    private Collection<ChiTietDonHang> chiTietDonHangs;
    @OneToMany(mappedBy = "sach")
    List<LichSuNhapHang> lichSuNhapHangList;

}
