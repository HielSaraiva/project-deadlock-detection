package br.com.ifce.so.projectdeadlockdetection.models;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
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

    public ArrayList<Integer> verificarDeadlock() {
        synchronized (mutex) {

            // Identificar processos ativos (que têm alocação ou requisição)
            boolean[] processoAtivo = new boolean[10];
            for (int i = 0; i < 10; i++) {
                processoAtivo[i] = false;
                for (int j = 0; j < 10; j++) {
                    if (recursos[j] != null && (C[i][j] > 0 || R[i][j] > 0)) {
                        processoAtivo[i] = true;
                        break;
                    }
                }
            }

            // Encontre o vetor de recursos disponíveis A
            int[] Work = new int[10]; // Cópia de A para simulação
            for (int i = 0; i < 10; i++) {
                if (recursos[i] != null) {
                    Work[i] = A[i].availablePermits();
                } else {
                    Work[i] = 0;
                }
            }

            // Vetor para marcar processos que podem terminar
            boolean[] Finish = new boolean[10];
            for (int i = 0; i < 10; i++) {
                Finish[i] = !processoAtivo[i]; // Processos inativos já estão "terminados"
            }

            // Algoritmo principal de detecção
            boolean mudanca = true;
            while (mudanca) {
                mudanca = false;
                for (int i = 0; i < 10; i++) {
                    if (processoAtivo[i] && !Finish[i]) {
                        boolean podeExecutar = true;
                        for (int j = 0; j < 10; j++) {
                            if (recursos[j] != null && R[i][j] > Work[j]) {
                                podeExecutar = false;
                                break;
                            }
                        }
                        if (podeExecutar) {
                            for (int j = 0; j < 10; j++) {
                                if (recursos[j] != null) {
                                    Work[j] += C[i][j];
                                }
                            }
                            Finish[i] = true;
                            mudanca = true;
                        }
                    }
                }
            }

            // Identificar processos em deadlock (apenas entre os ativos)
            ArrayList<Integer> processosEmDeadlock = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                if (processoAtivo[i] && !Finish[i]) {
                    processosEmDeadlock.add(i + 1); // IDs são de 1 a 10
                }
            }

            // Garantindo ordenação por id
            Collections.sort(processosEmDeadlock);

            // Log
            if (!processosEmDeadlock.isEmpty()) {
                System.out.println("DEADLOCK DETECTADO!");
                System.out.println("Processos em deadlock: " + processosEmDeadlock);
            } else {
                System.out.println("Sistema livre de deadlock");
            }

            return processosEmDeadlock;
        }
    }

    public Recurso solicitarRecurso(Integer processoId) {
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
            // Verifica se a thread foi interrompida antes de bloquear
            if (Thread.currentThread().isInterrupted()) {
                synchronized (mutex) {
                    R[processoId - 1][indiceRecurso]--;
                }
                return null;
            }

            A[indiceRecurso].acquire();

            // Verifica se a thread foi interrompida após adquirir o recurso
            if (Thread.currentThread().isInterrupted()) {
                synchronized (mutex) {
                    R[processoId - 1][indiceRecurso]--;
                    C[processoId - 1][indiceRecurso]++;
                }
                // Libera imediatamente o recurso
                A[indiceRecurso].release();
                return null;
            }

            synchronized (mutex) {
                R[processoId - 1][indiceRecurso]--;
                C[processoId - 1][indiceRecurso]++;
                System.out.println("Processo " + processoId + " alocou recurso " + recursoSolicitado.getNome());
            }

            return recursoSolicitado;

        } catch (InterruptedException e) {
            // Se for interrompido durante o acquire, desfaz a requisição
            synchronized (mutex) {
                R[processoId - 1][indiceRecurso]--;
            }
            Thread.currentThread().interrupt(); // Mantém o status de interrupção
            return null;
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

    public void zerarRequisicoes(int idProcesso) {
        synchronized (mutex) {
            for (int j = 0; j < 10; j++) {
                R[idProcesso - 1][j] = 0;
            }
        }
    }

    public void zerarAlocacoes(int idProcesso) {
        synchronized (mutex) {
            for (int j = 0; j < 10; j++) {
                C[idProcesso - 1][j] = 0;
            }
        }
    }
}
