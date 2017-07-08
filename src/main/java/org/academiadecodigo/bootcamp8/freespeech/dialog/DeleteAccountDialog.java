package org.academiadecodigo.bootcamp8.freespeech.client.utils;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */

//TODO refactor

public class DeleteAccountDialog extends Dialog<String> {

    private PasswordField passwordField;

    public DeleteAccountDialog() {

        styleStage();

        //TODO can we use buttons instead?
        ButtonType deleteButton = new ButtonType("", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(deleteButton, cancelButton);

        getDialogPane().lookupButton(deleteButton).setId("deleteAccountButton");
        getDialogPane().lookupButton(cancelButton).setId("cancelRemoveButton");
        getDialogPane().lookup(".button-bar").setId("buttons");

        VBox vBox = styleVBox();
        getDialogPane().setContent(vBox);

        focusField();

        setResultConverter(new Callback<ButtonType, String>() {
            @Override
            public String call(ButtonType param) {
                if (param == deleteButton) {
                    return passwordField.getText();
                }
                return null;
            }
        });
    }

    private void focusField() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                passwordField.requestFocus();
            }
        });
    }

    private void styleStage() {
        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.UNDECORATED);
        getDialogPane().getScene().getStylesheets().clear();
        getDialogPane().getScene().getStylesheets().add(Values.STYLESHEET);
    }

    private VBox styleVBox() {

        final String DELETE = "Enter password in order to delete account\n";

        Label label = new Label(DELETE);
        passwordField = new PasswordField();

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        vBox.getChildren().add(label);
        vBox.getChildren().add(passwordField);
        vBox.setPadding(new Insets(20));

        return vBox;

    }
}
