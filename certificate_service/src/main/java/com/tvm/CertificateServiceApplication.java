package com.tvm;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CertificateServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CertificateServiceApplication.class, args);
    }

}
