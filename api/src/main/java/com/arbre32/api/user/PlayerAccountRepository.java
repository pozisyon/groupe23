package com.arbre32.api.user;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.UUID;
public interface PlayerAccountRepository extends JpaRepository<PlayerAccount, UUID> {
  Optional<PlayerAccount> findByEmail(String email);
  Optional<PlayerAccount> findByHandle(String handle);
}
