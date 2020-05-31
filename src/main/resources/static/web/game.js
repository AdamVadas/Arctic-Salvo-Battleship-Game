var app = new Vue({
    el: '#app',
    data: {
        allData: [],
        gridAlphabet: ["", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        gridNumbers: ["", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        player1: "",
        player2: "WAITING FOR PLAYER TO JOIN",
        id: 0,
        shipsToAdd: [],
        addingShipsProcess: true,
        numberOfShipsPlaced: 0,
        numberOfAllCellsOfShipsToAdd: 0,
        picked: "Horizontal",
        centertext: "",
        salvoesToAdd: [],
        noOfHitsOnMe: 0,
        noOfHitsOnEnemy: 0,
        myShipsLeft: 5,
        enemyShipsLeft:  5,
        myTurnNo: 0,
        enemyTurnNo: 0,
        isMyTurn: false,
        salvoCounter: 0,
        salvoLocationArray: [],
        isGameOver: false,
        winner: "",
    },
    created: function () {
        this.getId();
        this.getData();
    },
    methods: {
        getData: function () {
            fetch("/api/game_view/" + this.id, {
                    method: "GET",
                    credentials: "include",
                })
                .then(r => r.json())
                .then(json => {
                    app.allData = json.gameView;
                    app.getPlayersInfo();
                    app.myShipLocations();
                    app.myShipsHit();
                    // console.log("addingshipsprocess? ", app.addingShips Process);
                    if (app.addingShipsProcess == false) {
                        if (app.player2 != "WAITING FOR PLAYER TO JOIN" && app.player2 != undefined) {
                            app.isGameOver = app.allData.is_game_over;
                            if (app.isGameOver) {
                                app.isMyTurn = true;
                                app.winner = app.allData.winner;
                            } else {
                                app.isMyTurn = app.allData.is_my_turn;
                            }
                            app.myTurnNo = app.allData.user_turn;
                            app.enemyTurnNo = app.allData.enemy_turn;
                            app.noOfHitsOnEnemy = app.allData.no_of_hits_on_enemy;
                            app.noOfHitsOnMe = app.allData.no_of_hits_on_me;
                            app.myShipsLeft = 5-app.allData.no_of_my_sunk_ships;
                            app.enemyShipsLeft = 5-app.allData.no_of_enemy_sunk_ships;
                            // console.log("My turn no: ", app.myTurnNo, " Enemy turn no: ", app.enemyTurnNo);
                            // console.log("Is it my turn?: ", app.isMyTurn);
                            // console.log("Is the game over?:", app.isGameOver);
                            // console.log("Winner: ", app.winner);
                            // console.log("Enemy new score: ", app.enemyScore);
                            // console.log("Player new score: ", app.playerScore);
                            // console.log("hits on enemy: ", app.allData.hits_on_enemy);
                            // console.log("hits on me: ", app.allData.hits_on_me);
                            // console.log("no of hits on enemy: ", app.noOfHitsOnEnemy);
                            // console.log("no of hits on me: ", app.noOfHitsOnMe);
                            // console.log("enemy salvoes: ", app.allData.enemy_salvos);
                            // console.log("enemy ships left: ", app.enemyShipsLeft);
                            // console.log("my ships left: ", app.myShipsLeft);
                            app.mySalvoLocations();
                            if (app.isMyTurn) {
                                app.changeCenterText("IT'S YOUR TURN NOW...")
                            } else {
                                app.changeCenterText("NOW IT'S THE ENEMY'S TURN...")
                            };
                        } else {
                            app.changeCenterText("WAITING FOR THE ENEMY TO JOIN...");
                        }
                        if (!app.isMyTurn) {
                            var interval = setTimeout(function () {
                                app.getData()
                            }, 2000);
                        }
                    } else {
                        this.changeCenterText("PLACE ALL 5 SHIPS ON YOUR GRID BY SELECTING SHIP TYPE AND ORIENTATION");
                    }
                })
                .catch(e => console.log(e));
        },
        getId: function () {
            this.id = location.search.split("=")[1];
        },
        getPlayersInfo: function () {
            const gamePlayers = this.allData.games.gamePlayers;
            if (gamePlayers[0].gamePlayerId == this.id) {
                this.player1 = gamePlayers[0].players.username;
                if (gamePlayers[1] != undefined) {
                    this.player2 = gamePlayers[1].players.username;
                }
            } else {
                if (gamePlayers[1] != undefined) {
                    this.player1 = gamePlayers[1].players.username;
                }
                this.player2 = gamePlayers[0].players.username;
            }
        },
        myShipLocations: function () {
            var ship = [];
            const ships = this.allData.ships;
            for (var i = 0; i < ships.length; i++) {
                for (var j = 0; j < ships[i].ship_locations.length; j++) {
                    ship.push(ships[i].ship_locations[j]);
                    var shipLocationCell = document.getElementById("grid1" + ships[i].ship_locations[j]);
                    shipLocationCell.setAttribute("class", "myship");
                }
            }
            this.numberOfShipsPlaced = ships.length;
            if (this.numberOfShipsPlaced < 5) {
                this.addingShipsProcess = true;
            } else {
                this.addingShipsProcess = false;
            }
        },
        mySalvoLocations: function () {
            var salvo = [];
            const salvoes = this.allData.salvoes;
            for (var i = 0; i < salvoes.length; i++) {
                for (var j = 0; j < salvoes[i].salvo_locations.length; j++) {
                    var actualLocation = salvoes[i].salvo_locations[j];
                    salvo.push(actualLocation);
                    var salvoLocationCell = document.getElementById("grid2" + actualLocation);
                    salvoLocationCell.setAttribute("class", "mysalvo");
                }
            }
            const hitsOnEnemy = this.allData.hits_on_enemy;
            if (hitsOnEnemy != null && hitsOnEnemy != undefined) {
                for (var i = 0; i < hitsOnEnemy.length; i++) {
                    var hitCell = document.getElementById("grid2" + hitsOnEnemy[i]);
                    // console.log("hitcell enemy before change: ", hitCell);
                    hitCell.setAttribute("class", "myshipdamaged");
                    hitCell.classList.remove("mysalvo");
                    // console.log("hitcell enemy after change: ", hitCell);
                }
            }
        },
        myShipsHit: function () {
            const hitsOnMe = this.allData.hits_on_me;
            if (hitsOnMe != undefined) {
                for (var i = 0; i < hitsOnMe.length; i++) {
                    var hitCell = document.getElementById("grid1" + hitsOnMe[i]);
                    // console.log("hitcell me: ", hitCell);
                    hitCell.setAttribute("class", "myshipdamaged");
                }
            }
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
                        window.location = '/web/games.html';
                    }
                })
                .catch(e => console.log(e))
        },
        goToMainMenu: function () {
            window.location = "/web/games.html";
        },
        removeClass: function (className) {
            document.getElementById("aircraftcarrier").classList.remove(className);
            document.getElementById("battleship").classList.remove(className);
            document.getElementById("destroyer").classList.remove(className);
            document.getElementById("submarine").classList.remove(className);
            document.getElementById("patrolboat").classList.remove(className);
        },
        selectShipToPut: function (id) {
            this.removeClass("ship-selected");
            let shipSelected = document.getElementById(id);
            if (!shipSelected.classList.contains("ship-selected") && !shipSelected.classList.contains("toblock")) {
                shipSelected.classList.add("ship-selected");
            } else if (shipSelected.classList.contains("toblock")) {
                let index = this.shipsToAdd.findIndex(x => x.shipType === shipSelected.getAttribute('id'));
                // console.log('shiplocations to be removed:', this.shipsToAdd[index].shipLocations);
                // console.log('index: ', index);
                // console.log('shipSelected: ', shipSelected.getAttribute('id'));

                for (let i = 0; i < this.shipsToAdd[index].shipLocations.length; i++) {
                    let cell = document.getElementById('grid1' + this.shipsToAdd[index].shipLocations[i]);
                    // console.log(cell);
                    cell.classList.remove("hoverCells");
                    cell.classList.remove("interimships");
                }

                this.shipsToAdd.splice(index, 1);
                // console.log('shipstoadd after remove: ', this.shipsToAdd);
                shipSelected.classList.remove("toblock");
            } else {
                shipSelected.classList.remove("ship-selected");
            }
        },
        moveHover: function (cellId, command) {
            let cellSelected = document.getElementById(cellId);
            let shipSelected = document.getElementsByClassName("ship-selected")[0];
            let locationArray = [];
            if (shipSelected != undefined) {
                if (shipSelected.getAttribute('id') == "aircraftcarrier") {
                    shipSize = 5;
                }
                if (shipSelected.getAttribute('id') == "battleship") {
                    shipSize = 4;
                }
                if (shipSelected.getAttribute('id') == "destroyer") {
                    shipSize = 3;
                }
                if (shipSelected.getAttribute('id') == "submarine") {
                    shipSize = 3;
                }
                if (shipSelected.getAttribute('id') == "patrolboat") {
                    shipSize = 2;
                }
                if (this.picked == "Horizontal") {
                    for (let i = 0; i < shipSize; i++) {
                        if (document.getElementById('grid1' + cellId.charAt(5) + (parseFloat(cellId.substr(6)) + i)) != null) {
                            if (command == "add") {
                                document.getElementById('grid1' + cellId.charAt(5) + (parseFloat(cellId.substr(6)) + i)).classList.add("hoverCells");
                            } else if (command == "remove") {
                                document.getElementById('grid1' + cellId.charAt(5) + (parseFloat(cellId.substr(6)) + i)).classList.remove("hoverCells");
                            }
                        }
                    }
                } else {
                    for (let i = 0; i < this.gridAlphabet.length; i++) {
                        if (cellId.charAt(5) == this.gridAlphabet[i]) {
                            locationArray.push(this.gridAlphabet[i]);
                        } else {
                            if (locationArray.length != 0) {
                                locationArray.push(this.gridAlphabet[i]);
                            }
                        }
                    }
                    for (let i = 0; i < shipSize; i++) {
                        if (locationArray[i] != null) {
                            if (command == "add") {
                                document.getElementById('grid1' + locationArray[i] + (parseFloat(cellId.substr(6)))).classList.add("hoverCells");
                            } else if (command == "remove") {
                                document.getElementById('grid1' + locationArray[i] + (parseFloat(cellId.substr(6)))).classList.remove("hoverCells");
                            }
                        }
                    }
                }
            }
        },
        selectGridCell: function (cellId) {
            let cellSelected = document.getElementById(cellId);
            let shipSelected = document.getElementsByClassName("ship-selected")[0];
            if (shipSelected != undefined) {
                if (!cellSelected.classList.contains("myship") && !cellSelected.classList.contains("interimships")) {
                    this.putShipInGrid(cellId);
                }
            }
        },
        putShipInGrid: function (cellId) {
            let ship = document.getElementsByClassName("ship-selected")[0];
            let cellSelected = document.getElementById(cellId);

            let shipSize;
            let type;
            let locationArray = [];

            if (ship.getAttribute('id') == "aircraftcarrier") {
                shipSize = 4;
                type = "aircraftcarrier";
            }

            if (ship.getAttribute('id') == "battleship") {
                shipSize = 3;
                type = "battleship";
            }
            if (ship.getAttribute('id') == "destroyer") {
                shipSize = 2;
                type = "destroyer";
            }
            if (ship.getAttribute('id') == "submarine") {
                shipSize = 2;
                type = "submarine";
            }
            if (ship.getAttribute('id') == "patrolboat") {
                shipSize = 1;
                type = "patrolboat";
            }

            let a = cellSelected.id.charAt(5);
            let n = parseFloat(cellSelected.id.substr(6));
            locationArray.push(a + n);

            for (let i = 0; i < shipSize; i++) {
                if (this.picked == "Horizontal") {
                    n++;
                    locationArray.push(a + n);
                } else {
                    a = this.gridAlphabet[this.gridAlphabet.indexOf(a) + 1];
                    locationArray.push(a + n);
                }
            }

            let outOfGrid = false;
            let isOverlap = false;
            for (let i = 0; i < locationArray.length; i++) {
                if (document.getElementById('grid1' + locationArray[i]) != null) {
                    let cellToCheck = document.getElementById('grid1' + locationArray[i]);
                    if (cellToCheck.classList.contains("myship") || cellToCheck.classList.contains("interimships")) {
                        isOverlap = true;
                    }
                } else {
                    outOfGrid = true;
                }
            }

            if (isOverlap == false && outOfGrid == false) {
                let newShip = {
                    shipType: type,
                    shipLocations: locationArray,
                }

                this.shipsToAdd.push(newShip);

                for (let i = 0; i < locationArray.length; i++) {
                    document.getElementById('grid1' + locationArray[i]).classList.add("interimships");
                    this.changeCenterText("IF YOU CLICK ON A SHIP TYPE AGAIN, IT GETS REMOVED FROM THE GRID");
                }
                this.removeShipSelection(type);

            } else if (isOverlap == true) {
                this.changeCenterText("YOU HAVE A SHIP THERE ALREADY!");
            } else if (outOfGrid == true) {
                this.changeCenterText("WATCH OUT FOR THE BORDERS!");
            }
        },
        removeShipSelection: function (shiptype) {
            document.getElementById(shiptype).classList.add("toblock");
            this.removeClass("ship-selected");
            this.numberOfAllCellsOfShipsToAdd = document.getElementsByClassName("interimships").length;
        },
        resetShips: function () {
            this.shipsToAdd = [];
            this.numberOfAllCellsOfShipsToAdd = 0;
            this.removeClass("tohide");
            this.removeClass("toblock");

            var cell = document.getElementsByClassName("td2");
            for (let i = 0; i < 121; i++) {
                cell[i].classList.remove('interimships');
                cell[i].classList.remove('hoverCells');
            }
        },
        changeCenterText: function (newText) {
            this.centertext = newText;
        },
        selectCellToShoot: function (cellId) {
            let cellSelected = document.getElementById(cellId);
            if (this.isMyTurn) {
                if (!cellSelected.classList.contains("mysalvo") && !cellSelected.classList.contains("interimsalvo") && !cellSelected.classList.contains("myshipdamaged")) {
                    if (this.salvoCounter < 5) {
                        this.salvoCounter++;
                        // console.log('counter: ', this.salvoCounter);
                        cellSelected.classList.add("interimsalvo");
                        this.changeCenterText("IF YOU CLICK ON A CELL WITH A SALVO AGAIN, IT GETS REMOVED FROM THE GRID");
                        this.makeNewSalvoes(cellId);
                    } else {
                        this.changeCenterText("YOU PLACED ALL SALVOES, NOW YOU CAN PRESS FIRE TO SHOOT")
                    }
                } else if (cellSelected.classList.contains("interimsalvo") && !cellSelected.classList.contains("mysalvo") && !cellSelected.classList.contains("myshipdamaged")) {
                    let a = cellSelected.id.charAt(5);
                    let n = parseFloat(cellSelected.id.substr(6));
                    let cellNumber = a + n;
                    let index = this.salvoLocationArray.indexOf(cellNumber);
                    let indexedCell = document.getElementById('grid2' + this.salvoLocationArray[index]);
                    // console.log('salvolocation array before remove:', this.salvoLocationArray);
                    // console.log('indexed cell: ', indexedCell);
                    indexedCell.classList.remove("interimsalvo");
                    this.salvoLocationArray.splice(index, 1);
                    // console.log('salvolocation array after remove: ', this.salvoLocationArray);
                    this.salvoCounter--;
                    // console.log("counter: ", this.salvoCounter);
                } else {
                    this.changeCenterText("YOU ALREADY SHOT THERE! :)");
                }
            } else {
                this.changeCenterText("NOT YOUR TURN! :)")
            }
        },
        makeNewSalvoes: function (cellId) {
            let cellSelected = document.getElementById(cellId);

            let a = cellSelected.id.charAt(5);
            let n = parseFloat(cellSelected.id.substr(6));
            this.salvoLocationArray.push(a + n);

            // console.log('salvolocationarray: ', this.salvoLocationArray);

            if (this.salvoLocationArray.length == 5) {
                let newSalvo = {
                    salvoLocations: this.salvoLocationArray,
                }

                this.salvoesToAdd = newSalvo;
                // console.log('salvoes to add: ', this.salvoesToAdd);
            }
        },
        addShips: function () {
            if (this.shipsToAdd.length == 5) {
                fetch("/api/games/players/" + this.id + "/ships", {
                        credentials: 'include',
                        method: 'POST',
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(this.shipsToAdd),
                    })
                    .then(r => r.json())
                    .then(r => {
                        // console.log(r);
                        this.addingShipsProcess = false;
                        this.numberOfShipsPlaced = 5;
                        app.getData();
                    })
                    .catch(e => {
                        // console.log(e);
                        this.changeCenterText("SOMETHING WENT WRONG. PLEASE TRY AGAIN");
                    })
                // console.log('ships added: ', this.shipsToAdd);
                var shipCell = document.getElementsByClassName("interimships");
                for (let i = 0; i < 17; i++) {
                    shipCell[i].classList.add("myship");
                }
            } else {
                this.changeCenterText("PLACE ALL YOUR SHIPS BEFORE FINALIZING!");
            }
        },
        addSalvoes: function () {
            if (this.salvoCounter != 5) {
                this.changeCenterText("PLACE ALL YOUR SALVOES BEFORE FIRING!");
            } else {
                // console.log('salvoes added: ', this.salvoesToAdd);
                var salvoCell = document.getElementsByClassName("interimsalvo");
                for (let i = 0; i < 5; i++) {
                    salvoCell[i].classList.add("mysalvo");
                }
                fetch("/api/games/players/" + this.id + "/salvoes", {
                        credentials: 'include',
                        method: 'POST',
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(this.salvoesToAdd),
                    })
                    .then(r => r.json())
                    .then(r => {
                        // console.log(r);
                        app.salvoLocationArray = [];
                        app.salvoCounter = 0;
                        app.getData();
                    })
                    .catch(e => {
                        // console.log(e);
                        this.changeCenterText("SOMETHING WENT WRONG. PLEASE TRY AGAIN");
                    })
            }
        }
    }
});
