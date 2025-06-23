package com.example.newcost.model;

import jakarta.persistence.*;

@Entity
@Table(name = "aws_credentials_request")
public class AwsCredentialsRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "access_key")
    private String accessKey;
    @Column(name = "secret_key")
    private String secretKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Column(name = "region")
    private String region;
    @Column(name = "account_name")
    private String accountName;

    // Getters and Setters
    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAccountId() {
        return accountName;
    }

    public void setAccountId(String accountId) {
        this.accountName = accountId;
    }
}