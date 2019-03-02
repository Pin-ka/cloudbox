package com.pinKa.cloud.client;

import com.pinKa.cloud.common.AbstractMessage;
import com.pinKa.cloud.common.Command;
import com.pinKa.cloud.common.FileMessage;
import com.pinKa.cloud.common.ReportMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerAutorisation implements Initializable {
    @FXML
    TextField login;
    @FXML
    TextField password;
    @FXML
    Label messageError;

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
                        if (command.getCommand().startsWith("authOk/")) {
                            isAuto = true;
                            Parent root = null;
                            try {
                                root = FXMLLoader.load(getClass().getResource("/selectFile.fxml"));
                                Stage stageMain = new Stage();
                                Scene scene = new Scene(root);
                                stageMain.setScene(scene);
                                stageMain.showAndWait();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else if (command.getCommand().startsWith("notFound")){
                            messageError.setText("Сочетание логин + пароль НЕ НАЙДЕНО");
                        }


                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        });
        t1.start();
    }


    public void getQuestion(ActionEvent actionEvent) {
        Network.sendMsg(new Command("auth/"+login.getText()+"/"+password.getText()));
    }
}
