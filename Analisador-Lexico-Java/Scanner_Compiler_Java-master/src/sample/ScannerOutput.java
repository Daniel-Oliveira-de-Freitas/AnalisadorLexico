package sample;

import Scanner.Literal;
import Scanner.NonToken;
import Scanner.Scan;
import Scanner.Token;
import com.sun.javafx.scene.control.TreeTableViewBackingList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ScannerOutput implements Initializable {
    //Token Table
    @FXML
    TableView<Token> tokenTable;
    @FXML
    TableColumn<Token,String> categoryColumn;
    @FXML
    TableColumn<Token,String> tNameColumn;
    @FXML
    TableColumn<Token,String> tValueColumn;
    @FXML
    TableColumn<Token,Integer> tLineNoColumn;
    @FXML
    Label lblTotalToken;
    //Literal Table
    @FXML
    TableView<Literal> literalTable;
    @FXML
    TableColumn<Literal,Integer> literalLineNoColumn;
    @FXML
    TableColumn<Literal,String> literalNameColumn;
    @FXML
    TableColumn<Literal,String> literalValueColumn;
    @FXML
    Label lblTotalLiteral;
    // Non Token Table
    @FXML
    TableView<NonToken> nonTokenTable;
    @FXML
    TableColumn<NonToken,String> nonTokenNameColumn;
    @FXML
    TableColumn<NonToken,String> nonTokenValueColumn;
    @FXML
    Label lblNonTokenTotal;
    //code area without nonTokens
    @FXML
    TextArea txtAreaCodeWithoutNonTokens;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initialize Token Table
        tLineNoColumn.setCellValueFactory(
                new PropertyValueFactory<Token, Integer>("lineNo"));
        categoryColumn.setCellValueFactory(
                new PropertyValueFactory<Token, String>("catagoy"));
        tNameColumn.setCellValueFactory(
                new PropertyValueFactory<Token, String>("name"));
        tValueColumn.setCellValueFactory(
                new PropertyValueFactory<Token, String>("value"));
        tokenTable.getItems().setAll(Controller.listToken);
        lblTotalToken.setText("Total Token are "+Controller.listToken.size());
        //Initialize Literal Table
        literalLineNoColumn.setCellValueFactory(
                new PropertyValueFactory<Literal, Integer>("lineNo"));
        literalNameColumn.setCellValueFactory(
                new PropertyValueFactory<Literal, String>("name"));
        literalValueColumn.setCellValueFactory(
                new PropertyValueFactory<Literal, String>("value"));
        literalTable.getItems().setAll(Controller.listLiteral);
        lblTotalLiteral.setText("Total Literals are "+Controller.listLiteral.size());
    }
}
