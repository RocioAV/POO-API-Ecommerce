package ar.edu.unju.fi.poo.tp8poo.repository;

import ar.edu.unju.fi.poo.tp8poo.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Cliente findByEmail(String email);
    Cliente findByCelular(String celular);

}
