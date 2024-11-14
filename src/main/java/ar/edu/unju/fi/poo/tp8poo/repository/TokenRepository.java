package ar.edu.unju.fi.poo.tp8poo.repository;

import ar.edu.unju.fi.poo.tp8poo.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository  extends JpaRepository<Token, Long> {
    Optional<Token> findByClienteId(Long clienteId );
}
