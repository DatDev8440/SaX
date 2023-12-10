package com.sax.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "danh_muc", schema = "dbo", catalog = "SaX")
public class DanhMuc {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "ten_danh_muc", nullable = false, columnDefinition = "nvarchar(255)")
    private String tenDanhMuc;
    @Basic
    @Column(name = "ghi_chu", nullable = true, columnDefinition = "nvarchar(255)")
    private String ghiChu;
    @Basic
    @Column(name = "id_loai_cha",insertable = false,updatable = false)
    private Integer idDanhMucCha;

    @ManyToOne
    @JoinColumn(name = "id_loai_cha", referencedColumnName = "id")
    private DanhMuc danhMucCha;

    @OneToMany(mappedBy = "danhMucCha")
    private Set<DanhMuc> danhMucCon;
    @ManyToMany(mappedBy = "setDanhMuc")
    private Set<Sach> setSach = new HashSet<>();
}
