package br.com.ifce.so.projectdeadlockdetection.models;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class GerenciadorRecursos {
    private final Object mutex;
    private int[] E; // Recursos existentes
    private Semaphore[] A; // Recursos disponíveis
    private int[][] C; // Matriz de alocação
    private int[][] R; // Matriz de requisição
    private Recurso[] recursos;

    public GerenciadorRecursos(Recurso[] recursos) {
        this.recursos = recursos;
        mutex = new Object();

        E = new int[10];
        A = new Semaphore[10];
        for (int i = 0; i < 10; i++) {
            if (recursos[i] != null) {
                E[i] = recursos[i].getQuantidadeInstancias();
                A[i] = new Semaphore(E[i]);
            }
        }

        C = new int[10][10];
        R = new int[10][10];
    }

    public void verificarDeadlock() {
        synchronized (mutex) {
            
        }
    }

    public Recurso solicitarRecurso(Integer processoId) { // <- AQUI
        int indiceRecurso = -1;
        Recurso recursoSolicitado = null;

        synchronized (mutex) {

            int recursosValidos = 0;
            for (int i = 0; i < recursos.length; i++) {
                if (recursos[i] != null) {
                    recursosValidos++;
                }
            }

            int numeroAleatorio = (int) (Math.random() * recursosValidos);

            int contador = 0;
            for (int i = 0; i < recursos.length; i++) {
                if (recursos[i] != null) {
                    if (contador == numeroAleatorio) {
                        indiceRecurso = i;
                        recursoSolicitado = recursos[i];
                        break;
                    }
                    contador++;
                }
            }

            R[processoId - 1][indiceRecurso]++;
            System.out.println("Processo " + processoId + " solicitou recurso " + recursoSolicitado.getNome());
        }

        try {
            A[indiceRecurso].acquire();

            synchronized (mutex) {
                R[processoId - 1][indiceRecurso]--;
                C[processoId - 1][indiceRecurso]++;
                System.out.println("Processo " + processoId + " alocou recurso " + recursoSolicitado.getNome());
            }

            return recursoSolicitado;

        } catch (Exception e) {
            synchronized (mutex) {
                R[processoId - 1][indiceRecurso]--;
            }
            return null;
        }
    }

    public void liberarRecurso(ArrayList<RecursoAlocado> paraLiberar, Integer processoId) {
        synchronized (mutex) {
            for (RecursoAlocado recursoAlocado : paraLiberar) {
                Recurso recurso = recursoAlocado.recurso();

                int indiceRecurso = -1;
                for (int i = 0; i < recursos.length; i++) {
                    if (recursos[i] != null && recursos[i].getId().equals(recurso.getId())) {
                        indiceRecurso = i;
                        break;
                    }
                }

                C[processoId - 1][indiceRecurso]--;
                System.out.println("Processo " + processoId + " liberou recurso " + recurso.getNome());
                A[indiceRecurso].release();
            }
        }
    }
}
