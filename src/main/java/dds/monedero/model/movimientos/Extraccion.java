package dds.monedero.model.movimientos;

import java.time.LocalDate;

public class Extraccion extends Movimiento {

  public Extraccion(LocalDate fecha, double monto) {
    super(fecha, monto);
  }

  @Override
  public double cantidadExtraida() {
    return getMonto();
  }
}