package usecase;

import domain.exception.SaldoInvalidoException;
import domain.gateway.ContaGateway;
import domain.model.Cliente;
import domain.model.Conta;
import domain.usecase.ContaUseCase;
import infra.gateway.ContaGatewayLocal;
import org.junit.*;

import java.util.Optional;

public class ContaUseCaseTest {

    private ContaUseCase contaUseCase;
    private ContaGateway contaGateway;

    // @BeforeClass - antes da classe ser instanciada
    @BeforeClass
    public static void beforeClass() {
        // Subir banco
        // Preparar alguma config
        System.out.println("Before class");
    }

    // @Before - antes de CADA teste
    @Before
    public void before() {
        System.out.println("before");

        contaGateway = new ContaGatewayLocal();
        contaUseCase = new ContaUseCase(contaGateway);

        Cliente cliente1 = new Cliente("Ana", "111.111.111.11");
        Conta conta1 = new Conta("1", cliente1);

        Cliente cliente2 = new Cliente("Carla", "222.222.222.22");
        Conta conta2 = new Conta("2", cliente2);

        contaGateway.save(conta1);
        contaGateway.save(conta2);
    }

    // @After
    @After
    public void after() {
        System.out.println("After");
    }

    // @AfterClass
    @AfterClass
    public static void afterClass() {
        System.out.println("After class");
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
        Conta conta1DB = contaGateway.findById("1");
        Assert.assertEquals(valorEsperadoConta1, conta1DB.getSaldo());

        Double valorEsperadoConta2 = 20.0;
        Conta conta2DB = contaGateway.findById("2");
        Assert.assertEquals(valorEsperadoConta2, conta2DB.getSaldo());
    }

    @Test
    public void deveDepositarCorretamente() throws Exception {
        System.out.println("deveDepositarCorretamente");
        // Given -  Dado

        // When - Quando
        contaUseCase.depositar("1", 10.0);
        Conta conta1 = contaGateway.findById("1");

        // Then
        Double valorEsperado = 10.0;
        Assert.assertEquals(valorEsperado, conta1.getSaldo());
    }

    @Test
    public void testeExemplo1() {
        System.out.println("testeExemplo");
    }

    @Test
    public void testandoSeContaEstaSendoCriada() {

        Cliente clienteNovo = new Cliente("Poucos mas Muito Loucos", "666.666.666-69");
        Conta contaNova = new Conta("69", clienteNovo);

        contaUseCase.criarConta(contaNova);
        Assert.assertEquals(contaNova, contaGateway.findById("69"));
    }

    @Test
    public void testarBuscaDeConta() {
        Cliente clienteNovo = new Cliente("Poucos mas Muito Loucos", "666.666.666-69");
        Conta contaNova = new Conta("69", clienteNovo);

        contaUseCase.criarConta(contaNova);

        Conta contaEncontrada = contaUseCase.buscarConta("69");
        Assert.assertNotNull(contaEncontrada);
//        System.out.println("CONTA ENCONTRADA ===> " + contaEncontrada);
    }

    @Test
    public void testarEmprestimoComSaldoInsuficiente() throws Exception {
        Cliente clienteNovo = new Cliente("Poucos mas Muito Loucos", "666.666.666-69");
        Conta contaNova = new Conta("69", clienteNovo);
        contaUseCase.criarConta(contaNova);

        contaNova.adicionarSaldoParaEmprestimo(100d);

        Assert.assertThrows(SaldoInvalidoException.class, () -> contaUseCase.emprestimo("69", 300d));
    }

    @Test
    public void testarEmprestimoComSaldoBoladao() throws Exception {
        Cliente clienteNovo = new Cliente("Poucos mas Muito Loucos", "666.666.666-69");
        Conta contaNova = new Conta("69", clienteNovo);
        contaUseCase.criarConta(contaNova);

        contaNova.adicionarSaldoParaEmprestimo(200000d);
        contaUseCase.emprestimo("69", 100000d);
        Assert.assertEquals(100000d, contaNova.getSaldoDisponivelParaEmprestimo(),0.000001);
    }

    @Test
    public void testarEmprestimoContaInvalida() throws Exception {
        Assert.assertThrows(Exception.class, () -> contaUseCase.emprestimo("32", 300d));
    }

    ////////////////////////////////////////////////////
    @Test(expected = Exception.class)
    public void testTransferirContaOrigemInvalida() throws Exception {
        contaUseCase.transferir("invalid_id", "2", 100.0);
    }

    @Test(expected = Exception.class)
    public void testTransferirContaDestinoInvalida() throws Exception {
        contaUseCase.transferir("1", "invalid_id", 100.0);
    }

    @Test
    public void testaTransferirSemSaldoLancaExcessao() {
        try {
            contaUseCase.transferir("1", "2", 1000.0);
            Assert.fail("Esperado SaldoInvalidoException");
        } catch (SaldoInvalidoException e) {
            Assert.assertEquals("Saldo para emprestimo insuficiente!", e.getMessage());
        } catch (Exception e) {
            Assert.fail("Expected SaldoInvalidoException, but got " + e.getClass().getSimpleName());
        }
    }

    @Test(expected = Exception.class)
    public void testaEmprestimoContaInvalidaLancaExcessao() throws Exception {
        contaUseCase.emprestimo("invalid_id", 100.0);
    }

    @Test
    public void testaEmprestimoSemSaudoLancaExcessao() {
        try {
            contaUseCase.emprestimo("1", 1000.0);
            Assert.fail("Expected SaldoInvalidoException");
        } catch (SaldoInvalidoException e) {
            Assert.assertEquals("Saldo para emprestimo insuficiente!", e.getMessage());
        } catch (Exception e) {
            Assert.fail("Expected SaldoInvalidoException, but got " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testaEmprestimoComSucessoEBalancoSuficiente() throws Exception {
        contaUseCase.emprestimo("2", 100.0);
        Conta conta = contaGateway.findById("2");
        Assert.assertEquals(100.0, conta.getSaldo(), 0.000001);
    }

}
