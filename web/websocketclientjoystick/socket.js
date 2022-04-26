var socket;
var socketID = "";
var nombreJugador;
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%5
//%%%%%%%%%%%%%%%% esta de adorno %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%5
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function empezar() {
    socket.send('{"name": "nombreJugador", "priority": "0","parameters": \n\[{"name": "nombreElegido", "value": "' + nombreJugador + '"},\n\]}');
    socket.send("start");

}

window.onload = function () {
    nombreJugador = prompt("Ingresa tu nombre", "");

    var page = document.createElement("a");
    page.href = window.location.href;
    // Define la url del servidor como la hostname de la pagina y el puerto definido 8080 del ws
    var url = "ws://" + page.hostname + ":8080";
    //servidor Edimbrujo
    //var url = "ws://" + page.hostname + ":60161";
    socket = new WebSocket(url + "/" + window.location.pathname.split('/')[1] + "/GameWebSocket");
    socket.onmessage = stateUpdate;
    socket.onopen = empezar;

    var botonOpcion1 = document.getElementById("opcion1");
    var botonOpcion2 = document.getElementById("opcion2");
    var botonOpcion3 = document.getElementById("opcion3");

    botonOpcion1.addEventListener("click", handlePressOpcion1, false);
    botonOpcion1.addEventListener("touchstart", handlePressOpcion1, false); // Todavia no lo probe si funciona

    botonOpcion2.addEventListener("click", handlePressOpcion2, false);
    botonOpcion2.addEventListener("touchstart", handlePressOpcion2, false);

    botonOpcion3.addEventListener("click", handlePressOpcion3, false);
    botonOpcion3.addEventListener("touchstart", handlePressOpcion3, false);

    // Temporal
    function handlePressOpcion1(event) {
        event.preventDefault();
        console.log("Respuesta: 1");
        socket.send('{"name": "respuesta", "priority": "0","parameters": \n\[{"name": "opcionElegida", "value": "' + 0 + '"},\n\]}');
    }

    function handlePressOpcion2(event) {
        event.preventDefault();
        console.log("Respuesta: 2");
        socket.send('{"name": "respuesta", "priority": "0","parameters": \n\[{"name": "opcionElegida", "value": "' + 1 + '"},\n\]}');
    }

    function handlePressOpcion3(event) {
        event.preventDefault();
        console.log("Respuesta: 3");
        socket.send('{"name": "respuesta", "priority": "0","parameters": \n\[{"name": "opcionElegida", "value": "' + 2 + '"},\n\]}');
    }

    function stateUpdate(event) {
        //console.log(socket);
        //console.log(event.data);
        var gameState = JSON.parse(event.data);
        //console.log(gameState);

        if (typeof gameState !== "undefined") {
            //console.log(game2State);
            if (gameState["id"] !== "undefined" && socketID === "") {
                socketID = gameState["id"];
                //console.log(socketID);
            }
            var i = 0;
            while (typeof gameState[i] !== "undefined") {
                if (typeof gameState[i]["NavePlayer"] !== "undefined") {
                    var id = gameState[i]["NavePlayer"]["super"]['Nave']['super']["Entity"]["super"]["State"]["id"];
                    var dead = gameState[i]["NavePlayer"]["dead"];
                    if (id == socketID) {
                        var health = gameState[i]["NavePlayer"]["health"];
                        //console.log(health);
                        if (health != null) {
                            updateHealth(health);
                        }
                        if (dead) {
                            socket.send("died");
                        }
                        var pregunta = gameState[i]["NavePlayer"]["pregunta"];
                        if (pregunta !== "") {
                            var opcion1 = gameState[i]["NavePlayer"]["opciones"]["opcion0"];
                            var opcion2 = gameState[i]["NavePlayer"]["opciones"]["opcion1"];
                            var opcion3 = gameState[i]["NavePlayer"]["opciones"]["opcion2"];
                            console.log(opcion1);
                            console.log(opcion2);
                            console.log(opcion3);
                            $("#preguntaContainer").css('display', 'block');
                            $("#pregunta").text(pregunta);
                            $("#opcion1").text(opcion1);
                            $("#opcion2").text(opcion2);
                            $("#opcion3").text(opcion3);
                            // updatePregunta(pregunta);
                            //socket.send('{"name": "respuesta", "priority": "0","parameters": \n\[{"name": "x", "value": "' + 1 + '"},\n\]}');
                        } else {
                            $("#preguntaContainer").css('display', 'none');
                        }
                    }
                }
                i++;
            }
        }
    }
};
