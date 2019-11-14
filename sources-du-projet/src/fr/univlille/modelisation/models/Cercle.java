package fr.univlille.modelisation.models;

import fr.univlille.modelisation.Vector;
import fr.univlille.modelisation.views.Representation;

public class Cercle extends Astre {

	Astre centre;
	double periode, posx, posy, rayon;
	double dPeriode = 0;

	public Cercle(String nom, Astre centre, double posx, double posy, double masse, double periode) {
		super();
		this.nom = nom;
		this.centre = centre;
		this.posx = posx;
		this.posy = posy;
		this.masse = masse;
		this.periode = periode;
		this.rayon = Math.sqrt(Math.pow(centre.getX() - posx, 2) + Math.pow(centre.getY() - posy, 2));
		this.position = getAstrePos();
	}

	@Override
	public void setRepresentation() {
		this.representation = Representation.CERCLE;
	}

	public Vector getAstrePos() {
		double x = centre.getX() + Math.cos(this.dPeriode / this.periode * 2 * Math.PI) * this.rayon;
		double y = centre.getY() + Math.sin(this.dPeriode / this.periode * 2 * Math.PI) * this.rayon;
		return new Vector(x, y);
	}

	@Override
	public void doPhysics() {
		this.nextPosition = getAstrePos();
		this.dPeriode = (this.dPeriode + univers.getDt()) % this.periode;

		update();
	}
	
	public String getCentreNom() {
		return centre.getNom();
	}
	
	public double getPeriode() {
		return periode;
	}
}
