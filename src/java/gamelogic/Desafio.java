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
 * @author PC
 */
public class Desafio extends State {

    public String idNaveNeutra;
    public String idNavePlayer;
    public String pregunta;
    public String[] opciones;
    public int correcta;
    protected int worldWidth;
    protected int worldHeight;

    public Desafio(String name, boolean destroy, String id, String idNeutra, String idPlayer, int worldWidth,int worldHeight) {
        super(name, destroy, id);   
        this.idNaveNeutra = idNeutra;
        this.idNavePlayer = idPlayer;
        this.opciones = new String[] { "" , "" , "" };
        this.worldWidth=worldWidth;
        this.worldHeight=worldHeight;

        Random r = new Random();
        int numeroPregunta1 = r.nextInt(100);
        int numeroPregunta2 = r.nextInt(100);
        int respuestaCorrecta = numeroPregunta1 + numeroPregunta2;
        int posicionCorrecta = r.nextInt(3);
        opciones[posicionCorrecta] = "" + respuestaCorrecta;
        for (int i = 0; i < opciones.length; i++) {
            if (!opciones[i].equalsIgnoreCase("" + respuestaCorrecta)) {
                int signo = r.nextDouble() > 0.5 ? 1 : -1;
                opciones[i] = "" + (respuestaCorrecta + ((r.nextInt(5) + 1)* signo));
            }
        }

        this.pregunta = numeroPregunta1 + "+" + numeroPregunta2;
        this.correcta = posicionCorrecta;
    }

    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        LinkedList<State> neutras = new LinkedList();
        LinkedList<Action> listAccion = actions.get(this.idNavePlayer);
        NavePlayer jugador = null;
        NaveNeutra neutra = null;
        if (listAccion != null) {
            for (Action accion : listAccion) {

                if (accion != null) {
                    switch (accion.getName()) {
                        case "respuesta":

                            int res = Integer.parseInt(accion.getParameter("opcionElegida"));
                            for (State estado : states) {
                                if (estado != null && estado.id.equalsIgnoreCase(this.idNavePlayer)) {
                                    jugador = (NavePlayer) estado;
                                } else {
                                    if (estado != null && estado.id.equalsIgnoreCase(this.idNaveNeutra)) {
                                        neutra = (NaveNeutra) estado;
                                    }
                                }

                            }

                            if (res == correcta) {

                                neutra.addEvent("destruir"); // Por que destruir la neutra? 

                                neutras.add(new NaveNeutra(neutra.name, neutra.destroy, this.idNaveNeutra, neutra.x, neutra.y, neutra.velocidad.x, neutra.velocidad.y,
                                        neutra.direccion.x, neutra.direccion.y, neutra.countProyectil, true, this.idNavePlayer, neutra.idBullets,worldWidth,worldHeight));
                            } else {
                                jugador.addEvent("incorrecta");
                                neutra.addEvent("destruir");

                                neutras.add(new NaveNeutra(neutra.name, neutra.destroy, this.idNaveNeutra, neutra.x, neutra.y, neutra.velocidad.x, neutra.velocidad.y,
                                        neutra.direccion.x, neutra.direccion.y, neutra.countProyectil, true, "", neutra.idBullets,worldWidth,worldHeight));
                            }
                            jugador.addEvent("liberar");
                            this.addEvent("destruir");
                            break;
                    }

                }
            }
        }
        return neutras;
    }

    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        //TODO in concrete class
        hasChanged = false;
        boolean nuevoDes = this.destroy;
        LinkedList<String> eventos = getEvents();

        if (!eventos.isEmpty()) {
            hasChanged = true;

            for (String evento : eventos) {
                switch (evento) {
                    case "destruir":
                        nuevoDes = true;

                        break;
                }
            }
        }
        Desafio nuevoDesafio = new Desafio(name, nuevoDes, id, this.idNaveNeutra, this.idNavePlayer,worldWidth,worldHeight);

        return nuevoDesafio;
    }

    public void setState(State desafio) {
        super.setState(desafio);
        this.idNaveNeutra = ((Desafio) desafio).idNaveNeutra;
        this.idNavePlayer = ((Desafio) desafio).idNavePlayer;

    }

    @Override
    public JSONObject toJSON() {
        JSONObject jDesafio = new JSONObject();
        JSONObject atributo = new JSONObject();
        JSONObject opciones = new JSONObject();

        for (int i = 0; i < this.opciones.length; i++) {
            opciones.put("opcion" + i, this.opciones[i]);
        }

        atributo.put("super", super.toJSON());
        atributo.put("idNaveNeutra", idNaveNeutra);
        atributo.put("idNavePlayer", idNavePlayer);
        atributo.put("pregunta", pregunta);
        atributo.put("opciones", opciones);
        atributo.put("correcta", correcta);

        jDesafio.put("Desafio", atributo);

        return jDesafio;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }

}
