package me.nyaruko166.michosauto.repository;

import me.nyaruko166.michosauto.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByType(String type);

    Account findById(Integer id);
}
