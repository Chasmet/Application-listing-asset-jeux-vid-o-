# CHK Asset Manager

Application Android hors ligne pour organiser les assets de plusieurs jeux vidéo.

## Fonctions disponibles

- Gestion de plusieurs jeux.
- Classement par région, île, niveau ou zone.
- Fiches d’assets avec plusieurs images de référence.
- Catégories : personnages, ennemis, boss, bateaux, bâtiments, textures, sons, modèles 3D, etc.
- Trois statuts : à créer, créé, intégré dans le jeu.
- Validation rapide avec le bouton vert.
- Recherche et filtres par statut.
- Nom du fichier final et chemin dans le projet.
- Détection des doublons exacts dans un même jeu.
- Sauvegarde et restauration au format JSON.
- Fonctionnement local sans compte, API ni service payant.

## Configuration Android

- Java 17
- minSdkVersion 21
- compileSdkVersion 34
- targetSdkVersion 34
- Base de données SQLite locale

## Compiler l’APK

```bash
./gradlew assembleDebug
```

L’APK est généré ici :

```text
app/build/outputs/apk/debug/app-debug.apk
```

Le workflow GitHub Actions `.github/workflows/build-apk.yml` compile automatiquement l’APK et le publie dans les Artifacts GitHub.
