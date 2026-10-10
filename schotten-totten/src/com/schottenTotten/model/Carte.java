package com.schottenTotten.model;

/**
 * Une carte Clan : une couleur et une valeur entre 1 et 9.
 */
public class Carte {

    private Couleur couleur;
    private int valeur;

    /**
     * Cree une carte.
     * Lance une IllegalArgumentException si la couleur est nulle
     * ou si la valeur n'est pas entre 1 et 9.
     */
    public Carte(Couleur couleur, int valeur) {
        if (couleur == null) {
            throw new IllegalArgumentException("La couleur ne peut pas etre nulle");
        }
        if (valeur < 1 || valeur > 9) {
            throw new IllegalArgumentException("Valeur invalide : " + valeur);
        }
        this.couleur = couleur;
        this.valeur = valeur;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public int getValeur() {
        return valeur;
    }

    /** Deux cartes sont egales si elles ont meme couleur et meme valeur. */
    public boolean equals(Object o) {
        if (!(o instanceof Carte)) {
            return false;
        }
        Carte autre = (Carte) o;
        return this.couleur == autre.couleur && this.valeur == autre.valeur;
    }

    /** Exemple d'affichage : "7 ROUGE". */
    public String toString() {
        return valeur + " " + couleur;
    }
}
