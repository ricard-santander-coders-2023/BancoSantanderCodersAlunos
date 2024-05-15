package com.example.demo.infra.repository;

import com.example.demo.domain.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaClienteRepository extends JpaRepository<Cliente, String> {
}
