# Snake Blockade

**UPEC - L1 INFO**

## Structure du projet

Le projet est divisé en plusieurs fichiers:

-   `App.java` : Classe principale du jeu, initialise JavaFX et attends que l'utilisateur appuie sur un bouton pour lance le jeu.
-   `Controller.java` : Classe qui gère les événements de l'interface graphique. Elle est responsable de la gestion des événements de la souris et du clavier, et c'est elle qui s'occupe d'afficher les éléments du jeu.
-   `Game.java` : Classe qui gère la logique du jeu. Elle est responsable de la gestion des collisions, de la mise à jour de la position des éléments du jeu, et de la gestion des événements de fin de partie.
-   `Snake.java` : Classe qui représente le serpent. Elle est responsable de la gestion de la taille du serpent, de la direction du serpent, et de la gestion des événements de collision (avec lui-même, la nourriture ou avec les murs).
-   `Player.java` : Classe qui représente le joueur. Elle est responsable de l'initialisation du joueur (il n'est pas possible de donner aux joueurs un nom personnalisé, mais il est facielment possible d'introduire une logique permettant de le faire), de la gestion du score du joueur, et de la gestion des coups du joueur.
-   `Board.java` : Classe qui représente le plateau de jeu. Elle est responsable de la gestion de la taille du plateau, de la génération de la nourriture et des murs, et de la gestion des collisions avec les murs.
-   `Point.java` : Classe utilitaire qui représente un point, un peu comme la classe `Point` de java.awt. Cette classe de java ne marche pas avec JavaFX, donc nous avons décidé de créer notre propre classe `Point`, sans non plus réimplementer toutes les méthodes qu'elle contient.
-   `resources/layout.fxml` : Fichier FXML qui contient la structure de l'interface graphique. Il est utilisé par JavaFX pour afficher les éléments graphiques.
-   `resources/graphics` : Dossier qui contient les images utilisées par l'interface graphique (comme les serpents, les obstacles, les pommes, etc.)

## Fonctionnalités

-   Les joueurs peuvent se déplacer dans les quatre directions (haut, bas, gauche, droite) en utilisant les flèches du clavier ou les quattres lettres `W`, `A`, `S` et `D`.
-   Le serpent rétrécit a chaque fois qu'il mange une pomme. Ces pommes sont générées aléatoirement sur le plateau de jeu et réapparaissent après lorsqu'elles sont mangées.
-   Le jeu se termine si un des serpents touche un mur, touche l'autre serpent, ou si un des serpents touche son propre corps.
-   Des obstacles sont générés aléatoirement sur le plateau de jeu. Les serpents ne peuvent pas traverser ces obstacles.
-   Chaque n tours, les serpents grandissent d'une case.
-   Il est possible d'arrêter le jeu à tout moment en appuyant sur la touche `Esc` ou `Echap` sur AZERTY, ou en appuyant sur le bouton `Stop Playing` de l'interface graphique.
-   Il est possible de rejouer après la fin d'une partie en appuyant sur le bouton `Play Again` de l'interface graphique. Les scores des joueurs sont conservés entre les parties.
-   Les paramètres du jeu (taille du plateau, nombre de murs, nombre de pommes, nombre de tours avant que les serpents grandissent) sont définis dans la classe `Game` et ne sont pas modifiables par l'utilisateur.
-   L'IA est implémentée dans la classe `Snake`. Elle est très simple et se contente de soit suivre la tête de l'autre serpent, soit d'aller vers la pomme la plus proche. L'IA est activée par défaut, mais il est possible de la désactiver en modifiant une ligne de code dans la classe `Game`. Il arrive souvent que l'IA se bloque toute seule.

## Comment jouer

Avant de pouvoir jouer, il est nécessaire de compiler le projet. Ce projet utilise Maven, donc il suffit de lancer la commande `mvn clean install` pour compiler le projet.

Pour jouer, il suffit de lancer le programme en exécutant la classe `App.java`. Pour cela, il suffit de lancer la commande `mvn javafx:run` dans le dossier du projet.

Une fenêtre s'ouvrira, et il suffira de cliquer sur le bouton `Start Playing` pour lancer le jeu. Les serpents peuvent être déplacés en utilisant les flèches du clavier ou les lettres `W`, `A`, `S` et `D`. Pour arrêter le jeu, il suffit d'appuyer sur la touche `Esc` ou `Echap` sur AZERTY, ou de cliquer sur le bouton `Stop Playing`. Pour rejouer, il suffit de cliquer sur le bouton `Play Again`.
