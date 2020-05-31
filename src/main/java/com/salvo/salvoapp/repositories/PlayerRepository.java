package com.salvo.salvoapp.repositories;

import com.salvo.salvoapp.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByUserName(@Param("name") String userName);
}