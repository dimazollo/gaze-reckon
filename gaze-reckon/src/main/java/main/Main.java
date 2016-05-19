package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.eyetracker.Message;
import model.test.Stimulus;
import serializer.Serializer;
import views.FDView;
import views.ParserView;
import views.RootLayoutView;
import views.graphs.ViewOfData;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @Author Dmitry Volovod
 * created on 10.09.2015
 */
public class Main extends Application {

//    public Property<ArrayList<Message>> messagesProperty;
//    public Property<ArrayList<Stimulus>> stimuliProperty;
    private volatile ArrayList<Message> messages;    // Set of tracker messages of eye-tracking data.
    private volatile ArrayList<Stimulus> stimuli;    // Set of stimuli of experiment.

    private Stage primaryStage;
    private Stage viewOfDataStage;

    private BorderPane rootLayout;

    private RootLayoutView rootLayoutView;
    private ViewOfData viewOfData;
    private ParserView parserView;
    private FDView fdView;

    public static void main(String[] args) {
        launch(args);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public ArrayList<Stimulus> getStimuli() {
        return stimuli;
    }

    public void setStimuli(ArrayList<Stimulus> stimuli) {
        this.stimuli = stimuli;
    }

    public ParserView getParserView() {
        return parserView;
    }

    public FDView getFdView() {
        return fdView;
    }

    public RootLayoutView getRootLayoutView() {
        return rootLayoutView;
    }

    public ViewOfData getViewOfData() {
        return viewOfData;
    }

    public Stage getViewOfDataStage() {
        return viewOfDataStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Gaze Reckon");
        // Adding icons.
        this.primaryStage.getIcons().add(new Image("/icons/AppIcon16.png"));
        this.primaryStage.getIcons().add(new Image("/icons/AppIcon32.png"));
        this.primaryStage.getIcons().add(new Image("/icons/AppIcon48.png"));

        initRootLayout();
        initViewOfData();
        Serializer deserializer = new Serializer(this);
        deserializer.load();
        // Setting up onClose saving configuration.
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            Serializer serializer = new Serializer(this);
            serializer.store();
            System.exit(0);
        });
    }

    private void initViewOfData() throws IOException {
        // Data graphs.
        Stage viewOfDataStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/graphs.fxml"));
        Parent root = fxmlLoader.load();
        viewOfData = fxmlLoader.getController();
        viewOfDataStage.setTitle("Graphs");
        viewOfDataStage.setScene(new Scene(root));
        viewOfDataStage.setMinWidth(1024);
        viewOfDataStage.setMinHeight(768);
        viewOfData.setMainApp(this);
        this.viewOfDataStage = viewOfDataStage;
    }

    private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/fxml/RootLayout.fxml"));
            rootLayout = loader.load();
            rootLayoutView = loader.getController();
            rootLayoutView.setMainApp(this);

            // Show the scene containing root layout.
            Scene scene = new Scene(rootLayout);
            HBox hBox = new HBox();
            hBox.getChildren().add(initFDLayout());
            hBox.getChildren().add(0, initParserLayout()); // Adding to the left side.

            rootLayout.setCenter(hBox);

            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AnchorPane initParserLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/fxml/ParserLayout.fxml"));
            AnchorPane parserForm = loader.load();
            parserView = loader.getController();
            parserView.setMainApp(this);
            return parserForm;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private AnchorPane initFDLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/fxml/FDLayout.fxml"));
            AnchorPane FDForm = loader.load();
            fdView = loader.getController();
            return FDForm;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}