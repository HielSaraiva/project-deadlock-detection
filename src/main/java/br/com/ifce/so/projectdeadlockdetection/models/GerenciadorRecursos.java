package br.com.ifce.so.projectdeadlockdetection.models;

import br.com.ifce.so.projectdeadlockdetection.ui.controller.MainController;
import javafx.application.Platform;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

public class GerenciadorRecursos {
    private final Object mutex;
    public int[] E; // Recursos existentes
    public Semaphore[] A; // Recursos disponíveis
    public int[][] C; // Matriz de alocação
    public int[][] R; // Matriz de requisição
    private Recurso[] recursos;

    private MainController controller;

    public GerenciadorRecursos(Recurso[] recursos, MainController controller) {
        this.recursos = recursos;
        this.controller = controller;
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
            ArrayList<Integer> indicesValidos = new ArrayList<>();
            for (int i = 0; i < recursos.length; i++) {
                if (recursos[i] != null) {
                    indicesValidos.add(i);
                }
            }
            Collections.shuffle(indicesValidos);

            for (int idx : indicesValidos) {
                int totalSolicitado = R[processoId - 1][idx] + C[processoId - 1][idx];
                if (totalSolicitado < E[idx]) {
                    indiceRecurso = idx;
                    recursoSolicitado = recursos[idx];
                    break;
                }
            }

            if (recursoSolicitado == null) {
                System.out.println("Processo " + processoId + " já solicitou o máximo de instâncias de todos os recursos.");
                return null;
            }

            Platform.runLater(() -> controller.atualizarEstadoRecursos(E, getAArray(), C, R));
            R[processoId - 1][indiceRecurso]++;
            Platform.runLater(() -> controller.atualizarEstadoRecursos(E, getAArray(), C, R));
            System.out.println("Processo " + processoId + " solicitou recurso " + recursoSolicitado.getNome());
        }

        try {
            if (Thread.currentThread().isInterrupted()) {
                synchronized (mutex) {
                    R[processoId - 1][indiceRecurso]--;
                }
                Platform.runLater(() -> controller.atualizarEstadoRecursos(E, getAArray(), C, R));
                return null;
            }

            Platform.runLater(() -> controller.atualizarEstadoRecursos(E, getAArray(), C, R));
            A[indiceRecurso].acquire();
            Platform.runLater(() -> controller.atualizarEstadoRecursos(E, getAArray(), C, R));

            if (Thread.currentThread().isInterrupted()) {
                synchronized (mutex) {
                    R[processoId - 1][indiceRecurso]--;
//                    C[processoId - 1][indiceRecurso]++;
                }
                Platform.runLater(() -> controller.atualizarEstadoRecursos(E, getAArray(), C, R));
                A[indiceRecurso].release();
                return null;
            }

            Platform.runLater(() -> controller.atualizarEstadoRecursos(E, getAArray(), C, R));
            synchronized (mutex) {
                R[processoId - 1][indiceRecurso]--;
                C[processoId - 1][indiceRecurso]++;
                System.out.println("Processo " + processoId + " alocou recurso " + recursoSolicitado.getNome());
            }

            Platform.runLater(() -> controller.atualizarEstadoRecursos(E, getAArray(), C, R));
            return recursoSolicitado;

        } catch (InterruptedException e) {
            synchronized (mutex) {
                R[processoId - 1][indiceRecurso]--;
                Platform.runLater(() -> controller.atualizarEstadoRecursos(E, getAArray(), C, R));
            }
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            synchronized (mutex) {
                R[processoId - 1][indiceRecurso]--;
                Platform.runLater(() -> controller.atualizarEstadoRecursos(E, getAArray(), C, R));
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
            Platform.runLater(() -> controller.atualizarEstadoRecursos(E, getAArray(), C, R));
        }
    }

    public void zerarRequisicoes(int idProcesso) {
        synchronized (mutex) {
            for (int j = 0; j < 10; j++) {
                R[idProcesso - 1][j] = 0;
            }
            Platform.runLater(() -> controller.atualizarEstadoRecursos(E, getAArray(), C, R));
        }
    }

    public void zerarAlocacoes(int idProcesso) {
        synchronized (mutex) {
            for (int j = 0; j < 10; j++) {
                C[idProcesso - 1][j] = 0;
            }
            Platform.runLater(() -> controller.atualizarEstadoRecursos(E, getAArray(), C, R));
        }
    }

    public int[] getAArray() {
        int[] arr = new int[10];
        for (int i = 0; i < 10; i++) {
            arr[i] = (A[i] != null) ? A[i].availablePermits() : 0;
        }
        return arr;
    }
}
