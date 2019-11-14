package fr.univlille.modelisation.views;

import javafx.scene.paint.Color;

public enum Representation {
    PLANETE(Color.WHITE), VAISSEAU(Color.GREY), CERCLE(Color.AQUA);

    private Color couleur;

    Representation(Color couleur) {
        this.couleur = couleur;
    }

    public Color getCouleur() {
        return couleur;
    }
}