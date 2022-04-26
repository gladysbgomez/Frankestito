/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamelogic;

import engine.Action;
import engine.State;
import engine.StaticState;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import org.json.simple.JSONObject;

/**
 *
 * @author emiliano
 */
public class Moneda extends Entity {
    protected int worldWidth;
    protected int worldHeight;

    public Moneda(String name, boolean destroy, String id, double x, double y, double velocidadX, double velocidadY, int worldWidth, int worldHeight) {
        super("Moneda", destroy, id, x, y, 0, 0, 32*(32/((worldHeight)*0.1)), 32*(32/((worldHeight)*0.1)),"");
        this.worldWidth=worldWidth;
        this.worldHeight=worldHeight;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates,
            HashMap<String, LinkedList<Action>> actions) {

        for (State state : states) {
            if (state.getName().equals("NavePlayer") && !((NavePlayer) state).dead) {
                NavePlayer player = ((NavePlayer) state);
                double dist = Math.sqrt((player.x - this.x) * (player.x - this.x) + (player.y - this.y) * (player.y - this.y));
                if (dist <= (this.width / 2 + player.width / 2) || dist <= (this.height / 2 + player.height / 2)) { // Esto va a cambiar segun si terminamos usando una libreria fisica
                    System.out.println("COLISION"); // Hasta aca llega bien
                    state.addEvent("collect");
                    this.addEvent("collide");
                }
            }
        }

        return null;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates,
            HashMap<String, LinkedList<Action>> actions) {
        boolean destruido = false;
        double nuevoX = x;
        double nuevoY = y;
        //int widthWorld = 800; // Esto estaria bueno tenerlo en la clase World y despues poder referenciarlo
        //int heightWorld = 600;
        hasChanged = true;
        LinkedList<String> events = getEvents();
        if (!events.isEmpty()) {
            
            for (String event : events) {
                switch (event) {
                    case "collide":
                        //destruido = true; // En vez de destruirlo, podemos reubicarlo (similar a las naves)
                        Random r = new Random();
                        nuevoX = r.nextInt(worldWidth);
                        nuevoY = r.nextInt(worldHeight);
                        break;
                }
            }
        }
        Moneda newMoneda = new Moneda(name, destruido, id, nuevoX, nuevoY, velocidad.x, velocidad.y, worldWidth, worldHeight);
        return newMoneda;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jMoneda = new JSONObject();
        JSONObject atributo = new JSONObject();

        atributo.put("super", super.toJSON());
        jMoneda.put("Moneda", atributo);

        return jMoneda;
    }

}
