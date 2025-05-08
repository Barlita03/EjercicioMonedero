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
  private int cantidadMaximaDeDepositosDiarios = 3;
  private int limiteDeExtraccionDiario = 1000;

  // --- Constructores ---

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
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

  public double getSaldo() {
    return saldo;
  }

  // --- Setters ---

  private void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  private void setSaldo(double saldo) {
    this.saldo = saldo;
  }

  // --- Metodos ---

  public void poner(double cuanto) {

    validarMontoPositivo(cuanto);
    validarDisponibilidadDeposito();

    agregarMovimiento(LocalDate.now(), cuanto, true);
  }

  public void sacar(double cuanto) {

    validarMontoPositivo(cuanto);
    validarSaldoDisponible(cuanto);
    validarLimiteDeExtraccionDiario(cuanto);

    agregarMovimiento(LocalDate.now(), cuanto, false);
  }

  private void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {

    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
    corregirSaldo(movimiento);
  }

  // FIXME: PREGUNTAR SI UN MOVIMIENTO ES O NO DEPOSITO ES UN TYPE TEST
  private void corregirSaldo(Movimiento movimiento) {
    if (movimiento.isDeposito()) {
      setSaldo(saldo + movimiento.getMonto());
    } else {
      setSaldo(saldo - movimiento.getMonto());
    }
  }

  // --- Validaciones ---

  private void validarMontoPositivo(double monto) {

    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a operar debe ser un valor positivo");
    }
  }

  private void validarDisponibilidadDeposito() {
    if (getMovimientos().stream()
            .filter(movimiento -> movimiento.fueDepositadoEn(LocalDate.now()))
            .count()
        >= cantidadMaximaDeDepositosDiarios) {
      throw new MaximaCantidadDepositosException(
          "Ya excedio los " + cantidadMaximaDeDepositosDiarios + " depositos diarios");
    }
  }

  private void validarSaldoDisponible(double monto) {
    if (getSaldo() - monto < 0) {
      throw new SaldoMenorException("No puede sacar mas de $" + getSaldo());
    }
  }

  private void validarLimiteDeExtraccionDiario(double monto) {

    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = limiteDeExtraccionDiario - montoExtraidoHoy;
    if (monto > limite) {
      throw new MaximoExtraccionDiarioException(
          "No puede extraer mas de $"
              + limiteDeExtraccionDiario
              + " diarios, "
              + "límite: "
              + limite);
    }
  }
}
