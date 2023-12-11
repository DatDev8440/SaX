package com.sax.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "ctkm", schema = "dbo", catalog = "SaX")
public class Ctkm {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "ten_su_kien", nullable = false,columnDefinition = "nvarchar(100)")
    private String tenSuKien;
    @Basic
    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;
    @Basic
    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDateTime ngayKetThuc;
    @Column(name = "kieu_giam_gia", nullable = false)
    private boolean kieuGiamGia;
    @OneToMany(mappedBy = "ctkm",cascade = CascadeType.REMOVE)
    private Collection<CtkmSach> ctkm;
}
