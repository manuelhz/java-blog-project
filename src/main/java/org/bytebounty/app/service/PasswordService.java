package org.bytebounty.app.service;

import java.util.Optional;

import org.bytebounty.app.model.Password;
import org.springframework.stereotype.Service;

@Service("passwordService")
public interface PasswordService {

    public void save(Password password);

    public Optional<Password> findByResetToken(String token);

    public Optional<Password> findById(Long passwordId);

    public boolean checkPassword(Password password, char[] keyWord);
}
