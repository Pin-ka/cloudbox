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
                        if (command.getCommand().equals("regOk")) {
                            isregOk = true;
                            getOutInMain(command.getName());
                        }else if (command.getCommand().equals("regFail")){
                                Platform.runLater(() -> {
                                    messageError.setText("Данный ник уже используется");
                                });
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
        Network.sendMsg(new Command("reg/"+login.getText()+"/"+password.getText()+"/"+nick.getText(),null));
    }

    private void getOutInMain(String newNick){
        Parent root = null;
        try {
            Controller.name=newNick;
            root = FXMLLoader.load(getClass().getResource("/main.fxml"));
                Parent rootf = root;
                Platform.runLater(() -> {
                    Scene scene = new Scene(rootf);
                    ((Stage)regBox.getScene().getWindow()).setTitle("Облако пользователя "+newNick);
                    ((Stage)regBox.getScene().getWindow()).setScene(scene);
                });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
