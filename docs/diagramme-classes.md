# Diagramme de classes — Schotten-Totten

Première proposition de conception. Elle couvre la variante de base, la variante tactique
et la variante experts. Elle est découpée en deux vues pour rester lisible :

1. le **modèle** (`com.schottenTotten.model`) ;
2. le **contrôleur, la vue et l'IA** (`controller`, `view`, `ai`).

---

## 1. Package `model`

```mermaid
classDiagram
    direction TB

    class Couleur {
        <<enumeration>>
        ROUGE
        BLEU
        VERT
        JAUNE
        VIOLET
        MARRON
    }

    class Carte {
        <<abstract>>
        +getNom() String
        +estTactique() boolean
    }

    class CarteClan {
        -Couleur couleur
        -int valeur
        +getCouleur() Couleur
        +getValeur() int
    }

    class CarteTactique {
        <<abstract>>
        +estTactique() boolean
    }

    class TroupeElite {
        <<abstract>>
        -Couleur couleurChoisie
        -int valeurChoisie
        +getValeursPossibles() List~Integer~
        +fixer(Couleur, int) void
    }
    class Joker
    class Espion
    class PorteBouclier

    class ModeCombat {
        <<abstract>>
        +modifier(Borne) void
    }
    class ColinMaillard
    class CombatDeBoue

    class Ruse {
        <<abstract>>
        +appliquer(ContexteRuse) void
    }
    class ChasseurDeTete
    class Stratege
    class Banshee
    class Traitre

    Carte <|-- CarteClan
    Carte <|-- CarteTactique
    CarteTactique <|-- TroupeElite
    CarteTactique <|-- ModeCombat
    CarteTactique <|-- Ruse
    TroupeElite <|-- Joker
    TroupeElite <|-- Espion
    TroupeElite <|-- PorteBouclier
    ModeCombat <|-- ColinMaillard
    ModeCombat <|-- CombatDeBoue
    Ruse <|-- ChasseurDeTete
    Ruse <|-- Stratege
    Ruse <|-- Banshee
    Ruse <|-- Traitre
    CarteClan --> Couleur

    class Pioche {
        -Deque~Carte~ cartes
        +melanger() void
        +piocher() Carte
        +remettreDessous(Carte) void
        +estVide() boolean
        +taille() int
    }

    class Main {
        -List~Carte~ cartes
        -int tailleMax
        +ajouter(Carte) void
        +retirer(int index) Carte
        +get(int index) Carte
        +taille() int
    }

    class Joueur {
        -String nom
        -Main main
        -StrategieJoueur strategie
        -int nbTactiquesJouees
        +getNom() String
        +getMain() Main
        +getStrategie() StrategieJoueur
    }

    class StrategieJoueur {
        <<interface>>
        +choisirCoup(VueJeu, Joueur) Coup
        +choisirRevendications(VueJeu, Joueur) List~Integer~
    }

    class Cote {
        <<enumeration>>
        J1
        J2
        +adverse() Cote
    }

    class Borne {
        -int numero
        -Map~Cote, List~Carte~~ cartes
        -List~ModeCombat~ modes
        -Cote proprietaire
        -Cote premierComplet
        +poser(Cote, Carte) void
        +retirer(Cote, Carte) void
        +nbCartesRequises() int
        +estComplete(Cote) boolean
        +estRevendiquee() boolean
        +revendiquer(Cote) void
    }

    class Plateau {
        -List~Borne~ bornes
        +getBorne(int) Borne
        +nbBornes(Cote) int
        +aTroisAdjacentes(Cote) boolean
        +gagnant() Optional~Cote~
    }

    class TypeCombinaison {
        <<enumeration>>
        SUITE_COULEUR
        BRELAN
        COULEUR
        SUITE
        SOMME
    }

    class Combinaison {
        -TypeCombinaison type
        -int somme
        +compareTo(Combinaison) int
    }

    class EvaluateurCombinaison {
        +evaluer(List~Carte~, boolean sommeSeule) Combinaison
    }

    class Coup {
        <<abstract>>
    }
    class CoupPoserCarte {
        -int indexCarte
        -int numeroBorne
    }
    class CoupJouerRuse {
        -int indexCarte
    }

    Coup <|-- CoupPoserCarte
    Coup <|-- CoupJouerRuse

    Joueur *-- Main
    Joueur --> StrategieJoueur
    Main o-- Carte
    Pioche o-- Carte
    Plateau *-- "9" Borne
    Borne o-- Carte
    Borne o-- ModeCombat
    Borne --> Cote
    Combinaison --> TypeCombinaison
    EvaluateurCombinaison ..> Combinaison : crée
    StrategieJoueur ..> Coup : renvoie
```

---

## 2. Packages `controller`, `view` et `ai`

