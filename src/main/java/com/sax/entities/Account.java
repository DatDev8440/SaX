package com.sax.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "account", schema = "dbo", catalog = "SaX")
public class Account implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "username", nullable = false, columnDefinition = "varchar(64)", unique = true)
    private String username;
    @Basic
    @Column(name = "password", nullable = false)
    private String password;
    @Basic
    @Column(name = "ten_nhan_vien", nullable = true, columnDefinition = "nvarchar(255)")
    private String tenNhanVien;
    @Basic
    @Column(name = "anh", nullable = true, columnDefinition = "varchar(64)")
    private String anh;
    @Basic
    @Column(name = "email", nullable = true, length = 255)
    private String email;
    @Column(name = "sdt", nullable = true, length = 13, unique = true)
    private String sdt;
    @Basic
    @Column(name = "ngay_dang_ki", nullable = false)
    private LocalDateTime ngayDangKi;
    @Basic
    @Column(name = "trang_thai", nullable = false)
    private boolean trangThai;
    @Basic
    @Column(name = "gioi_tinh", nullable = true)
    private Boolean gioiTinh;
    @Basic
    @Column(name = "vai_tro", nullable = false)
    private boolean vaiTro;
    @OneToMany(mappedBy = "account")
    private Collection<DonHang> donHangs;
}
