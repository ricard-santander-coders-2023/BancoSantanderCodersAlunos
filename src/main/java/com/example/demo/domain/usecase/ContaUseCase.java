package com.example.demo.domain.usecase;

import com.example.demo.domain.exception.SaldoInvalidoException;
import com.example.demo.domain.exception.ValorNegativoException;
import com.example.demo.domain.model.Conta;
import com.example.demo.domain.repository.ContaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContaUseCase {

    private ContaRepository contaGateway;

    public ContaUseCase(ContaRepository contaGateway) {
        this.contaGateway = contaGateway;
    }

    public void criarConta(Conta conta) {
        contaGateway.save(conta);
    }

    public Optional<Conta> buscarConta(String id) {
        return contaGateway.findById(id);
    }

    public void transferir(String idOrigem, String idDestino, Double valor) throws Exception {
        Optional<Conta> contaOrigem = contaGateway.findById(idOrigem);
        if (contaOrigem.isEmpty()) throw new Exception("Conta origem invalida - [id: " + idOrigem + "]");

        Optional<Conta> contaDestino = contaGateway.findById(idDestino);
        if (contaDestino.isEmpty()) throw new Exception("Conta destino invalida - [id: " + idDestino + "]");

        Conta contaOrigemExistente = contaOrigem.get();
        Conta contaDestinoExistente = contaDestino.get();

        contaOrigemExistente.sacar(valor);
        contaDestinoExistente.depositar(valor);

        contaGateway.save(contaOrigemExistente);
        contaGateway.save(contaDestinoExistente);
    }

    public void depositar(String idConta, Double valor) throws Exception {
        Optional<Conta> conta = contaGateway.findById(idConta);
        if (conta.isEmpty()) throw new Exception("Conta invalida - [id: " + idConta + "]");

        Conta contaExistente = conta.get();
        contaExistente.depositar(valor);
        contaGateway.save(contaExistente);
    }

    public void emprestimo(String idConta, Double valor) throws Exception {
        Optional<Conta> conta = contaGateway.findById(idConta);

        if (conta.isEmpty()) throw new Exception("Conta invalida - [id: " + idConta + "]");

        Conta contaExistente = conta.get();
        if(contaExistente.getSaldoDisponivelParaEmprestimo() < valor) throw new SaldoInvalidoException("Saldo para emprestimo insuficiente!");

        contaExistente.removerSaldoParaEmprestimo(valor);
        contaExistente.depositar(valor);

        contaGateway.save(contaExistente);
    }

    public void adicionarSaldoEmprestimo(String idConta, Double valor) throws Exception{
        Optional<Conta> conta = contaGateway.findById(idConta);
        if (conta.isEmpty()) throw new Exception("Conta invalida - [id: " + idConta + "]");

        if (valor < 0) throw new ValorNegativoException("O valor não pode ser negativo");

        Conta contaExistente = conta.get();
        contaExistente.adicionarSaldoParaEmprestimo(valor);

        contaGateway.save(contaExistente);
    }

}
