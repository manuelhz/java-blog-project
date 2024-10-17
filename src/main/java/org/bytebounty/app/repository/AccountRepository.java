package org.bytebounty.app.repository;

import java.util.Optional;

import org.bytebounty.app.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository <Account, Long> {

    Optional<Account> findOneByEmailIgnoreCase(String email);



}
