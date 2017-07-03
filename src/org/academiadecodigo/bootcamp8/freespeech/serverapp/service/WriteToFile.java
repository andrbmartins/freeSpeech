package org.academiadecodigo.bootcamp8.freespeech.serverapp.service;

import org.academiadecodigo.bootcamp8.freespeech.serverapp.Utils;
import java.io.*;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class WriteToFile {
    private BufferedWriter fileOutputStream;
    private File logSave;


    /**
     * Saves a String to a file in home folder. If file already exists it will append the new content
     * If user chooses to save to default file
     * @param data content to be saved to file
     */
    public void save(String data) {
        if (logSave == null) {
            setSavingFile(new File(System.getProperty("user.home") + Utils.DEFAULT_FILE));
        }

        StringBuilder builder = new StringBuilder(data);
        builder.append("\n");
        try {
            fileOutputStream.append(builder);
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets file to save data. If OutputStream is open, it will close it first.
     * @param file user given file
     */
    public void setSavingFile(File file) {
        if (fileOutputStream != null) {
            closeOutput();
        }
        logSave = file;
        createStream();

    }

    /**
     * Creates the OutputStream to write to file
     */
    private void createStream() {
        try {
            fileOutputStream = new BufferedWriter(new FileWriter(logSave));
        } catch (IOException e) {
            System.out.println("Unable to write fileOutputStream");
        }
    }

    /**
     * Closes the OutputStream
     */

    public void closeOutput() {
        try {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        } catch (IOException e) {
            System.out.println("Unable to close stream");
        }
    }
}