package gamelogic;

import engine.Action;
import engine.State;
import engine.StaticState;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dyn4j.geometry.Vector2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NavePlayer extends Nave {

    private int DISTANCIA_ALIANZA;

    protected String nombreJugador;
    protected int health;
    protected int healthMax;
    protected boolean leave;
    protected boolean dead;
    protected int puntaje;
    protected int idBullets;
    private String pregunta;
    private String[] opciones;
    public boolean bloqueado;
    private int respuesta;
    private String idDesafio;
    private int tiempo;
    protected int worldWidth;
    protected int worldHeight;
    
    protected String porcentaje_color_detectado;
    
    double fuerza_rotacion_max = 1.0; //1
    
    //distancia máxima de detección
    double distancia_max = 0.8;

    //es el ángulo que se suma y se resta al ángulo del robot para formar el cono de detección de objetos
    double angulo_cono = 0.5; //atributo del robot, no constante
    

    public NavePlayer(String name, String nombreJugador, boolean destroy, String id, double x, double y, double velocidadX, double velocidadY, 
                      double xDir, double yDir, int h, int hM, int cantProj, int puntaje, boolean leave, boolean dead, String preg, String[] op, 
                      boolean bq, int resp, int t, int worldWidth,int worldHeight, String color, String porcentaje_color_detectado) //angulo_cono
    {
        super("NavePlayer", destroy, id, x, y, velocidadX, velocidadY, xDir, yDir, cantProj, 64*(64/((worldWidth)*0.25)),64*(64/((worldWidth)*0.25)), 
              color);
        this.health = h;
        this.healthMax = hM;
        this.leave = leave;
        this.dead = dead;
        this.puntaje = puntaje;
        this.idBullets = 0;
        this.pregunta = preg;
        this.opciones = op;
        this.bloqueado = bq;
        this.respuesta = resp;
        this.tiempo = t;
        this.nombreJugador = nombreJugador;
        this.worldWidth=worldWidth;
        this.worldHeight=worldHeight;
        this.DISTANCIA_ALIANZA=(worldWidth-worldHeight)/50;
        
        this.porcentaje_color_detectado = porcentaje_color_detectado;        
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones) {
        LinkedList<Action> listAccion = acciones.get(id);
        LinkedList<State> listProyectil = new LinkedList();
        if (!bloqueado) {
            if (listAccion != null) {
                for (Action accion : listAccion) {
                    if (!dead) {
                        switch (accion.getName()) {
                            case "fire":
                                String idAux = id + "" + idBullets;
                                Proyectil proyectil = new Proyectil("Proyectil", false, idAux, id, x, y, velocidad.x, velocidad.y, direccion.x, direccion.y, angulo, 0,worldWidth,worldHeight);
                                listProyectil.add(proyectil);
                                idBullets++;
                        }
                    }
                }
            }

            double[] futuraPos = futuraPosicion(acciones);
            for (State estado : estados) {
                //Choque contra otra nave

                if (estado != this && estado.getName().equalsIgnoreCase("asteroide")) { // Choque contra asteroide
                    Asteroide ast = (Asteroide) estado;
                    double xFuturaAsteroide = ast.x + ast.velocidad.x;
                    double yFuturaAsteroide = ast.y + ast.velocidad.y;
                    if (futuraPos[0] == xFuturaAsteroide && futuraPos[1] == yFuturaAsteroide) {
                        this.addEvent("hit");
                    }
                } else if (estado != this && estado.getName().equalsIgnoreCase("proyectil")) { // Choque contra proyectil
                    Proyectil proj = (Proyectil) estado;
                    double xFuturaProyectil = proj.x + proj.velocidad.x;
                    double yFuturaProyectil = proj.y + proj.velocidad.y;
                    if (futuraPos[0] == xFuturaProyectil && futuraPos[1] == yFuturaProyectil) {
                        this.addEvent("hit");
                    }
                } else if (estado != this && estado.getName().equalsIgnoreCase("moneda")) { // Choque contra moneda
                    Moneda mon = (Moneda) estado;
                    if (futuraPos[0] == mon.x && futuraPos[1] == mon.y) {
                        this.addEvent("collect");
                    }

                } else if (estado != this && estado.getName().equalsIgnoreCase("naveneutra")) {
                    NaveNeutra neutra = (NaveNeutra) estado;
                    double dist = Math.sqrt((futuraPos[0] - neutra.x) * (futuraPos[0] - neutra.x) + (futuraPos[1] - neutra.y) * (futuraPos[1] - neutra.y));
                    if (dist <= DISTANCIA_ALIANZA && neutra.idPropietario.equalsIgnoreCase("") && neutra.disponible && tiempo == 0) {
                        boolean creaDesafio = true;

                        //este for recorre los estados 
                        for (State estAux : estados) {
                            if (estAux != null && estAux != this && estAux.getName().equalsIgnoreCase("naveplayer")
                                    && !((NavePlayer) estAux).bloqueado) {
                                NavePlayer est = ((NavePlayer) estAux);
                                double[] futuraPosAux = est.futuraPosicion(acciones);
                                double distAux = Math.sqrt((futuraPosAux[0] - neutra.x) * (futuraPosAux[0] - neutra.x)
                                        + (futuraPosAux[1] - neutra.y) * (futuraPosAux[1] - neutra.y));
                                if (dist > distAux) {
                                    creaDesafio = false;
                                }
                            }
                        }
                        if (creaDesafio) {
                            Desafio desa = new Desafio("Desafio", false, "idDest", neutra.id, this.id,worldWidth,worldHeight);
                            this.addEvent("desafiar");
                            listProyectil.add(desa); // Por que agregar el desafio a la lista de proyectiles?
                        }
                    }

                }

            }
        }
        return listProyectil;
    }

    public double[] futuraPosicion(HashMap<String, LinkedList<Action>> acciones) {
        double[] pos = new double[2];
        LinkedList<Action> listAccion = acciones.get(id);
        double nuevoX = x;
        double nuevoY = y;
        double nuevaVelX = velocidad.x;
        double nuevaVelY = velocidad.y;
        double nuevaDirX = direccion.x;
        double nuevaDirY = direccion.y;

        if (listAccion != null) {
            for (Action accion : listAccion) {
                if (accion != null) {
                    switch (accion.getName()) {//usar el agente para agregar acciones
                        case "move": 
                            if (accion.getParameter("x") != null && accion.getParameter("y") != null) {
                                nuevaVelX = Double.parseDouble(accion.getParameter("x"));
                                nuevaVelY = Double.parseDouble(accion.getParameter("y"));
                            }
                            break;
                        case "stop":
                            nuevaVelX = 0;
                            nuevaVelY = 0;
                            break;
                        case "adelante": 
                            if (accion.getParameter("tiempo") != null && accion.getParameter("velocidad") != null) {
                                nuevaVelX = Double.parseDouble(accion.getParameter("tiempo"));
                                nuevaVelY = Double.parseDouble(accion.getParameter("velocidad"));
                            }
                            break;
                        case "atras": 
                            if (accion.getParameter("tiempo") != null && accion.getParameter("velocidad") != null) {
                                nuevaVelX = Double.parseDouble(accion.getParameter("tiempo"));
                                nuevaVelY = Double.parseDouble(accion.getParameter("velocidad"));
                            }
                            break;
                        case "derecha": 
                            if (accion.getParameter("tiempo") != null && accion.getParameter("velocidad") != null) {
                                nuevaVelX = Double.parseDouble(accion.getParameter("tiempo"));
                                nuevaVelY = Double.parseDouble(accion.getParameter("velocidad"));
                            }
                            break;
                        case "izquierda": 
                            if (accion.getParameter("tiempo") != null && accion.getParameter("velocidad") != null) {
                                nuevaVelX = Double.parseDouble(accion.getParameter("tiempo"));
                                nuevaVelY = Double.parseDouble(accion.getParameter("velocidad"));
                            }
                            break;
                        /*case "cuanto_color": //va??
                            if (accion.getParameter("color") != null && accion.getParameter("estados") != null) {
                                String un_color = accion.getParameter("color"); 
                                String estados = accion.getParameter("estados"); 
                            }
                            break; */                           
                    }
                }
            }
        }
        pos[0] = nuevoX + nuevaVelX;
        pos[1] = nuevoY + nuevaVelY;

        return pos;
    }

    @Override
    public NavePlayer next(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones) {
        LinkedList<Action> listAccion = acciones.get(id);
        hasChanged = true;
        double nuevoX = x;
        double nuevoY = y;
        int nuevosProyectiles = countProyectil;
        boolean salir = leave;
        boolean muerto = dead;
        int nuevaVida = health;
        boolean destruido = destroy;
        double nuevaVelX = velocidad.x;
        double nuevaVelY = velocidad.y;
        double nuevaDirX = direccion.x;
        double nuevaDirY = direccion.y;
        int nuevoPuntaje = puntaje;
        boolean estaBloqueado = bloqueado;
        String[] op = this.opciones;
        int nuevaRespuesta = respuesta;
        String nuevaPregunta = pregunta;
        String nuevoDesafio = this.idDesafio;
        String nuevoNombreJugador = this.nombreJugador;
        int nuevoTiempo = tiempo - 1;//afectar este atributo con el tiempo del parámetro
        
        String nuevo_porcentaje_detectado= porcentaje_color_detectado;
        
        double tiempo_movimiento =0;
        double velocidad_movimiento=0;
        String movimiento="";
        
        String un_color ="";
        String estados_actuales = "";

        if (!bloqueado) {
            if (listAccion != null) {
                for (Action accion : listAccion) {

                    if (accion != null) {
                        System.out.println(accion.getName());
                        hasChanged = true;
                        if (!dead) {
                            switch (accion.getName()) {
                                case "move":
                                    if (accion.getParameter("x") != null && accion.getParameter("y") != null) {
                                        nuevaVelX = Double.parseDouble(accion.getParameter("x"));
                                        nuevaVelY = Double.parseDouble(accion.getParameter("y"));
                                        nuevaDirX = nuevaVelX;
                                        nuevaDirY = nuevaVelY;
                                        
                                        nuevoX = nuevoX + nuevaVelX;
                                        nuevoY = nuevoY + nuevaVelY;
                                    }
                                    break;
                                case "adelante"://ok
                                    if (accion.getParameter("tiempo") != null && accion.getParameter("velocidad") != null) {
                                        //try {
                                            tiempo_movimiento = Double.parseDouble(accion.getParameter("tiempo"));
                                            velocidad_movimiento = Double.parseDouble(accion.getParameter("velocidad"));
                                            /*Vector2 nuevaVelocidad;                                
                                            movimiento="adelante";
                                            nuevaVelocidad =  nueva_posicion(movimiento, tiempo_movimiento, velocidad_movimiento);
*/
                                            nuevaVelX = direccion.x * (velocidad_movimiento*tiempo_movimiento);
                                            nuevaVelY = direccion.y * (velocidad_movimiento*tiempo_movimiento);
                                            nuevoX = nuevoX + nuevaVelX;
                                            nuevoY = nuevoY + nuevaVelY;                                            
                                        /*} 
                                        catch (IOException ex) {
                                            Logger.getLogger(NavePlayer.class.getName()).log(Level.SEVERE, null, ex);
                                        }*/
                                    }
                                    break;
                                case "atras"://ok
                                    if (accion.getParameter("tiempo") != null && accion.getParameter("velocidad") != null) {                                        
                                        try {
                                            tiempo_movimiento = Double.parseDouble(accion.getParameter("tiempo"));
                                            velocidad_movimiento = Double.parseDouble(accion.getParameter("velocidad"));
                                            Vector2 nuevaVelocidad;                                
                                            movimiento="atras";
                                            nuevaVelocidad = nueva_posicion(movimiento, tiempo_movimiento, velocidad_movimiento);

                                            nuevaVelX = -1*direccion.x * (velocidad_movimiento*tiempo_movimiento);
                                            nuevaVelY = -1*direccion.y * (velocidad_movimiento*tiempo_movimiento);
                                            nuevoX = nuevoX + nuevaVelX;
                                            nuevoY = nuevoY + nuevaVelY;
                                        } 
                                        catch (IOException ex) {
                                            Logger.getLogger(NavePlayer.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                    break;                                    
                                case "derecha":
                                    if (accion.getParameter("tiempo") != null && accion.getParameter("velocidad") != null) {                                        
                                        //try {
                                            tiempo_movimiento = Double.parseDouble(accion.getParameter("tiempo"));
                                            velocidad_movimiento = Double.parseDouble(accion.getParameter("velocidad"));
                                            /*Vector2 vector_fuerza;                                
                                            movimiento="derecha";
                                            nuevaVelocidad = nueva_posicion(movimiento, tiempo_movimiento, velocidad_movimiento);

                                            nuevaVelX = nuevaVelocidad.x;
                                            nuevaVelY = nuevaVelocidad.y;
                                            
                                            nuevaVelocidad.x += nuevoX;
                                            nuevaVelocidad.y += nuevoY;
                                            
                                            nuevoX = nuevaVelocidad.x;
                                            nuevoY = nuevaVelocidad.y;
                                            */
                                            double fuerza = velocidad_movimiento*tiempo_movimiento;
                                            //Vector2 perpendicular = obtener_vector_perpendicular(direccion);
                                            //double fuerza_rotacion_max = 1;//es constante
                                            //direccion.getRightHandOrthogonalVector()*
                                            direccion.rotate(fuerza/fuerza_rotacion_max * Math.PI);//guardar este parámetro como constante para usar en los giros 
                                            /*
                                            vector_fuerza = new Vector2(fuerza,fuerza);
                                            vector_fuerza.normalize();                                           
                                            */
                                            nuevaDirX = direccion.x;
                                            nuevaDirY = direccion.y;
                                            
                                            //this.tiempo += tiempo_movimiento; //e ir restando en cada vuelta
                                        /*} 
                                        catch (IOException ex) {
                                            Logger.getLogger(NavePlayer.class.getName()).log(Level.SEVERE, null, ex);
                                        }*/
                                    }
                                    break;                                    
                                case "izquierda":
                                    if (accion.getParameter("tiempo") != null && accion.getParameter("velocidad") != null)
                                    {
                                        tiempo_movimiento = Double.parseDouble(accion.getParameter("tiempo"));
                                        velocidad_movimiento = Double.parseDouble(accion.getParameter("velocidad"));
                                        
                                        double fuerza = velocidad_movimiento*tiempo_movimiento;
                                        
                                        direccion.rotate(-1*fuerza/fuerza_rotacion_max * Math.PI);
                                        
                                        nuevaDirX = direccion.x;
                                        nuevaDirY = direccion.y;
                                    }
                                    break;
                                /*case "cuanto_color"://devuelve el porcentaje que cubre el color dado en la foto tomada por el robot, si no se encuentra el color entonces devuelve 0                                    
                                    if (accion.getParameter("color") != null && accion.getParameter("estados") != null) 
                                    {
                                        try
                                        {
                                            //todas pelotas son iguales
                                            //todos objetos tienen un solo color
                                            //porcentaje depende de la distancia al objeto
                                            //todos los objetos son esferas
                                            
                                            un_color = accion.getParameter("color"); 
                                            estados_actuales = accion.getParameter("estados"); 
                                            
                                            System.out.println(un_color);
                                            System.out.println(estados_actuales);

                                            //JSONObject states = new JSONObject();
                                            JSONObject states = (JSONObject) new JSONParser().parse(estados_actuales);
                                            
                                            int i=0;
                                            
                                            
                                            
                                            JSONObject obj = (JSONObject) states.get(0);
                                            
                                            
                                            
                                            World w = (World) ((JSONObject) states.get(0)).get("World");
                                            Asteroide a = (Asteroide) ((JSONObject) states.get(1)).get("Asteroide");
                                            
                                            int cant_obj_color=0;
                                            
                                            if(a.color=="ROJO")
                                            {
                                                cant_obj_color+=1;
                                            }
                                            
                                            System.out.println("Objetos de color " + cant_obj_color);
                                            /*
                                            states.put("super", super.toJSON());
                                            states.put("nombreJugador", nombreJugador);
                                            states.put("health", health);
                                            *
                                            nuevo_porcentaje_detectado= "" + cant_obj_color;
                                        }
                                        catch (ParseException ex) {
                                            Logger.getLogger(NavePlayer.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                    break;*/
                                case "stop":
                                    nuevaVelX = 0;
                                    nuevaVelY = 0;
                                    break;
                                case "respuesta":
                                    op = null;
                                    nuevaRespuesta = -1;
                                    nuevaPregunta = "";
                                    break;
                                case "fire":
                                    nuevosProyectiles++;
                                    break;
                                case "enter":
                                    salir = false;
                                    break;
                                case "leave":
                                    destruido = true;
                                    break;
                                case "nombreJugador":
                                    nuevoNombreJugador = accion.getParameter("nombreElegido");
                            }

                        } else {
                            System.out.println("respawn " + dead);
                            this.addEvent("respawn");
                        }
                    }
                }
            }
            //nuevoX = nuevoX + nuevaVelX;
            /* correccion limitaciones mapa se va a poder hacer un modulo para que quede mas prolijo*/
            if(nuevoX<0){
                nuevoX=0;
            }
            if(nuevoX>worldWidth){
                nuevoX=worldWidth;
            }
            
            //nuevoY = nuevoY + nuevaVelY;
            
            if(nuevoY<0){
                nuevoY=0;
            }
            if(nuevoY>worldHeight){
                nuevoY=worldHeight;
            }
        }
        LinkedList<String> eventos = getEvents();

        if (!eventos.isEmpty()) {
            hasChanged = true;
            boolean revivir = false;
            for (String evento : eventos) {
                switch (evento) {
                    case "hit":
                        if (!bloqueado) {
                            nuevaVida = nuevaVida + 1;
                            System.out.println(nuevaVida);
                            if (nuevaVida <= 0) {
                                nuevaVelX = 0;
                                nuevaVelY = 0;
                                muerto = true;
                                nuevoPuntaje=nuevoPuntaje+1;
                            }
                        }
                        break;
                    //ver colisiones
                    case "collide":
                        if (!revivir) {
                            nuevaVelX = velocidad.x;
                            nuevaVelY = velocidad.y;
                            nuevoX = x;
                            nuevoY = y;
                        }
                        break;
                    case "collect":
                        nuevoPuntaje = nuevoPuntaje + 10; 
                        break;
                    case "liberar":
                        estaBloqueado = false;
                        break;
                    case "incorrecta":
                        nuevoTiempo = 50;
                        break;
                    case "respawn":
                        revivir = true;
                        nuevoX = Math.random() * (worldWidth - 100) + 100;
                        nuevoY = Math.random() * (worldHeight - 100) + 100;
                        nuevaVelX = 0;
                        nuevaVelY = 0;
                        muerto = false;
                        nuevaVida = healthMax;
                        break;
                    case "despawn":
                        //Considera que el jugador sale del juego
                        destruido = true;
                        break;
                    case "desafiar":
                        estaBloqueado = true;
                        nuevaVelX = 0;
                        nuevaVelY = 0;
                        for (State estado : estados) {
                            if (estado != null && estado.getName().equalsIgnoreCase("desafio") && ((Desafio) estado).idNavePlayer.equalsIgnoreCase(id)) {
                                Desafio des = (Desafio) estado;
                                op = des.opciones;
                                nuevaRespuesta = des.correcta;
                                nuevaPregunta = des.pregunta;

                            }
                        }
                        break;
                }
            }
        }
        if (nuevoTiempo <= 0) {
            nuevoTiempo = 0;
        }
        
        NavePlayer nuevoJugador = new NavePlayer(name, nuevoNombreJugador, destruido, id, nuevoX, nuevoY, nuevaVelX, nuevaVelY,
                nuevaDirX, nuevaDirY, nuevaVida, healthMax, nuevosProyectiles, nuevoPuntaje, salir, muerto,
                nuevaPregunta, op, estaBloqueado, nuevaRespuesta, nuevoTiempo,worldWidth,worldHeight, color, nuevo_porcentaje_detectado);

        return nuevoJugador;
    }
    
    private Vector2 nueva_posicion(String movimiento, double tiempo, double velocidad) throws IOException
    {//completar
        Vector2 nuevaVelocidad = null;
        double pos_x = x;
        double pos_y = y;
        
        if(movimiento=="adelante")
        {       
            pos_x = recalcular_pos(pos_x, velocidad, tiempo, direccion.x);
            pos_y = recalcular_pos(pos_y, velocidad, tiempo, direccion.y);
            /*if((pos_x>0 && pos_y>0) || (pos_x>0 && pos_y<0))
            {//cuando el robot está en el primer o cuarto cuadrante
                //sobre el eje x positivo o no
                //y se mueve hacia adelante entonces varía su posición en x
                //considerando la velocidad y el tiempo de ése movimiento
                pos_x = recalcular_pos(pos_x, velocidad, tiempo, direccion.x);
                pos_y=0;
            }
            else
            {
                if(pos_x==0)
                {//robot está sobre el eje de ordenadas, varía su posición en y

                    if(pos_y>=0)
                    {//y es positivo
                        pos_y = recalcular_pos(pos_y, velocidad, tiempo, direccion.y);
                    }
                    else
                    {//y negativo
                        pos_y = -1 * recalcular_pos(-1*pos_y, velocidad, tiempo, direccion.y);
                    } 
                }
                else
                {//robot está en segundo o tercer cuadrante, sobre el eje x negativo o no
                    //varía su posición en x pero negativamente
                    pos_x = -1 * recalcular_pos(-1*pos_x, velocidad, tiempo, direccion.x);
                    pos_y=0;
                }
            } */          
        }
        else
        {
            if(movimiento=="atras")
            {
                if((pos_x>0 && pos_y>0) || (pos_x>0 && pos_y<0))
                {//cuando el robot está en el primer o cuarto cuadrante
                    //sobre el eje x positivo o no
                    //y se mueve hacia atrás entonces varía su posición en x
                    //considerando la velocidad y el tiempo de ése movimiento
                    pos_x = recalcular_pos(-1*pos_x, velocidad, tiempo, direccion.x);
                    pos_y=0;
                }
                else
                {
                    if(pos_x==0)
                    {//robot está sobre el eje de ordenadas, varía su posición en y

                        if(pos_y>=0)
                        {//y es positivo
                            pos_y = recalcular_pos(-1*pos_y, velocidad, tiempo, direccion.y);
                        }
                        else
                        {//y negativo
                            pos_y = -1 * recalcular_pos(pos_y, velocidad, tiempo, direccion.y);
                        } 
                    }
                    else
                    {//robot está en segundo o tercer cuadrante, sobre el eje x negativo o no
                        //varía su posición en x pero negativamente
                        pos_x = -1 * recalcular_pos(pos_x, velocidad, tiempo,direccion.x);
                        pos_y=0;
                    }
                }
            }
            else
            {
                if(movimiento=="derecha")
                {
                    pos_x = tiempo;
                    pos_y = velocidad;
                }
            }
            
        }
            
        nuevaVelocidad = new Vector2(pos_x,pos_y);
        nuevaVelocidad.normalize();
        return nuevaVelocidad;
    }
    
    //
    private double recalcular_pos(double pos_inicial, double velocidad, double tiempo, double direccion)
    {//corregir calculos con robofai.py
        //por ahora el cálculo se resolvió según
        //http://www.sc.ehu.es/sbweb/fisica_/cinematica/rectilineo/rectilineo/rectilineo_1.html
        //movimiento rectilíneo uniforme
        double nueva_pos = pos_inicial + (velocidad * tiempo) * direccion;        
        return nueva_pos;
    }
    
    private LinkedList<State> objetos_dentro_area()
    {
        LinkedList<State> objetos = new LinkedList();
        
        
        
        return objetos;
    }
    
    private double distancia(double x1, double y1, double x2, double y2)
    {//calcula la distancia entre dos puntos: (x1,y1) (x2,y2)
        return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }
    
    private Boolean dentro_del_area(double angulo_obj, double distancia_obj)
    {
        if ((angulo_obj >= angulo_0_a_360(2 * Math.PI + (angulo - angulo_cono)) || angulo_obj <= angulo_0_a_360(angulo + angulo_cono)) && 
            (distancia_obj < distancia_max))
        {
            return true;
        }
	else
        {
            return false; 
        }	
    }
    
    private double angulo_0_a_360(double angulo)
    {
        if (-angulo < 0)
        {
            angulo = 2 * Math.PI + angulo;
        }

	if (angulo > 2 * Math.PI)
        {
            angulo = angulo % (2 * Math.PI); //para los ángulos cuyo seno son equivalentes
        }
		
	return angulo;
    }
    

    @Override
    public void setState(State newPlayer) {
        super.setState(newPlayer);
        this.nombreJugador = ((NavePlayer) newPlayer).nombreJugador;
        this.id = ((NavePlayer) newPlayer).id;
        this.leave = ((NavePlayer) newPlayer).leave;
        this.health = ((NavePlayer) newPlayer).health;
        this.healthMax = ((NavePlayer) newPlayer).healthMax;
        this.dead = ((NavePlayer) newPlayer).dead;
        this.bloqueado = ((NavePlayer) newPlayer).bloqueado;
        this.puntaje = ((NavePlayer) newPlayer).puntaje;
        this.pregunta = ((NavePlayer) newPlayer).pregunta;
        this.opciones = ((NavePlayer) newPlayer).opciones;
        this.respuesta = ((NavePlayer) newPlayer).respuesta;
        this.tiempo = ((NavePlayer) newPlayer).tiempo;
        
        this.porcentaje_color_detectado = ((NavePlayer) newPlayer).porcentaje_color_detectado;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jJugador = new JSONObject();
        JSONObject atributo = new JSONObject();
        JSONObject opciones = new JSONObject();

        if (this.opciones != null) {
            for (int i = 0; i < this.opciones.length; i++) {
                opciones.put("opcion" + i, this.opciones[i]);
            }
        }

        atributo.put("super", super.toJSON());
        atributo.put("nombreJugador", nombreJugador);
        atributo.put("health", health);
        atributo.put("healthMax", healthMax);
        atributo.put("leave", leave);
        atributo.put("dead", dead);
        atributo.put("puntaje", puntaje);
        atributo.put("bloqueado", bloqueado);
        atributo.put("idBullets", idBullets);
        atributo.put("pregunta", pregunta);
        atributo.put("opciones", opciones);
        atributo.put("respuesta", respuesta);
        atributo.put("idDesafio", idDesafio);
        
        atributo.put("porcentaje_color_detectado", porcentaje_color_detectado);

        jJugador.put("NavePlayer", atributo);

        return jJugador;
    }

    @Override
    public JSONObject toJSON(String sessionId, LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions, 
                             JSONObject lastState) {
        NavePlayer thePlayer = getPlayer(sessionId, states);
        if (thePlayer == null || this.id.equalsIgnoreCase(sessionId)) {
            return lastState == null || hasChanged ? toJSON() : null;
        } else {
            return null;
        }
    }

}
