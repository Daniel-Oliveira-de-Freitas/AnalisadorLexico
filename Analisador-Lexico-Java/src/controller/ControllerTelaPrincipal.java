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
import tokens.AnaliseTokens;
import tokens.Token;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.*;

public class ControllerTelaPrincipal implements Initializable {

	static ObservableList<Token> listarTokens = FXCollections.observableArrayList();
	
	TextArea AreaTexto = new TextArea();
	static String codigoSemNaoTokens = "";
	StringBuffer temp;
	private File arquivo;
	boolean arquivoAberto = false;

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

	// Remover os Não tokens sendo principalmente trechos de comentarios pelo codigo
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
		le_tokens();
	}

	public void le_tokens() {
		try {
			boolean isString = false;
			//limpa a lista para uma nova visualização
			listarTokens.clear();
			temp = new StringBuffer(AreaTexto.getText());
			removerNaoToken("comentario unico", "//", "\n");
			removerNaoToken("varios comentario", "/*", "*/");
			removerNaoToken("preprocessor", "import", ";");
			removerNaoToken("package", "package", ";");
			codigoSemNaoTokens = temp.toString();
			StringTokenizer linhaTokenizer = new StringTokenizer(codigoSemNaoTokens, "\n", true);
			int linha = 1;
			while (linhaTokenizer.hasMoreTokens()) {
				String nextToken = linhaTokenizer.nextToken();
				if (nextToken.equals("\n")) {
					linha++;
					continue;
				}

				StringTokenizer spaceTokenizer = new StringTokenizer(nextToken, " ");
				while (spaceTokenizer.hasMoreTokens()) {
					String token = spaceTokenizer.nextToken();
					if (AnaliseTokens.ePalavraChave(token) && !isString)
						//Verifica-se se o que esta escrito no codigo não é uma palavra reservada pela linguagem java
						listarTokens.add(new Token(linha, "Palavra reservada", token, token));
					else {
						for (int i = 0; i < token.length(); i++) {
							String character = Character.toString(token.charAt(i));
							// Trecho em que se verifica os simbolos de operações no codigo como por exemplo o =
							if (AnaliseTokens.isSymbol(character) && !isString) {

								if (((i + 1) < token.length()) && ( character.equals("-") || character.equals("&") || character.equals("="))) {
									String character2 = Character.toString(token.charAt(i + 1));
									if (character == character2) {
										listarTokens.add(new Token(linha, "Token",AnaliseTokens.getNomeSimbolo(character + character2), character + character2));
										i++;
										continue;
									} else {
										listarTokens.add(new Token(linha, "Token", AnaliseTokens.getNomeSimbolo(character),
												character));
										continue;
									}
								} else if ((((i + 1) < token.length()) && (character.equals("<") || character.equals(">") || character.equals("*") || character.equals("+") ))) {
									String character2 = Character.toString(token.charAt(i + 1));
									if (character2.equals("=")) {
										listarTokens.add(new Token(linha, "Token",AnaliseTokens.getNomeSimbolo(character + character2), character + character2));
										i++;
										continue;
									} else {
										listarTokens.add(new Token(linha, "Token", AnaliseTokens.getNomeSimbolo(character),character));
										continue;
									}

								} else {
									//Trecho do codigo que serve para adiiconar os ;,(),{},[] a lista de tokens 
									//listarTokens.add(new Token(linha, "Simbolo", Scan.getSymbolName(character),character));
									continue;
								}
							}
							
							//Verifica-se se a palavra que esta sendo lida é um identificador de uma palavra que é reservado do sistema
							// ou se é um nome de variavel ID
							String tempToken = "";
							if (AnaliseTokens.eAlfabeto(character) && !isString) {
								do {
									tempToken += character;
									i++;
									if (i < token.length())
										character = Character.toString(token.charAt(i));
									else
										break;
								} while (AnaliseTokens.eAlfabeto(character) || AnaliseTokens.eNumero(character));
								--i;
								if (AnaliseTokens.ePalavraChave(tempToken))
									listarTokens.add(new Token(linha, "Palavra Reservada", tempToken, tempToken));
								else
									listarTokens.add(new Token(linha, "Token", "ID", tempToken));
								continue;
							}
							if (AnaliseTokens.eNumero(character) && !isString) {
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
								} while (AnaliseTokens.eNumero(character) || character.equals("."));
								if(tempToken.contains(".")) {
								listarTokens.add(new Token(linha, "Token", "Nreal", tempToken));
								}else { 
								listarTokens.add(new Token(linha, "Token", "Nint", tempToken));
								}
								if (dotError) {
									listarTokens.add(new Token(linha, "Erro", "erro", "erro"));
									listarTokens.add(new Token(linha, "Token", "simbolo", "."));
								}
								continue;
							}
							// Funções para se verificar caracteres que foram escritos dentro de aspas simples
							if (character.equals("\'") && !isString) {
								//Serve para adicionar a aspa simples inicial na lista de tokens
								//listarTokens.add(new Token(linha, "Token", "simbolo", "\'"));
								i++;
								if (i < token.length()) {
									character = Character.toString(token.charAt(i));
									listarTokens.add(new Token(linha, "Token", "Nstring", character));
								}
								i++;
								if (i < token.length()) {
									//Serve para adicionar a aspa simples final a lista de tokens
									character = Character.toString(token.charAt(i));
									//listarTokens.add(new Token(linha, "Token", "simbolo", character));
								}
								if (!character.equals("\'")) {
									listarTokens.add(new Token(linha, "Erro", "erro", "erro"));
								}
								continue;
							}
							tempToken += "";
							// Trecho do codigo para verificar as strings escritas em aspas duplas
							if (character.equals("\"") && !isString) {
								//Serve para adicionar a aspa dupla inicial na lista de tokens
								//listarTokens.add(new Token(linha, "Token", "simbolo", "\""));
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
								//Aqui ele ira adicionar o conteudo que esta entre aspas na lista de tokens
								listarTokens.add(new Token(linha, "Token", "Nstring", tempToken));
								if (character.equals("\"")) {
									//Serve para adicionar a aspa dupla final na lista de tokens
									//listarTokens.add(new Token(linha, "Token", "simbolo", "\""));
									continue;
								} else {
									isString = true;
									continue;
								}
							}
		
							//Trecho que se verifica se caso foi inserido alguma aspa adicional no inicio ou final da string 
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
									//Aqui irá adicionar a lista de tokens essa aspa adicional caso seja preciso so tirar o comentario
									//listarTokens.add(new Token(linha, "Token", "simbolo", "\""));
									isString = false;
									continue;
								} else
									isString = true;
								continue;

							}
							listarTokens.add(
									new Token(linha, "Invalido", "Invalido", token.substring(token.indexOf(character))));
							break;
						}
					}
				}
			}

			TelaAnaliseTokens();

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.toString());
		}
	}
	
	
	void TelaAnaliseTokens() throws Exception {
		Parent root;
		Stage primaryStage = new Stage();
		root = FXMLLoader.load(getClass().getResource("/telas/TelaAnalise.fxml"));
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
		arquivoAberto = true;
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
			if (file.leituraArquivo(arquivo)) {
				meuPainel.getChildren().clear();
				meuPainel.getChildren().add(AreaTexto);
				AreaTexto.setText(file.getConteudoArquivo());
				AreaTexto.setVisible(true);
				btnEscanear.setDisable(false);
				btnSalvar.setDisable(false);
				btnFechar.setDisable(false);
				btnSalvarComo.setDisable(false);
				arquivoAberto = true;
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
			file.setConteudoArquivo(AreaTexto.getText());
			file.escritaArquivo(arquivo);
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
			file.setConteudoArquivo(AreaTexto.getText());
			file.escritaArquivo(f);
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
		if (!arquivoAberto && button == "exit")
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
				arquivoAberto = false;
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