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

  public void poner(double cuanto) {

    validarMontoPositivo(cuanto);
    validarDisponibilidadDeposito();

    // FIXME: TOTALMENTE INNECESARIO DERIVAR AL MOVIMIENTO LA RESPONSABILIDAD DE AGREGARLO A LA
    //  LISTA.
    // FIXME: SOBRE TODO VIENDO QUE TENEMOS UN METODO PARA HACERLO Y TIENTA A ROMPE EL
    //  ENCAPSULAMIENTO (FEATURE ENVY)
    new Movimiento(LocalDate.now(), cuanto, true).agregateA(this);
  }

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

    movimientos.add(new Movimiento(fecha, cuanto, esDeposito));
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

  private double getSaldo() {
    return saldo;
  }

  // --- Setters ---

  private void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  private void setSaldo(double saldo) {
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

    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    // FIXME: EL LIMITE DIARIO (1000) PODRIA ESTAR GUARDADO EN UNA VARIABLE, EN CASO DE QUE
    //  HAYA QUE MODIFICARLO SERIA MAS SENCILLO
    double limite = 1000 - montoExtraidoHoy;
    if (monto > limite) {
      throw new MaximoExtraccionDiarioException(
          "No puede extraer mas de $ " + 1000 + " diarios, " + "límite: " + limite);
    }
  }
}
