package com.playsync.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.playsync.demo.Entities.CheapSharkJogosEPrecosApi;

@Repository
public interface CheapSharkJogosEPrecosApiRepository extends CrudRepository<CheapSharkJogosEPrecosApi, Long> {
    @Query("SELECT E FROM CheapSharkJogosEPrecosApi E WHERE LOWER(E.nomeJogo) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<CheapSharkJogosEPrecosApi> selectByTerm(@Param("termo") String termo);

}
