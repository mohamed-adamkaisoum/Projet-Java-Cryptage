# Script pour lancer l'application de cryptographie
# Verifie et lance l'application avec JavaFX

Write-Host "=== Systeme de Cryptographie des Donnees Utilisateur ===" -ForegroundColor Cyan
Write-Host ""

# Verifier que Java est installe
try {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Host "Java detecte: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "ERREUR: Java n'est pas installe ou pas dans le PATH" -ForegroundColor Red
    exit 1
}

# Verifier si les classes sont compilees
if (-not (Test-Path "target\classes\app\MainApp.class")) {
    Write-Host "ERREUR: Les classes ne sont pas compilees!" -ForegroundColor Red
    Write-Host "Veuillez d'abord compiler le projet avec: mvn clean compile" -ForegroundColor Yellow
    exit 1
}

# Methode 1: Essayer avec Maven (si disponible)
Write-Host ""
Write-Host "Tentative de lancement avec Maven..." -ForegroundColor Yellow
try {
    $mvnCheck = Get-Command mvn -ErrorAction SilentlyContinue
    if ($mvnCheck) {
        Write-Host "Maven trouve! Lancement de l'application..." -ForegroundColor Green
        mvn javafx:run
        exit 0
    } else {
        Write-Host "Maven non disponible dans le PATH" -ForegroundColor Yellow
    }
} catch {
    Write-Host "Maven non disponible" -ForegroundColor Yellow
}

# Methode 2: Essayer de trouver JavaFX dans le repertoire Maven local
Write-Host ""
Write-Host "Recherche de JavaFX..." -ForegroundColor Yellow

$javafxBasePath = "$env:USERPROFILE\.m2\repository\org\openjfx"
$javafxModules = @()

if (Test-Path $javafxBasePath) {
    # Chercher les modules JavaFX
    $modules = @("javafx-controls", "javafx-fxml", "javafx-base")
    foreach ($module in $modules) {
        $modulePath = Get-ChildItem -Path $javafxBasePath -Filter "$module-*" -Directory -ErrorAction SilentlyContinue | Sort-Object Name -Descending | Select-Object -First 1
        if ($modulePath) {
            $jarFile = Get-ChildItem -Path $modulePath.FullName -Filter "*.jar" | Select-Object -First 1
            if ($jarFile) {
                $javafxModules += $jarFile.FullName
                Write-Host "  JavaFX module $module trouve" -ForegroundColor Green
            }
        }
    }
}

if ($javafxModules.Count -gt 0) {
    Write-Host ""
    Write-Host "Lancement avec JavaFX trouve dans Maven..." -ForegroundColor Green
    
    # Construire le module-path avec tous les repertoires parents des modules
    $modulePaths = $javafxModules | ForEach-Object { Split-Path -Parent $_ } | Select-Object -Unique
    $modulePathString = $modulePaths -join ";"
    
    Write-Host "Utilisation du module-path: $modulePathString" -ForegroundColor Gray
    
    java --module-path $modulePathString --add-modules javafx.controls -cp "target/classes" app.MainApp
    exit 0
}

# Methode 3: Instructions pour installer JavaFX
Write-Host ""
Write-Host "JavaFX n'est pas disponible automatiquement." -ForegroundColor Red
Write-Host ""
Write-Host "OPTIONS POUR LANCER L'APPLICATION:" -ForegroundColor Cyan
Write-Host ""
Write-Host "Option 1 - Installer Maven (RECOMMANDE):" -ForegroundColor Yellow
Write-Host "  1. Telecharger depuis: https://maven.apache.org/download.cgi" -ForegroundColor White
Write-Host "  2. Extraire et ajouter au PATH systeme" -ForegroundColor White
Write-Host "  3. Executer: mvn clean javafx:run" -ForegroundColor White
Write-Host ""
Write-Host "Option 2 - Telecharger JavaFX SDK:" -ForegroundColor Yellow
Write-Host "  1. Telecharger depuis: https://openjfx.io/" -ForegroundColor White
Write-Host "  2. Extraire JavaFX SDK" -ForegroundColor White
Write-Host "  3. Executer avec le module-path JavaFX" -ForegroundColor White
Write-Host ""
Write-Host "Option 3 - Utiliser un JDK avec JavaFX integre:" -ForegroundColor Yellow
Write-Host "  Telecharger Liberica JDK avec JavaFX depuis: https://bell-sw.com/pages/downloads/" -ForegroundColor White
