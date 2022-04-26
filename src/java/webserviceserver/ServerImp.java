package webserviceserver;

import engine.Lobby;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import engine.State;//para cuanto_color

@Stateless
@Path("/server")
public class ServerImp {

    /*
    Acciones:
    "move" en un sentido (x,y)
    "fire"
    "stop"
     */
    private Lobby lobby;
    int session;

    @GET
    @Path("/enter")
    public String playerEnter(@QueryParam("rol") String rol) {
        lobby = Lobby.startGame();
        SecureRandom random = new SecureRandom();
        rol = rol + random.nextInt(1000000000);
        // String session = bytes.toString();
        lobby.addAction(rol, "enter");
        lobby.addAction(rol, "start");
        return rol;
    }

    @GET
    @Path("/test")
    public String test() {
        return "prueba";
    }

    @GET
    @Path("/exit")
    public void playerExit(@QueryParam("session") String session) {
        lobby = Lobby.startGame();
        lobby.addAction(session, "leave");
    }

    @GET
    @Path("/action")
    public String receiveAction(@QueryParam("action") String action, @QueryParam("session") String session) {
        System.out.println(action + " del jugador" + session);

        lobby = Lobby.startGame();
        if (action.equalsIgnoreCase("move")) { // Si es un "move" hay que indicar la dirección/velocidad
            String move = "{\"name\": \"move\", \"priority\": \"1\",\"parameters\": [{\"name\": \"x\", \"value\": \""
                    + 1 + "\"},{\"name\": \"y\", \"value\": \"" + 1 + "\"}]}";

            System.out.println(move);
            lobby.addAction(session, move);
        } else if (action.equalsIgnoreCase("respuesta")) { // Si es un "fire" no hay que hacer nada sobre la accion
            String res = "{\"name\": \"respuesta\", \"priority\": \"0\",\"parameters\": [{\"name\": \"opcionElegida\", \"value\": \"" + 1 + "\"}]}";
            System.out.println(res);
            lobby.addAction(session, res);
        } else {
            lobby.addAction(session, action);
        }
        return "okey";
    }

    //27/11/2020 15:30hs con adelante
    //software para consultar APIs REST desde Netbeans plugins
    //http://localhost:8080/Edimbrujo/webservice/server/actionMove?x=-1&y=0&session=pepe308048109
    //http://localhost:8080/Edimbrujo/webservice/server/getFullState
    @GET
    @Path("/actionMove")
    public String move(@QueryParam("x") String x, @QueryParam("y") String y,
            @QueryParam("session") String session) {

        lobby = Lobby.startGame();
        String move = "{\"name\": \"move\", \"priority\": \"0\",\"parameters\": [{\"name\": \"x\", \"value\": \"" + x + "\"},{\"name\": \"y\", \"value\": \"" + y + "\"}]}";

        System.out.println(move);
        lobby.addAction(session, move);
        return "okey";
    }
    
    //describir estas acciones
    @GET
    @Path("/actionAdelante")
    public String adelante(@QueryParam("tiempo") String t, @QueryParam("velocidad") String v, @QueryParam("session") String session)
    {
        lobby = Lobby.startGame();
        String move = "{\"name\": \"adelante\", \"priority\": \"0\",\"parameters\": [{\"name\": \"tiempo\", \"value\": \"" + t + 
                      "\"},{\"name\": \"velocidad\", \"value\": \"" + v + "\"}]}";

        System.out.println(move);
        lobby.addAction(session, move);
        return "ok";
    }
    
    @GET
    @Path("/actionAtras")
    public String atras(@QueryParam("tiempo") String t, @QueryParam("velocidad") String v, @QueryParam("session") String session)
    {
        lobby = Lobby.startGame();
        String move = "{\"name\": \"atras\", \"priority\": \"0\",\"parameters\": [{\"name\": \"tiempo\", \"value\": \"" + t + 
                      "\"},{\"name\": \"velocidad\", \"value\": \"" + v + "\"}]}";

        System.out.println(move);
        lobby.addAction(session, move);
        return "ok";
    }
    
