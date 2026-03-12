package com.playsync.demo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.playsync.demo.Entities.BuscaPorTermo;

@Repository
public interface BuscaPorTermoRepository extends CrudRepository<BuscaPorTermo, Long> {
}
