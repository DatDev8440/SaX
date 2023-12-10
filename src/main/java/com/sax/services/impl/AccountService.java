package com.sax.services.impl;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.sax.dtos.AccountDTO;
import com.sax.entities.Account;
import com.sax.repositories.IAccountRepository;
import com.sax.services.IAccountService;
import com.sax.utils.DTOUtils;
import com.sax.utils.HashUtils;
import com.sax.utils.ImageUtils;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class AccountService implements IAccountService {
    @Autowired
    IAccountRepository repository;
    private int totalPage;

    @Override
    public List<AccountDTO> getAll() {
        return DTOUtils.getInstance().convertToDTOList(repository.findAll(), AccountDTO.class);
    }

    @Override
    public List<AccountDTO> getAllByIds(List<Integer> ids) {
        return null;
    }

    @Override
    public AccountDTO getById(Integer id) {
        return DTOUtils.getInstance().
                converter(repository.findById(id)
                        .orElseThrow(() ->
                                new NoSuchElementException("Không tìm thấy!")), AccountDTO.class);
    }

    @Transactional
    @Override
    public AccountDTO insert(AccountDTO e) throws SQLServerException {
        AccountDTO accountDTO;
        Account account = DTOUtils.getInstance().converter(e, Account.class);
        e.setNgayDangKi(LocalDateTime.now());
        try {
            File file = new File(e.getAnh());
            account.setAnh(file.getName());
            account.setPassword(HashUtils.hashPassword(e.getPassword()));
            accountDTO = DTOUtils.getInstance().converter(repository.save(account), AccountDTO.class);
            ImageUtils.saveImage(file);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return accountDTO;
    }

    @Override
    public void update(AccountDTO e) throws SQLServerException {
        Account account = repository.findById(e.getId()).orElseThrow();
        account.setTenNhanVien(e.getTenNhanVien());
        account.setSdt(e.getSdt());
        account.setGioiTinh(e.isGioiTinh());
        account.setVaiTro(e.isVaiTro());
        account.setTrangThai(e.getTrangThai());
        account.setNgayDangKi(LocalDateTime.now());
        try {
            File file = new File(e.getAnh());
            if (!e.getAnh().isEmpty())account.setAnh(file.getName());
            if (repository.findByUsername(e.getUsername())==null){
                account.setUsername(e.getUsername());
            }
            else if(account.getUsername().equals(e.getUsername())){
                account.setUsername(e.getUsername());
            }
            else throw new RuntimeException("Trùng username");
            ImageUtils.saveImage(file);
        } catch (IOException ex) {
            e.setAnh("no-image.png");
        }
        if (e.getEmail().equals(account.getEmail()))
            DTOUtils.getInstance().converter(repository.save(account), AccountDTO.class);
        else {
            if (repository.findByEmail(e.getEmail()) != null) throw new RuntimeException("Trùng mail");
            else {
                account.setEmail(e.getEmail());
                DTOUtils.getInstance().converter(repository.save(account), AccountDTO.class);
            }
        }
    }

    @Override
    public void delete(Integer id) throws SQLServerException {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Set<Integer> ids) throws SQLServerException {
        StringBuilder name = new StringBuilder();
        ids.forEach(id->{
            Account e = repository.findRelative(id);
            if (e == null){
                Account account = repository.findById(id).orElseThrow();
                if (!account.isVaiTro())repository.deleteById(id);
                else name.append(" ").append(account.getTenNhanVien()).append(", ");
            }
            else {
                if (!repository.findById(id).orElseThrow().isVaiTro()){
                e.setTrangThai(false);
                repository.save(e);
                name.append(" ").append(e.getTenNhanVien()).append(", ");
                }
                else name.append(" ").append(e.getTenNhanVien()).append(", ");
            }
        });
        if (!name.isEmpty()) throw new DataIntegrityViolationException("Tài khoaản"+name + " không thể xoá, do tài khoản" +
                " đã bán hàng hoặc tài khoản là quản lý, Chuyển trạng nghỉ với tài khoản nhân viên");
    }

    @Override
    public int getTotalPage(int amount) {
        return repository.findAll(Pageable.ofSize(amount)).getTotalPages();
    }

    @Override
    public List<AccountDTO> getPage(Pageable page) {
        return DTOUtils.getInstance().convertToDTOList(repository.findAll(page).stream().toList(), AccountDTO.class);
    }

    @Override
    public List<AccountDTO> searchByKeyword(String keyword) {
        return DTOUtils.getInstance().convertToDTOList(repository.findAllByKeyword(keyword), AccountDTO.class);
    }


    @Override
    public AccountDTO getByUsername(String username) {
        return DTOUtils
                .getInstance()
                .converter(repository.findByUsername(username), AccountDTO.class);
    }

    @Override
    public AccountDTO getByEmail(String email) {
        return DTOUtils.getInstance()
                .converter(repository.findByEmail(email), AccountDTO.class);
    }

    @Override
    public void updateUsernamePassword(AccountDTO accountDTO) {
        Account account = repository.findById(accountDTO.getId()).orElseThrow();
        account.setPassword(HashUtils.hashPassword(accountDTO.getPassword()));
        repository.save(account);
    }

    @Override
    public void createAccount(AccountDTO accountDTO) {
        Account account = DTOUtils.getInstance().converter(accountDTO, Account.class);
        account.setPassword(HashUtils.hashPassword(accountDTO.getPassword()));
        account.setVaiTro(false);
        account.setNgayDangKi(LocalDateTime.now());
        account.setTrangThai(true);
        account.setTenNhanVien("Nhân viên mới");
        try {
            repository.save(account);
        } catch (Exception e) {
            throw new DataIntegrityViolationException("Username đã tồn tại");
        }
    }
}
