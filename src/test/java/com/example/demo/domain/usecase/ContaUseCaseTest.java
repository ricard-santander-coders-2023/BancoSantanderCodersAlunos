package com.example.demo.domain.usecase;

import com.example.demo.domain.exception.SaldoInvalidoException;
import com.example.demo.domain.exception.ValorNegativoException;
import com.example.demo.domain.model.Cliente;
import com.example.demo.domain.model.Conta;
import com.example.demo.domain.repository.ClienteRepository;
import com.example.demo.domain.repository.ContaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContaUseCaseTest {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ContaUseCase contaUseCase;


    @BeforeEach
    void setUp() {


        Cliente cliente1 = new Cliente("Ana", "111.111.111.11");
        Conta conta1 = new Conta("1", cliente1);

        Cliente cliente2 = new Cliente("Carla", "222.222.222.22");
        Conta conta2 = new Conta("2", cliente2);

        clienteRepository.save(cliente1);
        clienteRepository.save(cliente2);
        contaRepository.save(conta1);
        contaRepository.save(conta2);
    }

    @Test
    public void deveTransferirCorretamenteEntreDuasContas() throws Exception {
        // Mocks

        System.out.println("deveTransferirCorretamenteEntreDuasContas");
        // Given - Dado
        contaUseCase.depositar("1", 100.0);

        // When - Quando
        contaUseCase.transferir("1", "2", 20.0);

        // Then - Entao
        // - Valor esperado - Valor atual
        Double valorEsperadoConta1 = 80.0;
        Conta conta1DB = contaRepository.findById("1").get();
        Assertions.assertEquals(valorEsperadoConta1, conta1DB.getSaldo());

        Double valorEsperadoConta2 = 20.0;
        Conta conta2DB = contaRepository.findById("2").get();
        Assertions.assertEquals(valorEsperadoConta2, conta2DB.getSaldo());
    }


    @Test
    public void testeTransferirDeveLancarExcecaoQuandoContaOrigemForNull() {
        String idOrigem = "203940";
        Optional<Conta> contaDestino = contaUseCase.buscarConta("2");
        Assertions.assertThrows(Exception.class, () -> contaUseCase.transferir(idOrigem, contaDestino.get().getId(), 100.00),"Conta origem invalida - [id: " + idOrigem + "]");
    }

    @Test
    public void testeTransferirDeveLancarExcecaoQuandoContaDestinoForNull() {
        Optional<Conta> contaOrigem = contaUseCase.buscarConta("1");
        String idDestino = "203940";
        Assertions.assertThrows(Exception.class, () -> contaUseCase.transferir(contaOrigem.get().getId(), idDestino , 100.00),"Conta origem invalida - [id: " + idDestino + "]");
    }

    @Test
    public void deveDepositarCorretamente() throws Exception {
        System.out.println("deveDepositarCorretamente");
        // Given -  Dado

        // When - Quando
        contaUseCase.depositar("1", 10.0);
        Optional<Conta> conta1 = contaRepository.findById("1");

        // Then
        Double valorEsperado = 10.0;
        Assertions.assertEquals(valorEsperado, conta1.get().getSaldo());
    }

    @Test
    public void testarDepositarDeveLancarExcecaoQuandoContaForNull() {
        Assertions.assertThrows(Exception.class, () -> contaUseCase.depositar("10292", 100.00));
    }

    @Test
    public void testeExemplo1() {
        System.out.println("testeExemplo");
    }

    @Test
    public void testandoSeContaEstaSendoCriada() {

        Cliente clienteNovo = new Cliente("PouAssertionss Muito Loucos", "666.666.666-69");
        Conta contaNova = new Conta("69", clienteNovo);

        clienteRepository.save(clienteNovo);

        contaUseCase.criarConta(contaNova);
        Assertions.assertEquals(contaNova, contaRepository.findById("69").get());
    }

    @Test
    public void testarBuscaDeConta() {
        Cliente clienteNovo = new Cliente("Poucos mas Muito Loucos", "666.666.666-69");
        Conta contaNova = new Conta("69", clienteNovo);

        clienteRepository.save(clienteNovo);

        contaUseCase.criarConta(contaNova);

        Optional<Conta> contaEncontrada = contaUseCase.buscarConta("69");

        Assertions.assertNotNull(contaEncontrada.get());
    }

    @Test
    public void testarEmprestimoComSaldoInsuficiente() throws Exception {
        Cliente clienteNovo = new Cliente("Poucos mas Muito Loucos", "666.666.666-69");
        Conta contaNova = new Conta("69", clienteNovo);

        clienteRepository.save(clienteNovo);

        contaUseCase.criarConta(contaNova);

        contaNova.adicionarSaldoParaEmprestimo(100d);

        Assertions.assertThrows(SaldoInvalidoException.class, () -> contaUseCase.emprestimo("69", 300d));
    }

    @Test
    public void testarEmprestimoComSaldoBoladao() throws Exception {
        Cliente clienteNovo = new Cliente("Poucos mas Muito Loucos", "666.666.666-69");
        Conta contaNova = new Conta("69", clienteNovo);

        clienteRepository.save(clienteNovo);

        contaUseCase.criarConta(contaNova);

        contaUseCase.adicionarSaldoEmprestimo("69",200000d);
        contaUseCase.emprestimo("69", 100000d);

        Optional<Conta> contaDB = contaUseCase.buscarConta("69");

        Assertions.assertEquals(100000d, contaDB.get().getSaldoDisponivelParaEmprestimo(),0.000001);
    }

    @Test
    public void testarEmprestimoContaInvalida() {
        Assertions.assertThrows(Exception.class, () -> contaUseCase.emprestimo("32", 300d));
    }

    @Test
    public void testarEmprestimoComSaldoExato() throws Exception {
        Cliente cliente = new Cliente("Valido Cliente", "123.456.789-10");
        Conta conta = new Conta("123", cliente);
        conta.adicionarSaldoParaEmprestimo(5000d);

        clienteRepository.save(cliente);

        contaUseCase.criarConta(conta);

        contaUseCase.emprestimo("123", 5000d);

        Optional<Conta> contaDB = contaUseCase.buscarConta("123");
        Assertions.assertEquals(0d, contaDB.get().getSaldoDisponivelParaEmprestimo(), 0.001);
    }

    @Test
    public void testarAdicionarSaldoEmprestimoDeveLancarExcecaoQuandoContaForNula() {
        Assertions.assertThrows(Exception.class, () -> contaUseCase.adicionarSaldoEmprestimo("300",100d));
    }

    @Test
    public void testarAdicionarSaldoEmprestimoDeveLancarExcecaoQuandoValorForNegativo() {
        Cliente cliente = new Cliente("Cliente Teste", "122.222.333-10");
        Conta conta = new Conta("400", cliente);

        clienteRepository.save(cliente);
        contaUseCase.criarConta(conta);

        Assertions.assertThrows(ValorNegativoException.class, () -> contaUseCase.adicionarSaldoEmprestimo("400",-100d));
    }

}