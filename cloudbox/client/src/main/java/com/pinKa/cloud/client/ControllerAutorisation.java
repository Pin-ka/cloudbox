package com.pinKa.cloud.client;

import com.pinKa.cloud.common.AbstractMessage;
import com.pinKa.cloud.common.Command;
import com.pinKa.cloud.common.FileMessage;
import com.pinKa.cloud.common.ReportMessage;
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
import sun.security.ssl.SSLContextImpl;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ControllerAutorisation implements Initializable {
    @FXML
    TextField login;
    @FXML
    TextField password;
    @FXML
    Label messageError;
    @FXML
    VBox identBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
           Thread t1 = new Thread(() -> {
                try {
                    boolean isAuto=false;
                    while (!isAuto) {
                        AbstractMessage am = Network.readObject();
                        if (am instanceof Command) {
                            Command command = (Command) am;
                            if (command.getCommand().equals("authOk")) {
                                isAuto = true;
                                Parent root = null;
                                try {
                                    Controller.name=command.getName();
                                    Parent rootMain = FXMLLoader.load(getClass().getResource("/main.fxml"));
                                        Platform.runLater(() -> {
                                            Scene scene = new Scene(rootMain);
                                            ((Stage)identBox.getScene().getWindow()).setTitle("Облако пользователя "+command.getName());
                                            ((Stage)identBox.getScene().getWindow()).setScene(scene);
                                        });

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else if (command.getCommand().equals("notFound")){
                                    Platform.runLater(() -> {
                                        messageError.setText("Сочетание логин + пароль НЕ НАЙДЕНО");
                                    });
                            }
                        }
                    }
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            });
            t1.setDaemon(true);
            t1.start();
    }

    public void getQuestion(ActionEvent actionEvent) {
        Network.sendMsg(new Command("auth/"+login.getText()+"/"+password.getText(),null));
    }

    public void getUserData(ActionEvent actionEvent) throws IOException {
            Parent root = null;
            root = FXMLLoader.load(getClass().getResource("/registration.fxml"));
            Scene scene = new Scene(root);
            ((Stage)identBox.getScene().getWindow()).setTitle("Регистрация");
            ((Stage)identBox.getScene().getWindow()).setScene(scene);
    }
}
