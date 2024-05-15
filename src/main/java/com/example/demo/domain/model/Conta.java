package com.example.demo.domain.model;

import com.example.demo.domain.exception.SaldoInvalidoException;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import java.util.Objects;

@Entity
public class Conta {
    @Id
    private String id;
    @OneToOne
    private Cliente cliente;
    private Double saldo;
    private Double saldoDisponivelParaEmprestimo;
    // Por hora vamos utilziar Double para valores em reais
    // e no futuro vamos alterar para BigDecimal

    public Conta(String id, Cliente cliente) {
        this.id = id;
        this.cliente = cliente;
        this.saldo = 0.0;
        this.saldoDisponivelParaEmprestimo = 0.0;
    }

    public Conta() {
    }

    public void depositar(Double valor) {
        this.saldo += valor;
    }

    public void sacar(Double valor) {
        this.saldo -= valor;
    }

    public void adicionarSaldoParaEmprestimo(Double valor) {
        this.saldoDisponivelParaEmprestimo += valor;
    }

    public void removerSaldoParaEmprestimo(Double valor) throws Exception {
        // Exception
        if(this.saldoDisponivelParaEmprestimo < valor) {
            throw new SaldoInvalidoException("Saldo para emprestimo inferior ao solicitado");
        }

        this.saldoDisponivelParaEmprestimo -= valor;
    }

    public String getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Double getSaldo() {
        return saldo;
    }

    public Double getSaldoDisponivelParaEmprestimo() {
        return saldoDisponivelParaEmprestimo;
    }

    @Override
    public String toString() {
        return "Conta{" +
                "id='" + id + '\'' +
                ", cliente=" + cliente +
                ", saldo=" + saldo +
                ", saldoDisponivelParaEmprestimo=" + saldoDisponivelParaEmprestimo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta = (Conta) o;
        return Objects.equals(id, conta.id) && Objects.equals(cliente, conta.cliente) && Objects.equals(saldo, conta.saldo) && Objects.equals(saldoDisponivelParaEmprestimo, conta.saldoDisponivelParaEmprestimo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cliente, saldo, saldoDisponivelParaEmprestimo);
    }
}
