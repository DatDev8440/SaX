package com.sax.repositories;

import com.sax.entities.KhachHang;
import com.sax.entities.Sach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IKhachHangRepository extends JpaRepository<KhachHang,Integer> {
    @Query("SELECT COUNT (h) FROM DonHang h JOIN KhachHang e ON h.idKhach = e.id WHERE e.id=:id")
    public Integer getCountInvoiceByUid(@Param("id") int id);
    @Query("SELECT e FROM KhachHang e WHERE CAST(e.id AS string) like %:keyword% OR e.tenKhach LIKE %:keyword%")
    List<KhachHang> findAllByKeyword(@Param("keyword") String keyword);
    @Query("SELECT s FROM KhachHang s JOIN s.donHangs where s.id=:id")
    KhachHang findRelative(@Param("id")int id);
}
