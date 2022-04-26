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
import org.json.simple.JSONObject;
import org.dyn4j.geometry.Vector2;

public class NaveNeutra extends Nave {
    //si propietario es nulo es por que no fue reclutado

    //private String propietario; // Podria ser una NavePlayer tambien
    // public NavePlayer propietario;
    public String idPropietario;
    public boolean disponible;
    protected int idBullets;
    protected int worldWidth;
    protected int worldHeight;

    public NaveNeutra(String name, boolean destroy, String id, double x, double y, double velocidadX,
            double velocidadY, double xDir, double yDir, int cantProj,
            boolean disponible, String idPropietario, int idBullets, int worldWidth,int worldHeight) {

        super("NaveNeutra", destroy, id, x, y, velocidadX, velocidadY, xDir, yDir, cantProj,
            64*(64/((worldHeight)*0.09)),12*(12/((worldHeight)*0.09)),"");

        this.disponible = disponible;
        this.idPropietario = idPropietario;
        this.idBullets = idBullets;
        this.worldWidth=worldWidth;
        this.worldHeight=worldHeight;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones) {
        LinkedList<State> listProyectil = new LinkedList();
        if (disponible) {
            if (!this.idPropietario.equalsIgnoreCase("")) {
                //   System.out.println("TIENE PROPIETARIO  "+(idProp));
                LinkedList<Action> listAccion = acciones.get(this.idPropietario);
                if (listAccion != null) {
                    for (Action accion : listAccion) {
                        switch (accion.getName()) {
                            case "fire":
                                String idAux = id + "" + idBullets; // id de la bala
                                // Temporalmente, los proyectiles generados por la nave neutra tienen el mismo id del propietario (sino la bala cuenta como la de un enemigo)
                                Proyectil proyectil = new Proyectil("Proyectil", false, idAux, idPropietario, x, y, velocidad.x, velocidad.y, direccion.x, direccion.y, angulo, 0,worldWidth,worldHeight);
                                listProyectil.add(proyectil);
                                idBullets++;
                                break;
                        }
                    }
                }

            }
            
            for (State estado : estados) {
                if (estado != null && estado != this && estado.getName().equalsIgnoreCase("Desafio")) {
                    Desafio des = (Desafio) estado;
                    if (des.idNaveNeutra.equalsIgnoreCase(id)) {
                        this.addEvent("desafiado");

                    }
                }else if (estado != this && estado.getName().equalsIgnoreCase("liberar")) { 
                    this.addEvent("liberar");
                }
            }
        }
        return listProyectil;
    }

