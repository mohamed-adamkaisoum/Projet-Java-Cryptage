package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import service.AuthentificationService;
import model.Utilisateur;
import java.util.function.Consumer;

public class LoginView {
    
    private Stage stage;
    private AuthentificationService authService;
    private Consumer<Utilisateur> onLoginSuccess;
    
    public LoginView(Stage stage, AuthentificationService authService, Consumer<Utilisateur> onLoginSuccess) {
        this.stage = stage;
        this.authService = authService;
        this.onLoginSuccess = onLoginSuccess;
    }
    
    public void show() {
        stage.setTitle("Système de Cryptographie - Connexion");
        
        ImageView logoView = createLogoView();
        
        Label titleLabel = new Label("Système de Cryptographie des Données Utilisateur");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label usernameLabel = new Label("Nom d'utilisateur:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Entrez votre nom d'utilisateur");
        
        Label passwordLabel = new Label("Mot de passe:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Entrez votre mot de passe");
        
        Button loginButton = new Button("Se connecter");
        loginButton.setStyle("-fx-font-size: 14px; -fx-pref-width: 200px;");
        
        Button registerButton = new Button("S'inscrire");
        registerButton.setStyle("-fx-font-size: 12px; -fx-pref-width: 200px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        Label infoLabel = new Label("Utilisez le bouton 'S'inscrire' pour créer un compte");
        infoLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs requis", 
                    "Veuillez remplir tous les champs.");
                return;
            }
            
            Utilisateur user = authService.authenticate(username, password);
            if (user != null) {
                onLoginSuccess.accept(user);
            } else {
                showAlert(Alert.AlertType.ERROR, "Échec de l'authentification", 
                    "Nom d'utilisateur ou mot de passe incorrect.");
                passwordField.clear();
            }
        });
        
        registerButton.setOnAction(e -> showRegisterDialog());
        
        passwordField.setOnAction(e -> loginButton.fire());
        
        VBox centerContent = new VBox(15);
        centerContent.setPadding(new Insets(20));
        centerContent.setAlignment(Pos.CENTER);
        
        if (logoView != null) {
            centerContent.getChildren().add(logoView);
        }
        
        centerContent.getChildren().addAll(
            titleLabel,
            usernameLabel,
            usernameField,
            passwordLabel,
            passwordField,
            loginButton,
            registerButton,
            infoLabel
        );
        
        usernameField.setPrefWidth(250);
        passwordField.setPrefWidth(250);
        
        VBox creditsBox = createCreditsBox();
        
        BorderPane root = new BorderPane();
        root.setCenter(centerContent);
        root.setBottom(creditsBox);
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        Scene scene = new Scene(root, 400, 550);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        
        usernameField.requestFocus();
    }
    
    private void showRegisterDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Inscription");
        dialog.setHeaderText("Créer un nouveau compte");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");
        usernameField.setPrefWidth(250);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setPrefWidth(250);
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmer le mot de passe");
        confirmPasswordField.setPrefWidth(250);
        
        VBox dialogContent = new VBox(10);
        dialogContent.setPadding(new Insets(20));
        dialogContent.setAlignment(Pos.CENTER);
        
        dialogContent.getChildren().addAll(
            new Label("Nom d'utilisateur:"),
            usernameField,
            new Label("Mot de passe:"),
            passwordField,
            new Label("Confirmer le mot de passe:"),
            confirmPasswordField
        );
        
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, e -> {
            if (!validateAndRegister(usernameField, passwordField, confirmPasswordField)) {
                e.consume();
            }
        });
        
        dialog.setHeight(350);
        
        dialog.showAndWait();
    }
    
    private boolean validateAndRegister(TextField usernameField, PasswordField passwordField, PasswordField confirmPasswordField) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs requis", 
                "Veuillez remplir tous les champs.");
            return false;
        }
        
        if (username.length() < 3) {
            showAlert(Alert.AlertType.WARNING, "Nom d'utilisateur invalide", 
                "Le nom d'utilisateur doit contenir au moins 3 caractères.");
            return false;
        }
        
        if (password.length() < 4) {
            showAlert(Alert.AlertType.WARNING, "Mot de passe trop court", 
                "Le mot de passe doit contenir au moins 4 caractères.");
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Mots de passe différents", 
                "Les mots de passe ne correspondent pas.");
            return false;
        }
        
        if (authService.registerUser(username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Inscription réussie", 
                "Votre compte a été créé avec succès. Vous pouvez maintenant vous connecter.");
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Échec de l'inscription", 
                "Ce nom d'utilisateur est déjà utilisé. Veuillez en choisir un autre.");
            return false;
        }
    }
    
    private ImageView createLogoView() {
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
            ImageView logoView = new ImageView(logoImage);
            logoView.setFitWidth(150);
            logoView.setFitHeight(150);
            logoView.setPreserveRatio(true);
            return logoView;
        } catch (Exception e) {
            return null;
        }
    }
    
    private VBox createCreditsBox() {
        VBox creditsBox = new VBox(5);
        creditsBox.setPadding(new Insets(10));
        creditsBox.setAlignment(Pos.CENTER);
        creditsBox.setStyle("-fx-background-color: #e0e0e0;");
        
        Label credit1 = new Label("Réalisé par: Adam Kaisoum");
        credit1.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");
        
        Label credit2 = new Label("Encadré par: Prof: TOUIMI Yassine");
        credit2.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");
        
        creditsBox.getChildren().addAll(credit1, credit2);
        return creditsBox;
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
