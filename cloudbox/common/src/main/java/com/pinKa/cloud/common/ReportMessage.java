package com.pinKa.cloud.common;

import java.util.ArrayList;

public class ReportMessage extends AbstractMessage {
    ArrayList<String> serverFilesList;

    public ArrayList<String> getServerFilesList() {
        return serverFilesList;
    }

    public ReportMessage(ArrayList<String> serverFilesList) {
        this.serverFilesList = serverFilesList;
    }
}
