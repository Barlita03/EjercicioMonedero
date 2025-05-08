package dds.monedero.model;

import java.time.LocalDate;

public class Movimiento {
  private double monto;
  private LocalDate fecha;
  private boolean esDeposito;

  // --- Constructor ---

  // NOTE: Y... son solo 3 parametros pero quizas podrian envolverse en otra clase
  public Movimiento(LocalDate fecha, double monto, boolean esDeposito) {
    this.fecha = fecha;
    this.monto = monto;
    this.esDeposito = esDeposito;
  }

  // --- Getters ---

  public double getMonto() {
    return monto;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  // --- Metodos ---

  public boolean fueDepositadoEn(LocalDate fecha) {
    return isDeposito() && esDeLaFecha(fecha);
  }

  public boolean fueExtraidoEn(LocalDate fecha) {
    return isExtraccion() && esDeLaFecha(fecha);
  }

  public boolean esDeLaFecha(LocalDate fecha) {
    return this.fecha.equals(fecha);
  }

  public boolean isDeposito() {
    return esDeposito;
  }

  public boolean isExtraccion() {
    return !esDeposito;
  }
}