    @Override
    public NaveNeutra next(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones) {

        hasChanged = true;
        double nuevoX = x;
        double nuevoY = y;
        int nuevosProyectiles = countProyectil;
        boolean destruido = destroy;
        double nuevaVelX = velocidad.x;
        double nuevaVelY = velocidad.y;
        double nuevaDirX = direccion.x;
        double nuevaDirY = direccion.y;
        NavePlayer nuevoPropietario = null;
        String nuevoIdPropietario = this.idPropietario;
        //String nuevoPos = this.idPosP;
        boolean nuevaDisponibilidad = disponible;
        int resp;
        int nuevoIdBullets = this.idBullets;
        if (disponible) {
            for (State estado : estados) {
                if (estado.getId().equalsIgnoreCase(nuevoIdPropietario)) {
                    nuevoPropietario = (NavePlayer) estado;
                    //System.out.println(nuevoPropietario);
                }
            }

            if (nuevoPropietario != null && !nuevoPropietario.dead ) {
                LinkedList<Action> listAccion = acciones.get(nuevoPropietario.id); // Obtengo las acciones del propietario
                if (listAccion != null) {
                    for (Action accion : listAccion) {
                        if (accion != null) {
                            System.out.println(accion.getName());
                            hasChanged = true;
                            //System.out.println("has change");

                            switch (accion.getName()) {
                                case "move":
                                    if(!nuevoPropietario.bloqueado)
                                    {
                                    LinkedList<Nave> navesAliadas = new LinkedList();
                                    for (State estado : estados) {
                                        if (estado.getName().equalsIgnoreCase("NaveNeutra")) {
                                            NaveNeutra naveA = (NaveNeutra) estado;
                                            if (naveA.disponible && naveA.idPropietario.equalsIgnoreCase(nuevoIdPropietario)) {
                                                navesAliadas.add(naveA);
                                            }
                                        }
                                    }
                                    //this.flock(nuevoPropietario, navesAliadas);
                                    nuevaVelX = Double.parseDouble(accion.getParameter("x"));
                                    nuevaVelY = Double.parseDouble(accion.getParameter("y"));
                                    nuevaDirX = nuevaVelX;
                                    nuevaDirY = nuevaVelY;}
                                    break;
                                    
                                case "stop":

                                    nuevaVelX = 0;
                                    nuevaVelY = 0;
                                    break;
                                case "fire":
                                    nuevosProyectiles++;
                                    break;

                            }
                        }
                    }
                }
                nuevoX = nuevoX + nuevaVelX;
                nuevoY = nuevoY + nuevaVelY;
            } else if (nuevoPropietario != null && nuevoPropietario.dead ) {
                nuevoPropietario = null;
                nuevoIdPropietario = "";
                nuevaVelX = 0;
                nuevaVelY = 0;
                nuevaDirX = 0;
                nuevaDirY = 1;
                nuevaDisponibilidad = true;
            }else{
                if(nuevoPropietario == null){
                    nuevoPropietario = null;
                    nuevoIdPropietario = "";
                    nuevaDisponibilidad = true;
                    //System.out.println("sin propietario");
                }
            }

            //System.out.println("(velX,velY): " + nuevaVelX + "," + nuevaVelY);
        }
        LinkedList<String> eventos = getEvents();

        if (!eventos.isEmpty()) {
            hasChanged = true;

            //System.out.println(eventos);
            for (String evento : eventos) {
                switch (evento) {
                    case "desafiado":
                        nuevaDisponibilidad = false;
                        break;
                    case "liberar":
                        nuevaDisponibilidad = true; //no se esta usando
                        break;
                    case "destruir":
                        destruido = true;
                        break;
                }
            }
        }

        NaveNeutra nuevaNeutra = new NaveNeutra(name, destruido, id, nuevoX, nuevoY, nuevaVelX, nuevaVelY,
                nuevaDirX, nuevaDirY, nuevosProyectiles, nuevaDisponibilidad, nuevoIdPropietario, nuevoIdBullets,worldWidth,worldHeight);

        /* NaveNeutra nuevaNeutra = new NaveNeutra(name,destruido, id, nuevoX, nuevoY, nuevaVelX, 
                nuevaVelY,nuevaDirX,nuevaDirY, nuevosProyectiles, nuevoPropietario,nuevoPos,nuevoIdP);*/
        return nuevaNeutra;
    }

    public Vector2 flock(NavePlayer nuevoPropietario, LinkedList<Nave> navesAliadas) {

        navesAliadas.add(nuevoPropietario);
        Vector2 alignment = computeAlignment(this, navesAliadas).multiply(2); // De donde saco los vecinos?
        Vector2 cohesion = computeCohesion(this, navesAliadas).multiply(5);
        Vector2 separation = computeSeparation(this, navesAliadas);
        Vector2 aux = alignment.sum(cohesion.sum(separation));
        aux.setMagnitude(100);
        this.velocidad.x = aux.x;
        this.velocidad.y = aux.y;
        return aux;
    }

