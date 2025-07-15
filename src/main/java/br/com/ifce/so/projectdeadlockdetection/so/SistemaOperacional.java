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
        Long contador = deltaT;
        try {
            while (true) {
                contador--; // Decrementa antes de mostrar
                final Long contadorFinal = contador;
                Platform.runLater(() -> controller.atualizarContador(contadorFinal));

                if (contador == 0) {
                    var processosEmDeadlock = gerenciador.verificarDeadlock();
                    Platform.runLater(() -> controller.atualizarDeadlock(processosEmDeadlock));
                    contador = deltaT;
                }

                sleep(1000);
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
