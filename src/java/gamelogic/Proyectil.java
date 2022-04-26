package gamelogic;

import engine.Action;
import engine.State;
import engine.StaticState;
import java.util.HashMap;
import java.util.LinkedList;
import org.dyn4j.geometry.Vector2;
import org.json.simple.JSONObject;

public class Proyectil extends Entity {

    protected int number;
    protected String idPlayer;
    protected Vector2 direccion;
    protected double angulo;
    protected double VELOCIDAD_MAX = 25;
    protected int worldWidth;
    protected int worldHeight;

    public Proyectil(String name, boolean destroy, String id, String idPlayer, double x, double y, double velocidadX, double velocidadY, double xDir, double yDir, double angulo, int number,int worldWidth,int worldHeight) {
        super("Proyectil", destroy, id, x, y, velocidadX, velocidadY, 12*(12/((worldWidth)*0.2)), 64*(64/((worldHeight)*0.2)),"");
        this.velocidad.set(xDir, yDir).setMagnitude(VELOCIDAD_MAX);
        this.number = number;
        this.idPlayer = idPlayer;
        this.angulo = angulo;
        this.direccion = new Vector2(xDir, yDir);
        this.worldWidth=worldWidth;
        this.worldHeight=worldHeight;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        for (State state : states) {
            if (state.getName().equals("NavePlayer") && !((NavePlayer) state).dead) {
                NavePlayer player = ((NavePlayer) state);
                double dist = Math.sqrt((player.x - this.x) * (player.x - this.x) + (player.y - this.y) * (player.y - this.y));
                if (player.id != this.idPlayer) {
                    if (dist <= (this.width / 2 + player.width / 2) || dist <= (this.height / 2 + player.height / 2)) {
                        state.addEvent("hit");
                        this.addEvent("hit");
                    }
                }
            }
        }
        return null;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        hasChanged = true;
        double nuevoX = x + direccion.x + velocidad.x;
        double nuevoY = y + direccion.y + velocidad.y;
        boolean destruido = destroy;
        int width = 4500; // Esto estaria bueno tenerlo en la clase World y despues poder referenciarlo
        int height = 2048;
        if (x > width || y > height || y < 0 || x < 0) {
            destruido = true;
        } else {
            //falta considerar que es un mundo de 360°
            LinkedList<String> events = getEvents();
            if (!events.isEmpty()) {
                hasChanged = true;
                for (String event : events) {
                    switch (event) {
                        case "hit":
                            destruido = true;
                            break;
                    }
                }
            }
        }
        Proyectil newArrow = new Proyectil(name, destruido, id, idPlayer, nuevoX, nuevoY, velocidad.x, velocidad.y, direccion.x, direccion.y, angulo, number,worldWidth,worldHeight);
        return newArrow;
    }

    @Override
    public void setState(State newProyectil) {
        super.setState(newProyectil);
        idPlayer = ((Proyectil) newProyectil).idPlayer;
        number = ((Proyectil) newProyectil).number;
        angulo = ((Proyectil) newProyectil).angulo;
    }

    @Override
    protected Object clone() {
        Proyectil clon = new Proyectil(name, destroy, id, idPlayer, x, y, velocidad.x, velocidad.y, direccion.x, direccion.y, angulo, number,worldWidth,worldHeight);
        return clon;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jProyectil = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("super", super.toJSON());
        jsonAttrs.put("number", number);
        jsonAttrs.put("angulo", angulo);
        jsonAttrs.put("idPlayer", idPlayer);
        jsonAttrs.put("xDir", direccion.x);
        jsonAttrs.put("yDir", direccion.y);

        jProyectil.put("Proyectil", jsonAttrs);
        return jProyectil;
    }

}
