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
import org.dyn4j.geometry.Vector2;

import org.json.simple.JSONObject;

/**
 *
 * @author karen
 */
public abstract class Nave extends Entity {

    protected int countProyectil;
    protected Vector2 direccion;
    protected double angulo;

    //depende de como tratemos la orientacion puede no ser un int
    public Nave(String name,boolean destroy ,String id, double x, double y, double velocidadX, double velocidadY,double xDir, double yDir, int cantProj,
                double naveWidth, double naveHeight, String color) {
        super(name, destroy, id, x, y, velocidadX, velocidadY, naveWidth, naveHeight, color);
        this.countProyectil = cantProj;
        this.direccion = new Vector2(xDir,yDir);
        this.angulo = direccion.getDirection()*(180/Math.PI);//convierte el vector en radianes a grados   

    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        return null;
    }

    public Nave next(LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        hasChanged = false;
        return this;
    }

    public int getCountProyectil() {
        return countProyectil;
    }

    public void setCountProyectil(int countProyectil) {
        this.countProyectil = countProyectil;
    }
    
    public double getAngulo() {
        return angulo;
    }
    
    public void setAngulo(double un_angulo) {
        this.angulo = un_angulo;
    }
    
    public Vector2 getDireccion() {
        return direccion;
    }

    @Override
    public void setState(State newNave) {
        super.setState(newNave);
        direccion.x = ((Nave) newNave).direccion.x;
        direccion.y = ((Nave) newNave).direccion.y;
        countProyectil =((Nave) newNave).countProyectil;
        angulo = ((Nave) newNave).angulo;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jNave = new JSONObject();
        JSONObject atributo = new JSONObject();

        atributo.put("super", super.toJSON());
        atributo.put("xDir",direccion.x); // Para ser consistentes deberiamos hacer un objecto 'direccion' y que tenga como atributos a 'x','y'
        atributo.put("yDir",direccion.y);
        atributo.put("angulo", angulo);
        atributo.put("countProyectil", countProyectil);
        jNave.put("Nave", atributo);

        return jNave;
    }
}
