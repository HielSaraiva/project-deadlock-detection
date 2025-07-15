package br.com.ifce.so.projectdeadlockdetection.so;

import br.com.ifce.so.projectdeadlockdetection.models.GerenciadorRecursos;
import br.com.ifce.so.projectdeadlockdetection.ui.controller.MainController;
import javafx.application.Platform;

public class SistemaOperacional extends Thread {
    private final Long deltaT;
    private final GerenciadorRecursos gerenciador;
    private MainController controller;

    public SistemaOperacional(Long deltaT, GerenciadorRecursos gerenciador, MainController controller) {
        this.deltaT = deltaT;
        this.gerenciador = gerenciador;
        this.controller = controller;
        setDaemon(true);
    }

    @Override
    public void run() {
        long tempoRestanteMs = deltaT * 1000;
        try {
            while (true) {
                long inicio = System.currentTimeMillis();
                for (long ms = tempoRestanteMs; ms >= 0; ms -= 10) {
                    double segundos = ms / 1000.0;
                    Platform.runLater(() -> controller.atualizarContador(segundos));
                    Thread.sleep(10);
                }
                var processosEmDeadlock = gerenciador.verificarDeadlock();
                Platform.runLater(() -> controller.atualizarDeadlock(processosEmDeadlock));
                tempoRestanteMs = deltaT * 1000;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        return "SistemaOperacional{" +
                "deltaT=" + deltaT +
                '}';
    }
}
