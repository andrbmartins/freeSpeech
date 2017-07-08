package org.academiadecodigo.bootcamp8.freespeech.client.dialog;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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

public class ChangePassDialog extends Dialog<String[]> {
    private PasswordField currPassword;
    private PasswordField newPassword;
    private PasswordField confirmPass;

    public ChangePassDialog() {

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.UNDECORATED);
        getDialogPane().getScene().getStylesheets().clear();
        getDialogPane().getScene().getStylesheets().add(Values.STYLESHEET);

        Label label = new Label("Confirm your current password and enter new one");

        ButtonType confirmButton = new ButtonType("", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        getDialogPane().lookupButton(confirmButton).setId("changePassButton");
        getDialogPane().lookupButton(cancelButton).setId("cancelPassButton");

        currPassword = new PasswordField();
        currPassword.setPromptText("current password_");

        newPassword = new PasswordField();
        newPassword.setPromptText("new password_");

        confirmPass = new PasswordField();
        confirmPass.setPromptText("confirm new password_");

        GridPane grid = new GridPane();
        GridPane.setHalignment(label, HPos.CENTER);
        GridPane.setHalignment(currPassword, HPos.CENTER);
        GridPane.setHalignment(newPassword, HPos.CENTER);
        GridPane.setHalignment(confirmPass, HPos.CENTER);



        grid.add(label, 0, 0);
        grid.add(currPassword, 0, 1);
        grid.add(newPassword, 0, 2);
        grid.add(confirmPass, 0, 3);
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
                if (param == confirmButton) {
                    String[] result = {currPassword.getText(), newPassword.getText(), confirmPass.getText()};
                    return result;
                }
                return null;
            }
        });
    }
}