    @GET
    @Path("/actionDerecha")
    public String derecha(@QueryParam("tiempo") String t, @QueryParam("velocidad") String v, @QueryParam("session") String session)
    {
        lobby = Lobby.startGame();
        String move = "{\"name\": \"derecha\", \"priority\": \"0\",\"parameters\": [{\"name\": \"tiempo\", \"value\": \"" + t + 
                      "\"},{\"name\": \"velocidad\", \"value\": \"" + v + "\"}]}";

        System.out.println(move);
        lobby.addAction(session, move);
        return "ok";
    }
    
    @GET
    @Path("/actionIzquierda")
    public String izquierda(@QueryParam("tiempo") String t, @QueryParam("velocidad") String v, @QueryParam("session") String session)
    {
        lobby = Lobby.startGame();
        String move = "{\"name\": \"izquierda\", \"priority\": \"0\",\"parameters\": [{\"name\": \"tiempo\", \"value\": \"" + t + 
                      "\"},{\"name\": \"velocidad\", \"value\": \"" + v + "\"}]}";

        System.out.println(move);
        lobby.addAction(session, move);
        return "ok";
    }
    
    @GET
    @Path("/actionCuanto_color")
    public String cuanto_color(@QueryParam("color") String c, @QueryParam("session") String session)
    {//color puede ser ROJO, VERDE, AZUL, BLANCO o NEGRO
        lobby = Lobby.startGame();       
        
        String state = "error";
        try 
        {//resolver aquí
            /* esa opción no sirve pues para brindar la respuesta al cliente no se requiere de la ejecución de una acción sobre el robot
               ya que no modifica su estado. Además de requerir una ejecución extra para responder
            
            state = lobby.getFullState();
            String move = "{\"name\": \"cuanto_color\", \"priority\": \"0\",\"parameters\": [{\"name\": \"color\", \"value\": \"" + c + "\"},"+
                          "{\"name\": \"estados\", \"value\": \"" + state + "\"}]}";

            System.out.println(move);
            lobby.addAction(session, move);*/
            
            //convertir (otra opción)
            
            state = lobby.getCuanto_color(c, session); //lobby.getFullState();
            //String jugador = lobby.get_jugador(session);//lobby.getFullState();
            //System.out.println("Cuanto color");
            
            //state += " Jugador: " + jugador;
            /*
            State un_jugador = (State) jugador;
            World w = (World) ((JSONObject) states.get(0)).get("World");
            */
            //state = jugador.cuanto_color(c, lobby);

        } catch (InterruptedException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return state;//json
        
        //return "10";//debería devolver el porcentaje detectado guardado en un atributo del robot
    }
    
    @GET
    @Path("/actionBuscar_color")
    public String buscar_color(@QueryParam("color") String c, @QueryParam("session") String session)
    {//color puede ser ROJO, VERDE, AZUL, BLANCO o NEGRO
        lobby = Lobby.startGame();       
        
        String state = "error";
        try 
        {            
            state = lobby.getBuscar_color(c, session);
            System.out.println("Buscar color");            

        } catch (InterruptedException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return state;
    }

    @GET
    @Path("/actionAnswer")
    public String answer(@QueryParam("respuesta") String x, @QueryParam("session") String session) {

        lobby = Lobby.startGame();
        //String move = "{\"name\": \"move\", \"priority\": \"0\",\"parameters\": [{\"name\": \"x\", \"value\": \"" + x + "\"},{\"name\": \"y\", \"value\": \"" + y + "\"}]}";
        String res = "{\"name\": \"respuesta\", \"priority\": \"0\",\"parameters\": [{\"name\": \"opcionElegida\", \"value\": \"" + x + "\"}]}";

        System.out.println(res);
        lobby.addAction(session, res);
        return "okey";
    }

    @GET
    @Path("/getFullState")
    public String getFullState() throws InterruptedException {
        lobby = Lobby.startGame();
        String state = "error";
        try {
            state = lobby.getFullState();

        } catch (InterruptedException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return state;
    }

    @GET
    @Path("/getFullStaticState")
    public String getFullStaticState() {
        lobby = Lobby.startGame();
        String state = "error";
        try {
            state = lobby.getStaticState();

        } catch (InterruptedException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return state;
    }

    @GET
    @Path("/getState")
    public String getState() {
        lobby = Lobby.startGame();
        String state = "error";
        try {
            state = lobby.getState();

        } catch (InterruptedException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return state;
    }

}
