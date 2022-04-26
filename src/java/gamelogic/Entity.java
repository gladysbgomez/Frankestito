package gamelogic;

import engine.Action;
import engine.State;
import engine.StaticState;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import org.dyn4j.geometry.Vector2;
import org.json.simple.JSONObject;

public class Entity extends State {

    protected double x; // Centro de la entidad
    protected double y; // Centro de la entidad
    protected double width; // Ancho (Necesario para la detección de colisiones - Deberia ser del mismo tamaño que el sprite del cliente visual)
    protected double height; // Alto
    protected Vector2 velocidad; // Todavia no hago uso de esto, capaz sirve para steering behaviour
    
    protected String color;//color del objeto que representa la entidad

    public Entity(String name, boolean destroy, String id, double x, double y, double velocidadX, double velocidadY, double width, double height, String color) {
        super(name, destroy, id == null ? UUID.randomUUID().toString() : id);
        this.x = x;
        this.y = y;
        velocidad = new Vector2(velocidadX, velocidadY);
        this.width = width;
        this.height = height;
        
        this.color = color;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {//int
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {//int
        this.y = y;
    }
    
    public String get_color() {
        return color;
    }

    public void set_color(String color) {
        this.color = color;
    }
    
    public Vector2 getVelocidad() {
        return velocidad;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        return null;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        hasChanged = false;
        Entity newEntity = new Entity(name, destroy, id, x, y, velocidad.x, velocidad.y, 0, 0, color);
        return newEntity;
    }

    @Override
    public void setState(State newEntity) {
        super.setState(newEntity);
        x = ((Entity) newEntity).x;
        y = ((Entity) newEntity).y;
        velocidad.x = ((Entity) newEntity).velocidad.x;
        velocidad.y = ((Entity) newEntity).velocidad.y;
        width = ((Entity) newEntity).width;
        height = ((Entity) newEntity).height;
        
        color = ((Entity) newEntity).color;
    }

    @Override
    protected Object clone() {
        Entity clon = new Entity(name, destroy, id, x, y, velocidad.x, velocidad.y, 0, 0, color);
        return clon;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("super", super.toJSON());
        jsonAttrs.put("x", x); // Para ser consistentes deberiamos hacer un objecto 'posicion' y que tenga como atributos a 'x','y'
        jsonAttrs.put("y", y);
        jsonAttrs.put("width", width);  // Para ser consistentes deberiamos hacer un objecto 'tamaño' y que tenga como atributos a 'width','height'
        jsonAttrs.put("height", height);
        jsonAttrs.put("velocidadX", velocidad.x); // Para ser consistentes deberiamos hacer un objecto 'velocidad' y que tenga como atributos a 'x','y'
        jsonAttrs.put("velocidadY", velocidad.y);
        
        jsonAttrs.put("color", color);
        
        jsonEntity.put("Entity", jsonAttrs);
        return jsonEntity;
    }

}
