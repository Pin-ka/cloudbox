package com.pinKa.cloud.client;

import com.pinKa.cloud.common.AbstractMessage;
import com.pinKa.cloud.common.FileMessage;
import com.pinKa.cloud.common.Command;
import com.pinKa.cloud.common.ReportMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    TextField tfFileName;

    @FXML
    ListView<String> filesList;

    @FXML
    TextField clientFileName;

    @FXML
    ListView<String> networkFilesList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        Command getReport=new Command("getReport");
        Network.sendMsg(getReport);;
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get("client_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalFilesList();
                    }
                    if (am instanceof ReportMessage){
                        refreshServerFilesList((ReportMessage)am);
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
        refreshLocalFilesList();

        ContextMenu contextMenu=new ContextMenu();
        MenuItem selectDownload=new MenuItem("Скачать");
        MenuItem selectDelete=new MenuItem("Удалить");
        selectDownload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Network.sendMsg(new Command(networkFilesList.getSelectionModel().getSelectedItem()));
            }
        });
        selectDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Network.sendMsg(new Command("delete/"+networkFilesList.getSelectionModel().getSelectedItem()));
            }
        });
        contextMenu.getItems().addAll(selectDownload,selectDelete);
        networkFilesList.setContextMenu(contextMenu);
    }

    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        if (tfFileName.getLength() > 0) {
            Network.sendMsg(new Command(tfFileName.getText()));
            tfFileName.clear();
        }
    }

    public void pressOnPullBtn(ActionEvent actionEvent) {
        try {
            if (Files.exists(Paths.get("client_storage/" + clientFileName.getText()))) {
                FileMessage fm = new FileMessage(Paths.get("client_storage/" + clientFileName.getText()));
                Network.sendMsg(fm);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clientFileName.clear();
        }
    }

    public void pressOnDeleteBtn(ActionEvent actionEvent){
        if (clientFileName.getLength() > 0) {
            Network.sendMsg(new Command("delete/"+clientFileName.getText()));
            clientFileName.clear();
        }
    }

    public void refreshLocalFilesList() {
        if (Platform.isFxApplicationThread()) {
            try {
                filesList.getItems().clear();
                Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o -> filesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Platform.runLater(() -> {
                try {
                    filesList.getItems().clear();
                    Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o -> filesList.getItems().add(o));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void refreshServerFilesList(ReportMessage am){
        if (Platform.isFxApplicationThread()) {
            networkFilesList.getItems().clear();
            networkFilesList.getItems().addAll(am.getServerFilesList());
        } else {
            Platform.runLater(() -> {
                networkFilesList.getItems().clear();
                networkFilesList.getItems().addAll(am.getServerFilesList());
            });
        }
    }

}
