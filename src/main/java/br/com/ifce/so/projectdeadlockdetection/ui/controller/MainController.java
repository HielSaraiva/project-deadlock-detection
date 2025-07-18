package br.com.ifce.so.projectdeadlockdetection.ui.controller;

import br.com.ifce.so.projectdeadlockdetection.models.GerenciadorRecursos;
import br.com.ifce.so.projectdeadlockdetection.models.Processo;
import br.com.ifce.so.projectdeadlockdetection.models.Recurso;
import br.com.ifce.so.projectdeadlockdetection.models.RecursoAlocado;
import br.com.ifce.so.projectdeadlockdetection.so.SistemaOperacional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class MainController {

    // Variáveis de Controle
    private Recurso[] recursos = new Recurso[10];
    private Processo[] processos = new Processo[10];
    private SistemaOperacional so;
    private GerenciadorRecursos gerenciador;

    // Componentes FXML - Cadastrar Recurso
    @FXML
    private Label mensagemRecursoCheio;
    @FXML
    private TextField nomeRecursoField;
    @FXML
    private TextField idRecursoField;
    @FXML
    private TextField quantidadeInstanciasRecursoField;
    @FXML
    private Button cadastrarRecursoBtn;

    // Componentes FXML - Configurar SO
    @FXML
    private TextField tempoVerificacaoField;
    @FXML
    private Button configurarSOBtn;

    // Componentes FXML - SO DeadLock e Contador
    @FXML
    private Label labelDeadlock;
    @FXML
    private Label labelContador;

    // Componentes FXML - Criar Processo
    @FXML
    private TextField idProcessoCriarField;
    @FXML
    private TextField tempoSolicitacaoField;
    @FXML
    private TextField tempoUsoField;
    @FXML
    private Button criarProcessoBtn;

    // Componentes FXML - Eliminar Processo
    @FXML
    private TextField idProcessoEliminarField;
    @FXML
    private Button eliminarProcessoBtn;

    // Componentes FXML - Recursos
    @FXML
    private Label labelE;
    @FXML
    private Label labelA;
    @FXML
    private Label labelC;
    @FXML
    private Label labelR;

    // Componentes FXML - Processos BLOQUEADO/RODANDO
    @FXML
    private VBox listaBloqueados;
    @FXML
    private VBox listaRodando;

    // Componentes FXML - Log do Sistema
    @FXML
    private TextArea logArea;


    @FXML
    public void initialize() {
        inicializarLogRedirect();
    }

    // Controladores
    @FXML
    private void aoCadastrarRecurso() {

        try {
            Integer idRecurso = Integer.parseInt(idRecursoField.getText());
            String nomeRecurso = nomeRecursoField.getText();
            Integer quantidadeInstanciasRecurso = Integer.parseInt(quantidadeInstanciasRecursoField.getText());

            // Verificação de existência para array
            boolean existe = false;
            for (Recurso r : recursos) {
                if (r != null && (r.getId().equals(idRecurso) || r.getNome().equalsIgnoreCase(nomeRecurso))) {
                    existe = true;
                    break;
                }
            }

            if (existe) {
                System.out.println("Já existe um recurso com este id ou nome.");
                return;
            }

            if (idRecurso <= 0 || idRecurso > 10) {
                System.out.println("O id deve estar contido no intervalor [1, 10]");
                return;
            }

            Recurso recurso = new Recurso(idRecurso, nomeRecurso, quantidadeInstanciasRecurso);
            recursos[recurso.getId() - 1] = recurso;

            labelE.setText("Recursos Existentes:\n " + recursosToString(recursos));

            nomeRecursoField.clear();
            idRecursoField.clear();
            quantidadeInstanciasRecursoField.clear();
            configurarSOBtn.setDisable(false);
            tempoVerificacaoField.setDisable(false);
            System.out.println("Recurso Cadastrado: " + recurso.toString());

            int quantRecursos = 0;
            for (Recurso r : recursos) {
                if (r != null) {
                    quantRecursos++;
                }
            }

            if (quantRecursos == 10) {
                mensagemRecursoCheio.setVisible(true);
                cadastrarRecursoBtn.setDisable(true);
                idRecursoField.setDisable(true);
                nomeRecursoField.setDisable(true);
                quantidadeInstanciasRecursoField.setDisable(true);
            }

        } catch (Exception e) {
            System.out.println("Erro ao cadastrar recurso: " + e.getMessage());
        }
    }

    private String recursosToString(Recurso[] recursos) {
        int max = -1;
        for (int i = 0; i < recursos.length; i++) {
            if (recursos[i] != null) max = i;
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i <= max; i++) {
            if (i > 0) sb.append(", ");
            sb.append(recursos[i] != null ? recursos[i].getQuantidadeInstancias() : 0);
        }
        sb.append("]");
        return sb.toString();
    }

    @FXML
    private void aoConfigurarSO() {

        try {
            Long tempoVerificacao = Long.parseLong(tempoVerificacaoField.getText());

            gerenciador = new GerenciadorRecursos(recursos, this);
            so = new SistemaOperacional(tempoVerificacao, gerenciador, this);
            so.start();

            Platform.runLater(() -> this.atualizarEstadoRecursos(gerenciador.E, gerenciador.getAArray(), gerenciador.C, gerenciador.R));

            tempoVerificacaoField.clear();
            configurarSOBtn.setDisable(true);
            cadastrarRecursoBtn.setDisable(true);
            tempoVerificacaoField.setDisable(true);
            idRecursoField.setDisable(true);
            nomeRecursoField.setDisable(true);
            quantidadeInstanciasRecursoField.setDisable(true);
            idProcessoCriarField.setDisable(false);
            tempoSolicitacaoField.setDisable(false);
            tempoUsoField.setDisable(false);
            criarProcessoBtn.setDisable(false);
            idProcessoEliminarField.setDisable(false);
            eliminarProcessoBtn.setDisable(false);
            System.out.println("SO Configurado com Sucesso: " + so.toString());
        } catch (Exception e) {
            System.out.println("Erro ao configurar SO: " + e.getMessage());
        }
    }

    @FXML
    public void aoCriarProcesso() {

        try {
            Integer idProcesso = Integer.parseInt(idProcessoCriarField.getText());
            Long tempoSolicitacao = Long.parseLong(tempoSolicitacaoField.getText());
            Long tempoUso = Long.parseLong(tempoUsoField.getText());

            // Validar o ID do processo
            if (idProcesso <= 0 || idProcesso > 10) {
                System.out.println("O ID do processo deve estar no intervalo [1, 10]");
                return;
            }

            // Verificar se já existe um processo com este ID
            if (processos[idProcesso - 1] != null) {
                System.out.println("Já existe um processo com o ID " + idProcesso);
                return;
            }

            // Criar e iniciar o processo
            Processo novoProcesso = new Processo(idProcesso, tempoSolicitacao, tempoUso, gerenciador);
            processos[idProcesso - 1] = novoProcesso;
            novoProcesso.start();

            // Limpar campos
            idProcessoCriarField.clear();
            tempoSolicitacaoField.clear();
            tempoUsoField.clear();

            System.out.println("Processo criado com sucesso: " + novoProcesso);
            Platform.runLater(() -> atualizarEstadoRecursos(gerenciador.E, gerenciador.getAArray(), gerenciador.C, gerenciador.R));

        } catch (Exception e) {
            System.out.println("Erro ao criar Processo: " + e.getMessage());
        }
    }

    @FXML
    public void aoEliminarProcesso() {
        try {
            Integer idProcesso = Integer.parseInt(idProcessoEliminarField.getText());

            if (idProcesso <= 0 || idProcesso > 10) {
                System.out.println("O ID do processo deve estar no intervalo [1, 10]");
                return;
            }

            Processo processo = processos[idProcesso - 1];
            if (processo == null) {
                System.out.println("Não existe um processo com o ID " + idProcesso);
                return;
            }

            // Interrompe a thread do processo
            processo.interrupt();
            try {
                processo.join(500);
            } catch (InterruptedException e) {
                System.out.println("Erro ao tentar");
            }

            processos[idProcesso - 1] = null;
            idProcessoEliminarField.clear();
            System.out.println("Processo " + idProcesso + " eliminado com sucesso");

        } catch (Exception e) {
            System.out.println("Erro ao eliminar processo: " + e.getMessage());
        }
    }

    public void atualizarDeadlock(ArrayList<Integer> processosEmDeadlock) {
        if (processosEmDeadlock == null || processosEmDeadlock.isEmpty()) {
            labelDeadlock.setText("Processos em deadlock: Nenhum");
        } else {
            labelDeadlock.setText("Processos em deadlock: " + processosEmDeadlock);
        }
        atualizarListasProcessos(processosEmDeadlock);
    }

    public void atualizarContador(double segundosRestantes) {
        labelContador.setText(String.format("Tempo para verificação: %.2fs", segundosRestantes));
    }

    // No MainController.java

    public void atualizarEstadoRecursos(int[] E, int[] A, int[][] C, int[][] R) {
        int maxRecurso = getMaxIndiceRecurso();
        int maxProcesso = getMaxIndiceProcesso();

        labelE.setText("Recursos Existentes:\n " + vetorParaString(E, maxRecurso));
        labelA.setText("Recursos Disponíveis:\n " + vetorParaString(A, maxRecurso));
        labelC.setText("Matriz de Alocação:\n" + matrizParaString(C, maxProcesso, maxRecurso));
        labelR.setText("Matriz de Requisição:\n" + matrizParaString(R, maxProcesso, maxRecurso));
    }

    private int getMaxIndiceRecurso() {
        int max = -1;
        for (int i = 0; i < recursos.length; i++) {
            if (recursos[i] != null) max = i;
        }
        return max + 1; // +1 para incluir o último recurso
    }

    private int getMaxIndiceProcesso() {
        int max = -1;
        for (int i = 0; i < processos.length; i++) {
            if (processos[i] != null) max = i;
        }
        return max + 1;
    }

    private String vetorParaString(int[] vetor, int tamanho) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < tamanho; i++) {
            if (i > 0) sb.append(", ");
            sb.append(vetor[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    private String matrizParaString(int[][] matriz, int linhas, int colunas) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < linhas; i++) {
            sb.append("[");
            for (int j = 0; j < colunas; j++) {
                if (j > 0) sb.append(", ");
                sb.append(matriz[i][j]);
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    // src/main/java/br/com/ifce/so/projectdeadlockdetection/ui/controller/MainController.java

    public void atualizarListasProcessos(ArrayList<Integer> processosEmDeadlock) {
        listaBloqueados.getChildren().clear();
        listaRodando.getChildren().clear();

        for (Processo p : processos) {
            if (p != null) {
                String recursosStr = getRecursosAlocadosString(p);

                String info = String.format("id: %d | deltaTs: %d | deltaTu: %d%s",
                        p.getIdP(), p.getDeltaTs(), p.getDeltaTu(),
                        recursosStr.isEmpty() ? "" : " | " + recursosStr);

                Label l = new Label(info);

                if (processosEmDeadlock != null && processosEmDeadlock.contains(p.getIdP())) {
                    listaBloqueados.getChildren().add(l);
                } else {
                    listaRodando.getChildren().add(l);
                }
            }
        }
    }

    // Monta a string dos recursos alocados, agrupando por nome e contando instâncias
    private String getRecursosAlocadosString(Processo p) {
        var recursosAlocados = p.getRecursosAlocados();
        if (recursosAlocados == null || recursosAlocados.isEmpty()) return "";

        // Agrupa por nome e conta instâncias
        java.util.Map<String, Integer> recursoCount = new java.util.HashMap<>();
        for (RecursoAlocado ra : recursosAlocados) {
            String nome = ra.recurso().getNome();
            recursoCount.put(nome, recursoCount.getOrDefault(nome, 0) + 1);
        }

        StringBuilder sb = new StringBuilder();
        recursoCount.forEach((nome, qtd) -> {
            if (sb.length() > 0) sb.append(", ");
            sb.append(nome).append("[").append(qtd).append("]");
        });

        return sb.toString();
    }

    public void inicializarLogRedirect() {
        PrintStream printStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                Platform.runLater(() -> appendLogText(String.valueOf((char) b)));
            }
            @Override
            public void write(byte[] b, int off, int len) {
                String texto = new String(b, off, len);
                Platform.runLater(() -> appendLogText(texto));
            }
            private void appendLogText(String texto) {
                double scrollTop = logArea.getScrollTop();
                int caretPosition = logArea.getCaretPosition();
                logArea.appendText(texto);
                logArea.positionCaret(caretPosition);
                logArea.setScrollTop(scrollTop);
            }
        }, true, java.nio.charset.StandardCharsets.UTF_8);
        System.setOut(printStream);
        System.setErr(printStream);
    }
}