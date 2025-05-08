package dds.monedero.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  @DisplayName("Es posible poner $1500 en una cuenta vacía")
  void Poner() {
    cuenta.poner(1500);

    assertEquals(1500, cuenta.getSaldo());
  }

  @Test
  @DisplayName("No es posible poner montos negativos")
  void PonerMontoNegativo() {
    Exception e = assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));

    assertEquals("-1500.0: el monto a operar debe ser un valor positivo", e.getMessage());
  }

  @Test
  @DisplayName("No es posible extraer un monto negativo")
  void ExtraerMontoNegativo() {
    Exception e = assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));

    assertEquals("-500.0: el monto a operar debe ser un valor positivo", e.getMessage());
  }

  @Test
  @DisplayName("Es posible realizar múltiples depósitos consecutivos")
  void TresDepositos() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);

    assertEquals(1500 + 456 + 1900, cuenta.getSaldo());
  }

  @Test
  @DisplayName("No es posible superar la máxima cantidad de depositos diarios")
  void MasDeTresDepositos() {
    Exception e = assertThrows(
        MaximaCantidadDepositosException.class,
        () -> {
          cuenta.poner(1500);
          cuenta.poner(456);
          cuenta.poner(1900);
          cuenta.poner(245);
        });

    assertEquals("Ya excedio los 3 depositos diarios", e.getMessage());
  }

  @Test
  @DisplayName("No es posible extraer más que el saldo disponible")
  void ExtraerMasQueElSaldo() {
    Exception e = assertThrows(
        SaldoMenorException.class,
        () -> {
          cuenta.poner(90);
          cuenta.sacar(100);
        });

    assertEquals("No puede sacar mas de $90.0", e.getMessage());
  }

  @Test
  @DisplayName("No es posible extraer más que el límite diario")
  void ExtraerMasDe1000() {
    Exception e = assertThrows(
        MaximoExtraccionDiarioException.class,
        () -> {
          cuenta.poner(5000);
          cuenta.sacar(1001);
        });

    assertEquals("No puede extraer mas de $1000 diarios, límite: 1000.0", e.getMessage());
  }
}
