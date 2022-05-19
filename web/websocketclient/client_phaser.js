var config = {
    type: Phaser.AUTO,
    parent: 'map',
    width: window.innerWidth,
    height: window.innerHeight,
    scale: {
        mode: Phaser.Scale.FIT,
        autoCenter: Phaser.Scale.CENTER_BOTH,
        //width: 4500,
        //height: 2048,
    },
    physics: {
        default: "arcade",
        arcade: {
            fps: 60,
            gravity: {y: 0}
        }
    },
    scene: {
        preload: preload,
        create: create,
        update: update
    }
};
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% COMENTARIOS %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
/*
Para un futuro faltaria corregir la nave player no desaparace por completo, los agentes aun las detectan
Pasa lo mismo con las balas ?
*/

var game = new Phaser.Game(config);
//coneccion
var socket;
var socketID = "";
var objetosNuevos = [];
//cursor
var cursors;
//dimensiones juego
var width = window.innerWidth;
var height = window.innerHeight;
//arreglos
var asteroides = [];
var players = [];
var neutras = [];
var coins = [];
var particles;
var bullets = [];
var tablaPuntajes = [];
var colors = [];
var emitters = [];
var punteroColor = 0;
//var canvas = document.querySelector("canvas");

function preload() {//modificar fondo e imagenes de jugador y asteroide, no cargar elementos innecesarioss
    //backgroud
    //this.load.image('background', 'assets/space/nebula.jpg');
    this.load.image('background', 'assets/space/pasto.png');
    //this.load.image('background', 'assets/space/cancha_usar.png');
    //starts
    //this.load.image('stars', 'assets/space/stars.png');
    //space 
    this.load.atlas('space', 'assets/space/space.png', 'assets/space/space.json');
    // robot
    this.load.spritesheet('ship', 'assets/sprites/robot_amarillo_sin_fondo.png', {
        frameWidth: 64,
        frameHeight: 64
    });
    
    // pelota
    this.load.spritesheet('pelota', 'assets/sprites/pelotas_rojas.png', {
    //this.load.spritesheet('pelota', 'assets/sprites/pelota_blanca.png', {
        frameWidth: 34,
        frameHeight: 32
    });
    
    /* nave
    this.load.spritesheet('ship', 'assets/sprites/ship1.png', {
        frameWidth: 64,
        frameHeight: 64
    });
    */
    this.load.spritesheet('bullet', 'assets/sprites/bullets/bullet11.png', {
        frameWidth: 64,
        frameHeight: 12
    });
    /* nave neutra
    this.load.spritesheet('shipNeutra', 'assets/sprites/thrust_ship.png', {
        frameWidth: 21,
        frameHeight: 28
    });
    //coins*/
    this.load.spritesheet("coin", "assets/sprites/coin.png", {
        frameWidth: 32,
        frameHeight: 32
    });
    //sprite explosion
    this.load.spritesheet("explosion", "assets/sprites/explosion.png", {
        frameWidth: 64,
        frameHeight: 64
    });
    // crear efecto de barra cargandose 
    this.fullBar = this.add.graphics();
    this.fullBar.fillStyle(0xda7a34, 1);
    this.fullBar.fillRect((this.cameras.main.width / 4) - 2, (this.cameras.main.height / 2) - 18, (this.cameras.main.width / 2) + 4, 20);
    this.progress = this.add.graphics();
}

