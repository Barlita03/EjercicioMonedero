package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import dds.monedero.model.movimientos.Deposito;
import dds.monedero.model.movimientos.Extraccion;
import dds.monedero.model.movimientos.Movimiento;

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
        .filter(movimiento -> movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::cantidadExtraida)
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

    agregarDeposito(LocalDate.now(), cuanto);
  }

  public void sacar(double cuanto) {

    validarMontoPositivo(cuanto);
    validarSaldoDisponible(cuanto);
    validarLimiteDeExtraccionDiario(cuanto);

    agregarExtracion(LocalDate.now(), cuanto);
  }

  private void agregarDeposito(LocalDate fecha, double cuanto) {

    Movimiento movimiento = new Deposito(fecha, cuanto);
    movimientos.add(movimiento);
    setSaldo(saldo + cuanto);
  }

  private void agregarExtracion(LocalDate fecha, double cuanto) {

    Movimiento movimiento = new Extraccion(fecha, cuanto);
    movimientos.add(movimiento);
    setSaldo(saldo - cuanto);
  }

  // --- Validaciones ---

  private void validarMontoPositivo(double monto) {

    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a operar debe ser un valor positivo");
    }
  }

  private void validarDisponibilidadDeposito() {
    if (getMovimientos().stream()
            .filter(movimiento -> movimiento.fueRealizadoEn(LocalDate.now()))
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
