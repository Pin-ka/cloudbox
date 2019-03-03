package com.pinKa.cloud.client;

import com.pinKa.cloud.common.AbstractMessage;
import com.pinKa.cloud.common.Command;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerRegistration implements Initializable {
    @FXML
    TextField login;
    @FXML
    TextField password;
    @FXML
    TextField nick;
    @FXML
    Label messageError;
    @FXML
    VBox regBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        Thread t1 = new Thread(() -> {
            try {
                boolean isregOk=false;
                while (!isregOk) {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof Command) {
                        Command command = (Command) am;
                        if (command.getCommand().startsWith("RegOk/")) {
                            isregOk = true;
                            Parent root = null;
                            try {
                                root = FXMLLoader.load(getClass().getResource("/main.fxml"));
                                if (Platform.isFxApplicationThread()) {
                                    Stage stageMain = new Stage();
                                    Scene scene = new Scene(root);
                                    stageMain.setScene(scene);
                                    ((Stage)regBox.getScene().getWindow()).close();
                                    stageMain.showAndWait();
                                } else {
                                    Parent fRoot = root;
                                    Platform.runLater(() -> {
                                        Stage stageMain = new Stage();
                                        Scene scene = new Scene(fRoot);
                                        stageMain.setScene(scene);
                                        ((Stage)regBox.getScene().getWindow()).close();
                                        stageMain.showAndWait();
                                    });
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else if (command.getCommand().equals("RegFail")){
                            if (Platform.isFxApplicationThread()) {
                                messageError.setText("Данный ник уже используется");
                            } else {
                                Platform.runLater(() -> {
                                    messageError.setText("Данный ник уже используется");
                                });
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        });
        t1.start();
    }

    public void setDataOnServer(ActionEvent actionEvent) {
        Network.sendMsg(new Command("reg/"+login.getText()+"/"+password.getText()+"/"+nick.getText()));
    }

}
