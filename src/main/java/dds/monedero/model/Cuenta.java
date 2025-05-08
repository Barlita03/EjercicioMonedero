package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private List<Movimiento> movimientos = new ArrayList<>();

  // --- Constructores ---

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  // --- Metodos ---

  // FIXME: LAS VALIDACIONES PODRIAN SER ABSTRAIDAS EN OTROS METODOS (LONGMETHOD)
  public void poner(double cuanto) {

    validarMontoPositivo(cuanto);
    validarDisponibilidadDeposito();

    // FIXME: TOTALMENTE INNECESARIO DERIVAR AL MOVIMIENTO LA RESPONSABILIDAD DE AGREGARLO A LA
    //  LISTA.
    // FIXME: SOBRE TODO VIENDO QUE TENEMOS UN METODO PARA HACERLO Y TIENTA A ROMPE EL
    //  ENCAPSULAMIENTO (FEATURE ENVY)
    new Movimiento(LocalDate.now(), cuanto, true).agregateA(this);
  }

  // FIXME: LAS VALIDACIONES PODRIAN SER ABSTRAIDAS EN OTROS METODOS (LONGMETHOD)
  public void sacar(double cuanto) {

    validarMontoPositivo(cuanto);
    validarSaldoDisponible(cuanto);
    validarLimiteDeExtraccionDiario(cuanto);

    // FIXME: TOTALMENTE INNECESARIO DERIVAR AL MOVIMIENTO LA RESPONSABILIDAD DE AGREGARLO A LA
    //  LISTA.
    // FIXME: SOBRE TODO VIENDO QUE TENEMOS UN METODO PARA HACERLO Y TIENTA A ROMPE EL
    //  ENCAPSULAMIENTO (FEATURE ENVY)
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

  // --- Validaciones ---

  private void validarMontoPositivo(double monto) {

    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a operar debe ser un valor positivo");
    }
  }

  private void validarDisponibilidadDeposito() {
    if (getMovimientos().stream()
        .filter(movimiento -> movimiento.fueDepositado(LocalDate.now()))
        .count()
        // FIXME: LA CANTIDAD MAXIMA DE DEPOSITOS DIARIOS PUEDE ESTAR GUARDADA EN UNA VARIABLE,
        //  EN CASO DE QUE HAYA QUE MODIFICARLO SERIA MAS SENCILLO
        >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  private void validarSaldoDisponible(double monto) {
    if (getSaldo() - monto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  private void validarLimiteDeExtraccionDiario(double monto) {

    // FIXME: SE USO VAR EN LUGAR DEL TIPO DE DATO CORRESPONDIENTE, ES UN LENGUAJE TIPADO...
    //  APROVECHEMOSLO
    var montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    // FIXME: EL LIMITE DIARIO (1000) PODRIA ESTAR GUARDADO EN UNA VARIABLE, EN CASO DE QUE
    //  HAYA QUE MODIFICARLO SERIA MAS SENCILLO
    var limite = 1000 - montoExtraidoHoy;
    if (monto > limite) {
      throw new MaximoExtraccionDiarioException(
          "No puede extraer mas de $ " + 1000 + " diarios, " + "límite: " + limite);
    }
  }
}
