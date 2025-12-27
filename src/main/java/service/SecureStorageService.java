package service;

import model.SecureFile;
import util.Db;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SecureStorageService {
    
    private CryptoService cryptoService;

    public SecureStorageService(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    public SecureFile encryptAndStore(File filePath, String ownerUsername) {
        try {
            byte[] fileData = Files.readAllBytes(filePath.toPath());
            String originalFileName = filePath.getName();
            byte[] encryptedData = cryptoService.encryptFile(fileData);
            String encryptedFileName = UUID.randomUUID().toString() + ".encrypted";

            SecureFile secureFile = new SecureFile(
                originalFileName,
                encryptedFileName,
                ownerUsername,
                fileData.length
            );

            insertMetadataWithBlob(secureFile, encryptedData);

            return secureFile;
        } catch (Exception e) {
            System.err.println("Erreur lors du chiffrement et stockage du fichier: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public List<SecureFile> getUserFiles(String username) {
        List<SecureFile> userFiles = new ArrayList<>();
        String sql = "SELECT sf.original_name, sf.encrypted_name, sf.file_size_bytes, sf.stored_at " +
                     "FROM secure_files sf JOIN users u ON u.id = sf.owner_user_id " +
                     "WHERE u.username = ? ORDER BY sf.stored_at DESC";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SecureFile file = new SecureFile(
                            rs.getString("original_name"),
                            rs.getString("encrypted_name"),
                            username,
                            rs.getLong("file_size_bytes")
                    );
                    java.sql.Timestamp ts = rs.getTimestamp("stored_at");
                    if (ts != null) {
                        file.setTimestamp(ts.getTime());
                    }
                    userFiles.add(file);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des fichiers: " + e.getMessage());
        }
        return userFiles;
    }
    
    public boolean decryptAndRetrieve(SecureFile secureFile, Path outputPath) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT encrypted_blob FROM secure_files WHERE encrypted_name = ?")) {
            ps.setString(1, secureFile.getEncryptedFileName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    byte[] encryptedData = rs.getBytes("encrypted_blob");
                    byte[] decryptedData = cryptoService.decryptFile(encryptedData);
                    Files.write(outputPath, decryptedData);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du déchiffrement du fichier: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteFile(SecureFile secureFile) {
        try {
            deleteMetadata(secureFile);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du fichier: " + e.getMessage());
            return false;
        }
    }

    private void insertMetadataWithBlob(SecureFile secureFile, byte[] encryptedData) {
        String sql = "INSERT INTO secure_files (original_name, encrypted_name, encrypted_blob, owner_user_id, file_size_bytes) " +
                     "VALUES (?, ?, ?, (SELECT id FROM users WHERE username = ?), ?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, secureFile.getFileName());
            ps.setString(2, secureFile.getEncryptedFileName());
            ps.setBytes(3, encryptedData);
            ps.setString(4, secureFile.getOwnerUsername());
            ps.setLong(5, secureFile.getFileSize());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement des métadonnées: " + e.getMessage());
        }
    }

    private void deleteMetadata(SecureFile secureFile) {
        String sql = "DELETE FROM secure_files WHERE encrypted_name = ? AND owner_user_id = (SELECT id FROM users WHERE username = ?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, secureFile.getEncryptedFileName());
            ps.setString(2, secureFile.getOwnerUsername());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des métadonnées: " + e.getMessage());
        }
    }
}
