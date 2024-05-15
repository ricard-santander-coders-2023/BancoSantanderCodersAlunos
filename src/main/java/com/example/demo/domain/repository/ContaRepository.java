package com.example.demo.domain.repository;

import com.example.demo.domain.model.Conta;

import java.util.Optional;

public interface ContaRepository {
    Optional<Conta> findById(String id);
    Conta save(Conta conta);
}