function create() {
    console.log(width);
    console.log(height);
    //console.log(game.scene.scenes[0]==this);
    //console.log(this);


    //console.log("CREATE");
    //  Prepare some spritesheets and animations
    this.textures.addSpriteSheetFromAtlas('mine-sheet', {atlas: 'space', frame: 'mine', frameWidth: 64});
    this.textures.addSpriteSheetFromAtlas('asteroid1-sheet', {atlas: 'space', frame: 'asteroid1', frameWidth: 96});
    this.textures.addSpriteSheetFromAtlas('asteroid2-sheet', {atlas: 'space', frame: 'asteroid2', frameWidth: 96});
    this.textures.addSpriteSheetFromAtlas('asteroid3-sheet', {atlas: 'space', frame: 'asteroid3', frameWidth: 96});
    this.textures.addSpriteSheetFromAtlas('asteroid4-sheet', {atlas: 'space', frame: 'asteroid4', frameWidth: 64});

    //animaciones
    this.anims.create({key: 'mine-anim', frames: this.anims.generateFrameNumbers('mine-sheet', {start: 0, end: 15}), frameRate: 20, repeat: -1});
    this.anims.create({key: 'asteroid1-anim', frames: this.anims.generateFrameNumbers('asteroid1-sheet', {start: 0, end: 24}), frameRate: 20, repeat: -1});
    this.anims.create({key: 'asteroid2-anim', frames: this.anims.generateFrameNumbers('asteroid2-sheet', {start: 0, end: 24}), frameRate: 20, repeat: -1});
    this.anims.create({key: 'asteroid3-anim', frames: this.anims.generateFrameNumbers('asteroid3-sheet', {start: 0, end: 24}), frameRate: 20, repeat: -1});
    this.anims.create({key: 'asteroid4-anim', frames: this.anims.generateFrameNumbers('asteroid4-sheet', {start: 0, end: 24}), frameRate: 20, repeat: -1});
    //this.anims.create({key: 'efectoMoneda', frames: this.anims.generateFrameNumbers('coin', {start: 0, end: 5}), frameRate: 10, repeat: -1});
    this.anims.create({key: 'explosion-anim', frames: this.anims.generateFrameNumbers('explosion', {start: 0, end: 23}), frameRate: 100, repeat: 1});
    //this.anims.create({key: 'neutra-anim', frames: this.anims.generateFrameNumbers('shipNeutra', {start: 0, end: 0}), frameRate: 20, repeat: -1});
    //this.anims.create({key: "efectoMoneda",frames: this.anims.generateFrameNumbers("coin", {start: 0, end: 5}),frameRate: 10,repeat: -1});
    this.anims.create({key: "giro_pelota",frames: this.anims.generateFrameNumbers("pelota", {start: 0, end: 3}),frameRate: 10,repeat: -1});
    //world 2048*2048
    this.physics.world.setBounds(0, 0, width, height);
    //fondo con dimesiones port encima de las dimensiones del world para que no queden partes sin fondo
    //background = this.add.tileSprite(0, 0, width * 2, height * 2, 'background').setScrollFactor(0);
    background = this.add.tileSprite(0, 0, width * 3.17, height * 2, 'background').setScrollFactor(0);
    background.setDepth(0);
    /*  agrego planetas ,etc
    var bluePlanet = this.add.image((width - (width / 1.1)), (height - (height / 1.3)), 'space', 'blue-planet').setOrigin(0).setScrollFactor(0.6);
    var sun = this.add.image((width - (width / 2.1)), (height - (height / 1.1)), 'space', 'sun').setOrigin(0).setScrollFactor(0.6);
    var galaxy = this.add.image((width - (width / 4)), (height - (height / 3)), 'space', 'galaxy').setBlendMode(1).setScrollFactor(0.6);
    //escalas
    //galaxy
    galaxy.scaleX = galaxy.height / (width * 10);
    galaxy.scaleY = galaxy.width / (width * 10);
    //Sol
    sun.scaleX = sun.height / (width * 3);
    sun.scaleY = sun.width / (width * 3);
    //planeta
    bluePlanet.scaleX = bluePlanet.height / (width * 6);
    bluePlanet.scaleY = bluePlanet.width / (width * 6);
    */
    background.setDepth(0);

    /*efecto estres de luz
    for (var i = 0; i < 6; i++)
    {
        var eyes = this.add.image(Phaser.Math.Between(0, width), Phaser.Math.Between(0, height), 'space', 'eyes').setBlendMode(1).setScrollFactor(0.8);
        eyes.scaleX = eyes.height / (width * 2);
        eyes.scaleY = eyes.width / (width * 2);
    }
    */
    /*estrellas
    var stars = this.add.tileSprite(Phaser.Math.Between(0, width), Phaser.Math.Between(0, height), 2000, 2000, 'stars').setScrollFactor(0);
    stars.scaleX = stars.height / (width * 2);
    stars.scaleY = stars.width / (width * 2);
*/
    //particulas 
    particles = this.add.particles('space');

    /*animacion galaxy
    this.tweens.add({
        targets: galaxy,
        angle: 360,
        duration: 100000,
        ease: 'Linear',
        loop: -1
    });
    */
    //tabla score
    //tablaPosiciones = this.add.text(16, 16, 'Tabla Posiciones \n', {fontSize: '10px', fill: '#fff'});
    tablaPosiciones = this.add.text(16, 16, '', {fontSize: '10px', fill: '#fff'});
    cursors = this.input.keyboard.createCursorKeys();

    //colores para las particulas
    colors = ['red', 'green', 'blue', 'yellow', 'white']; 
    
}

