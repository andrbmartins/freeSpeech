package org.academiadecodigo.bootcamp8.freespeech.dialog;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class DeleteAccountDialog extends Dialog<String> {
    private PasswordField passwordField;

    public DeleteAccountDialog() {
        setTitle("Account manager");
        setHeaderText("You must enter your password in order to delete your account");

        ButtonType delete = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(delete, ButtonType.CANCEL);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        HBox hBox = new HBox();
        hBox.getChildren().add(passwordField);
        hBox.setPadding(new Insets(20));

        HBox.setHgrow(passwordField, Priority.ALWAYS);

        getDialogPane().setContent(hBox);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                passwordField.requestFocus();
            }
        });

        setResultConverter(new Callback<ButtonType, String>() {
            @Override
            public String call(ButtonType param) {
                if (param == delete) {
                    return passwordField.getText();
                }
                return null;
            }
        });
    }
}
