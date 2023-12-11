package com.sax.services.impl;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.sax.dtos.CtkmDTO;
import com.sax.dtos.CtkmSachDTO;
import com.sax.dtos.SachDTO;
import com.sax.entities.Ctkm;
import com.sax.entities.CtkmSach;
import com.sax.repositories.ICtkmSachRepository;
import com.sax.repositories.ISachRepository;
import com.sax.services.ICtkmSachService;
import com.sax.utils.DTOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CtkmSachService implements ICtkmSachService {
    @Autowired
    ICtkmSachRepository repository;
    @Autowired
    ISachRepository sachRepository;

    @Override
    public List<CtkmSachDTO> getAll() {
        return DTOUtils.getInstance().convertToDTOList(repository.findAll(), CtkmSachDTO.class);
    }

    @Override
    public List<CtkmSachDTO> getAllByIds(List<Integer> ids) throws SQLServerException {
        return null;
    }

    @Override
    public CtkmSachDTO getById(Integer id) {
        return DTOUtils.getInstance()
                .converter(repository.findById(id).
                        orElseThrow(() -> new NoSuchElementException("Không tìm thấy!")), CtkmSachDTO.class);
    }

    @Override
    public CtkmSachDTO insert(CtkmSachDTO e) throws SQLServerException {
        if (e.getGiaTriGiam() > e.getSach().getGiaBan()) {
            throw new RuntimeException("Giá giảm không được quá giá gốc");
        }
        return DTOUtils.getInstance()
                .converter(repository.
                        save(DTOUtils.getInstance()
                                .converter(e, CtkmSach.class)), CtkmSachDTO.class);
    }

    @Override
    public void update(CtkmSachDTO e) throws SQLServerException {
        if (e.getCtkm().getNgayKetThuc().isAfter(LocalDateTime.now())) {
            if (e.getCtkm().isKieuGiamGia()){
               if (e.getGiaTriGiam()<=100) repository.save(DTOUtils.getInstance().converter(e, CtkmSach.class));
               else throw new RuntimeException("Giá trị giảm không được vượt quá 100%");
            }
            else {
                if (e.getGiaTriGiam()<=e.getSach().getGiaBan()) repository.save(DTOUtils.getInstance().converter(e, CtkmSach.class));
                else throw new RuntimeException("Giá trị giảm không được vượt quá giá bán");
            }
        } else throw new RuntimeException("Không thể cập nhật, do chương trình đã kết thúc!");
    }

    @Override
    public void delete(Integer id) throws SQLServerException {
        repository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteAll(Set<Integer> ids) throws SQLServerException {
        boolean check = true;
        StringBuilder name = new StringBuilder("Sách ");
        for (Integer x : ids) {
            CtkmSach ctkmSach = repository.findById(x).orElseThrow();
            if (ctkmSach.getCtkm().getNgayKetThuc().isBefore(LocalDateTime.now())) {
                check = false;
                name.append(" " + ctkmSach.getSach().getTenSach() + ", ");
            } else repository.deleteById(x);
        }
        if (!check) throw new RuntimeException(name + " .Không thể xoá, do chương trình đã kết thúc!");
    }

    @Override
    public int getTotalPage(int amount) {
        return repository.findAll(Pageable.ofSize(amount)).getTotalPages();
    }

    @Override
    public List<CtkmSachDTO> getPage(Pageable page) {
        return DTOUtils.getInstance().convertToDTOList(repository.findAll(page).stream().toList(), CtkmSachDTO.class);
    }

    @Override
    public List<CtkmSachDTO> searchByKeyword(String keyword) {
        return DTOUtils.getInstance().convertToDTOList(repository.findAllByKeyword(keyword), CtkmSachDTO.class);
    }

    @Override
    public List<CtkmSachDTO> insetAll(List<CtkmSachDTO> e) {
        List<String> tenSachList = e.stream()
                .filter(ctkmSachDTO -> !(ctkmSachDTO.getGiaTriGiam() <= ctkmSachDTO.getSach().getGiaBan()))
                .map(ctkmSachDTO -> ctkmSachDTO.getSach().getTenSach())
                .toList();
        StringBuilder sb = new StringBuilder();
        tenSachList.forEach(tenSach -> sb.append(tenSach).append(", "));
        List<CtkmSachDTO> dtoList = new ArrayList<>();
       if (sb.isEmpty()){
           List<CtkmSachDTO> dtos = e.stream()
                   .filter(ctkmSachDTO ->
                           ctkmSachDTO.getGiaTriGiam()<=ctkmSachDTO.getSach().getGiaBan()).toList();
           dtoList= DTOUtils.getInstance()
                   .convertToDTOList(repository.saveAll(DTOUtils.getInstance()
                                   .convertToDTOList(dtos, CtkmSach.class)),
                           CtkmSachDTO.class);
       }
       else {
           throw new RuntimeException("Sách :"+sb+" giá giảm vượt quá giá bán quyển sách");
       }
       return dtoList;
    }

    @Override
    public List<CtkmSachDTO> searchAllSachInCtkm(String kw, CtkmDTO ctkmDTO) {
        Ctkm ctkm = DTOUtils.getInstance().converter(ctkmDTO, Ctkm.class);
        return DTOUtils.getInstance()
                .convertToDTOList(repository.searchAllSachInCtkm(kw, ctkm),
                        CtkmSachDTO.class);
    }

    @Override
    public List<CtkmSachDTO> getAllSachInCtkm(CtkmDTO ctkmDTO) {
        Ctkm ctkm = DTOUtils.getInstance().converter(ctkmDTO, Ctkm.class);
        return DTOUtils.getInstance()
                .convertToDTOList(repository.findAllByCtkm(ctkm)
                        , CtkmSachDTO.class);
    }

    @Override
    public List<CtkmSachDTO> getAllSachByIdCtkm(Integer id) {
        return DTOUtils.getInstance().convertToDTOList(repository.findAllByIdKM(id), CtkmSachDTO.class);
    }

}