```mermaid
classDiagram
    direction TB

    namespace controller {
        class Jeu {
            <<abstract>>
            #Plateau plateau
            #Joueur[] joueurs
            #Pioche piocheClan
            #Cote joueurCourant
            #ArbitreRevendication arbitre
            #List~ObservateurJeu~ observateurs
            +jouerPartie() Cote
            #jouerTour() void
            #appliquer(Coup) void
            #verifierCoup(Coup) void
            #piocher(Joueur) void
            #tailleMain()* int
            #revendicationEnDebutDeTour()* boolean
            #creerPioches()* void
        }
        class JeuBase
        class JeuExpert
        class JeuTactique {
            -Pioche piocheTactique
            +peutJouerTactique(Cote) boolean
        }
        class Variante {
            <<enumeration>>
            BASE
            TACTIQUE
            EXPERT
        }
        class JeuFactory {
            +creerJeu(Variante, Joueur, Joueur)$ Jeu
        }
        class ArbitreRevendication {
            -EvaluateurCombinaison evaluateur
            +peutRevendiquer(Borne, Cote, List~Carte~ cartesVisibles) boolean
        }
        class VueJeu {
            <<interface>>
            +getPlateau() Plateau
            +getMain(Cote) Main
            +nbCartesPioche() int
        }
        class ObservateurJeu {
            <<interface>>
            +tourCommence(Joueur) void
            +coupJoue(Joueur, Coup) void
            +borneRevendiquee(Borne, Joueur) void
            +partieTerminee(Joueur) void
        }
        class CoupInvalideException
    }

    namespace view {
        class VueConsole {
            +afficherPlateau(Plateau) void
            +afficherMain(Main) void
        }
        class SaisieConsole {
            +lireEntier(String, int min, int max) int
            +lireTexte(String) String
        }
        class MenuConfiguration {
            +choisirVariante() Variante
            +configurerJoueur(int) Joueur
        }
        class StrategieHumaine
        class App {
            +main(String[])$ void
        }
    }

    namespace ai {
        class IAAleatoire {
            -Random random
        }
        class IAGloutonne
        class IAFactory {
            +creer(String type)$ StrategieJoueur
        }
    }

    Jeu <|-- JeuBase
    JeuBase <|-- JeuExpert
    JeuBase <|-- JeuTactique
    Jeu ..|> VueJeu
    Jeu --> ArbitreRevendication
    Jeu o-- ObservateurJeu
    JeuFactory ..> Jeu : crée
    JeuFactory ..> Variante

    VueConsole ..|> ObservateurJeu
    StrategieHumaine ..|> StrategieJoueur
    StrategieHumaine --> SaisieConsole
    StrategieHumaine --> VueConsole
    MenuConfiguration --> SaisieConsole
    MenuConfiguration ..> IAFactory
    App ..> MenuConfiguration
    App ..> JeuFactory

    IAAleatoire ..|> StrategieJoueur
    IAGloutonne ..|> StrategieJoueur
    IAFactory ..> IAAleatoire
    IAFactory ..> IAGloutonne

    class StrategieJoueur {
        <<interface>>
    }
```

---

## Choix de conception

| Exigence du sujet | Réponse dans la conception |
|---|---|
| **Héritage** | Hiérarchie `Carte` → `CarteClan` / `CarteTactique` → `TroupeElite`, `ModeCombat`, `Ruse` ; hiérarchie `Jeu` → `JeuBase` → `JeuExpert`, `JeuTactique`. |
| **Polymorphisme** | Chaque `Ruse` redéfinit `appliquer()` et chaque `ModeCombat` redéfinit `modifier()`. Le `Jeu` appelle `strategie.choisirCoup()` sans savoir s'il parle à un humain ou à une IA. |
| **Encapsulation** | Une `Borne` vérifie elle-même qu'on ne pose pas plus de cartes que `nbCartesRequises()` ; la `Main` vérifie les indices ; la `Pioche` lève une exception si elle est vide. |
| **Factory** | `JeuFactory.creerJeu(Variante, ...)` instancie la bonne sous-classe de `Jeu` ; `IAFactory` fait de même pour les IA. |
| **Patron de méthode** | `Jeu.jouerPartie()` et `jouerTour()` fixent le déroulement commun ; les sous-classes ne redéfinissent que les points qui changent (taille de main, moment de la revendication, pioches). |
| **Stratégie** | `StrategieJoueur` est implémentée par `StrategieHumaine` (console) et par les IA. On ajoute une IA en écrivant une seule classe. |
| **Observateur** | Le contrôleur prévient la vue via `ObservateurJeu` : il ne dépend jamais de la console, donc on pourrait brancher une interface graphique sans le modifier. |
| **Séparation des packages** | `model` ne dépend de rien ; `controller` dépend de `model` ; `view` et `ai` dépendent de `model` et `controller`. |

### Comment étendre

- **Nouvelle IA** : créer une classe dans `ai` qui implémente `StrategieJoueur`, puis l'enregistrer dans `IAFactory`.
- **Nouvelle variante** : créer une sous-classe de `Jeu` (ou de `JeuBase`), ajouter une valeur à `Variante` et un cas dans `JeuFactory`.
- **Nouvelle carte tactique** : créer une sous-classe de `TroupeElite`, `ModeCombat` ou `Ruse` ; le reste du jeu la manipule déjà comme une `Carte`.

### Points à discuter

- `StrategieJoueur` et `Coup` sont placés dans `model` pour que `Joueur` puisse les référencer sans dépendre de `controller`. On peut aussi les déplacer dans `controller` et faire porter la stratégie par `Jeu` plutôt que par `Joueur`.
- `JeuTactique` hérite de `JeuBase` pour réutiliser ses règles. Si les deux divergent trop, on pourra les faire hériter tous les deux directement de `Jeu`.
- La revendication « par preuve », avant que l'adversaire ait posé ses trois cartes, est la partie la plus délicate : elle est isolée dans `ArbitreRevendication` pour pouvoir la tester seule avec JUnit.
