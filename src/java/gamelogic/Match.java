package gamelogic;

import engine.Action;
import engine.State;
import engine.StaticState;
import java.util.HashMap;
import java.util.LinkedList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

// Esta clase no la estamos usando en ningun lado
public class Match extends State {

    protected LinkedList<String> players;
    protected LinkedList<String> playingPlayers;

    public Match(String id, LinkedList<String> players, LinkedList<String> playingPlayers, boolean destroy) 
    {
        super("Match", destroy, id);
        this.players = players;
        this.playingPlayers = playingPlayers;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        hasChanged = false;
        LinkedList<String> newPlayers = (LinkedList<String>) players.clone();
        LinkedList<String> newPlayingPlayers = (LinkedList<String>) playingPlayers.clone();
        for (java.util.Map.Entry<String, LinkedList<Action>> actionEntry : actions.entrySet()) {
            String id = actionEntry.getKey();
            LinkedList<Action> action = actionEntry.getValue();
            for(Action act:action)
            {
            hasChanged = true;
            switch (act.getName()) {
                case "enter":
                    newPlayers.add(id);
                    break;
                case "leave":
                    newPlayers.remove(id);
                    newPlayingPlayers.remove(id);
                    //newReady.remove(id);
                    break;
            }
            }
        }
        return null;
    }

    @Override
    public void setState(State newMatch) {
        super.setState(newMatch);
        
        players = ((Match) newMatch).players;
        playingPlayers = ((Match) newMatch).playingPlayers;
    }

    @Override
    protected Object clone() {
        Match clon = new Match(id,players, playingPlayers, destroy);
        return clon;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonMatch = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("super", super.toJSON());
        

        JSONArray jsonPlayers = new JSONArray();
        for (String player : players) {
            jsonPlayers.add(player);
        }
        jsonAttrs.put("players", jsonPlayers);

        JSONArray jsonPlayingPlayers = new JSONArray();
        for (String playingPlayer : playingPlayers) {
            jsonPlayingPlayers.add(playingPlayer);
        }
        jsonAttrs.put("playingPlayers", jsonPlayingPlayers);

        

        jsonMatch.put("Match", jsonAttrs);
        return jsonMatch;
    }

}
