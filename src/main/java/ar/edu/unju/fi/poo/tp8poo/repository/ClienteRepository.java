package ar.edu.unju.fi.poo.tp8poo.repository;

import ar.edu.unju.fi.poo.tp8poo.entity.Cliente;
import ar.edu.unju.fi.poo.tp8poo.entity.ClienteEstandar;
import ar.edu.unju.fi.poo.tp8poo.entity.ClientePremium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Cliente findByEmail(String email);
    Cliente findByCelular(String celular);

}
