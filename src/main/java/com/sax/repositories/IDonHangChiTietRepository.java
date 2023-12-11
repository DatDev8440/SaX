package com.sax.repositories;

import com.sax.entities.ChiTietDonHang;
import com.sax.entities.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDonHangChiTietRepository extends JpaRepository<ChiTietDonHang,Integer> {
    public List<ChiTietDonHang> findAllByDonHang(DonHang donHang);

}
