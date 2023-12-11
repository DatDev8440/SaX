package com.sax.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CtkmDTO extends AbstractDTO {
    private String tenSuKien;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private boolean kieuGiamGia;

    public String getTrangThai() {
        if (LocalDateTime.now().isAfter(ngayBatDau) && LocalDateTime.now().isBefore(ngayKetThuc)) return "Đang diễn ra";
        else if (LocalDateTime.now().isAfter(ngayKetThuc)) return "Đã kết thúc";
        return "Chưa bắt đầu";
    }

    @Override
    public String toString() {
        return tenSuKien;
    }
}
