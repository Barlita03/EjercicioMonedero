package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  // FIXME: NO ES NECESARIO INSTANCIARLO EN 0 YA QUE O TOMA UN MONTO INICIAL O 0 OBLIGATORIAMENTE
  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  // --- Constructores ---

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  // --- Metodos ---

  // FIXME: LAS VALIDACIONES PODRIAN SER ABSTRAIDAS EN OTROS METODOS
  public void poner(double cuanto) {

    // TODO: ABSTRACCION VALIDAR MONTO POSITIVO
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }

    // TODO: ABSTRACCION VALIDAR DISPONIBILIDAD DEPOSITO
    if (getMovimientos().stream()
            .filter(movimiento -> movimiento.fueDepositado(LocalDate.now()))
            .count()
        >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }

    // FIXME: TOTALMENTE INNECESARIO DERIVAR AL MOVIMIENTO LA RESPONSABILIDAD DE AGREGARLO A LA
    // FIXME: LISTA.
    // FIXME: SOBRE TODO VIENDO QUE TENEMOS UN METODO PARA HACERLO Y TIENTA A ROMPE EL
    // FIXME: ENCAPSULAMIENTO
    new Movimiento(LocalDate.now(), cuanto, true).agregateA(this);
  }

  // FIXME: LAS VALIDACIONES PODRIAN SER ABSTRAIDAS EN OTROS METODOS
  public void sacar(double cuanto) {

    // TODO: ABSTRACCION VALIDAR MONTO POSITIVO
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }

    // TODO: ABSTRACCION VALIDAR SALDO DISPONIBLE
    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }

    // FIXME: ABSTRACCION VALIDAR LIMITE
    // FIXME: SE USO VAR EN LUGAR DEL TIPO DE DATO CORRESPONDIENTE, ES UN LENGUAJE TIPADO...
    // FIXME: APROVECHEMOSLO
    var montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    var limite = 1000 - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException(
          "No puede extraer mas de $ " + 1000 + " diarios, " + "límite: " + limite);
    }

    new Movimiento(LocalDate.now(), cuanto, false).agregateA(this);
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {

    // FIXME: NO ESTA MAL COMO TAL PERO PODRIA AHORRARSE EL CREAR LA VARIABLE MOVIMIENTO
    var movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  // --- Getters ---

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  // --- Setters ---

  // FIXME: ES MUY PELIGROSO QUE UN SETTER SEA PUBLICO (TIENTA A ROMPER EL ENCAPSULAMIENTO)
  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  // FIXME: ES MUY PELIGROSO QUE UN SETTER SEA PUBLICO (TIENTA A ROMPER EL ENCAPSULAMIENTO)
  public double getSaldo() {
    return saldo;
  }

  // FIXME: ES MUY PELIGROSO QUE UN SETTER SEA PUBLICO (TIENTA A ROMPER EL ENCAPSULAMIENTO)
  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }
}
