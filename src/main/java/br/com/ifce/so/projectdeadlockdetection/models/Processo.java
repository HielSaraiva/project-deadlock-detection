package br.com.ifce.so.projectdeadlockdetection.models;

import java.util.ArrayList;

public class Processo extends Thread {
    private final Integer id; // [1, 10]
    private final Long deltaTs;
    private final Long deltaTu;
    private final GerenciadorRecursos gerenciador;

    private ArrayList<RecursoAlocado> recursosAlocados;

    public Processo(Integer id, Long deltaTs, Long deltaTu, GerenciadorRecursos gerenciador) {
        this.id = id;
        this.deltaTs = deltaTs;
        this.deltaTu = deltaTu;
        this.gerenciador = gerenciador;
        this.recursosAlocados = new ArrayList<>();
        setDaemon(true);
    }

    public ArrayList<RecursoAlocado> getRecursosAlocados() {
        return recursosAlocados;
    }

    private ArrayList<RecursoAlocado> recursosParaLiberar(Long tempoAtual) {
        ArrayList<RecursoAlocado> paraLiberar = new ArrayList<>();
        for (RecursoAlocado r : recursosAlocados) {
            if (r.tempoAlocacao() + deltaTu == tempoAtual) {
                paraLiberar.add(r);
            }
        }
        return paraLiberar.isEmpty() ? null : paraLiberar;
    }

    @Override
    public void run() {
        Long tempo = 0L;
        try {
            while (!Thread.interrupted()) {

                ArrayList<RecursoAlocado> paraLiberar = recursosParaLiberar(tempo);

                if (paraLiberar != null && tempo != 0L) {
                    // Libera recurso
                    gerenciador.liberarRecurso(paraLiberar, id);
                    recursosAlocados.removeAll(paraLiberar);
                }

                if (tempo % deltaTs == 0 && tempo != 0L) {
                    // Solicita recurso
                    Recurso recursoSolicitado = gerenciador.solicitarRecurso(id);

                    if (recursoSolicitado != null) {
                        recursosAlocados.add(new RecursoAlocado(recursoSolicitado, tempo));
                    }
                }

                // Simula o processamento do processo
                sleep(1000);
                tempo++;
            }
        } catch (InterruptedException e) {
            // Thread foi interrompida durante o sleep
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Libera recursos restantes ao encerrar
            if (!recursosAlocados.isEmpty()) {
                gerenciador.liberarRecurso(new ArrayList<>(recursosAlocados), id);
                recursosAlocados.clear();
            }
            // Zera as matrizes do processo
            gerenciador.zerarRequisicoes(id);
            gerenciador.zerarAlocacoes(id);
        }
    }

    public Long getDeltaTs() {
        return deltaTs;
    }

    public Long getDeltaTu() {
        return deltaTu;
    }

    public Integer getIdP() {
        return id;
    }

    @Override
    public String toString() {
        return "Processo{" +
                "id=" + id +
                ", deltaTs=" + deltaTs +
                ", deltaTu=" + deltaTu +
                '}';
    }
}
