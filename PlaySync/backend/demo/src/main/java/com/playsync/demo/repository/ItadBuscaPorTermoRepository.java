package com.playsync.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.playsync.demo.Entities.ItadBuscaPorTermo;

@Repository
public interface ItadBuscaPorTermoRepository extends CrudRepository<ItadBuscaPorTermo, Long> {

}
