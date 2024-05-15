package com.example.demo.infra.repository;

import com.example.demo.domain.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaContaRepository extends JpaRepository<Conta,String> {
}
