package com.example.dbestoque.repository;

import com.example.dbestoque.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Modifying
    @Query("DELETE FROM Produto p where p.id = ?1")
    void deleteById(Long id);

    @Modifying
    @Query("SELECT p FROM Produto p where p.nome like %?1% and p.preco <= ?2")
    Produto findByNomeLikeIgnoreCaseAndPrecoLessThan(String nome, BigDecimal preco);

}
