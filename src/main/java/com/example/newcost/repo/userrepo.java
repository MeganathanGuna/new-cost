package com.example.newcost.repo;

import com.example.newcost.model.user;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface userrepo extends JpaRepository<user, Long> {
    Optional<user> findByEmail(String email);
}
