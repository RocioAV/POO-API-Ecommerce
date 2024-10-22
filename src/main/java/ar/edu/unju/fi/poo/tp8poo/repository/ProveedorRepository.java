package ar.edu.unju.fi.poo.tp8poo.repository;

import ar.edu.unju.fi.poo.tp8poo.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
}