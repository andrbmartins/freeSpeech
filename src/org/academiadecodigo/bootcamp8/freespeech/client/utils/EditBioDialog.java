package org.academiadecodigo.bootcamp8.freespeech.client.utils;

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
public class EditBioDialog extends Dialog {
    private TextField username;
    private TextField email;
    private TextField birthdate;
    private TextArea bio;


    public EditBioDialog() {

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.UNDECORATED);

        setTitle("freeSpeech - Public profile");
        setHeaderText("Edit your public profile here. You can also change your username here");


        ButtonType setBio = new ButtonType("Edit bio", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(setBio, ButtonType.CANCEL);

        username = new TextField();
        email = new TextField();
        birthdate = new TextField();
        bio = new TextArea();

        Label user = new Label("Username");
        Label mail = new Label("Email");
        Label date = new Label("Date of birth");
        Label bio1 = new Label("Your awesomeness");
        Label max = new Label("500 characters max...");

        GridPane grid = new GridPane();

        grid.add(user, 0, 0);
        grid.add(username, 1, 0);
        grid.add(mail, 0, 1);
        grid.add(email, 1, 1);
        grid.add(date, 0, 2);
        grid.add(birthdate, 1, 2);
        grid.add(bio1, 0, 3);
        grid.add(bio, 1, 3, 1, 4);
        grid.add(max, 1, 7);
        grid.setHgap(40);
        grid.setVgap(10);
        grid.setPadding(new Insets(40));


        getDialogPane().setContent(grid);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                username.requestFocus();
            }
        });

        setResultConverter(new Callback<ButtonType, String[]>() {
            @Override
            public String[] call(ButtonType param) {
                if (param == setBio) {
                    String[] result = {username.getText(), email.getText(),
                            birthdate.getText(), bio.getText()};
                    return result;
                }
                return null;
            }
        });
    }

    public void setUsername(String username) {
        this.username.setText(username);
    }

    public void setEmail(String email) {
        this.email.setText(email);
    }

    public void setBirthdate(String birthdate) {
        this.birthdate.setText(birthdate);
    }

    public void setBio(String bio) {
        this.bio.setText(bio);
    }
}
