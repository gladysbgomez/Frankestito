/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamelogic;

import java.util.HashMap;
import java.util.LinkedList;

import engine.Action;
import engine.State;
import engine.StaticState;
import org.json.simple.JSONObject;

import org.dyn4j.geometry.Vector2;

/**
 *
 * @author emiliano
 */
public class Asteroide extends Entity {//Ball.java
    protected int worldWidth;
    protected int worldHeight;
    protected double LONGITUD_MAX = 25;
    protected double FUERZA_CTE = 5;//HACER VARIABLE SU VALOR DE ACUERDO A LA VELOCIDAD DEL ROBOT
    
    //atributos agregados para resolver cuanto_color(c)
    protected Vector2 direccion;
    protected double angulo;

    public Asteroide(String name, boolean destroy, String id, double x, double y, double velocidadX, double velocidadY, int worldWidth,int worldHeight, 
                     String color/*, double xDir, double yDir*/) {
        super(name, false, id, x, y, velocidadX, velocidadY, 32*(32/((worldWidth)*0.1)), 32*(32/((worldWidth)*0.1)), color);
        this.worldWidth=worldWidth;
        this.worldHeight=worldHeight;
        
        //this.direccion = new Vector2(xDir,yDir);
        //this.angulo = direccion.getDirection()*(180/Math.PI);//convierte el vector en radianes a grados
    }   

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates,
            HashMap<String, LinkedList<Action>> actions) {

        for (State state : states) {
            if (state.getName().equals("NavePlayer") && !((NavePlayer) state).dead) {
                NavePlayer player = ((NavePlayer) state);
                double dist = Math.sqrt((player.x - this.x) * (player.x - this.x) + (player.y - this.y) * (player.y - this.y));
                if (dist <= (this.width / 2 + player.width / 2) || dist <= (this.height / 2 + player.height / 2)) { // Esto va a cambiar segun si terminamos usando una libreria fisica
                    //System.out.println("COLISION"); // Hasta aca llega bien
                    //state.addEvent("hit"); // Si la nave no muere entonces deberia ser un collide
                    
                    this.velocidad = player.direccion.add(player.velocidad);//el asteroide directamente toma la dirección y velocidad del jugador que lo choca
                    //this.FUERZA_CTE = player.velocidad.y+ player.velocidad.x;
                    this.velocidad.setMagnitude(LONGITUD_MAX);
                    //luego este valor se suma a la posición que ya tiene el asteroide
                    //this.velocidad.add(player.direccion.add(player.velocidad));//no siempre funciona
                    this.addEvent("collide");//para que el asteroide modifique su posición con su nuevo vector de velocidad
                    
                }
            } else if (state.getName().equals("Proyectil")) {
                Proyectil proyectil = ((Proyectil) state);
                double dist = Math.sqrt((proyectil.x - this.x) * (proyectil.x - this.x) + (proyectil.y - this.y) * (proyectil.y - this.y));
                if (dist <= (this.width / 2 + proyectil.width / 2) || dist <= (this.height / 2 + proyectil.height / 2)) { // Esto va a cambiar segun si terminamos usando una libreria fisica
                    state.addEvent("hit");
                    //this.addEvent("collide"); // No se que tan importante es agregar el collide en el asteroide
                }
            }
        }

        return null;
    }
    
    public double getAngulo() {
        return angulo;
    }

    public void setAngulo(double a) {
        this.angulo = a;
    }
    
    public int getWorldWidth() {
        return worldWidth;
    }
    
    public int getWorldHeight() {
        return worldHeight;
    }
    
    /* next original
    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates,
            HashMap<String, LinkedList<Action>> actions) {
        hasChanged = true; // El asteroide siempre cambia porque siempre esta en movimiento
        
        //recalcular las posiciones de x e y del asteroide según 
        //LinkedList<Action> listAccion = actions.get(id);
        //ya que esta clase se transformaría en Pelota, 
        //por lo que puede recibir la acción "movete en la misma dirección del robot que te chocó"
        //ccopiar de NavePlayer
        
        double nuevoX = x + velocidad.x;
        double nuevoY = y + velocidad.y;
        // La velocidad es constante 
        if (nuevoX > worldWidth) {
            nuevoX = 0;
        }
        Asteroide newAsteroide = new Asteroide(name, false, id, nuevoX, nuevoY, velocidad.x, velocidad.y,worldWidth,worldHeight);
        return newAsteroide;
    }*/

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates,
            HashMap<String, LinkedList<Action>> actions) {
        hasChanged = false; // El asteroide no cambia a menos que lo choquen
        double nuevoX = x;
        double nuevoY = y;
        double nuevaVelX = velocidad.x;
        double nuevaVelY = velocidad.y;
                
        LinkedList<String> eventos = getEvents();//recupero las posibles colisiones que tuvo el asteroide para recalcular su posición
        
        if (!eventos.isEmpty())
        {//hubo al menos un choque
            hasChanged = true;
            for (String evento : eventos)
            {
                switch (evento)
                {
                    //hay colisión
                    //verificar rebote con bordes
                    //restar a la fuerza en cada vuelta
                    case "collide":
                            nuevoX += nuevaVelX * FUERZA_CTE;
                            nuevoY += nuevaVelY * FUERZA_CTE;
                        break;
                }       
                
                if(FUERZA_CTE>0)
                {//SE REDUCE LA FUERZA EN CADA ITERACION
                    FUERZA_CTE = FUERZA_CTE-1;
                }
                else
                {
                    FUERZA_CTE = 5;//se vuelve a su valor inicial
                }
            }
        }
        /*
        if (nuevoX > worldWidth) {
            nuevoX = 0;
        }*/
        
        //rebote con paredes: con fuerza mayor al golpe del robot
        //
        if(nuevoX>worldWidth){
            nuevoX = worldWidth - 2*FUERZA_CTE;
        }
        else
        {
            if(nuevoX<=0)
            {
                nuevoX = 2*FUERZA_CTE;
            }               
        }
        
        if(nuevoY>worldHeight){
            nuevoY = worldHeight - 2*FUERZA_CTE;
        }
        else
        {
            if(nuevoY<=0)
            {
                nuevoY = 2*FUERZA_CTE;
            } 
        }    
        
        Asteroide newAsteroide = new Asteroide(name, false, id, nuevoX, nuevoY, nuevaVelX, nuevaVelY,worldWidth,worldHeight, 
                                                color/*, direccion.x,direccion.y*/);
        return newAsteroide;
    }    

    @Override
    public JSONObject toJSON() {
        JSONObject jAsteroide = new JSONObject();
        JSONObject atributo = new JSONObject();

        atributo.put("super", super.toJSON());
        jAsteroide.put("Asteroide", atributo);

        return jAsteroide;
    }
}
