package fr.univlille.modelisation.models;

import fr.univlille.modelisation.Vector;

public class Fixe extends Astre {

	public Fixe(String nom, Vector pos, double masse) {
		super();
		this.nom = nom;
		this.position = pos;
		this.masse = masse;
		this.setVitesse(new Vector(0,0));
	}

	@Override
	public void doPhysics() {
		this.nextPosition = this.position.copy();
		update();
	}
	
}
