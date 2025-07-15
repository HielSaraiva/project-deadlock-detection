package br.com.ifce.so.projectdeadlockdetection.ui.controller;

import br.com.ifce.so.projectdeadlockdetection.models.GerenciadorRecursos;
import br.com.ifce.so.projectdeadlockdetection.models.Processo;
import br.com.ifce.so.projectdeadlockdetection.models.Recurso;
import br.com.ifce.so.projectdeadlockdetection.models.RecursoAlocado;
import br.com.ifce.so.projectdeadlockdetection.so.SistemaOperacional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

    @FXML
    private void aoConfigurarSO() {

        try {
            Long tempoVerificacao = Long.parseLong(tempoVerificacaoField.getText());

            gerenciador = new GerenciadorRecursos(recursos);
            so = new SistemaOperacional(tempoVerificacao, gerenciador);
            so.start();

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
}