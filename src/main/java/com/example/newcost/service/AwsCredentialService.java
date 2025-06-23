package com.example.newcost.service;

import com.example.newcost.model.AwsCredentialsRequest;
import com.example.newcost.repo.acctrepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AwsCredentialService {
    @Autowired
    private acctrepo repository;

    public AwsCredentialsRequest save(AwsCredentialsRequest credential) {
        return repository.save(credential);
    }

    public List<AwsCredentialsRequest> getAllAccounts() {
        return repository.findAll();
    }

    public Optional<AwsCredentialsRequest> getByAccountName(String name) {
        return repository.findByAccountName(name);
    }
}
