package fr.univlille.modelisation.models;

import fr.univlille.modelisation.Vector;

public class Ellipse extends Astre {

	private Fixe foyer1, foyer2;
	private double periode, posx, posy;
	private double dPeriode = 0;
	
	public Ellipse(String nom, double posx, double posy, Fixe foyer1, Fixe foyer2, double masse, double periode) {
		super();
		this.nom = nom;
		this.posx = posx;
		this.posy = posy;
		this.foyer1 = foyer1;
		this.foyer2 = foyer2;
		this.masse = masse;
		this.periode = periode;
		this.position = getAstrePos();
		
	}
	
	private Vector getCenter() {
		double x = (foyer1.getX() + foyer2.getX()) / 2;
		double y = (foyer1.getY() + foyer2.getY()) / 2;
		return new Vector(x, y);
	}
	
	private double getCos() {
		
		return (foyer2.getX() - foyer1.getX()) / Math.sqrt(Math.pow(foyer2.getY() - foyer1.getY(), 2)
				+ Math.pow(foyer2.getX() - foyer1.getX(), 2));
		
	}
	
	private double getSin() {
		
		return (foyer2.getY() - foyer1.getY()) / Math.sqrt(Math.pow(foyer2.getY() - foyer1.getY(), 2)
				+ Math.pow(foyer2.getX() - foyer1.getX(), 2));
		
	}
	
	private double getA() {
		
		return (Math.sqrt(Math.pow(posx - foyer1.getX(), 2) + Math.pow(posy - foyer1.getX(), 2)) / 2)
				+ (Math.sqrt(Math.pow(posx - foyer2.getX(), 2) + Math.pow(posy - foyer2.getX(), 2)) / 2);
		
	}
	
	private double getB() {
		
		return Math.sqrt(Math.pow(getA(), 2) - (0.25 * (Math.pow(foyer2.getX() - foyer1.getX(), 2)
				+ Math.pow(foyer2.getY() - foyer1.getY(), 2))));
				
	}
	
	public Vector getAstrePos() {
		
		double x = this.getCenter().getX() + this.getA() * this.getCos() * Math.cos(this.dPeriode/this.periode*2*Math.PI)
				- this.getB() * this.getSin() * Math.sin(this.dPeriode/this.periode*2*Math.PI);
		
		double y = this.getCenter().getY() + this.getA() * this.getSin() * Math.cos(this.dPeriode/this.periode*2*Math.PI)
				+ this.getB() * this.getCos() * Math.sin(this.dPeriode/this.periode*2*Math.PI);

		return new Vector(x, y);
	}

	@Override
	public void doPhysics() {
		this.nextPosition = getAstrePos();
		this.dPeriode = (this.dPeriode+univers.getDt())%this.periode;

		update();
	}

	public String getFoyer1Nom() {
		return foyer1.getNom();
	}
	
	public String getFoyer2Nom() {
		return foyer2.getNom();
	}
	
	public double getPeriode() {
		return periode;
	}
}
