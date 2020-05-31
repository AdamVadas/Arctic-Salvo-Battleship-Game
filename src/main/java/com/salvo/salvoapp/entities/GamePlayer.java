package com.salvo.salvoapp.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long gamePlayId;
    private Date gamePlayTimeStamp = new Date();

    private long gameScore;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Ship> ships;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Salvo> salvoes;

    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public GamePlayer() {
    }

    public GamePlayer(Game game, Player player, Date gamePlayTimeStamp) {
        this.game = game;
        this.player = player;
        this.gamePlayTimeStamp = gamePlayTimeStamp;
    }

    public Long getGamePlayId() {
        return gamePlayId;
    }

    public void setGamePlayId(Long gamePlayId) {
        this.gamePlayId = gamePlayId;
    }

    public Date getGamePlayTimeStamp() {
        return gamePlayTimeStamp;
    }

    public void setGamePlayTimeStamp(Date gamePlayTimeStamp) {
        this.gamePlayTimeStamp = gamePlayTimeStamp;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public void setSalvoes(Set<Salvo> salvoes) {
        this.salvoes = salvoes;
    }

    public Score getScore() {
        return player.getScores().stream().filter(score -> score.getGame() == game).findFirst().orElse(null);
    }
}
