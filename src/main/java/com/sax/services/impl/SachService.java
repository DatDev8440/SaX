package com.sax.services.impl;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.sax.dtos.SachDTO;
import com.sax.entities.*;
import com.sax.repositories.*;
import com.sax.services.ISachService;
import com.sax.utils.DTOUtils;
import com.sax.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class SachService implements ISachService {
    @Autowired
    private ISachRepository repository;
    @Autowired
    private IDanhMucRepository danhMucRepository;
    private LocalDateTime date = LocalDateTime.now();

    @Override
    public List<SachDTO> getAll() {
        return DTOUtils.getInstance().convertToDTOList(repository.findAll(), SachDTO.class);
    }

    @Override
    public List<SachDTO> getAllByIds(List<Integer> ids) throws SQLServerException {
        return DTOUtils.getInstance().convertToDTOList(repository.findAllById(ids), SachDTO.class);
    }

    @Override
    public SachDTO getById(Integer id) {
        return DTOUtils.getInstance().converter(repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thấy")), SachDTO.class);
    }

    @Override
    public List<SachDTO> getAllSachInOrNotInCTKM() {
        List<Sach> list = repository.findAllByTrangThai(true);
        return findAllGiaGiam(list);
    }
    List<SachDTO> findAllGiaGiam(List<Sach> list){
        List<SachDTO> dtoList = DTOUtils.getInstance().convertToDTOList(list, SachDTO.class);
        for (int i = 0; i < list.size(); i++) {
            for (CtkmSach x:list.get(i).getCtkmSach()) {
                if (date.isBefore(x.getCtkm().getNgayKetThuc()) && date.isAfter(x.getCtkm().getNgayBatDau())) {
                    if (x.getCtkm().isKieuGiamGia()) {
                        dtoList.get(i).setGiaGiam((x.getGiaTriGiam() * x.getSach().getGiaBan()) / 100);
                    } else {
                        dtoList.get(i).setGiaGiam(x.getGiaTriGiam());
                    }
                }
            }
        }
        return dtoList;
    }

    @Override
    public List<SachDTO> getAllSachNotInCTKM() {
        return DTOUtils.getInstance().convertToDTOList(repository.findAllSachNotInCTKM(), SachDTO.class);
    }

    @Override
    public List<SachDTO> getAllSachByIdDanhMuc(Integer id) {
        DanhMuc danhMuc = danhMucRepository.findById(id).orElseThrow();
        List<Sach> list = new ArrayList<>(danhMuc.getSetSach());
        return findAllGiaGiam(list.stream().filter(Sach::isTrangThai).toList());
    }

    @Override
    public List<SachDTO> getAllCtkSachInAllAvailablePromote(Pageable pageable) {
        return DTOUtils.getInstance()
                .convertToDTOList(
                        repository.findAllCtkmSachInAllAvailablePromote(pageable).getContent(), SachDTO.class);
    }

    @Override
    public List<SachDTO> getAllCtkSachNotInAllAvailablePromote(Pageable pageable) {
        return DTOUtils.getInstance()
                .convertToDTOList(
                        repository.findAllCtkmSachNotAllAvailablePromote(pageable).getContent(), SachDTO.class);
    }

    @Override
    public int getToTalPageCtkSachNotInAllAvailablePromote(Pageable pageable) {
        return repository.findAllCtkmSachNotAllAvailablePromote(pageable).getTotalPages();
    }

    @Override
    public int getToTalPageCtkSachInAllAvailablePromote(Pageable pageable) {
        return repository.findAllCtkmSachInAllAvailablePromote(pageable).getTotalPages();
    }

    @Override
    public SachDTO getByBarCode(String barcode) {
        Sach sach = repository.findByBarCode(barcode).orElseThrow(() -> new NoSuchElementException("Không tìm thấy sách"));
        SachDTO dto = DTOUtils.getInstance().converter(sach, SachDTO.class);
        if (dto.getTrangThai()){
            sach.getCtkmSach().forEach(ctkmSach -> {
                if (date.isBefore(ctkmSach.getCtkm().getNgayKetThuc()) && date.isAfter(ctkmSach.getCtkm().getNgayBatDau())) {
                    if (ctkmSach.getCtkm().isKieuGiamGia()) {
                        dto.setGiaGiam((ctkmSach.getGiaTriGiam() * ctkmSach.getSach().getGiaBan()) / 100);
                    } else {
                        dto.setGiaGiam(ctkmSach.getGiaTriGiam());
                    }
                }
            });
        }
        else{
            throw new RuntimeException("Đang tạm ngưng bán");
        }
        return dto;
    }

    @Override
    public List<SachDTO> getAllSachByKeyWord(String kw) {
        List<Sach> list = repository.findAllByKeyword(kw);
        return findAllGiaGiam(list);
    }

    @Override
    public List<SachDTO> getAllAvailableSachByKeyWord(String kw) {
        return DTOUtils.getInstance().convertToDTOList(repository.findAllAvailableByKeyword(kw),SachDTO.class);
    }



    @Override
    public SachDTO insert(SachDTO e) throws SQLServerException {
        SachDTO dto = null;
        e.setNgayThem(LocalDateTime.now());
        Sach sach = DTOUtils.getInstance().converter(e, Sach.class);
        if (e.getSetDanhMuc() != null) {
            Set<DanhMuc> danhMucSet = DTOUtils.getInstance().convertToDTOSet(e.getSetDanhMuc(), DanhMuc.class);
            sach.setSetDanhMuc(danhMucSet);
        }
        try {
            if (!(e.getHinhAnh() == null)){
                File file = new File(e.getHinhAnh());
                sach.setHinhAnh(file.getName());
                ImageUtils.saveImage(file);
            }else {
                sach.setHinhAnh("no-image.png");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        if (repository.findByBarCode(e.getBarCode()).isEmpty())
            dto = DTOUtils.getInstance().converter(repository.save(sach), SachDTO.class);
        else
            throw new RuntimeException("Trùng barcode");
        return dto;
    }

    @Override
    public void update(SachDTO e) throws SQLServerException {
        Sach sach = repository.findById(e.getId()).orElseThrow(() -> new NoSuchElementException("Khong tim thay"));
        Set<DanhMuc> danhMucSet = DTOUtils.getInstance().convertToDTOSet(e.getSetDanhMuc(), DanhMuc.class);
        sach.setSetDanhMuc(danhMucSet);
        sach.setTenSach(e.getTenSach());
        sach.setNxb(e.getNxb());
        sach.setTrangThai(e.getTrangThai());
        sach.setNgaySua(LocalDateTime.now());
        sach.setSoLuong(e.getSoLuong());
        sach.setHinhAnh(e.getHinhAnh());
        sach.setGiaBan(e.getGiaBan());
        try {
            File file = new File(e.getHinhAnh());
            if (e.getHinhAnh().equals(sach.getHinhAnh())){
                sach.setHinhAnh(file.getName());
                ImageUtils.saveImage(file);
//                ImageUtils.deleteImage(original);
            }
        } catch (IOException ex) {
            sach.setHinhAnh(sach.getHinhAnh());
        }

        if (e.getBarCode().equals(repository.findById(e.getId()).get().getBarCode()))
            repository.save(sach);
        else {
            if (repository.findByBarCode(e.getBarCode()).isPresent())
                throw  new RuntimeException("Trùng barcode");
            else {
            sach.setBarCode(e.getBarCode());
            repository.save(sach);
            }
        }
    }

    @Override
    public void delete(Integer id) throws SQLServerException {
        repository.deleteById(id);
    }
    @Override
    public void deleteAll(Set<Integer> ids) throws SQLServerException {
        boolean check = true;
        StringBuilder name = new StringBuilder("Sách");
        for (Integer x : ids) {
            Sach e = repository.findRelative(x).orElse(null);
            if (e == null){
                repository.deleteById(x);
            }
            else {
                e.setTrangThai(false);
                repository.save(e);
                name.append(" ").append(e.getTenSach()).append(", ");
                check = false;
            }
        }
        if (!check) throw new DataIntegrityViolationException(name + ".Không thể xoá, đo sách liên quan thông tin. các sách chuyển trạng thái sang ẩn!");
    }

    @Override
    public int getTotalPage(int amount) {
        return repository.findAll(Pageable.ofSize(amount)).getTotalPages();
    }

    @Override
    public List<SachDTO> getPage(Pageable page) {
        return DTOUtils.getInstance().convertToDTOList(repository.findAll(page).getContent(), SachDTO.class);
    }

    @Override
    public List<SachDTO> searchByKeyword(String keyword) {
        return DTOUtils.getInstance().convertToDTOList(repository.findAllByKeyword(keyword), SachDTO.class);
    }

}
