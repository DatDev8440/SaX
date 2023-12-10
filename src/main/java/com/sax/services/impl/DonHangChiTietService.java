package com.sax.services.impl;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.sax.dtos.ChiTietDonHangDTO;
import com.sax.dtos.DonHangDTO;
import com.sax.entities.ChiTietDonHang;
import com.sax.entities.DonHang;
import com.sax.entities.Sach;
import com.sax.repositories.IDonHangChiTietRepository;
import com.sax.repositories.IDonHangRepository;
import com.sax.repositories.ISachRepository;
import com.sax.services.IDonHangChiTetService;
import com.sax.utils.DTOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class DonHangChiTietService implements IDonHangChiTetService {
    @Autowired
    private IDonHangChiTietRepository repository;
    @Autowired
    private ISachRepository sachRepository;
    @Autowired
    IDonHangRepository donHangRepository;

    @Override
    public List<ChiTietDonHangDTO> getAll() {
        return DTOUtils.getInstance().convertToDTOList(repository.findAll(), ChiTietDonHangDTO.class);
    }

    @Override
    public List<ChiTietDonHangDTO> getAllByIds(List<Integer> ids) throws SQLServerException {
        return null;
    }

    @Override
    public ChiTietDonHangDTO getById(Integer id) {
        return DTOUtils.getInstance()
                .converter(repository.findById(id)
                        .orElseThrow(()
                                -> new NoSuchElementException("Khong tim thay")), ChiTietDonHangDTO.class);
    }

    @Override
    public ChiTietDonHangDTO insert(ChiTietDonHangDTO e) throws SQLServerException {
        return null;
    }


    @Override
    public void update(ChiTietDonHangDTO e) throws SQLServerException {
        ChiTietDonHang chiTietDonHang = repository.findById(e.getId()).orElseThrow();
        Sach sach = chiTietDonHang.getSach();
        int soLuong = e.getSoLuong() - chiTietDonHang.getSoLuong();
        sach.setSoLuong(sach.getSoLuong()+soLuong);
        chiTietDonHang.setSoLuong(chiTietDonHang.getSoLuong()+soLuong);
        long giaSach = (chiTietDonHang.getGiaBan()-chiTietDonHang.getGiaGiam())*soLuong;
        DonHang donHang = chiTietDonHang.getDonHang();
        donHang.setTienHang(donHang.getTienHang()-giaSach);
        donHang.setTongTien(donHang.getTongTien()-giaSach);
        sachRepository.save(sach);
        donHangRepository.save(donHang);
        repository.save(chiTietDonHang);
    }

    @Override
    public void delete(Integer id) throws SQLServerException {
        ChiTietDonHang chiTietDonHang = repository.findById(id).orElseThrow();
        Sach sach = chiTietDonHang.getSach();
        sach.setSoLuong(sach.getSoLuong() - chiTietDonHang.getSoLuong());
        DonHang donHang = chiTietDonHang.getDonHang();
        donHang.setTongTien(donHang.getTongTien() - chiTietDonHang.getSoLuong() *
                (chiTietDonHang.getGiaBan() - chiTietDonHang.getGiaGiam()));
        donHang.setTienHang(donHang.getTienHang()-chiTietDonHang.getSoLuong() *
                (chiTietDonHang.getGiaBan() - chiTietDonHang.getGiaGiam()));
        sachRepository.save(sach);
        donHangRepository.save(donHang);
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Set<Integer> ids) throws SQLServerException {

    }

    @Override
    public int getTotalPage(int amount) {
        return repository.findAll(Pageable.ofSize(amount)).getTotalPages();
    }

    @Override
    public List<ChiTietDonHangDTO> getPage(Pageable page) {
        return DTOUtils.getInstance().convertToDTOList(repository.findAll(page).stream().toList(), ChiTietDonHangDTO.class);
    }

    @Override
    public List<ChiTietDonHangDTO> searchByKeyword(String keyword) {
        return null;
    }


    @Override
    public List<ChiTietDonHangDTO> getAllByDonHang(DonHangDTO donHangDTO) {
        return DTOUtils.getInstance()
                .convertToDTOList(repository.findAllByDonHang(DTOUtils.getInstance().
                        converter(donHangDTO, DonHang.class)), ChiTietDonHangDTO.class);
    }
}