    public Vector2 computeAlignment(NaveNeutra miAgente, LinkedList<Nave> agentes) {

        Vector2 vectorAlignment = new Vector2(0, 0);
        int vecinos = 0;

        double xAgente = miAgente.x;
        double yAgente = miAgente.y;
        for (int i = 0; i < agentes.size(); i++) {
            Nave agente = agentes.get(i);
            if (agentes.get(i) != miAgente) {
                double xAgenteVecino = agente.x;
                double yAgenteVecino = agente.y;

                Vector2 vectorDistancia = new Vector2(xAgente, yAgente).subtract(new Vector2(xAgenteVecino, yAgenteVecino));
                double distanceToTarget = vectorDistancia.getMagnitude();

                if (distanceToTarget < 300) {
                    vectorAlignment.sum(agente.velocidad);
                    vecinos++;
                }
            }
        }

        if (vecinos == 0) {
            return vectorAlignment;
        }

        vectorAlignment.x /= vecinos;
        vectorAlignment.y /= vecinos;
        vectorAlignment.normalize();
        return vectorAlignment;
    }

    public Vector2 computeCohesion(NaveNeutra miAgente, LinkedList<Nave> agentes) {
        Vector2 vectorAlignment = new Vector2(0, 0);
        int vecinos = 0;
        double xAgente = miAgente.x;
        double yAgente = miAgente.y;
        for (int i = 0; i < agentes.size(); i++) {
            Nave agente = agentes.get(i);
            if (agente != miAgente) {
                double xAgenteVecino = agente.x;
                double yAgenteVecino = agente.y;
                Vector2 vectorDistancia = new Vector2(xAgente, yAgente).subtract(new Vector2(xAgenteVecino, yAgenteVecino));
                double distanceToTarget = vectorDistancia.getMagnitude();
                if (distanceToTarget < 300) {
                    vectorAlignment.x += xAgenteVecino;
                    vectorAlignment.y += yAgenteVecino;
                    vecinos++;
                }
            }
        }
        if (vecinos == 0) {
            return vectorAlignment;
        }
        vectorAlignment.x /= vecinos;
        vectorAlignment.y /= vecinos;
        vectorAlignment = new Vector2(
                vectorAlignment.x - xAgente,
                vectorAlignment.y - yAgente
        );
        vectorAlignment.normalize();
        return vectorAlignment;
    }

    public Vector2 computeSeparation(NaveNeutra miAgente, LinkedList<Nave> agentes) {
        Vector2 vectorAlignment = new Vector2(0, 0);
        int vecinos = 0;
        double xAgente = miAgente.x;
        double yAgente = miAgente.y;
        for (int i = 0; i < agentes.size(); i++) {
            Nave agente = agentes.get(i);
            if (agente != miAgente) {
                double xAgenteVecino = agente.x;
                double yAgenteVecino = agente.y;
                Vector2 vectorDistancia = new Vector2(xAgente, yAgente).subtract(new Vector2(xAgenteVecino, yAgenteVecino));
                double distanceToTarget = vectorDistancia.getMagnitude();
                if (distanceToTarget < 300) {
                    vectorAlignment.x += xAgenteVecino - xAgente;
                    vectorAlignment.y += yAgenteVecino - yAgente;
                    vecinos++;
                }
            }
        }
        if (vecinos == 0) {
            return vectorAlignment;
        }
        vectorAlignment.x /= vecinos;
        vectorAlignment.y /= vecinos;
        vectorAlignment.negate();
        vectorAlignment.normalize();
        return vectorAlignment;
    }

    @Override
    public void setState(State neutra) {
        super.setState(neutra);
        this.id = ((NaveNeutra) neutra).id;
        this.disponible = ((NaveNeutra) neutra).disponible;
        this.idPropietario = ((NaveNeutra) neutra).idPropietario;

        // this.countProyectil= ((NaveNeutra)neutra).countProyectil;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jNeutra = new JSONObject();
        JSONObject atributo = new JSONObject();

        atributo.put("super", super.toJSON());
        atributo.put("idPropietario", idPropietario);
        atributo.put("disponible", disponible);
        atributo.put("idBullets", idBullets);

        jNeutra.put("NaveNeutra", atributo);

        return jNeutra;
    }
}
