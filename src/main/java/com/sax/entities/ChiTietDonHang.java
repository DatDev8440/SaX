package com.sax.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "hoa_don_chi_tiet", schema = "dbo", catalog = "SaX")
public class ChiTietDonHang {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "gia_ban", nullable = false)
    private long giaBan;
    @Basic
    @Column(name = "gia_giam", nullable = false)
    private long giaGiam;
    @Basic
    @Column(name = "so_luong", nullable = false)
    private int soLuong;
    @Basic
    @Column(name = "ghichu", nullable = true,columnDefinition = "nvarchar(255)")
    private String ghichu;
    @Basic
    @Column(name = "id_hoa_don",insertable = false,updatable = false)
    private int idDonHang;
    @Basic
    @Column(name = "id_sach",insertable = false,updatable = false)
    private int idSach;
    @ManyToOne
    @JoinColumn(name = "id_hoa_don", referencedColumnName = "id", nullable = false)
    private DonHang donHang;
    @ManyToOne
    @JoinColumn(name = "id_sach", referencedColumnName = "id", nullable = false)
    private Sach sach;
}
