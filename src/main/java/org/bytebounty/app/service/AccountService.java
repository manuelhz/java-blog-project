package org.bytebounty.app.service;

import java.util.List;
import java.util.Optional;

import org.bytebounty.app.model.Account;
import org.springframework.stereotype.Service;

@Service("accountService")
public interface AccountService {

    public Account save(Account account);

    public Optional<Account> findOneByEmail(String email);

    public Optional<Account> findOneById(Long i);

    public List<Account> findAll();

    public void delete(Account account);

    public void updateLastLoginByEmail(String email);

}
