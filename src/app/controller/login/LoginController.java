package app.controller.login;

import app.Main;
import app.Util;
import app.View;
import app.model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private ResourceBundle stringBundle;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginBtn;

    @FXML
    private void loginUser() {
        Optional<User> found = findUser(usernameField.getText(), passwordField.getText());
        if (found.isPresent()) {
            Main.setLoggedInUser(found.get());
            Util.logSignIn();
            Main.getViewSetter().set(View.HOME);
            System.out.println("User's credentials are valid");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(stringBundle.getString("alert"));
        alert.setHeaderText(stringBundle.getString("invalid") + ".");
        alert.setContentText(stringBundle.getString("please-try-again") + ".");
        alert.showAndWait();
    }

    private Optional<User> findUser(String username, String password) {
       return Main.getObservables().getUsers().stream()
           .filter(u -> u.getUserName().equals(username) && u.getPassword().equals(password))
           .findFirst();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Locale currentLocale = Locale.getDefault();
//        Locale currentLocale = new Locale("fr", "FR");
        stringBundle = ResourceBundle.getBundle("app.controller.login.i18n.StringBundle",
                currentLocale);
        System.out.println("Initialize login called");
        usernameField.setPromptText(stringBundle.getString("username"));
        passwordField.setPromptText(stringBundle.getString("password"));
        loginBtn.setText(stringBundle.getString("login"));
    }
}
