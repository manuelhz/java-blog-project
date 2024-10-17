package org.bytebounty.app.repository;

import java.util.Optional;

import org.bytebounty.app.model.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Long>{

    Optional<Password> findByResetToken(String token);
    
}
