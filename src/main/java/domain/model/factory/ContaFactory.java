package domain.model.factory;

import domain.model.Cliente;
import domain.model.Conta;

public class ContaFactory {
    public static Conta criarConta(String id, Cliente cliente, Double saldo, Double saldoDisponivelParaEmprestimo) {
        Conta conta = new Conta(id, cliente);
        conta.depositar(saldo);
        conta.adicionarSaldoParaEmprestimo(saldoDisponivelParaEmprestimo);
        return conta;
    }
}
