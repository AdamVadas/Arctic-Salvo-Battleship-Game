package com.salvo.salvoapp.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long shipId;
    private String shipType;
    private Boolean isSunk = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gameplayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="ship_locations")
    private List<String> shipLocations = new ArrayList<>();

    public Ship() {
    }

    public Ship(String shipType, List<String> shipLocations, GamePlayer gamePlayer) {
        this.shipType = shipType;
        this.shipLocations = shipLocations;
        this.gamePlayer = gamePlayer;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Long getShipId() {
        return shipId;
    }

    public void setShipId(Long shipId) {
        this.shipId = shipId;
    }

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    public List<String> getShipLocations() {
        return shipLocations;
    }

    public void setShipLocations(List<String> shipLocations) {
        this.shipLocations = shipLocations;
    }

    public Boolean getSunk() {
        return isSunk;
    }

    public void setSunk(Boolean sunk) {
        isSunk = sunk;
    }
}
