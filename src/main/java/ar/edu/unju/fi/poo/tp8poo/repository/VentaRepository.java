package ar.edu.unju.fi.poo.tp8poo.repository;

import ar.edu.unju.fi.poo.tp8poo.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    @Query("SELECT v FROM Venta v WHERE " +
            "(:nombreCliente IS NULL OR v.cliente.nombre LIKE %:nombreCliente%) AND " +
            "(:idCliente IS NULL OR v.cliente.id = :idCliente) AND " +
            "(:fechaDesde IS NULL OR v.fechaYHora >= :fechaDesde) AND " +
            "(:fechaHasta IS NULL OR v.fechaYHora <= :fechaHasta)")
    List<Venta> filtrarVentas(@Param("nombreCliente") String nombreCliente,
                              @Param("idCliente") Long idCliente,
                              @Param("fechaDesde") LocalDateTime fechaDesde,
                              @Param("fechaHasta") LocalDateTime fechaHasta);
}
