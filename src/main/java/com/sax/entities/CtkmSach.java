package com.sax.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "ctkm_sach", schema = "dbo", catalog = "SaX")
public class CtkmSach {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "gia_tri_giam", nullable = false)
    private long giaTriGiam;
    @Column(name = "id_sach", nullable = false,insertable = false,updatable = false)
    private int idSach;
    @Basic
    @Column(name = "id_giam_gia", nullable = false,insertable = false,updatable = false)
    private int idKM;
    @ManyToOne
    @JoinColumn(name = "id_sach", referencedColumnName = "id", nullable = false)
    private Sach sach;
    @ManyToOne
    @JoinColumn(name = "id_giam_gia", referencedColumnName = "id", nullable = false)
    private Ctkm ctkm;
}
