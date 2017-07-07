package org.academiadecodigo.bootcamp8.freespeech.dialog;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */

public class ChangePassDialog extends Dialog<String[]> {
    private PasswordField currPassword;
    private PasswordField newPassword;
    private PasswordField confirmPass;

    public ChangePassDialog() {

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.UNDECORATED);

        setTitle("freeSpeech Password Change");
        setHeaderText("Confirm your current password and enter new one");

        ButtonType setPass = new ButtonType("Set New Password", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(setPass, ButtonType.CANCEL);

        currPassword = new PasswordField();
        currPassword.setPromptText("current password");

        newPassword = new PasswordField();
        newPassword.setPromptText("new password");

        confirmPass = new PasswordField();
        confirmPass.setPromptText("confirm password");

        Label currPass = new Label("Current password");
        Label newPass = new Label("New password");
        Label confirm = new Label("Confirm new password");

        GridPane grid = new GridPane();

        grid.add(currPass, 0, 0);
        grid.add(currPassword, 1, 0);
        grid.add(newPass, 0, 1);
        grid.add(newPassword, 1, 1);
        grid.add(confirm, 0, 2);
        grid.add(confirmPass, 1, 2);
        grid.setHgap(40);
        grid.setVgap(10);
        grid.setPadding(new Insets(40));


        getDialogPane().setContent(grid);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                currPassword.requestFocus();
            }
        });

        setResultConverter(new Callback<ButtonType, String[]>() {
            @Override
            public String[] call(ButtonType param) {
                if (param == setPass) {
                    String[] result = {currPassword.getText(), newPassword.getText(), confirmPass.getText()};
                    return result;
                }
                return null;           }
        });
    }
}
