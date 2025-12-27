package app;

import javafx.application.Application;
import javafx.stage.Stage;
import service.AuthentificationService;
import service.CryptoService;
import service.SecureStorageService;
import ui.LoginView;
import ui.MainView;
import model.Utilisateur;

public class MainApp extends Application {
    
    private Stage primaryStage;
    private AuthentificationService authService;
    private CryptoService cryptoService;
    private SecureStorageService storageService;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        authService = new AuthentificationService();
        cryptoService = new CryptoService();
        storageService = new SecureStorageService(cryptoService);
        
        showLoginView();
    }
    
    private void showLoginView() {
        LoginView loginView = new LoginView(primaryStage, authService, this::onLoginSuccess);
        loginView.show();
    }
    
    private void onLoginSuccess(Utilisateur currentUser) {
        MainView mainView = new MainView(primaryStage, currentUser, cryptoService, storageService);
        mainView.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
