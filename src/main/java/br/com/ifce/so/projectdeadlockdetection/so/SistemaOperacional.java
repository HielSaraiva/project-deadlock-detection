package br.com.ifce.so.projectdeadlockdetection.so;

import br.com.ifce.so.projectdeadlockdetection.models.GerenciadorRecursos;

public class SistemaOperacional extends Thread {
    private final Long deltaT;
    private final GerenciadorRecursos gerenciador;

    public SistemaOperacional(Long deltaT, GerenciadorRecursos gerenciador) {
        this.deltaT = deltaT;
        this.gerenciador = gerenciador;
        setDaemon(true);
    }

    @Override
    public void run() {
        Long tempo = 0L;
        try {
            while (true) {

                if (tempo % deltaT == 0 && tempo != 0L) {
                    gerenciador.verificarDeadlock();
                }

                // Simula o processamento do Sistema Operacional
                sleep(1000);
                tempo++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
