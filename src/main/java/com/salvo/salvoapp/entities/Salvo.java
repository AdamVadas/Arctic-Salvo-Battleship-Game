package com.salvo.salvoapp.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long salvoId;
    private Integer turnNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gameplayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "salvo_locations")
    private List<String> salvoLocations = new ArrayList<>();

    public Salvo() {
    }

    public Salvo(Integer turnNumber, List<String> salvoLocations, GamePlayer gamePlayer) {
        this.salvoLocations = salvoLocations;
        this.gamePlayer = gamePlayer;
        this.turnNumber = turnNumber;
    }

    public Long getSalvoId() {
        return salvoId;
    }

    public void setSalvoId(Long salvoId) {
        this.salvoId = salvoId;
    }

    public Integer getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(Integer turnNumber) {
        this.turnNumber = turnNumber;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }
}

