package me.nyaruko166.michosauto.repository;

import me.nyaruko166.michosauto.model.SkportAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkportAccountRepository extends JpaRepository<SkportAccount, Long> {

    List<SkportAccount> findAllByOwnerDiscordId(String ownerDiscordId);

}
