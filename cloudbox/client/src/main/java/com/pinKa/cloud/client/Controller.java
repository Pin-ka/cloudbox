package com.pinKa.cloud.client;

import com.pinKa.cloud.common.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    ListView<String> networkFilesList;

    @FXML
    VBox rootNode;

    public static String name;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Command getReport=new Command("getReport",name);
        Network.sendMsg(getReport);
        ArrayList<String> serverFilesList=new ArrayList<>();
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get("client_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                            Platform.runLater(() -> {
                                Alert info=new Alert(Alert.AlertType.INFORMATION);
                                info.setTitle("Отчет о загрузке");
                                info.setHeaderText(null);
                                info.setContentText("Файл успешно загружен");
                                info.showAndWait();
                            });
                    }
                    if (am instanceof ReportMessage){
                        serverFilesList.clear();
                        serverFilesList.addAll(((ReportMessage) am).getServerFilesList());
                            Platform.runLater(() -> {
                                networkFilesList.getItems().clear();
                                networkFilesList.getItems().addAll(serverFilesList);
                            });
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.start();

        ContextMenu contextMenu=new ContextMenu();
        MenuItem selectDownload=new MenuItem("Скачать");
        MenuItem selectDelete=new MenuItem("Удалить");
        selectDownload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Network.sendMsg(new Command(networkFilesList.getSelectionModel().getSelectedItem(),name));
            }
        });
        selectDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Network.sendMsg(new Command("delete/"+networkFilesList.getSelectionModel().getSelectedItem(),name));
            }
        });
        contextMenu.getItems().addAll(selectDownload,selectDelete);
        networkFilesList.setContextMenu(contextMenu);
    }

    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        if(networkFilesList.getSelectionModel().getSelectedItem()!=null) {
            Network.sendMsg(new Command(networkFilesList.getSelectionModel().getSelectedItem(),name));
        }

    }

    public void pressOnPullBtn(ActionEvent actionEvent) throws IOException {
        Parent root = null;
        root = FXMLLoader.load(getClass().getResource("/selectFile.fxml"));
        Stage stageMain = new Stage();
        Scene scene = new Scene(root);
        stageMain.setScene(scene);
        stageMain.showAndWait();
    }

    public void pressOnDeleteBtn(ActionEvent actionEvent) {
        if(networkFilesList.getSelectionModel().getSelectedItem()!=null) {
            Network.sendMsg(new Command("delete/" + networkFilesList.getSelectionModel().getSelectedItem(),name));
        }
    }

    public void exit(ActionEvent actionEvent) {
        ((Stage)rootNode.getScene().getWindow()).close();
        Network.stop();
    }
}
