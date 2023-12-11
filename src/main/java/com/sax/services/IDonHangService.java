package com.sax.services;

import com.sax.dtos.DonHangDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface IDonHangService extends ICrudServices<DonHangDTO,Integer>{
    void updateStatus(Set<Integer>ids,boolean status);
    int countByTrangThai(Boolean trangThai);
    List<DonHangDTO> getAllHindenInvoice();
}
