package engine;

import gamelogic.Asteroide;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import org.json.simple.JSONObject;
import gamelogic.NavePlayer;

public abstract class State {

    public String id;
    public String name;
    private LinkedList<State> record;
    private LinkedList<String> events;
    public boolean hasChanged;
    public boolean destroy;
    
    //public String color_;

    public State(String name, boolean destroy, String id) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.record = new LinkedList<>();
        this.events = new LinkedList<>();
        this.hasChanged = false;
        this.destroy = destroy;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        //TODO in concrete class
        return null;
    }

    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        //TODO in concrete class
        hasChanged = false;
        return this;
    }

    public void createState(State newState) {
        //record.add((State) this.clone());
        this.setState(newState);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    /*
    public String getColor() {
        return color_;
    }*/

    public State getState(int numState) {
        return record.get(numState);
    }

    public void setState(State newState) {
        id = newState.id;
        name = newState.name;
        destroy = newState.destroy;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }

    public JSONObject toJSON() {
        JSONObject jsonState = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("id", id);
        jsonAttrs.put("name", name);
        jsonAttrs.put("destroy", destroy);
        jsonState.put("State", jsonAttrs);
        return jsonState;
    }

    public JSONObject toJSON(String sessionId, LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions, JSONObject lastState) {
        NavePlayer player= getPlayer(sessionId, states);
        if(player==null){
            return lastState == null || hasChanged ? toJSON() : null;
        }
        return null;
    }

    protected NavePlayer getPlayer(String sessionId, LinkedList<State> states) {
        NavePlayer thePlayer = null;
        int i = 0;
        while (thePlayer == null && i < states.size()) {
            State state = states.get(i);
            if (state.getName().equals("NavePlayer") && ((NavePlayer) state).id.equals(sessionId)) {
                thePlayer = (NavePlayer) state;
            }
            i++;
        }
        return thePlayer;
    }
    
    protected LinkedList<Asteroide> getAsteroides(LinkedList<State> states)
    {
        LinkedList<Asteroide> lista = new LinkedList();
        
        Asteroide a = null;
        int i = 0;
        while (/*a == null &&*/ i < states.size()) {
            State state = states.get(i);
            if (state.getName().equals("Asteroide") ) {
                a = (Asteroide) state;
                lista.add(a);
            }
            i++;
        }
        
        return lista;
    }

    protected JSONObject toJSONRemover() {
        JSONObject jsonState = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("id", id);
        jsonState.put("Remove", jsonAttrs);
        return jsonState;
    }

    protected boolean isJSONRemover(JSONObject json) {
        return json.get("Remove") != null
                && ((JSONObject) json.get("Remove")).get("id") != null
                && ((JSONObject) json.get("Remove")).get("id").equals(id);
    }

    @Override
    protected Object clone() {
        //TODO in concrete class
        return null;
    }

    public void addEvent(String event) {
        events.add(event);
    }

    public LinkedList<String> getEvents() {
        return events;
    }

    public void clearEvents() {
        events.clear();
    }

}
