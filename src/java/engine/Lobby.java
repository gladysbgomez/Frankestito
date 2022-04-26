package engine;

public class Lobby {

    private Game game;

    private static Lobby lobby;

    private Lobby() {
        game = new Game(this);
        Thread threadGame = new Thread(game);
        threadGame.start();
    }

    public synchronized static Lobby startGame() 
    {
        if (lobby == null) {
            lobby = new Lobby();
        }
        return lobby;
    }

    public synchronized void addAction(String sessionId, String action) {
        game.addAction(sessionId, action);
    }
    
    public synchronized void addPlayer(String sessionId) {
        game.addPlayer(sessionId);
    }
    
    public synchronized void removePlayer(String sessionId) {
        game.removePlayer(sessionId);
    }

    public synchronized void stateReady() {
        notifyAll();
    }

    public synchronized String getState() throws InterruptedException 
    {//Devuelve todos los jugadores que existen
        wait();
        return game.getGameState();
    }
    
    public synchronized String getState(String sessionId) throws InterruptedException 
    {//Devuelve el jugador que coreesponde al identificador sessionId
        wait();
        return game.getGameState(sessionId);
    }

    public synchronized String getFullState() throws InterruptedException {
        wait();
        return game.getGameFullState();
    }
    
    public synchronized /*State*/String get_jugador(String id) throws InterruptedException {
        wait();
        return game.get_jugador(id);
    }

    public synchronized String getStaticState() throws InterruptedException {
        wait();
        return game.getGameStaticState();
    }
    
    public synchronized String getCuanto_color(String c, String session) throws InterruptedException
    {
        wait();
        
        return game.getCuanto_color(c, session);
    }
    
    public synchronized String getBuscar_color(String c, String session) throws InterruptedException
    {
        wait();
        
        return game.getBuscar_color(c, session);
    }

    public boolean isEndGame() {
        return game.isEndGame();
    }

}
