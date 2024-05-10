package model;

import exception.SaldoInvalidoException;
import org.junit.Assert;
import org.junit.Test;

public class ContaTest {

    @Test
    public void deveLancarExceptionCasoValorSolicitadoSejaMaiorQueSaldoDisponivel(){
        // Given
        Cliente cliente1 = new Cliente("Ana", "123.123.123-12");
        Conta conta1 = new Conta("1", cliente1);

        // When
        try {
            conta1.removerSaldoParaEmprestimo(100.0);
        } catch (SaldoInvalidoException sie){
            Assert.assertEquals("Saldo para emprestimo inferior ao solicitado", sie.getMessage());
        }

    }
}
