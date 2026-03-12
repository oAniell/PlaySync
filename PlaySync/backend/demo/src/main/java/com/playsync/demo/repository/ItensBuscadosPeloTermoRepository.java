package com.playsync.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.playsync.demo.Entities.ItensBuscadorPeloTermo;

@Repository
public interface ItensBuscadosPeloTermoRepository extends CrudRepository<ItensBuscadorPeloTermo, Long> {
	@Query("SELECT E FROM ItensBuscadorPeloTermo E WHERE LOWER(E.nome) LIKE %:term%")
	List<ItensBuscadorPeloTermo> findByName(@Param("term") String term);

}
