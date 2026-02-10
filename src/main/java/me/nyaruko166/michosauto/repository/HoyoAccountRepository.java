package me.nyaruko166.michosauto.repository;

import me.nyaruko166.michosauto.model.HoyoAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoyoAccountRepository extends JpaRepository<HoyoAccount, Long> {

    List<HoyoAccount> findByType(String type);

    HoyoAccount findById(Integer id);
}
