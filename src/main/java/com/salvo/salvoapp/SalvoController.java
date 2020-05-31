package com.salvo.salvoapp;

import com.salvo.salvoapp.entities.*;
import com.salvo.salvoapp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;

import static java.util.stream.Collectors.toList;

@EnableAutoConfiguration
@Configuration
@ComponentScan
@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @RequestMapping("/current-player")
    private Map<String, Object> getCurrentPlayer(Authentication authentication) {
        makePlayerDTO(playerRepository.findByUserName(authentication.getName()));
        return makePlayerDTO(playerRepository.findByUserName(authentication.getName()));
    }

    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> newGame(Authentication authentication) {
        Map<String, Object> loggedInPlayer = new LinkedHashMap<>();
        if (authentication != null) {
            Player player = playerRepository.findByUserName(authentication.getName());
            Game game = new Game(new Date());
            gameRepository.save(game);
            GamePlayer gameplayer = new GamePlayer(game, player, new Date());
            gamePlayerRepository.save(gameplayer);
            return new ResponseEntity<>(makeMap("gpid", gameplayer.getGamePlayId()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(makeMap("error", "Unauthorised"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> newShips(@PathVariable Long gamePlayerId,
                                                        @RequestBody Set<Ship> ships,
                                                        Authentication authentication) {
        if (authentication != null) {
            Player player = playerRepository.findByUserName(authentication.getName());
            GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
            for (Ship oneShip : ships) {
                oneShip.setGamePlayer(gamePlayer);
                shipRepository.save(oneShip);
            }
            return new ResponseEntity<>(makeMap("success", "ships posted"), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(makeMap("error", "Unauthorised"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> newSalvoes(@PathVariable Long gamePlayerId,
                                                          @RequestBody Salvo salvo,
                                                          Authentication authentication) {
        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
        Boolean isMyTurn = isMyTurn(gamePlayer);
        if (authentication != null) {
            if (isMyTurn) {
                Integer turn = getLastTurn(gamePlayer);
                salvo.setTurnNumber(turn);
                salvo.setGamePlayer(gamePlayer);
                salvo.setTurnNumber(getLastTurn(gamePlayer) + 1);
                salvoRepository.save(salvo);
                return new ResponseEntity<>(makeMap("success", "salvoes posted"), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(makeMap("error", "not your turn"), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(makeMap("error", "Unauthorised"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> newJoiner(@PathVariable Long gameId,
                                                         Authentication authentication) {
        Map<String, Object> loggedInPlayer = new LinkedHashMap<>();
        if (authentication != null) {
            Player player = playerRepository.findByUserName(authentication.getName());
            Game gameToJoin = gameRepository.findOne(gameId);
            GamePlayer gamePlayer = new GamePlayer(gameToJoin, player, new Date());
            gamePlayerRepository.save(gamePlayer);
            if (gameToJoin.getGamePlayers().size() > 1) {
                return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(makeMap("gpid", gamePlayer.getGamePlayId()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(makeMap("error", "Unauthorised"), HttpStatus.UNAUTHORIZED);
        }
    }


    @RequestMapping(value = "/games", method = RequestMethod.GET)
    private Map<String, Object> getPlayerData(Authentication authentication) {
        Map<String, Object> loggedInPlayer = new LinkedHashMap<>();
        if (authentication != null) {
            Player player = playerRepository.findByUserName(authentication.getName());
            loggedInPlayer.put("player", makePlayerDTO(player));
            loggedInPlayer.put("games", gameRepository.findAll().stream().map(this::makeGameDTO).collect(toList()));
        } else {
            loggedInPlayer.put("error", "log in");
        }
        return loggedInPlayer;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @RequestMapping(path = "/game_view/{gamePlayerId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getOneGame(@PathVariable Long gamePlayerId,
                                                          Authentication authentication) {
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
        Player loggedInPlayer = playerRepository.findByUserName(authentication.getName());
        Player gameViewPlayer = gamePlayer.getPlayer();
        if (loggedInPlayer.getId().equals(gameViewPlayer.getId())) {
            Map<String, Object> oneGame = new LinkedHashMap<>();
            boolean isGameOver = false;
            oneGame.put("games", makeGameDTO(gamePlayer.getGame()));
            oneGame.put("ships", gamePlayer.getShips()
                    .stream()
                    .map(this::makeShipDTO)
                    .collect(toList()));
            oneGame.put("salvoes", gamePlayer.getSalvoes()
                    .stream()
                    .map(this::makeSalvoDTO)
                    .collect(toList()));
            oneGame.put("user_turn", getLastTurn(gamePlayer));

            if (gamePlayer.getGame().getGamePlayers().size() == 2) {
                GamePlayer enemyGamePlayer = getOtherGamePlayer(gamePlayer);
                if (enemyGamePlayer != null) {
                    oneGame.put("enemy_salvoes", enemyGamePlayer.getSalvoes()
                            .stream()
                            .map(this::makeSalvoDTO)
                            .collect(toList()));
                    oneGame.put("enemy_ships", enemyGamePlayer.getShips()
                            .stream()
                            .map(this::makeShipDTO)
                            .collect(toList()));
                    oneGame.put("my_ships_sunk", gamePlayer.getShips()
                            .stream()
                            .filter(oneShip -> isShipSunk(getSalvoLocations(getOtherGamePlayer(gamePlayer)), oneShip)).count());
                    oneGame.put("enemy_ships_sunk", getOtherGamePlayer(gamePlayer).getShips()
                            .stream()
                            .filter(oneShip -> isShipSunk(getSalvoLocations(gamePlayer), oneShip)).count());
                    oneGame.put("enemy_salvos", getSalvoInfo(enemyGamePlayer));
                    oneGame.put("hits_on_enemy", getHitLocations(gamePlayer));
                    oneGame.put("hits_on_me", getHitLocations(getOtherGamePlayer(gamePlayer)));
                    oneGame.put("no_of_hits_on_enemy", getHitLocations(gamePlayer).size());
                    oneGame.put("no_of_hits_on_me", getHitLocations(getOtherGamePlayer(gamePlayer)).size());
                    oneGame.put("enemy_turn", getLastTurn(enemyGamePlayer));
                    oneGame.put("is_my_turn", isMyTurn(gamePlayer));
                    String winner = "the game is not over yet";
                    if (getLastTurn(gamePlayer).equals(getLastTurn(enemyGamePlayer)) && gamePlayer.getScore() == null) {
                        if (getAllSunkShips(gamePlayer.getShips()).size() == 5 || getAllSunkShips(enemyGamePlayer.getShips()).size() == 5) {
                            endTheGame(gamePlayer);
                            isGameOver = true;
                            if (!areAllShipsSunk(getAllSunkShips(gamePlayer.getShips()))) {
                                winner = gamePlayer.getPlayer().getUserName();
                            } else if (!areAllShipsSunk(getAllSunkShips(getOtherGamePlayer(gamePlayer).getShips()))) {
                                winner = getOtherGamePlayer(gamePlayer).getPlayer().getUserName();
                            } else {
                                winner = "DRAW";
                            }
                            oneGame.put("winner", winner);
                        }
                    }
                    oneGame.put("is_game_over", isGameOver);
                    oneGame.put("are_the_turns_equal", getLastTurn(gamePlayer).equals((getLastTurn(enemyGamePlayer))));
                    oneGame.put("no_of_my_sunk_ships", getAllSunkShips(gamePlayer.getShips()).size());
                    oneGame.put("no_of_enemy_sunk_ships", getAllSunkShips(enemyGamePlayer.getShips()).size());
                    //oneGame.put("gameplayer_score", gamePlayer.getScore());
                }
            }

            return new ResponseEntity<>(makeMap("gameView", oneGame), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(makeMap("error", "Unauthorised gameplayer"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String userName, @RequestParam String password) {

        if (userName.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(userName) != null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(userName, password));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(path = "/players", method = RequestMethod.GET)
    public List<Object> getAllPlayers() {
        return playerRepository
                .findAll()
                .stream()
                .map(this::makePlayerDTO)
                .collect(toList());
    }

    //@RequestMapping("/gamePlayers")
    public List<Object> getAllGamePlayers() {
        return gamePlayerRepository
                .findAll()
                .stream()
                .map(this::makeGamePlayerDTO)
                .collect(toList());
    }

    @RequestMapping("/top_scores")
    public List<Object> getAllScores() {
        return playerRepository
                .findAll()
                .stream()
                .map(this::makeScoreDTO)
                .collect(toList());
    }

    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("gameId", game.getGameId());
        dto.put("created", game.getTimeStamp());
        dto.put("gamePlayers", game.getGamePlayers()
                .stream()
                .map(this::makeGamePlayerDTO)
                .collect(toList()));
        dto.remove("", "1");
        return dto;
    }

    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("gamePlayerId", gamePlayer.getGamePlayId());
        dto.put("players", makePlayerDTO(gamePlayer.getPlayer()));
        dto.put("scores", makeScoreDTO(gamePlayer.getPlayer()));
        return dto;
    }

    private Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (player != null) {
            dto.put("playerId", player.getId());
            dto.put("username", player.getUserName());
        }
        return dto;
    }

    private Map<String, Object> makeShipDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("ship_type", ship.getShipType());
        dto.put("ship_locations", ship.getShipLocations());
        return dto;
    }

    private List<Object> getSalvoInfo(GamePlayer gamePlayer) {
        List<Object> salvoList = new ArrayList<>();
        Set<Salvo> salvos = gamePlayer.getSalvoes();
        for (Salvo oneSalvo : salvos) {
            Map<String, Object> salvoMap = new LinkedHashMap<>();
            salvoMap.put("gameplayer_id", oneSalvo.getGamePlayer().getGamePlayId());
            salvoMap.put("location", oneSalvo.getSalvoLocations());
            salvoMap.put("turn", oneSalvo.getTurnNumber());
            salvoList.add(salvoMap);
        }
        return salvoList;
    }

    private Map<String, Object> makeSalvoDTO(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn_no", salvo.getTurnNumber());
        dto.put("salvo_locations", salvo.getSalvoLocations());
        return dto;
    }

    public Map<String, Object> makeScoreDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (player != null) {
            dto.put("player", player.getUserName());
            dto.put("total_score", player.getScores().stream().mapToDouble(Score::getScore).sum());
            dto.put("wins", player.getScores().stream().filter(score -> score.getScore() == 1.0).count());
            dto.put("losses", player.getScores().stream().filter(score -> score.getScore() == 0.0).count());
            dto.put("ties", player.getScores().stream().filter(score -> score.getScore() == 0.5).count());

        }
        return dto;
    }

    private GamePlayer getOtherGamePlayer(GamePlayer gamePlayer) {
        GamePlayer theOther = null;
        for (GamePlayer gp : gamePlayer.getGame().getGamePlayers()) {
            if (gp.getGamePlayId() != gamePlayer.getGamePlayId()) {
                theOther = gp;
            }
        }
        return theOther;
    }

    private Integer getLastTurn(GamePlayer gamePlayer) {
        Set<Salvo> salvoSet = gamePlayer.getSalvoes();
        Integer lastTurn = 0;
        for (Salvo salvo : salvoSet) {
            Integer turn = salvo.getTurnNumber();
            if (turn > lastTurn) {
                lastTurn = turn;
            }
        }
        return lastTurn;
    }

    private Boolean isMyTurn(GamePlayer gameplayer) {
        boolean isMyTurn;
        GamePlayer enemyPlayer = getOtherGamePlayer(gameplayer);
        Integer playerTurn = getLastTurn(gameplayer);
        Integer enemyPlayerTurn = getLastTurn(enemyPlayer);

        if (enemyPlayerTurn > playerTurn) {
            isMyTurn = true;
        } else if (playerTurn > enemyPlayerTurn) {
            isMyTurn = false;
        } else {
            isMyTurn = gameplayer.getGamePlayId() < enemyPlayer.getGamePlayId();
        }
        return isMyTurn;
    }

    private List<String> getShipLocations(GamePlayer gameplayer) {
        Set<Ship> shipLocations = gameplayer.getShips();
        return shipLocations
                .stream()
                .map(Ship::getShipLocations)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private List<String> getSalvoLocations(GamePlayer gameplayer) {
        Set<Salvo> salvos = gameplayer.getSalvoes();

        List<String> array = new ArrayList<>();
        for (Salvo salvo : salvos) {
            array.addAll(salvo.getSalvoLocations());
        }


        return array;
    }

    private List<String> getHitLocations(GamePlayer gamePlayer) {
        List<String> hitLocations = new ArrayList<>();
        List<String> shipLocations = getShipLocations(getOtherGamePlayer(gamePlayer));
        List<String> salvoLocations = getSalvoLocations(gamePlayer);
        for (String oneSalvoLocation : salvoLocations) {
            for (String oneShipLocation : shipLocations) {
                if (oneSalvoLocation.equals(oneShipLocation) && !hitLocations.contains(oneShipLocation)) {
                    hitLocations.add(oneSalvoLocation);
                }
            }
        }
        return hitLocations;
    }

    private Boolean isShipSunk(List<String> salvoLocations, Ship ship) {
        boolean isShipSunk = false;
        List<String> hits = new ArrayList<>();
        for (String salvoLocation : salvoLocations) {
            if (ship.getShipLocations().contains(salvoLocation))
                hits.add(salvoLocation);
        }
        // System.out.println(hits);
        // System.out.println(ship.getShipLocations());
        if(ship.getShipLocations().size() == hits.size())
            isShipSunk = true;


        if (isShipSunk) {
            ship.setSunk(true);
            shipRepository.save(ship);
        }
        return isShipSunk;
    }

    private List<String> getAllSunkShips(Set<Ship> ships) {
        List<String> sunkShipsList = new ArrayList<>();
        for (Ship oneShip : ships) {
            if (oneShip.getSunk()) {
                sunkShipsList.add(oneShip.getShipType());
            }
        }
        return sunkShipsList;
    }

    private Boolean areAllShipsSunk(List<String> arrayOfSunkShips) {
        boolean areAllShipsSunk = false;
        if (arrayOfSunkShips.size() == 5) {
            areAllShipsSunk = true;
        }
        return areAllShipsSunk;
    }

    private Score setScore(GamePlayer gamePlayer, Double score) {
        return new Score(score, gamePlayer.getPlayer(), gamePlayer.getGame(), new Date());
    }

    private void setFinalScore(GamePlayer gamePlayer) {
        Boolean playerShipsCheck = areAllShipsSunk(getAllSunkShips(gamePlayer.getShips()));
        Boolean enemyPlayerShipsCheck = areAllShipsSunk(getAllSunkShips(getOtherGamePlayer(gamePlayer).getShips()));

        if (!playerShipsCheck && enemyPlayerShipsCheck) {
            scoreRepository.save(setScore(gamePlayer, 1.0));
        } else if (playerShipsCheck && !enemyPlayerShipsCheck) {
            scoreRepository.save(setScore(gamePlayer, 0.0));
        } else if (playerShipsCheck) {
            scoreRepository.save(setScore(gamePlayer, 0.5));
        }
    }

    private void endTheGame(GamePlayer gamePlayer) {
        setFinalScore(gamePlayer);
    }
}