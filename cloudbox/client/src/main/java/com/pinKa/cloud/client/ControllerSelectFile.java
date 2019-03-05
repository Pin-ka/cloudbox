package com.pinKa.cloud.client;

import com.pinKa.cloud.common.Command;
import com.pinKa.cloud.common.FileMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ControllerSelectFile implements Initializable {
    @FXML
    ListView<String> clientFilesList;
    @FXML
    VBox selectBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshLocalFilesList();
    }

    public void refreshLocalFilesList() {
            Platform.runLater(() -> {
                try {
                    clientFilesList.getItems().clear();
                    Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o -> clientFilesList.getItems().add(o));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }

    public void pull(ActionEvent actionEvent) {
        try {
            if (Files.exists(Paths.get("client_storage/" + clientFilesList.getSelectionModel().getSelectedItem()))) {
                FileMessage fm = new FileMessage(Paths.get("client_storage/" + clientFilesList.getSelectionModel().getSelectedItem()));
                fm.setUserName(Controller.name);
                Network.sendMsg(fm);
                ((Stage)selectBox.getScene().getWindow()).close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(ActionEvent actionEvent) {
        ((Stage)selectBox.getScene().getWindow()).close();
    }
}
