package dds.monedero.model.movimientos;

import java.time.LocalDate;

public abstract class Movimiento {
  private double monto;
  private LocalDate fecha;

  // --- Constructor ---

  public Movimiento(LocalDate fecha, double monto) {
    this.fecha = fecha;
    this.monto = monto;
  }

  // --- Getters ---

  public double getMonto() {
    return monto;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  // --- Metodos ---

  public boolean fueRealizadoEn(LocalDate fecha) {
    return esDeLaFecha(fecha);
  }

  public boolean esDeLaFecha(LocalDate fecha) {
    return this.fecha.equals(fecha);
  }

  public double cantidadExtraida() {
    return 0;
  }

  public double cantidadDepositada() {
    return 0;
  }
}
