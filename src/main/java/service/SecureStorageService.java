package service;

import model.SecureFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service de stockage sécurisé des fichiers
 * Gère le stockage des fichiers chiffrés et leurs métadonnées
 */
public class SecureStorageService {
    
    private static final String STORAGE_DIR = "src/main/resources/secure_storage/";
    private static final String METADATA_FILE = "src/main/resources/files_metadata.dat";
    
    private List<SecureFile> storedFiles;
    private CryptoService cryptoService;
    
    public SecureStorageService(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
        this.storedFiles = new ArrayList<>();
        ensureStorageDirectoryExists();
        loadMetadata();
    }
    
    /**
     * S'assure que le répertoire de stockage existe
     */
    private void ensureStorageDirectoryExists() {
        try {
            Path dir = Paths.get(STORAGE_DIR);
            Files.createDirectories(dir);
        } catch (IOException e) {
            System.err.println("Erreur lors de la création du répertoire de stockage: " + e.getMessage());
        }
    }
    
    /**
     * Charge les métadonnées des fichiers depuis le fichier de stockage
     */
    @SuppressWarnings("unchecked")
    private void loadMetadata() {
        File file = new File(METADATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                storedFiles = (List<SecureFile>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Erreur lors du chargement des métadonnées: " + e.getMessage());
                storedFiles = new ArrayList<>();
            }
        }
    }
    
    /**
     * Sauvegarde les métadonnées des fichiers
     */
    private void saveMetadata() {
        try {
            File file = new File(METADATA_FILE);
            file.getParentFile().mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(storedFiles);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des métadonnées: " + e.getMessage());
        }
    }
    
    /**
     * Chiffre et stocke un fichier pour un utilisateur
     * 
     * @param filePath Le chemin du fichier à chiffrer
     * @param ownerUsername Le nom d'utilisateur propriétaire
     * @return Le SecureFile créé, ou null en cas d'erreur
     */
    public SecureFile encryptAndStore(File filePath, String ownerUsername) {
        try {
            // Lire le fichier
            byte[] fileData = Files.readAllBytes(filePath.toPath());
            String originalFileName = filePath.getName();
            
            // Chiffrer le fichier
            byte[] encryptedData = cryptoService.encryptFile(fileData);
            
            // Générer un nom unique pour le fichier chiffré
            String encryptedFileName = UUID.randomUUID().toString() + ".encrypted";
            Path encryptedFilePath = Paths.get(STORAGE_DIR, encryptedFileName);
            
            // Sauvegarder le fichier chiffré
            Files.write(encryptedFilePath, encryptedData);
            
            // Créer les métadonnées
            SecureFile secureFile = new SecureFile(
                originalFileName,
                encryptedFileName,
                ownerUsername,
                fileData.length
            );
            
            storedFiles.add(secureFile);
            saveMetadata();
            
            return secureFile;
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chiffrement et stockage du fichier: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Récupère tous les fichiers d'un utilisateur
     * 
     * @param username Le nom d'utilisateur
     * @return La liste des fichiers de l'utilisateur
     */
    public List<SecureFile> getUserFiles(String username) {
        List<SecureFile> userFiles = new ArrayList<>();
        for (SecureFile file : storedFiles) {
            if (file.getOwnerUsername().equals(username)) {
                userFiles.add(file);
            }
        }
        return userFiles;
    }
    
    /**
     * Déchiffre et récupère un fichier
     * 
     * @param secureFile Le fichier sécurisé à déchiffrer
     * @param outputPath Le chemin où sauvegarder le fichier déchiffré
     * @return true si le déchiffrement a réussi, false sinon
     */
    public boolean decryptAndRetrieve(SecureFile secureFile, Path outputPath) {
        try {
            // Lire le fichier chiffré
            Path encryptedFilePath = Paths.get(STORAGE_DIR, secureFile.getEncryptedFileName());
            byte[] encryptedData = Files.readAllBytes(encryptedFilePath);
            
            // Déchiffrer
            byte[] decryptedData = cryptoService.decryptFile(encryptedData);
            
            // Sauvegarder le fichier déchiffré
            Files.write(outputPath, decryptedData);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Erreur lors du déchiffrement du fichier: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Supprime un fichier sécurisé
     * 
     * @param secureFile Le fichier à supprimer
     * @return true si la suppression a réussi
     */
    public boolean deleteFile(SecureFile secureFile) {
        try {
            // Supprimer le fichier chiffré du disque
            Path encryptedFilePath = Paths.get(STORAGE_DIR, secureFile.getEncryptedFileName());
            Files.deleteIfExists(encryptedFilePath);
            
            // Supprimer des métadonnées
            storedFiles.remove(secureFile);
            saveMetadata();
            
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de la suppression du fichier: " + e.getMessage());
            return false;
        }
    }
}






