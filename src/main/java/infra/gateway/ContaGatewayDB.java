package infra.gateway;

import domain.gateway.ContaGateway;
import domain.model.Cliente;
import domain.model.Conta;
import domain.model.factory.ContaFactory;
import infra.factory.database.DatabaseConnectionFactory;

import java.sql.*;

public class ContaGatewayDB implements ContaGateway {

    private final String url = "jdbc:h2:mem:teste;DB_CLOSE_DELAY=-1";
    private final String usuario = "sa";
    private final String senha = "";

    private void incializarDB() {
        try (Connection conexao = DatabaseConnectionFactory.getConexao(url,usuario,senha)){
            Statement stmt = conexao.createStatement();
//            stmt.execute("CREATE DATABASE IF NOT EXISTS banco_santander");
            stmt.execute("""
                         CREATE TABLE IF NOT EXISTS clientes (
                         cpf VARCHAR(255) PRIMARY KEY,
                         nome VARCHAR(255) NOT NULL
                         )
                         """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS contas (
                        id VARCHAR(255) PRIMARY KEY,
                        cpf_cliente VARCHAR(255),
                        saldo DOUBLE PRECISION NOT NULL,
                        saldoDisponivelParaEmprestimo DOUBLE PRECISION NOT NULL,
                        FOREIGN KEY (cpf_cliente) REFERENCES clientes(cpf)
                        )
                        """);

        } catch (SQLException e) {
            throw new RuntimeException("banco nao incializado", e);
        }
    }

    public ContaGatewayDB() {
        this.incializarDB();
    }

    @Override
    public Conta findById(String id) {

        String sql = "SELECT c.id, c.cpf_cliente, c.saldo, c.saldoDisponivelParaEmprestimo, cl.nome " +
                "FROM contas c " +
                "JOIN clientes cl ON c.cpf_cliente = cl.cpf " +
                "WHERE c.id = ?";

        try (Connection conexao = DatabaseConnectionFactory.getConexao(url,usuario,senha);
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1,id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String idConta = rs.getString("id");
                String nomeCliente = rs.getString("nome");
                String cpfCliente = rs.getString("cpf_cliente");
                Double saldo = rs.getDouble("saldo");
                Double saldoDisponivelParaEmprestimo = rs.getDouble("saldoDisponivelParaEmprestimo");

                Cliente cliente = new Cliente(nomeCliente, cpfCliente);

                return ContaFactory.criarConta(idConta, cliente,saldo,saldoDisponivelParaEmprestimo);
            }

        } catch (SQLException e) {
            throw new RuntimeException("erro ao buscar por id",e);
        }
        return null;
    }

    @Override
    public Conta save(Conta conta) {
        String sql = "MERGE INTO contas (id, cpf_cliente, saldo, saldoDisponivelParaEmprestimo) VALUES (?, ?, ?, ?)";
        try (Connection conexao = DatabaseConnectionFactory.getConexao(url, usuario, senha);
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            this.saveCliente(conta.getCliente());
            ps.setString(1, conta.getId());
            ps.setString(2, conta.getCliente().getCpf());
            ps.setDouble(3, conta.getSaldo());
            ps.setDouble(4, conta.getSaldoDisponivelParaEmprestimo());
            ps.executeUpdate();
            return conta;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("erro salvand CONTA");
        }
    }

    public Cliente saveCliente(Cliente cliente) {
        String sql = "MERGE INTO clientes (cpf, nome) VALUES (?, ?)";
        try (Connection conexao = DatabaseConnectionFactory.getConexao(url, usuario, senha);
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, cliente.getCpf());
            ps.setString(2, cliente.getNome());

            ps.executeUpdate();
            return cliente;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("erro salvand CLIENTE");
        }
    }

}