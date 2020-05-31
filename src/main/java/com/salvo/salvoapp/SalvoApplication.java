package com.salvo.salvoapp;

import com.salvo.salvoapp.entities.*;
import com.salvo.salvoapp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@ComponentScan
public class SalvoApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {

        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository,
                                      GameRepository gameRepository,
                                      GamePlayerRepository gamePlayerRepository,
                                      ShipRepository shipRepository,
                                      SalvoRepository salvoRepository,
                                      ScoreRepository scoreRepository) {
        return (args) -> {
            Player player1 = new Player("j.bauer@ctu.gov", "24");
            Player player2 = new Player("c.obrian@ctu.gov", "42");
            Player player3 = new Player("kim_bauer@gmail.com", "kb");
            Player player4 = new Player("t.almeida@ctu.gov", "mole");

            playerRepository.save(player1);
            playerRepository.save(player2);
            playerRepository.save(player3);
            playerRepository.save(player4);

            Date date1 = new Date();
            Date date2 = Date.from(date1.toInstant().plusSeconds(3600));
            Date date3 = Date.from(date2.toInstant().plusSeconds(3600));
            Date date4 = Date.from(date3.toInstant().plusSeconds(3600));
            Date date5 = Date.from(date4.toInstant().plusSeconds(3600));
            Date date6 = Date.from(date5.toInstant().plusSeconds(3600));
            Date date7 = Date.from(date6.toInstant().plusSeconds(3600));
            Date date8 = Date.from(date7.toInstant().plusSeconds(3600));

            Date date2end = Date.from(date2.toInstant().plusSeconds(1800));
            Date date1end = Date.from(date1.toInstant().plusSeconds(1800));
            Date date3end = Date.from(date3.toInstant().plusSeconds(1800));
            Date date4end = Date.from(date4.toInstant().plusSeconds(1800));
            Date date5end = Date.from(date5.toInstant().plusSeconds(1800));
            Date date6end = Date.from(date6.toInstant().plusSeconds(1800));
            Date date7end = Date.from(date7.toInstant().plusSeconds(1800));
            Date date8end = Date.from(date8.toInstant().plusSeconds(1800));

            Game game1 = new Game(date1);
            Game game2 = new Game(date2);
            Game game3 = new Game(date3);
            Game game4 = new Game(date4);
            Game game5 = new Game(date5);
            Game game6 = new Game(date6);
            Game game8 = new Game(date8);

            gameRepository.save(game1);
            gameRepository.save(game2);
            gameRepository.save(game3);
            gameRepository.save(game4);
            gameRepository.save(game5);
            gameRepository.save(game6);
            gameRepository.save(game8);

            GamePlayer gamePlayer1 = new GamePlayer(game1, player1, date1);
            GamePlayer gamePlayer2 = new GamePlayer(game1, player2, date1);

            GamePlayer gamePlayer3 = new GamePlayer(game2, player1, date2);
            GamePlayer gamePlayer4 = new GamePlayer(game2, player2, date2);

            GamePlayer gamePlayer5 = new GamePlayer(game3, player2, date3);
            GamePlayer gamePlayer6 = new GamePlayer(game3, player4, date3);

            GamePlayer gamePlayer7 = new GamePlayer(game4, player2, date4);
            GamePlayer gamePlayer8 = new GamePlayer(game4, player1, date4);

            GamePlayer gamePlayer9 = new GamePlayer(game5, player4, date5);
            GamePlayer gamePlayer10 = new GamePlayer(game5, player1, date5);

            GamePlayer gamePlayer11 = new GamePlayer(game6, player3, date6);
            GamePlayer gamePlayer12 = new GamePlayer(game6, player4, date6);

            GamePlayer gamePlayer13 = new GamePlayer(game8, player1, date7);
            GamePlayer gamePlayer14 = new GamePlayer(game8, player3, date7);

            gamePlayerRepository.save(gamePlayer1);
            gamePlayerRepository.save(gamePlayer2);
            gamePlayerRepository.save(gamePlayer3);
            gamePlayerRepository.save(gamePlayer4);
            gamePlayerRepository.save(gamePlayer5);
            gamePlayerRepository.save(gamePlayer6);
            gamePlayerRepository.save(gamePlayer7);
            gamePlayerRepository.save(gamePlayer8);
            gamePlayerRepository.save(gamePlayer9);
            gamePlayerRepository.save(gamePlayer10);
            gamePlayerRepository.save(gamePlayer11);
            gamePlayerRepository.save(gamePlayer12);
            gamePlayerRepository.save(gamePlayer13);
            gamePlayerRepository.save(gamePlayer14);

            List<String> shipLocation1 = Arrays.asList("A2", "A3");
            List<String> shipLocation2 = Arrays.asList("E1", "F1", "G1");
            List<String> shipLocation3 = Arrays.asList("C4", "C5", "C6");
            List<String> shipLocation4 = Arrays.asList("A10", "B10", "C10", "D10");
            List<String> shipLocation5 = Arrays.asList("I1", "I2", "I3", "I4", "I5");

            shipRepository.save(new Ship("patrol boat", shipLocation1, gamePlayer1));
            shipRepository.save(new Ship("submarine", shipLocation2, gamePlayer1));
            shipRepository.save(new Ship("destroyer", shipLocation3, gamePlayer1));
            shipRepository.save(new Ship("battleship", shipLocation4, gamePlayer1));
            shipRepository.save(new Ship("aircraft carrier", shipLocation5, gamePlayer1));

            shipRepository.save(new Ship("patrol boat", shipLocation1, gamePlayer2));
            shipRepository.save(new Ship("submarine", shipLocation2, gamePlayer2));
            shipRepository.save(new Ship("destroyer", shipLocation3, gamePlayer2));
            shipRepository.save(new Ship("battleship", shipLocation4, gamePlayer2));
            shipRepository.save(new Ship("aircraft carrier", shipLocation5, gamePlayer2));

            shipRepository.save(new Ship("patrol boat", shipLocation1, gamePlayer3));
            shipRepository.save(new Ship("submarine", shipLocation2, gamePlayer3));
            shipRepository.save(new Ship("destroyer", shipLocation3, gamePlayer3));
            shipRepository.save(new Ship("battleship", shipLocation4, gamePlayer3));
            shipRepository.save(new Ship("aircraft carrier", shipLocation5, gamePlayer3));

            shipRepository.save(new Ship("patrol boat", shipLocation1, gamePlayer4));
            shipRepository.save(new Ship("submarine", shipLocation2, gamePlayer4));
            shipRepository.save(new Ship("destroyer", shipLocation3, gamePlayer4));
            shipRepository.save(new Ship("battleship", shipLocation4, gamePlayer4));
            shipRepository.save(new Ship("aircraft carrier", shipLocation5, gamePlayer4));

            shipRepository.save(new Ship("patrol boat", shipLocation1, gamePlayer5));
            shipRepository.save(new Ship("submarine", shipLocation2, gamePlayer5));
            shipRepository.save(new Ship("destroyer", shipLocation3, gamePlayer5));
            shipRepository.save(new Ship("battleship", shipLocation4, gamePlayer5));
            shipRepository.save(new Ship("aircraft carrier", shipLocation5, gamePlayer5));

            shipRepository.save(new Ship("patrol boat", shipLocation1, gamePlayer6));
            shipRepository.save(new Ship("submarine", shipLocation2, gamePlayer6));
            shipRepository.save(new Ship("destroyer", shipLocation3, gamePlayer6));
            shipRepository.save(new Ship("battleship", shipLocation4, gamePlayer6));
            shipRepository.save(new Ship("aircraft carrier", shipLocation5, gamePlayer6));

            shipRepository.save(new Ship("patrol boat", shipLocation1, gamePlayer7));
            shipRepository.save(new Ship("submarine", shipLocation2, gamePlayer7));
            shipRepository.save(new Ship("destroyer", shipLocation3, gamePlayer7));
            shipRepository.save(new Ship("battleship", shipLocation4, gamePlayer7));
            shipRepository.save(new Ship("aircraft carrier", shipLocation5, gamePlayer7));

            shipRepository.save(new Ship("patrol boat", shipLocation1, gamePlayer8));
            shipRepository.save(new Ship("submarine", shipLocation2, gamePlayer8));
            shipRepository.save(new Ship("destroyer", shipLocation3, gamePlayer8));
            shipRepository.save(new Ship("battleship", shipLocation4, gamePlayer8));
            shipRepository.save(new Ship("aircraft carrier", shipLocation5, gamePlayer8));

            shipRepository.save(new Ship("patrol boat", shipLocation1, gamePlayer9));
            shipRepository.save(new Ship("submarine", shipLocation2, gamePlayer9));
            shipRepository.save(new Ship("destroyer", shipLocation3, gamePlayer9));
            shipRepository.save(new Ship("battleship", shipLocation4, gamePlayer9));
            shipRepository.save(new Ship("aircraft carrier", shipLocation5, gamePlayer9));

            shipRepository.save(new Ship("patrol boat", shipLocation1, gamePlayer10));
            shipRepository.save(new Ship("submarine", shipLocation2, gamePlayer10));
            shipRepository.save(new Ship("destroyer", shipLocation3, gamePlayer10));
            shipRepository.save(new Ship("battleship", shipLocation4, gamePlayer10));
            shipRepository.save(new Ship("aircraft carrier", shipLocation5, gamePlayer10));

            shipRepository.save(new Ship("patrol boat", shipLocation1, gamePlayer11));
            shipRepository.save(new Ship("submarine", shipLocation2, gamePlayer11));
            shipRepository.save(new Ship("destroyer", shipLocation3, gamePlayer11));
            shipRepository.save(new Ship("battleship", shipLocation4, gamePlayer11));
            shipRepository.save(new Ship("aircraft carrier", shipLocation5, gamePlayer11));

            shipRepository.save(new Ship("patrol boat", shipLocation1, gamePlayer13));
            shipRepository.save(new Ship("submarine", shipLocation2, gamePlayer13));
            shipRepository.save(new Ship("destroyer", shipLocation3, gamePlayer13));
            shipRepository.save(new Ship("battleship", shipLocation4, gamePlayer13));
            shipRepository.save(new Ship("aircraft carrier", shipLocation5, gamePlayer13));

            shipRepository.save(new Ship("patrol boat", shipLocation1, gamePlayer14));
            shipRepository.save(new Ship("submarine", shipLocation2, gamePlayer14));
            shipRepository.save(new Ship("destroyer", shipLocation3, gamePlayer14));
            shipRepository.save(new Ship("battleship", shipLocation4, gamePlayer14));
            shipRepository.save(new Ship("aircraft carrier", shipLocation5, gamePlayer14));


            // SALVOES

            salvoRepository.save(new Salvo(1, Arrays.asList("B5", "C5", "F1"), gamePlayer1));
            salvoRepository.save(new Salvo(1, Arrays.asList("B4", "B5", "B6"), gamePlayer2));

            salvoRepository.save(new Salvo(2, Arrays.asList("F2", "D5"), gamePlayer1));
            salvoRepository.save(new Salvo(2, Arrays.asList("E1", "H3", "A2"), gamePlayer2));

            salvoRepository.save(new Salvo(1, Arrays.asList("A2", "A4", "G6"), gamePlayer3));
            salvoRepository.save(new Salvo(1, Arrays.asList("B5", "D5", "C7"), gamePlayer4));

            salvoRepository.save(new Salvo(2, Arrays.asList("A3", "H6"), gamePlayer3));
            salvoRepository.save(new Salvo(2, Arrays.asList("C5", "C6"), gamePlayer4));

            salvoRepository.save(new Salvo(1, Arrays.asList("G6", "H6", "A4"), gamePlayer5));
            salvoRepository.save(new Salvo(1, Arrays.asList("H1", "H2", "H3"), gamePlayer6));

            salvoRepository.save(new Salvo(2, Arrays.asList("A2", "A3", "D8"), gamePlayer5));
            salvoRepository.save(new Salvo(2, Arrays.asList("E1", "F2", "G3"), gamePlayer6));

            salvoRepository.save(new Salvo(1, Arrays.asList("A3", "A4", "F7"), gamePlayer7));
            salvoRepository.save(new Salvo(1, Arrays.asList("B5", "C6", "H1"), gamePlayer8));

            salvoRepository.save(new Salvo(2, Arrays.asList("A2", "G6", "H6"), gamePlayer7));
            salvoRepository.save(new Salvo(2, Arrays.asList("C5", "C7", "D5"), gamePlayer8));

            salvoRepository.save(new Salvo(1, Arrays.asList("A1", "A2", "A3"), gamePlayer9));
            salvoRepository.save(new Salvo(1, Arrays.asList("B5", "B6", "C7"), gamePlayer10));
            salvoRepository.save(new Salvo(2, Arrays.asList("G6", "G7", "G8"), gamePlayer9));
            salvoRepository.save(new Salvo(2, Arrays.asList("C6", "D6", "E6"), gamePlayer10));
            salvoRepository.save(new Salvo(3, Arrays.asList("G6", "G7", "G8"), gamePlayer9));
            salvoRepository.save(new Salvo(3, Arrays.asList("H1", "H8"), gamePlayer10));

            // SCORES

            scoreRepository.save(new Score(1.0, player1, game1, date1end));
            scoreRepository.save(new Score(0.0, player2, game1, date1end));

            scoreRepository.save(new Score(0.5, player1, game2, date2end));
            scoreRepository.save(new Score(0.5, player2, game2, date2end));

            scoreRepository.save(new Score(1.0, player2, game3, date3end));
            scoreRepository.save(new Score(0.0, player4, game3, date3end));

            scoreRepository.save(new Score(0.5, player2, game4, date4end));
            scoreRepository.save(new Score(0.5, player1, game4, date4end));
            /*
            scoreRepository.save(new Score(null, player4, game5, null));
            scoreRepository.save(new Score(null, player1, game5, null));

            scoreRepository.save(new Score(null, player3, game6, null));
            scoreRepository.save(new Score(null, null, game6, null));

            scoreRepository.save(new Score(null, player4, game7, null));
            scoreRepository.save(new Score(null, null, game7, null));

            scoreRepository.save(new Score(null, player3, game8, null));
            scoreRepository.save(new Score(null, player4, game8, null));
            */

        };
    }
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userName -> {
            Player player = playerRepository.findByUserName(userName);
            if (player != null) {
                return new User(player.getUserName(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + userName);
            }
        });
    }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()

                .antMatchers("/**").permitAll()
                .and()
                .formLogin();

               /* .antMatchers("/admin/**").hasAuthority("ADMIN")
                .antMatchers("/rest/**").denyAll()
                .antMatchers("/**").hasAuthority("USER")
                .antMatchers("/api/top_scores").permitAll()
                .antMatchers("/web/**").permitAll()
                .antMatchers("/**").permitAll()
                .anyRequest().fullyAuthenticated()
                .and()
                .formLogin(); */

        http.formLogin()
                .usernameParameter("userName")
                .passwordParameter("password")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");

        // turn off checking for CSRF tokens
        http.csrf().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }

    // Define a private method that takes an Authentication object and returns the Player object
    // that has the user name in the Authentication object. It should return null if no one is logged in,
    // or if whoever is logged in is not a player (this may not be possible, but it's good to check for it.)
}

