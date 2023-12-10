package com.sax.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "khach_hang", schema = "dbo", catalog = "SaX")
public class KhachHang {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "ten_khach", nullable = false,columnDefinition = "nvarchar(255)")
    private String tenKhach;
    @Basic
    @Column(name = "sdt", nullable = false, length = 15)
    private String sdt;
    @Basic
    @Column(name = "diem", nullable = false)
    private Integer diem;
    @Basic
    @Column(name = "ngay_them", nullable = false)
    private LocalDateTime ngayThem;
    @Basic
    @Column(name = "gioi_tinh", nullable = false)
    private boolean gioiTinh;
    @OneToMany(mappedBy = "khachHang")
    private Collection<DonHang> donHangs;
}
