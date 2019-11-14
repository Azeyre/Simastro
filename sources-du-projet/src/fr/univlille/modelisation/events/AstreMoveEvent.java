package fr.univlille.modelisation.events;

import java.util.EventObject;

import fr.univlille.modelisation.Vector;
import fr.univlille.modelisation.models.Astre;

public class AstreMoveEvent extends EventObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3782377978453631041L;
	private Astre astre;

	public AstreMoveEvent(Object source, Astre astre) {
		super(source);

		this.astre = astre;
	}

	public Astre getAstre() {
		return astre;
	}
	
	public Vector getVitesse() {
		return astre.getVitesse();
	}

	public Vector getPosition() {
		return astre.getPosition();
	}

	public double getMasse() {
		return astre.getMasse();
	}

}
