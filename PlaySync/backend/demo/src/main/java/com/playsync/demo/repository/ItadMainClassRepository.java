package com.playsync.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.playsync.demo.Entities.ItadMainClass;

@Repository
public interface ItadMainClassRepository extends CrudRepository<Long, ItadMainClass> {
    @Query("SELECT E FROM ItadMainClass E WHERE E.idGame IN :ids")
    List<ItadMainClass> findByIds(@Param("ids") List<String> ids);
}
