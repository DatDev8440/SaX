import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.sax.dtos.AccountDTO;
import com.sax.dtos.DanhMucDTO;
import com.sax.dtos.LichSuNhapHangDTO;
import com.sax.dtos.SachDTO;
import com.sax.entities.DanhMuc;
import com.sax.entities.Sach;
import com.sax.repositories.IDanhMucRepository;
import com.sax.repositories.IDonHangRepository;
import com.sax.repositories.IKhachHangRepository;
import com.sax.repositories.ISachRepository;
import com.sax.services.*;
import com.sax.services.impl.*;
import com.sax.utils.AccountUtils;
import com.sax.utils.ContextUtils;
import com.sax.utils.ImageUtils;
import com.sax.utils.MailService;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestFile {

    @Test
    public void name() throws SQLServerException {
        ICtkmSachService service = ContextUtils.getBean(CtkmSachService.class);
        ICtkmService ctkmService = ContextUtils.getBean(CtkmService.class);
//        ctkmService.searchByKeyword("1").forEach(System.out::println);
//        service.searchAllSachInCtkm("đổ",ctkmService.getById(2027)).forEach(System.out::println);
//        Set<Integer> id = Set.of(3035,3036);
//        service.deleteAll(id);
        service.searchByKeyword("nh").forEach(System.out::println);
    }

    @Test
    public void reg() {
        IAccountService service = ContextUtils.getBean(AccountService.class);
        service.searchByKeyword("1").forEach(System.out::println);
    }

    @Test
    public void s() {
        IKhachHangRepository repository = ContextUtils.getBean(IKhachHangRepository.class);
        System.out.println(repository.findRelative(3018).getId());
    }

    @Test
    public void sach() {
        ISachRepository repository = ContextUtils.getBean(ISachRepository.class);
        List<Sach> sachList = repository.findAll()
                .stream()
                .peek(sach -> {
                    List<DanhMuc> newDanhMucList = sach.getSetDanhMuc()
                            .stream()
                            .filter(danhMucDTO -> danhMucDTO.getId() != 11)
                            .toList();

                    sach.setSetDanhMuc(new HashSet<>(newDanhMucList));
                })
                .toList();
        sachList.forEach(sach -> {
            sach.getSetDanhMuc().forEach(danhMuc -> {
                System.out.println(danhMuc.getTenDanhMuc());
            });
        });
    }

    @Test
    public void danhMuc() {
        IDanhMucRepository danhMucRepository = ContextUtils.getBean(IDanhMucRepository.class);
        danhMucRepository.findAllExceptDescendants(6100).forEach(danhMuc -> System.out.println(danhMuc.getTenDanhMuc()));
    }

    @Test
    public void test() throws SQLServerException {
        ILichSuNhapHangService service =ContextUtils.getBean(LichSuNhapHangService.class);
        LichSuNhapHangDTO dto = service.getById(1);
//        dto.setSoLuong(100);
        service.delete(1);
    }

    @Test
    public void des() {
        IDonHangService service = ContextUtils.getBean(IDonHangService.class);
        IDonHangChiTetService iDonHangChiTetService = ContextUtils.getBean(IDonHangChiTetService.class);
        iDonHangChiTetService.getAllByDonHang(service.getById(17145)).forEach(System.out::println);
    }

    @Test
    public void test1() {
        IThongKeService service = ContextUtils.getBean(ThongKeService.class);
        service.getAllTongTienTheoThang(12,2023).forEach(System.out::println);
    }

    @Test
    public void des1() {
        // Lấy danh sách tất cả các máy in
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        // Liệt kê các máy in
        System.out.println("Danh sách các máy in:");
        for (PrintService printService : printServices) {
            System.out.println(printService.getName());
        }
    }

    @Test
    public void email() {
        IAccountService service = ContextUtils.getBean(AccountService.class);
        AccountDTO dto = service.getById(2008);
        dto.setUsername("ngu");
        dto.setPassword("c4ca4238a0b923820dcc509a6f75849b");
        service.updateUsernamePassword(dto);
    }

    @Test
    public void delete() {
        IDonHangService service = ContextUtils.getBean(IDonHangService.class);
        service.getAllHindenInvoice().forEach(System.out::println);
    }
}
