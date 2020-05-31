package com.salvo.salvoapp.repositories;

import com.salvo.salvoapp.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByTimeStamp (LocalDate timeStamp);
}