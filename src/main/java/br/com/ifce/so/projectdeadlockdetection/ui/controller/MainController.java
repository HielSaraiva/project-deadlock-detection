package br.com.ifce.so.projectdeadlockdetection.ui.controller;

import br.com.ifce.so.projectdeadlockdetection.models.GerenciadorRecursos;
import br.com.ifce.so.projectdeadlockdetection.models.Recurso;
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
    private SistemaOperacional so;
    private GerenciadorRecursos gerenciador;

    //Componentes FXML
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
    @FXML
    private TextField tempoVerificacaoField;
    @FXML
    private Button configurarSOBtn;

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
            recursos[recurso.getId()-1] = recurso;

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
            System.out.println("SO Configurado com Sucesso: " + "");
        } catch (Exception e) {
            System.out.println("Erro ao configurar SO: " + e.getMessage());
        }
    }
}