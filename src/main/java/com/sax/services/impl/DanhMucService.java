package com.sax.services.impl;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.sax.dtos.DanhMucDTO;
import com.sax.dtos.SachDTO;
import com.sax.entities.DanhMuc;
import com.sax.entities.Sach;
import com.sax.repositories.IDanhMucRepository;
import com.sax.repositories.ISachRepository;
import com.sax.services.IDanhMucService;
import com.sax.utils.DTOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DanhMucService implements IDanhMucService {
    @Autowired
    private IDanhMucRepository repository;
    @Autowired
    private ISachRepository sachRepository;
    @Override
    public List<DanhMucDTO> getAll() {
        List<DanhMucDTO> danhMucDTOList = new ArrayList<>();
        repository.findAllByDanhMucCha(null).forEach(danhMuc -> {
            danhMucDTOList.addAll(getChildDanhMucDTO(danhMuc, 0));
        });
        return danhMucDTOList;
    }

    @Override
    public List<DanhMucDTO> getAllByIds(List<Integer> ids) throws SQLServerException {
        return null;
    }

    List<DanhMucDTO> getChildDanhMucDTO(DanhMuc danhMuc, int level) {
        List<DanhMucDTO> danhMucDTOList = new ArrayList<>();
        DanhMucDTO dto = DTOUtils.getInstance().converter(danhMuc, DanhMucDTO.class);
        for (int i = 0; i < level; i++) {
            dto.setTenDanhMuc("-" + dto.getTenDanhMuc());
        }
        danhMucDTOList.add(dto);
        Set<DanhMuc> danhMucSet = danhMuc.getDanhMucCon();
        danhMucSet.forEach(danhMuc1 -> {
            danhMucDTOList.addAll(getChildDanhMucDTO(danhMuc1, level + 1));
        });
        return danhMucDTOList;
    }

    @Override
    public DanhMucDTO getById(Integer id) {
        return DTOUtils.getInstance()
                .converter(repository
                        .findById(id)
                        .orElseThrow(()
                                -> new NoSuchElementException("Khong tim thau")), DanhMucDTO.class);
    }

    @Override
    public DanhMucDTO insert(DanhMucDTO e) throws SQLServerException {
        return DTOUtils.getInstance().converter(repository.save(DTOUtils.getInstance()
                .converter(e, DanhMuc.class)), DanhMucDTO.class);
    }

    @Override
    public void update(DanhMucDTO e) throws SQLServerException {
        repository.save(DTOUtils.getInstance().converter(e, DanhMuc.class));
    }

    @Override
    public void delete(Integer id) throws SQLServerException {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Set<Integer> ids) throws SQLServerException {
        repository.deleteAllById(ids);
    }


    @Override
    public List<DanhMucDTO> getAllDanhMucCha() {
        return DTOUtils.getInstance().convertToDTOList(repository.findAllByDanhMucCha(null), DanhMucDTO.class);
    }

    @Override
    @Transactional
    public void deleteAllDanhMucSach(Set<Integer> ids) {
        List<Sach> sachList = repository.findAllById(ids).stream()
                .flatMap(ditMe -> ditMe.getSetSach().stream())
                .peek(sach -> repository.findAllById(ids).forEach(sach.getSetDanhMuc()::remove))
                .collect(Collectors.toList());
        ids.forEach(id -> repository.updateAllByDanhMucCha(id));
        sachRepository.saveAll(sachList);
        repository.deleteAllById(ids);
    }

    @Override
    public List<DanhMucDTO> getAllDanhMucForUpdate(int id) {
        DanhMuc danhMuc = repository.findById(id).orElseThrow(() -> new NoSuchElementException("sai id ko tim thay"));
        List<DanhMucDTO> listAll = this.getAll();
        listAll.removeAll(getChildDanhMucDTO(danhMuc, 0));
        return listAll;
    }

    @Override
    public int getTotalPage(int amount) {
        return repository.findAll(Pageable.ofSize(amount)).getTotalPages();
    }

    @Override
    public List<DanhMucDTO> getPage(Pageable page) {
        return DTOUtils.getInstance().convertToDTOList(repository.findAll(page).stream().toList(), DanhMucDTO.class);
    }

    @Override
    public List<DanhMucDTO> searchByKeyword(String keyword) {
        List<DanhMucDTO> danhMucDTOS = new ArrayList<>();
        repository.findAllByKeyword(keyword).forEach(danhMuc -> {
            danhMucDTOS.addAll(getChildDanhMucDTO(danhMuc, 0));
        });
        return danhMucDTOS;
    }
}
