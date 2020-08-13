package app.controller.login;

import app.Main;
import app.View;
import app.dao.UserDao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Locale;
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
        if (isValidCredentials(usernameField.getText(), passwordField.getText())) {
            Main.setLoggedInUser(usernameField.getText());
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

    private boolean isValidCredentials(String username, String password) {
        UserDao userDao = UserDao.getInstance(Main.getDb());
        return userDao.authenticate(username, password);
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
