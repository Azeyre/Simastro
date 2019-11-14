package fr.univlille.modelisation.models;

import java.util.ArrayList;
import java.util.List;

import fr.univlille.modelisation.Univers;
import fr.univlille.modelisation.Vector;
import fr.univlille.modelisation.events.AstreMoveEvent;
import fr.univlille.modelisation.events.AstreSpawnEvent;
import fr.univlille.modelisation.events.UniversListener;
import fr.univlille.modelisation.views.Representation;

public abstract class Astre {

	protected String nom;
	protected Vector position; //position de l'astre ( ou les forces sont apliqué ) ==> pour un cercle pas le centre !
	protected Vector vitesse = new Vector(0, 0); // vitesse de l'astre ( simulé et vaisseau only )
	protected Vector nextPosition = new Vector(0,0); // position d'avant modification pour ne pas influencer pendant les calculs
	protected Univers univers;
	protected boolean selected;
	private final int TAILLE_TRAIL = 100;
	private Vector[] ancienPos = new Vector[TAILLE_TRAIL];
	private int index = 0;

	private static int SCALE = 20;

	double masse;
	protected Representation representation;

	public void setRepresentation(){
		this.representation = Representation.PLANETE;
	}

	public Representation getRepresentation() { return representation; }

	public void setRepresentation(Representation representation) {
		this.representation = representation;
	}

	public Astre() {
		setRepresentation();
	}

	private List<UniversListener> listeners = new ArrayList<>();

	public void setMasse(double masse) {
		this.masse = masse;
	}

	public double getMasse() {
		return this.masse;
	}

	public Vector getPosition() {
		return this.position;
	}

	public Vector getVitesse() {
		return this.vitesse;
	}

	void setPosition(Vector position) {
		ancienPos[index++] = position;
		if(index >= TAILLE_TRAIL) index = 0;
		this.position = position;
	}

	public void setVitesse(Vector vitesse) {
		this.vitesse = vitesse;
	}

	public void setNextPos() {
		this.setPosition(this.nextPosition);
	}

	public void setUnivers(Univers univers) {
		this.univers = univers;
	}

	public Univers getUnivers() {
		return univers;
	}

	public void applySpeed() {
		this.position.addVector(this.vitesse);
	}

	public void addListener(UniversListener universListener) {
		listeners.add(universListener);
		universListener.spawnAstre(new AstreSpawnEvent(this, this));
	}

	public abstract void doPhysics();

	void update() {
		for(UniversListener universListener: listeners) {
			universListener.updateAstre(new AstreMoveEvent(this, this));
		}
	}

	@Override
	public String toString() {
		return nom + " : [vitesse = " + vitesse + " ; position = " + position + " ; masse = "+ masse + "]";
	}

	public String getNom() {
		return nom;
	}

	public Vector[] getTrail() {
		return ancienPos;
	}

	public double getRayon(){
		return SCALE + Math.log(masse);
	}


	public Astre getCollider() {
		for(Astre astre:getUnivers().astres) {
			if(astre.getPosition().distance(this.getPosition()) < (astre.getRayon()/2 + this.getRayon()/2) && astre != this) {
				System.out.println(astre);
				return astre;
			}
		}
		return null;
	}

	public double getX() {
		return this.position.getX();
	}

	public double getY() {
		return this.position.getY();
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public double getVitesseMetre() {
		int i = index - 2;
		if(i == -2) i = TAILLE_TRAIL - 2;
		if(i == -1) i = TAILLE_TRAIL - 1;
		return Math.sqrt(Math.pow(position.getX() - ancienPos[i].getX(), 2) + Math.pow(position.getY() - ancienPos[i].getY(), 2));
	}

	public boolean isVaisseau(){
		return false;
	}

}