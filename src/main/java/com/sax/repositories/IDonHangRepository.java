package com.sax.repositories;
import com.sax.dtos.DoanhThuNamDTO;
import com.sax.dtos.DoanhThuNgayDTO;
import com.sax.entities.DonHang;
import com.sax.entities.Sach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IDonHangRepository extends JpaRepository<DonHang,Integer>, PagingAndSortingRepository<DonHang,Integer> {

    @Query("SELECT sum(e.tongTien) FROM DonHang e" +
            " WHERE DAY(e.ngayTao) = :day " +
            "AND MONTH(e.ngayTao) = :month " +
            "AND YEAR(e.ngayTao) = :year")
     Long findDailyRevenueForDate(@Param("day") int day,@Param("month") int month, @Param("year") int year);

    @Query("select sum(ls.soLuong*ls.giaNhap) FROM LichSuNhapHang ls" +
            "    WHERE DAY(ls.ngayNhap) = :day AND MONTH(ls.ngayNhap) = :month AND YEAR(ls.ngayNhap) = :year")
    Long findDailyExpenseForDate(@Param("day") int day,@Param("month") int month, @Param("year") int year);

    @Query("SELECT sum(e.tongTien) FROM DonHang e" +
            " WHERE " +
            "MONTH(e.ngayTao) = :month " +
            "AND YEAR(e.ngayTao) = :year")
    Long findDailyRevenueForMonth(@Param("month") int month, @Param("year") int year);

    @Query("select sum(ls.soLuong*ls.giaNhap) FROM LichSuNhapHang ls" +
            "    WHERE MONTH(ls.ngayNhap) = :month AND YEAR(ls.ngayNhap) = :year")
    Long findDailyExpenseForMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT e FROM DonHang e WHERE CAST(e.id AS string) like %:keyword% OR e.account.tenNhanVien LIKE %:keyword% OR e.khachHang.tenKhach LIKE %:keyword%")
    List<DonHang> findAllByKeyword(@Param("keyword") String keyword);
    Page<DonHang> findAllByTrangThai(Boolean trangThai, Pageable pageable);
}
