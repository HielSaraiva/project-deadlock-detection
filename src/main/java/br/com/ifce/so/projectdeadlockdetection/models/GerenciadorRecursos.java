package br.com.ifce.so.projectdeadlockdetection.models;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class GerenciadorRecursos {
    private Integer[] E; // Recursos existentes
    private Semaphore[] A; // Recursos disponíveis
    private Integer[][] C; // Matriz de alocação
    private Integer[][] R; // Matriz de requisição

    private ArrayList<Recurso> recursos;

    public GerenciadorRecursos(ArrayList<Recurso> recursos) {
        this.recursos = recursos;

        E = new Integer[recursos.size()];
        A = new Semaphore[recursos.size()];
        for (int i = 0; i < recursos.size(); i++) {
            E[i] = recursos.get(i).getQuantidadeInstancias();
            A[i] = new Semaphore(E[i]);
        }

        C = new Integer[10][recursos.size()];
        R = new Integer[10][recursos.size()];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < recursos.size(); j++) {
                C[i][j] = 0;
                R[i][j] = 0;
            }
        }
    }

    public synchronized void verificarDeadlock() {

    }

    public synchronized Recurso solicitarRecurso() {
        return null;
    }

    public synchronized void liberarRecurso(ArrayList<RecursoAlocado> paraLiberar) {

    }
}
