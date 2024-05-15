package com.example.demo.infra.gateway;

import com.example.demo.domain.model.Cliente;
import com.example.demo.domain.repository.ClienteRepository;
import com.example.demo.infra.repository.JpaClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClienteGatewayImpl implements ClienteRepository {

    private JpaClienteRepository jpaClienteRepository;

    @Autowired
    public ClienteGatewayImpl(JpaClienteRepository jpaClienteRepository) {
        this.jpaClienteRepository = jpaClienteRepository;
    }

    @Override
    public Cliente save(Cliente cliente) {
        return jpaClienteRepository.save(cliente);
    }
}
