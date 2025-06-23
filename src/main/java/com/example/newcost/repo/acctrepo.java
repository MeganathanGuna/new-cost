package com.example.newcost.repo;

import com.example.newcost.model.AwsCredentialsRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface acctrepo extends JpaRepository<AwsCredentialsRequest, Long> {
    Optional<AwsCredentialsRequest> findByAccountName(String accountName);
    List<AwsCredentialsRequest> findAll();
}
