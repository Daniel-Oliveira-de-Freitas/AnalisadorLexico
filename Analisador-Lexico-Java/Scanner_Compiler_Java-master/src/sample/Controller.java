package sample;
import Scanner.Scan;
import Scanner.NonToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import Scanner.Token;
import Scanner.Literal;

public class Controller implements Initializable {

    static ObservableList<NonToken> listNonToken=FXCollections.observableArrayList();
    static ObservableList<Token> listToken=FXCollections.observableArrayList();
    static ObservableList<Literal> listLiteral=FXCollections.observableArrayList();
    //CodeArea codeArea;
    TextArea textArea=new TextArea();
    static String codeWithoutNonTokens="";
    StringBuffer temp;
    private File f;
    boolean isFileOpen=false;
    @FXML
    Button btnNew;
    @FXML
    Button btnSave;
    @FXML
    Button btnClose;
    @FXML
    Button btnSaveAs;
    @FXML
    Button btnOpen;
    @FXML
    AnchorPane myPane;
    @FXML
    Button btnScan;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textArea.setPrefSize(888,540);
      //  textArea.setId("textArea");
    }
    //remOve nonToken
    void removeNonToken(String tokenName,String tStart,String tEnd){
        while(temp.indexOf(tStart)!=-1)
        {
            int start=temp.indexOf(tStart);
            String tempEnd=temp.substring(start);
            String comment=temp.substring(start,start+tempEnd.indexOf(tEnd)+2);
            listNonToken.add(new NonToken(tokenName,comment));
          if(tokenName.equals("single comment"))
            temp.delete(start,start+tempEnd.indexOf(tEnd));
            else if(tokenName.equals("preprocessor")||tokenName.equals("package"))
                temp.delete(start,start+tempEnd.indexOf(tEnd)+1);
            else {//multi comments
              int linesCount = comment.length()-comment.replace("\n","").length();
              String lines="";
              for(int i=1;i<=linesCount;i++)
              {
                  lines+="\n";
              }
              temp.delete(start,start+tempEnd.indexOf(tEnd)+2);
              temp.insert(start,lines);
          }
        }
    }
    @FXML
    void clickScan() {
        try {
            boolean isString = false;
            listToken.clear();
            listLiteral.clear();
            listNonToken.clear();
            temp = new StringBuffer(textArea.getText());
            removeNonToken("single comment", "//", "\n");
            removeNonToken("multi comment", "/*", "*/");
            removeNonToken("preprocessor", "import", ";");
            removeNonToken("package", "package", ";");
            codeWithoutNonTokens = temp.toString();
            StringTokenizer lineTokenizer = new StringTokenizer(codeWithoutNonTokens, "\n",true);
            int lineNo=1;
            while (lineTokenizer.hasMoreTokens()) {
                String nextToken=lineTokenizer.nextToken();
                if(nextToken.equals("\n")) {lineNo++; continue;};
                StringTokenizer spaceTokenizer = new StringTokenizer(nextToken, " ");
                while (spaceTokenizer.hasMoreTokens()) {
                    String token = spaceTokenizer.nextToken();
                    if (Scan.isKeyword(token) && !isString)
                        //is it a Keyword ??
                        listToken.add(new Token(lineNo,"KEYWORD", token, token));
                    else {
                        for (int i = 0; i < token.length(); i++) {
                            String character = Character.toString(token.charAt(i));
                            //is it a spcial symbol??
                            if (Scan.isSymbol(character) && !isString) {

                                if (((i + 1) < token.length()) && (character.equals("+") || character.equals("-") || character.equals("&") || character.equals("|") || character.equals("="))) {
                                    String character2 = Character.toString(token.charAt(i + 1));
                                    if (character == character2) {
                                        listToken.add(new Token(lineNo,"SPCIAL SYMBOL", Scan.getSymbolName(character + character2), character + character2));
                                        i++;
                                        continue;
                                    } else {
                                        listToken.add(new Token(lineNo,"SPCIAL SYMBOL", Scan.getSymbolName(character), character));
                                        continue;
                                    }
                                } else if ((((i + 1) < token.length()) && (character.equals("!") || character.equals("<") || character.equals(">")))) {
                                    String character2 = Character.toString(token.charAt(i + 1));
                                    if (character2.equals("=")) {
                                        listToken.add(new Token(lineNo,"SPCIAL SYMBOL", Scan.getSymbolName(character + character2), character + character2));
                                        i++;
                                        continue;
                                    } else {
                                        listToken.add(new Token(lineNo,"SPCIAL SYMBOL", Scan.getSymbolName(character), character));
                                        continue;
                                    }

                                } else {
                                    listToken.add(new Token(lineNo,"SPCIAL SYMBOL", Scan.getSymbolName(character), character));
                                    continue;
                                }
                            }
                            // is it a identifier??
                            String tempToken = "";
                            if (Scan.isAlaphabet(character) && !isString) {

                                do {
                                    tempToken += character;
                                    i++;
                                    if (i < token.length())
                                        character = Character.toString(token.charAt(i));
                                    else break;
                                } while (Scan.isAlaphabet(character) || Scan.isNumber(character));
                                --i;
                                if (Scan.isKeyword(tempToken))
                                    listToken.add(new Token(lineNo,"KEYWORD", tempToken, tempToken));
                                else
                                    listToken.add(new Token(lineNo,"OTHER", "Identfier", tempToken));
                                continue;
                            }
                            if (Scan.isNumber(character) && !isString) {
                                boolean dotError = false;
                                tempToken = "";
                                do {
                                    tempToken += character;
                                    ++i;
                                    if (i < token.length()) {
                                        character = Character.toString(token.charAt(i));
                                        if (tempToken.contains(".") && character.equals(".")) {
                                            dotError = true;
                                            break;
                                        }
                                    } else break;
                                } while (Scan.isNumber(character) || character.equals("."));
                                //--i;
                                listToken.add(new Token(lineNo,"OTHER", "number", tempToken));
                                if (dotError) {
                                    listToken.add(new Token(lineNo,"Error", "error", "error"));
                                    listToken.add(new Token(lineNo,"SPCIAL SYMBOL", "symbol", "."));
                                }
                                continue;
                            }
                            // for characters
                            if(character.equals("\'")&&!isString)
                            {
                                listToken.add(new Token(lineNo,"SPCIAL SYMBOL", "symbol", "\'"));
                                i++;
                                if(i<token.length()) {
                                    character = Character.toString(token.charAt(i));
                                    listToken.add(new Token(lineNo, "OTHER", "char", character));
                                }
                                i++;
                                if(i<token.length()) {
                                    character = Character.toString(token.charAt(i));
                                    listToken.add(new Token(lineNo, "SPCIAL SYMBOL", "symbol", character));
                                }
                                if(!character.equals("\'")){
                                    listToken.add(new Token(lineNo,"ERROR", "error", "Error"));
                                }
                                continue;
                            }
                            tempToken += "";
                            // handle strings
                            if (character.equals("\"") && !isString) {
                                listToken.add(new Token(lineNo,"SPCIAL SYMBOL", "symbol", "\""));
                                i++;
                                if (i < token.length())
                                    character = Character.toString(token.charAt(i));
                                else break;
                                while (!character.equals("\"")) {
                                    tempToken += character;
                                    i++;
                                    if (i < token.length())
                                        character = Character.toString(token.charAt(i));
                                    else break;
                                }
                                listToken.add(new Token(lineNo,"OTHER", "string", tempToken));
                                if (character.equals("\"")) {
                                    listToken.add(new Token(lineNo,"SPCIAL SYMBOL", "SYMBOL", "\""));
                                    continue;
                                } else {
                                    isString = true;
                                    continue;
                                }
                            }
                            // if(character.equals("'")&& !isString){
                            //     listToken.add(new Token("SPCIAL SYMBOL","symbol","'"));
                            // }
                            if (isString) {
                                while (!character.equals("\"")) {
                                    tempToken += character;
                                    i++;
                                    if (i < token.length())
                                        character = Character.toString(token.charAt(i));
                                    else break;
                                }
                                listToken.get(listToken.size() - 1).updateValue(tempToken = " " + tempToken);
                                if (character.equals("\"")) {
                                    listToken.add(new Token(lineNo,"SPCIAL SYMBOL", "SYMBOL", "\""));
                                    isString = false;
                                    continue;
                                } else isString = true;
                                continue;

                            }
                            listToken.add(new Token(lineNo,"INVALID", "invlaid", token.substring(token.indexOf(character))));
                            break;
                        }
                    }
                }
            }
            // Add literals and invalids to List
            Iterator<Token> i=listToken.iterator();
            while (i.hasNext()){
                Token token=i.next();
                if(token.getCatagoy().equals("OTHER")&&(token.getName().equals("string")||token.getName().equals("number")||token.getName().equals("char")))
                listLiteral.add(new Literal(token.getLineNo(),token.getName(),token.getValue()));
            }
            scannerOutputPage();


        }
        catch (Exception ex)
       {
            JOptionPane.showMessageDialog(null,ex.toString());
       }
    }
    void scannerOutputPage() throws Exception
    {
        Parent root ;
        Stage primaryStage=new Stage();
        root = FXMLLoader.load(getClass().getResource("ScannerOutput.fxml"));
        primaryStage.setTitle("Scanner Result");
        Scene scene=new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("styling.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.show();

    }

    @FXML
    void clickNew(){
        myPane.getChildren().clear();
        myPane.getChildren().add(textArea);
        textArea.setText(sampleCode);
        textArea.setVisible(true);
        btnClose.setDisable(false);
        btnSaveAs.setDisable(false);
        isFileOpen=true;
        btnNew.setDisable(true);
        btnOpen.setDisable(true);
        btnScan.setDisable(false);
        //txtArea.setStyle("text-area-background:"+"\""+Main.backgroundColor+"\";"
        //        +"-fx-font-size: "+Main.fontSize+"px;"+
        //        "-fx-text-fill: "+"\""+Main.fontColor+"\";");
    }
    @FXML
    void clickOpen()
    {

        try {
            FileChooser chooser = new FileChooser();
            f= chooser.showOpenDialog(null);
            if(!(f.getName()).contains(".java")) throw new Exception();
            TempFile file = new TempFile();
            if (file.input(f)) {
                myPane.getChildren().clear();
                myPane.getChildren().add(textArea);
                textArea.setText(file.getFiletext());
                textArea.setVisible(true);
                btnScan.setDisable(false);
                btnSave.setDisable(false);
                btnClose.setDisable(false);
                btnSaveAs.setDisable(false);
                  isFileOpen=true;
                btnNew.setDisable(true);
             //   txtArea.setStyle("text-area-background:"+"\""+Main.backgroundColor+"\";"
             //           +"-fx-font-size: "+Main.fontSize+"px;"+
             //           "-fx-text-fill: "+"\""+Main.fontColor+"\";");
            } else {
                throw new Exception();
            }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, "Cant Open File");
        }
    }
    @FXML
    void clickSave()
    {
        try{
            TempFile file=new TempFile();
            file.setFiletext(textArea.getText());
            file.output(f);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "Cant Save File");
        }
    }
    @FXML
    void clickSaveAs()
    {
        try {
            FileChooser chooser = new FileChooser();
            File f =chooser.showSaveDialog(null);
            TempFile file = new TempFile();
            file.setFiletext(textArea.getText());
            file.output(f);
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Cant Save File");
        }
    }
    @FXML
    void clickClose()
    {
        onclose("close");
        btnScan.setDisable(true);
        btnSaveAs.setDisable(true);
        btnSave.setDisable(true);
        btnClose.setDisable(false);
        btnNew.setDisable(false);
        btnOpen.setDisable(false);
    }
    @FXML
    void clickExit()
    {
        onclose("exit");
        btnNew.setDisable(false);
    }
    void onclose(String button) {
        if (!isFileOpen && button=="exit")
            System.exit(0);
        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Save");
            alert.setHeaderText("Save Change Before Exit");
            ButtonType btnTSave = new ButtonType("Yes");
            ButtonType btnTDontSave = new ButtonType("No");
            ButtonType btnTCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(btnTSave, btnTDontSave, btnTCancel);
            Optional<ButtonType> result = alert.showAndWait();

            if(result.get()==btnTSave)

                clickSaveAs();
            if(button=="close")
            {
                textArea.clear();
                textArea.setVisible(false);
                isFileOpen=false;
            }
            else{
                System.exit(0);
            }
        }
    }
    private static final String sampleCode = String.join("\n", new String[] {
            "package com.asad;",
            "",
            "import java.util.*;",
            "",
            "// one line comment",
            "while(@wee322 asad123_As324 test)",
            "2332 22.232.23 'a' ",
            "/* multi line comment */",
            "\"@2e%6  Asad1232_ads2\"",
            "public class MyClass extends YourClass implements MyInterface {",
            "",
            "    /*",
            "     * multi-line comment",
            "     */",
            "    public static void main(String[] args) {",
            "        // single-line comment",
            "        for(String arg: args) {",
            "            if(arg.length() != 0)",
            "                System.out.println(arg);",
            "            else",
            "                System.err.println(\"Warning: empty string as argument\");",
            "        }",
            "    }",
            "",
            "}"
    });


}