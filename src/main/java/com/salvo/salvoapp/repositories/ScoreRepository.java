package com.salvo.salvoapp.repositories;

import com.salvo.salvoapp.entities.Game;
import com.salvo.salvoapp.entities.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource
public interface ScoreRepository extends JpaRepository<Score, Long>{
    List<Game> findByFinishDate (LocalDate finishDate);
}