// Retorna un entero aleatorio entre min (incluido) y max (excluido)
// ¡Usando Math.round() te dará una distribución no-uniforme!
function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
}

function update(time, delta) {
    //tablaPosiciones.setText('Tabla Posiciones \n');
    var maxScore=0;
    for (var key in tablaPuntajes) {
        if (tablaPuntajes.hasOwnProperty(key)) {
            //tablaPosiciones.text += tablaPuntajes[key][0] + ' score: ' + tablaPuntajes[key][1] + '\n';           
        }
    }
    /*
    for (let i = 0; i < coins.length; i++) {
        coins[i].anims.play("efectoMoneda", true);
    }

    for (var key in neutras) {
        neutras[key].anims.play("neutra-anim", true);
    }
*/
    for (let i = 0; i < asteroides.length; i++) {
        switch (i) {
            case 1:
                asteroides[i].anims.play('giro_pelota', true);//asteroides[i].anims.play('asteroid1-anim', true);
                //asteroides[i].body.collideWorldBounds=true;
                break;
            case 2:
                asteroides[i].anims.play('giro_pelota', true);//asteroides[i].anims.play('asteroid2-anim', true);
                //asteroides[i].body.collideWorldBounds=true;
                break;
            case 3:
                asteroides[i].anims.play('giro_pelota', true);//asteroides[i].anims.play('asteroid3-anim', true);
                //asteroides[i].body.collideWorldBounds=true;
                break
            default:
                asteroides[i].anims.play('giro_pelota', true);//asteroides[i].anims.play('asteroid4-anim', true);
                //asteroides[i].body.collideWorldBounds=true;
                break;
        }
    }
    
}

function particle(ship, id) {
    var emitter = particles.createEmitter({
        frame: '' + colors[punteroColor],
        speed: 15,
        lifespan: {
            onEmit: function (particle, key, t, value) {
                return 500;
            }
        },
        alpha: {
            onEmit: function (particle, key, t, value) {
                return 100;
            }
        },
        angle: {
            onEmit: function (particle, key, t, value) {
                var v = Phaser.Math.Between(-10, 10);
                return (ship.angle - 180) + v;
                //return (ship.angle - 90) + v;
            }
        },
        scale: {start: (ship.width / (width * 0.30)), end: 0},
        blendMode: 'ADD'
    });
    punteroColor++;
    if (punteroColor >= colors.length) {
        punteroColor = 0
    }
    emitters[id] = [particles, emitter];
    emitter.startFollow(ship);
}

function infoPantalla() {
    socket.send('{"name": "tamañoCanvas", "priority": "0","parameters": [{"name": "width", "value": "' + width + '"},\n\
                                                                     {"name": "height", "value": "' + height + '"}]}');
}

