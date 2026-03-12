package com.playsync.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.playsync.demo.Entities.PrecosJogos;

@Repository
public interface PrecoPorJogoRepository extends CrudRepository<PrecosJogos, Long> {

}
