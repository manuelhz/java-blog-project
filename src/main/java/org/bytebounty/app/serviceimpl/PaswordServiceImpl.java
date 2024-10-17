package org.bytebounty.app.serviceimpl;

import java.nio.CharBuffer;
import java.util.Optional;

import org.bytebounty.app.model.Password;
import org.bytebounty.app.repository.PasswordRepository;
import org.bytebounty.app.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("passwordService")
public class PaswordServiceImpl implements PasswordService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    PasswordRepository passwordRepository;

    @Override
    public void save(Password password) {
        password.setWord(passwordEncoder.encode(CharBuffer.wrap(password.getWord())).toCharArray());

        passwordRepository.save(password);
        
    }

    @Override
    public Optional<Password> findByResetToken(String token) {
        return passwordRepository.findByResetToken(token);
    }

    @Override
    public Optional<Password> findById(Long passwordId) {
        return passwordRepository.findById(passwordId);
    }

    @Override
    public boolean checkPassword(Password password, char[] keyWord) {
        return passwordEncoder.matches(String.valueOf(keyWord), String.valueOf(password.getWord()));
    }
    
}
