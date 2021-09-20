package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import tokens.Token;

import java.net.URL;
import java.util.ResourceBundle;

public class ScannerOutput implements Initializable {
    //Ids das colunas e das tabelas de token
    @FXML
    TableView<Token> TabelaTokens;
    @FXML
    TableColumn<Token,String> ColunaCategora;
    @FXML
    TableColumn<Token,String> ColunaNome;
    @FXML
    TableColumn<Token,String> ColunaValor;
    @FXML
    TableColumn<Token,Integer> ColunaLinha;
    @FXML
    Label TotalTokens;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Inicializador da tabela de tokens
    	ColunaLinha.setCellValueFactory(
                new PropertyValueFactory<Token, Integer>("nmrLinha"));
    	ColunaCategora.setCellValueFactory(
                new PropertyValueFactory<Token, String>("categoria"));
        ColunaNome.setCellValueFactory(
                new PropertyValueFactory<Token, String>("nome"));
        ColunaValor.setCellValueFactory(
                new PropertyValueFactory<Token, String>("valor"));
        TabelaTokens.getItems().setAll(Controller.listarTokens);
        TotalTokens.setText("O total de tokens é "+Controller.listarTokens.size());
    }
}
