package com.playsync.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.playsync.demo.Entities.ItadBuscaPorTermo;

@Repository
public interface ItadBuscaPorTermoRepository extends CrudRepository<ItadBuscaPorTermo, Long> {
    @Query("SELECT E FROM ItadBuscaPorTermo E WHERE LOWER(E.nomeJogo) LIKE %:termo%")
    List<ItadBuscaPorTermo> findByNome(@Param("termo") String termo);
    
    List<ItadBuscaPorTermo> findByIdGameIn(List<String> ids);

}
