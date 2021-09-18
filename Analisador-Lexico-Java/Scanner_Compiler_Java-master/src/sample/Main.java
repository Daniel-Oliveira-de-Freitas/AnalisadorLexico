package sample;
import java.time.Duration;

import java.util.Collection;

import java.util.Collections;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;

import java.util.regex.Pattern;



import javafx.application.Application;

import javafx.application.Platform;

import javafx.concurrent.Task;
import javafx.scene.Scene;

import javafx.scene.input.KeyCode;

import javafx.scene.input.KeyEvent;

import javafx.scene.layout.StackPane;

import javafx.stage.Stage;



import org.fxmisc.flowless.VirtualizedScrollPane;

import org.fxmisc.richtext.CodeArea;

import org.fxmisc.richtext.LineNumberFactory;

import org.fxmisc.richtext.model.StyleSpans;

import org.fxmisc.richtext.model.StyleSpansBuilder;

import org.reactfx.Subscription;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Java Scanner");
        Scene scene=new Scene(root);
       scene.getStylesheets().add(Main.class.getResource("styling.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

