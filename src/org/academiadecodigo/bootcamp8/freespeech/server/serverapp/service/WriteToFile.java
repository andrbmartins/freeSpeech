package org.academiadecodigo.bootcamp8.freespeech.server.serverapp.service;

import org.academiadecodigo.bootcamp8.freespeech.server.serverapp.Utils;

import java.io.*;

/**
 * Created by Prashanta on 30/06/17.
 */
public class WriteToFile {
    private BufferedWriter fileOutputStream;
    private File logSave;

    public WriteToFile() {
        logSave = new File(System.getProperty("user.home") + Utils.FILE_PATH);
        createStream();
    }


    public void save(String queryResult) {
        StringBuilder builder = new StringBuilder(queryResult);
        builder.append("\n");
        try {
            fileOutputStream.append(builder);
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createStream() {
        try {
            fileOutputStream = new BufferedWriter(new FileWriter(logSave));
        } catch (IOException e) {
            System.out.println("Unable to write fileOutputStream");
        }
    }

    public void closeOutput() {
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            System.out.println("Unable to close stream");
        }
    }


}
