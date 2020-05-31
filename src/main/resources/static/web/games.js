var app = new Vue({
    el: '#app',
    data: {
        allScores: [],
        topScores: [],
        allGames: [],
        gameInfo: [],
        path: 1,
        userNameEntered: '',
        passwordEntered: '',
        password2Entered: '',
        warning1: false,
        warning2: false,
        warning3: false,
        warning4: false,
        warning5: false,
        loggedIn: false,
    },
    created: function () {
        this.getData('allScores', "/api/top_scores");
        this.getData('allGames', "/api/games");
    },
    methods: {
        getData: function (array, link) {
            fetch(link, {
                    method: "GET",
                })
                .then(response => response.json())
                .then(json => {
                    app[array] = json;
                    if (this.allGames.games != undefined) {
                        this.userNameEntered = this.allGames.player.username
                        this.path = 3;
                        this.loggedIn = true;
                        this.warning1 = false;
                        this.warning2 = false;
                        app.listOfGames();
                    }
                })
                .catch(error => error);
        },
        login: function () {


            // is length < than 8

            fetch("/api/login", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: 'userName=' + this.userNameEntered + '&password=' + this.passwordEntered,
                })
                .then(r => {
                    if (r.status == 200) {
                        // console.log(r)
                        this.loggedIn = true;
                        this.path = 3;
                        this.warning1 = false;
                        this.getData('allGames', "/api/games");
                        app.listOfGames();
                    } else if (r.status == 401) {
                        // console.log(r)
                        this.warning1 = true;
                    }
                    // console.log('this.userNameEntered: ', this.userNameEntered);
                    // console.log('this.passwordEntered: ', this.passwordEntered);
                })
                .catch(e => console.log(e))
        },
        logout: function () {
            fetch("/api/logout", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                })
                .then(r => {
                    if (r.status == 200) {
                        // console.log(r)
                        this.loggedIn = false;
                        this.path = 1;
                    }
                })
                .catch(e => console.log(e))
        },
        createNewPlayer: function () {
            if (this.passwordEntered != this.password2Entered) {
                this.warning2 = true;
            } else if (this.passwordEntered.length < 8) {
                this.warning5 = true;
            } else {
                fetch("/api/players", {
                        credentials: 'include',
                        method: 'POST',
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/x-www-form-urlencoded'
                        },
                        body: 'userName=' + this.userNameEntered + '&password=' + this.passwordEntered,
                    })
                    .then(r => {
                        if (r.status == 403) {
                            this.warning1 = false;
                            this.warning2 = false;
                            this.warning3 = false;
                            this.warning4 = true;
                            this.path = 2;
                        } else if (r.status == 201) {
                            console.log(r)
                            this.warning1 = false;
                            this.warning2 = false;
                            this.warning3 = true;
                            this.warning4 = false;
                            this.path = 1;
                            this.userNameEntered = '';
                            this.passwordEntered = '';
                        }
                    })
                    .catch(e => console.log(e))
                this.path = 1;
            }
        },
        changeContent: function (content) {
            this.path = content;
            this.warning1 = false;
            this.warning2 = false;
            this.warning3 = false;
            this.warning4 = false;
            this.warning5 = false;
        },
        goToGame: function (gamePlayerId) {
            window.location = "/web/game.html?gp=" + gamePlayerId;
        },
        createGame: function () {
            fetch("/api/games", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                })
                .then(response => response.json())
                .then(json => location.replace("/web/game.html?gp=" + json.gpid))
                .catch(e => {
                    console.log(e);
                    alert("Oops! Something went wrong! Please try again.");
                })
        },
        joinGame: function (gameId) {
            fetch("/api/game/" + gameId + "/players", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                })
                .then(response => response.json())
                .then(json => location.replace("/web/game.html?gp=" + json.gpid))
                .catch(e => {
                    console.log(e);
                    alert("Oops! Something went wrong! Please try again.");
                })
        },
        listOfGames: function () {
            // console.log('listofgames launched');
            for (var i = 0; i < app.allGames.games.length; i++) {
                var dataObject = {};
                var gameId = app.allGames.games[i].gameId;
                var gameDate = new Date(app.allGames.games[i].created).toLocaleString();
                var gamePlayer1 = app.allGames.games[i].gamePlayers[0].players.username;
                var gamePLayer1id = app.allGames.games[i].gamePlayers[0].gamePlayerId;
                if (app.allGames.games[i].gamePlayers[1] == null || app.allGames.games[i].gamePlayers[1] == undefined) {
                    var gamePlayer2 = undefined;
                    var gamePlayer2id = null;
                } else {
                    var gamePlayer2 = app.allGames.games[i].gamePlayers[1].players.username;
                    var gamePlayer2id = app.allGames.games[i].gamePlayers[1].gamePlayerId;
                }

                dataObject = {
                    gameId: gameId,
                    gameDate: gameDate,
                    gamePlayer1: gamePlayer1,
                    gamePlayer1id: gamePLayer1id,
                    gamePlayer2: gamePlayer2,
                    gamePlayer2id: gamePlayer2id,

                }
                if (app.gameInfo.length < app.allGames.games.length) {
                    app.gameInfo.push(dataObject);
                }

            }
            // console.log(app.gameInfo);
        },
        getTopScores: function () {
            var allScoresToFilter = this.allScores;
            var allTopScores = [];
            for (let i = 0; i < allScoresToFilter.length; i++) {
                for (let j = i; j < allScoresToFilter.length; j++) {
                    if (allScoresToFilter[i].total_score > allScoresToFilter[j].total_score && !allTopScores.includes(allScoresToFilter[i])) {
                        allTopScores.push(allScoresToFilter[i]);
                    }
                }
            }
            // console.log("allscores: ", this.allScores);
            // console.log("all top scores:", allTopScores);
        }
    }
});
