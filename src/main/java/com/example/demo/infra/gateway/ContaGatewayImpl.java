package com.example.demo.infra.gateway;

import com.example.demo.domain.model.Conta;
import com.example.demo.domain.repository.ContaRepository;
import com.example.demo.infra.repository.JpaContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ContaGatewayImpl implements ContaRepository {

    private JpaContaRepository jpaContaRepository;

    @Autowired
    public ContaGatewayImpl(JpaContaRepository jpaContaRepository) {
        this.jpaContaRepository = jpaContaRepository;
    }

    @Override
    public Optional<Conta> findById(String id) {
        return jpaContaRepository.findById(id);
    }

    @Override
    public Conta save(Conta conta) {
        return jpaContaRepository.save(conta);
    }
}
