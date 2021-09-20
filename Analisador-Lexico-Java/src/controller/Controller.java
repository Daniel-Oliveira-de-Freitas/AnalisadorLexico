package controller;

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
import tokens.Scan;
import tokens.Token;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

	static ObservableList<Token> listarTokens = FXCollections.observableArrayList();
	
	TextArea AreaTexto = new TextArea();
	static String codeWithoutNonTokens = "";
	StringBuffer temp;
	private File arquivo;
	boolean isFileOpen = false;

	@FXML
	Button btnNovo;
	@FXML
	Button btnSalvar;
	@FXML
	Button btnFechar;
	@FXML
	Button btnSalvarComo;
	@FXML
	Button btnAbrir;
	@FXML
	AnchorPane meuPainel;
	@FXML
	Button btnEscanear;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		AreaTexto.setPrefSize(888, 540);
	}

	// Remover os Não tokens
	void removerNaoToken(String tokenName, String tStart, String tEnd) {
		while (temp.indexOf(tStart) != -1) {
			int start = temp.indexOf(tStart);
			String tempEnd = temp.substring(start);
			String comment = temp.substring(start, start + tempEnd.indexOf(tEnd) + 2);
			if (tokenName.equals("single comment"))
				temp.delete(start, start + tempEnd.indexOf(tEnd));
			else if (tokenName.equals("import") || tokenName.equals("package"))
				temp.delete(start, start + tempEnd.indexOf(tEnd) + 1);
			else {// multi comments
				int linesCount = comment.length() - comment.replace("\n", "").length();
				String lines = "";
				for (int i = 1; i <= linesCount; i++) {
					lines += "\n";
				}
				temp.delete(start, start + tempEnd.indexOf(tEnd) + 2);
				temp.insert(start, lines);
			}
		}
	}

	@FXML
	void clicarEscanear() {
		try {
			boolean isString = false;
			listarTokens.clear();
			temp = new StringBuffer(AreaTexto.getText());
			removerNaoToken("comentario unico", "//", "\n");
			removerNaoToken("varios comentario", "/*", "*/");
			removerNaoToken("preprocessor", "import", ";");
			removerNaoToken("package", "package", ";");
			codeWithoutNonTokens = temp.toString();
			StringTokenizer lineTokenizer = new StringTokenizer(codeWithoutNonTokens, "\n", true);
			int lineNo = 1;
			while (lineTokenizer.hasMoreTokens()) {
				String nextToken = lineTokenizer.nextToken();
				if (nextToken.equals("\n")) {
					lineNo++;
					continue;
				}
				;
				StringTokenizer spaceTokenizer = new StringTokenizer(nextToken, " ");
				while (spaceTokenizer.hasMoreTokens()) {
					String token = spaceTokenizer.nextToken();
					if (Scan.isKeyword(token) && !isString)
						// is it a Keyword ??
						listarTokens.add(new Token(lineNo, "KEYWORD", token, token));
					else {
						for (int i = 0; i < token.length(); i++) {
							String character = Character.toString(token.charAt(i));
							// is it a spcial symbol??
							if (Scan.isSymbol(character) && !isString) {

								if (((i + 1) < token.length()) && (character.equals("+") || character.equals("-")
										|| character.equals("&") || character.equals("|") || character.equals("="))) {
									String character2 = Character.toString(token.charAt(i + 1));
									if (character == character2) {
										listarTokens.add(new Token(lineNo, "SPCIAL SYMBOL",
												Scan.getSymbolName(character + character2), character + character2));
										i++;
										continue;
									} else {
										listarTokens.add(new Token(lineNo, "SPCIAL SYMBOL", Scan.getSymbolName(character),
												character));
										continue;
									}
								} else if ((((i + 1) < token.length())
										&& (character.equals("!") || character.equals("<") || character.equals(">")))) {
									String character2 = Character.toString(token.charAt(i + 1));
									if (character2.equals("=")) {
										listarTokens.add(new Token(lineNo, "SPCIAL SYMBOL",
												Scan.getSymbolName(character + character2), character + character2));
										i++;
										continue;
									} else {
										listarTokens.add(new Token(lineNo, "SPCIAL SYMBOL", Scan.getSymbolName(character),
												character));
										continue;
									}

								} else {
									listarTokens.add(new Token(lineNo, "SPCIAL SYMBOL", Scan.getSymbolName(character),
											character));
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
									else
										break;
								} while (Scan.isAlaphabet(character) || Scan.isNumber(character));
								--i;
								if (Scan.isKeyword(tempToken))
									listarTokens.add(new Token(lineNo, "KEYWORD", tempToken, tempToken));
								else
									listarTokens.add(new Token(lineNo, "OTHER", "Identfier", tempToken));
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
									} else
										break;
								} while (Scan.isNumber(character) || character.equals("."));
								// --i;
								listarTokens.add(new Token(lineNo, "OTHER", "number", tempToken));
								if (dotError) {
									listarTokens.add(new Token(lineNo, "Error", "error", "error"));
									listarTokens.add(new Token(lineNo, "SPCIAL SYMBOL", "symbol", "."));
								}
								continue;
							}
							// for characters
							if (character.equals("\'") && !isString) {
								listarTokens.add(new Token(lineNo, "SPCIAL SYMBOL", "symbol", "\'"));
								i++;
								if (i < token.length()) {
									character = Character.toString(token.charAt(i));
									listarTokens.add(new Token(lineNo, "OTHER", "char", character));
								}
								i++;
								if (i < token.length()) {
									character = Character.toString(token.charAt(i));
									listarTokens.add(new Token(lineNo, "SPCIAL SYMBOL", "symbol", character));
								}
								if (!character.equals("\'")) {
									listarTokens.add(new Token(lineNo, "ERROR", "error", "Error"));
								}
								continue;
							}
							tempToken += "";
							// handle strings
							if (character.equals("\"") && !isString) {
								listarTokens.add(new Token(lineNo, "SPCIAL SYMBOL", "symbol", "\""));
								i++;
								if (i < token.length())
									character = Character.toString(token.charAt(i));
								else
									break;
								while (!character.equals("\"")) {
									tempToken += character;
									i++;
									if (i < token.length())
										character = Character.toString(token.charAt(i));
									else
										break;
								}
								listarTokens.add(new Token(lineNo, "OTHER", "string", tempToken));
								if (character.equals("\"")) {
									listarTokens.add(new Token(lineNo, "SPCIAL SYMBOL", "SYMBOL", "\""));
									continue;
								} else {
									isString = true;
									continue;
								}
							}
		
							if (isString) {
								while (!character.equals("\"")) {
									tempToken += character;
									i++;
									if (i < token.length())
										character = Character.toString(token.charAt(i));
									else
										break;
								}
								listarTokens.get(listarTokens.size() - 1).atualizarValor(tempToken = " " + tempToken);
								if (character.equals("\"")) {
									listarTokens.add(new Token(lineNo, "SPCIAL SYMBOL", "SYMBOL", "\""));
									isString = false;
									continue;
								} else
									isString = true;
								continue;

							}
							listarTokens.add(
									new Token(lineNo, "INVALID", "invlaid", token.substring(token.indexOf(character))));
							break;
						}
					}
				}
			}

			scannerOutputPage();

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.toString());
		}
	}

	void scannerOutputPage() throws Exception {
		Parent root;
		Stage primaryStage = new Stage();
		root = FXMLLoader.load(getClass().getResource("ScannerOutput.fxml"));
		primaryStage.setTitle("Resultado do scanner");
		Scene scene = new Scene(root);
		scene.getStylesheets().add(Main.class.getResource("styling.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.show();

	}

	@FXML
	void clicarNovo() {
		meuPainel.getChildren().clear();
		meuPainel.getChildren().add(AreaTexto);
		AreaTexto.setText(codigoExemplo);
		AreaTexto.setVisible(true);
		btnFechar.setDisable(false);
		btnSalvarComo.setDisable(false);
		isFileOpen = true;
		btnNovo.setDisable(true);
		btnAbrir.setDisable(true);
		btnEscanear.setDisable(false);
	}

	@FXML
	void clicarAbrir() {
		try {
			FileChooser chooser = new FileChooser();
			arquivo = chooser.showOpenDialog(null);
			if (!(arquivo.getName()).contains(".java"))
				throw new Exception();
			Arquivo file = new Arquivo();
			if (file.input(arquivo)) {
				meuPainel.getChildren().clear();
				meuPainel.getChildren().add(AreaTexto);
				AreaTexto.setText(file.getFiletext());
				AreaTexto.setVisible(true);
				btnEscanear.setDisable(false);
				btnSalvar.setDisable(false);
				btnFechar.setDisable(false);
				btnSalvarComo.setDisable(false);
				isFileOpen = true;
				btnNovo.setDisable(true);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,  "Não foi possivel salvar o arquivo");
		}
	}

	@FXML
	void clicarSalvar() {
		try {
			Arquivo file = new Arquivo();
			file.setFiletext(AreaTexto.getText());
			file.output(arquivo);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,  "Não foi possivel salvar o arquivo");
		}
	}

	@FXML
	void clicarSalvarComo() {
		try {
			FileChooser chooser = new FileChooser();
			File f = chooser.showSaveDialog(null);
			Arquivo file = new Arquivo();
			file.setFiletext(AreaTexto.getText());
			file.output(f);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Não foi possivel salvar o arquivo");
		}
	}

	@FXML
	void clicarFechar() {
		aoFechar("close");
		btnEscanear.setDisable(true);
		btnSalvarComo.setDisable(true);
		btnSalvar.setDisable(true);
		btnFechar.setDisable(false);
		btnNovo.setDisable(false);
		btnAbrir.setDisable(false);
	}

	@FXML
	void clicarSair() {
		aoFechar("exit");
		btnNovo.setDisable(false);
	}

	void aoFechar(String button) {
		if (!isFileOpen && button == "exit")
			System.exit(0);
		else {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Save");
			alert.setHeaderText("Deseja salvar as alterações antes de sair");
			ButtonType btnSalvar = new ButtonType("Sim");
			ButtonType btnNaoSalvar = new ButtonType("Não");
			ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
			alert.getButtonTypes().setAll(btnSalvar, btnNaoSalvar, btnCancelar);
			Optional<ButtonType> resultado = alert.showAndWait();

			if (resultado.get() == btnSalvar)
				clicarSalvarComo();
			if (button == "close") {
				AreaTexto.clear();
				AreaTexto.setVisible(false);
				isFileOpen = false;
			} else {
				System.exit(0);
			}
		}
	}

	private static final String codigoExemplo = String.join("\n",
			new String[] {"public class CodigoTeste1{\r\n"
					+ "\r\n"
					+ " int num;\r\n"
					+ " boolean char;\r\n"
					+ " float real;\r\n"
					+ " String texto;\r\n"
					+ "\r\n"
					+ " public static void main (String args[]){\r\n"
					+ " num =1;\r\n"
					+ " if(num == 1)\r\n"
					+ " System.out.print(\"Hello\");\r\n"
					+ " else \r\n"
					+ " System.out.print(\"World\");\r\n"
					+ " }\r\n"
					+ "\r\n"
					+ " }" });
}