window.onload = function () {
    // Crea la conexion con WebSocket
    var page = document.createElement('a');
    page.href = window.location.href;

    //define la url del servidor como la hostname de la pagina y el puerto definido 8080 del ws
    //servidor local
    var url = "ws://" + page.hostname + ":8080";

    //servidor Edimbrujo
    //var url = "ws://" + page.hostname + ":60161";
    socket = new WebSocket(url + "/" + window.location.pathname.split('/')[1] + "/GameWebSocket");
    socket.onmessage = stateUpdate;
    socket.onopen = infoPantalla;
    //actualiza la vista del juego cuando recive un nuevo estado desde el servidor
    function stateUpdate(event) {
        var gameState = JSON.parse(event.data);
        var i = 0;
        while (typeof gameState[i] !== "undefined") {
            //console.log(gameState);
            if (typeof gameState[i]["Remove"] !== "undefined") {
                console.log("remove");
                var id = gameState[i]["Remove"]["id"];
                if (players[id] != null) {
                    players[id].destroy;
                    players[id] = null;
                } else {
                    if (bullets[id] != null) {
                        bullets[id].destroy;
                        bullets[id] = null;
                    }
                }
            } else if (typeof gameState[i]["NavePlayer"] !== "undefined") {
                /* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Nave Player %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
                var id = gameState[i]["NavePlayer"]["super"]['Nave']['super']["Entity"]["super"]["State"]["id"];
                var destroy = gameState[i]["NavePlayer"]["super"]['Nave']['super']["Entity"]["super"]["State"]["destroy"];
                var leave = gameState[i]["NavePlayer"]["leave"];
                var dead = gameState[i]["NavePlayer"]["dead"];
                var x = gameState[i]["NavePlayer"]["super"]['Nave']['super']["Entity"]["x"];
                var y = gameState[i]["NavePlayer"]["super"]['Nave']['super']["Entity"]["y"];
                var nombreJugador = gameState[i]["NavePlayer"]["nombreJugador"];
                var health = gameState[i]["NavePlayer"]["health"]; //puedo usarlo para saber cuando hay colisiones
                var puntaje = gameState[i]["NavePlayer"]["puntaje"];
                var angulo = gameState[i]["NavePlayer"]["super"]['Nave']['angulo'];
                
                var color = gameState[i]["NavePlayer"]["super"]['Nave']['super']["Entity"]["color"];
                

                if (players[id] == null) {
                    players[id] = game.scene.scenes[0].physics.add.sprite(x, y, "ship");
                    players[id].setDepth(1);
                    players[id].scaleX = players[id].height / (width * 0.25);
                    players[id].scaleY = players[id].width / (width * 0.25);

                    if (particles !== undefined) {
                        particle(players[id], id);//provoca la rotación de la imagen??
                    }
                }
                /* cargo puntaje en tabla de puntaje */
                if(nombreJugador==null){
                    nombreJugador==id;
                }
                tablaPuntajes[id] = [nombreJugador, puntaje];
                /* seteo angulo y coordenadas de la nave */
                players[id].angle = angulo;
                players[id].y = y;
                players[id].x = x;
                players[id].z = y;
                
                players[id].color = color;
                
                //se usa ?
                if (leave) {
                    players[id].destroy();
                    delete tablaPuntajes[id];
                }
                if (destroy) {
                    players[id].destroy(); // no esta funcionando, averiguar porque
                    delete tablaPuntajes[id];
                    if (emitters[id] !== "undefined") {
                        for (var i = 0; i < emitters[id][0].emitters.list.length; i++) {
                            if (emitters[id][1] == emitters[id][0].emitters.list[i]) {
                                emitters[id][0].emitters.list.pop(i)
                            }
                        }
                        delete emitters[id][0]; //elimino del arreglo
                        delete emitters[id][1]; //elimino del arreglo
                    }
                }
            } else if (typeof gameState[i]['Asteroide'] !== "undefined") {
                /* %%%%%%%%%%%%%%%%%%%%%%%%%% Asteroides %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
                var id = gameState[i]["Asteroide"]['super']["Entity"]["super"]["State"]["id"];
                var x = gameState[i]["Asteroide"]['super']["Entity"]["x"];
                var y = gameState[i]["Asteroide"]['super']["Entity"]["y"];
                
                var color = gameState[i]["Asteroide"]["super"]["Entity"]["color"];
                //var angulo = gameState[i]["Asteroide"]['angulo'];
                
                if (asteroides[id] == null) {
                    asteroides[id] = game.scene.scenes[0].physics.add.sprite(x, y, "pelota");//game.scene.scenes[0].physics.add.sprite(x, y, "asteroid1");
                    asteroides[id].setDepth(1);
                    asteroides[id].scaleX = asteroides[id].height / (width * 0.1);
                    asteroides[id].scaleY = asteroides[id].width / (width * 0.1);
                }
                asteroides[id].y = y;
                asteroides[id].x = x;
                asteroides[id].z = y;
                
                asteroides[id].color = color;
                //asteroides[id].angle = angulo;
                
            } else if (typeof gameState[i]["NaveNeutra"] !== "undefined") {
                /* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%5 Nave Neutra %%%%%%%%%%%%%%%%%%%%%%%%%%%%% */
                /* leo informacion JSON */
                var id = gameState[i]["NaveNeutra"]["super"]['Nave']['super']["Entity"]["super"]["State"]["id"];
                var destroy = gameState[i]["NaveNeutra"]["super"]['Nave']['super']["Entity"]["super"]["State"]["destroy"];
                var leave = gameState[i]["NaveNeutra"]["leave"];
                var x = gameState[i]["NaveNeutra"]["super"]['Nave']['super']["Entity"]["x"];
                var y = gameState[i]["NaveNeutra"]["super"]['Nave']['super']["Entity"]["y"];
                var angulo = gameState[i]["NaveNeutra"]["super"]['Nave']['angulo'];
                /* Si no existia la creo */
                if (neutras[id] == null) {
                    neutras[id] = game.scene.scenes[0].add.sprite(x, y, "shipNeutra");
                    neutras[id].setDepth(1);
                    neutras[id].scaleX = neutras[id].height / (height * 0.09);
                    neutras[id].scaleY = neutras[id].width / (height * 0.09);
                }
                /* seteo coordenadas */
                neutras[id].angle = angulo;
                neutras[id].y = y;
                neutras[id].x = x;
                neutras[id].z = y;
            } else if (typeof gameState[i]["Proyectil"] !== "undefined") {
                /* %%%%%%%%%%%%%%%%%%%%%%%%%% Proyectil %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
                var id = gameState[i]["Proyectil"]['super']["Entity"]["super"]["State"]["id"];
                var x = gameState[i]["Proyectil"]['super']["Entity"]["x"];
                var y = gameState[i]["Proyectil"]['super']["Entity"]["y"];
                var destroy = gameState[i]["Proyectil"]['super']["Entity"]["super"]["State"]["destroy"];
                var angulo = gameState[i]["Proyectil"]['angulo'];
                /* si no exitia un bala con ese mismo id la creo*/
                if (bullets[id] == null) {
                    bullets[id] = game.scene.scenes[0].add.sprite(x, y, 'bullet');
                    bullets[id].scaleX = bullets[id].width / (width * 0.2);
                    bullets[id].scaleY = bullets[id].width / (width * 0.2);
                }
                /* cargo angulo y seteo coordenadas*/
                bullets[id].angle = angulo;
                bullets[id].y = y;
                bullets[id].x = x;
                bullets[id].z = y;
                if (destroy) {
                    bullets[id].destroy();
                }

            } else if (typeof gameState[i]["Moneda"] !== "undefined") {
                /* %%%%%%%%%%%%%%%%%%%%%%%%% Monedas %%%%%%%%%%%%%%%%%%%%%%%%%%%%%5*/
                var id = gameState[i]["Moneda"]['super']["Entity"]["super"]["State"]["id"];
                var x = gameState[i]["Moneda"]['super']["Entity"]["x"];
                var y = gameState[i]["Moneda"]['super']["Entity"]["y"];
                if (coins[id] == null) {
                    coins[id] = game.scene.scenes[0].physics.add.sprite(x, y, "asteroid1");
                    coins[id].setDepth(1);
                    coins[id].scaleX = coins[id].height / (height * 0.1);
                    coins[id].scaleY = coins[id].width / (height * 0.1);
                }

                coins[id].y = y;
                coins[id].x = x;
                coins[id].z = y;
            }
            i++;
        }
    }
};
        