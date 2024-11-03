package ar.edu.unju.fi.poo.tp8poo.repository;

import ar.edu.unju.fi.poo.tp8poo.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByClienteNombreContaining(String nombre); // Filtrar por nombre del cliente
    List<Venta> findByClienteId(Long id);               // Filtrar por DNI del cliente
    List<Venta> findByFechaYHoraBetween(LocalDateTime fechaDesde, LocalDateTime fechaHasta); // Filtrar por rango de fechas
}
