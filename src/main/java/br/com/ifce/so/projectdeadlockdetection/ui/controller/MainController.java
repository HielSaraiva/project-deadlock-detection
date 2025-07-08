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
    private ArrayList<Recurso> recursos = new ArrayList<>();
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

            boolean existe = recursos.stream().anyMatch(r ->
                    r.getId().equals(idRecurso) || r.getNome().equalsIgnoreCase(nomeRecurso)
            );

            if (existe) {
                System.out.println("Já existe um recurso com este id ou nome.");
                return;
            }

            Recurso recurso = new Recurso(idRecurso, nomeRecurso, quantidadeInstanciasRecurso);
            recursos.add(recurso);

            nomeRecursoField.clear();
            idRecursoField.clear();
            quantidadeInstanciasRecursoField.clear();
            configurarSOBtn.setDisable(false);
            tempoVerificacaoField.setDisable(false);
            System.out.println("Recurso Cadastrado: " + recurso.toString());

            if (recursos.size() == 10) {
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