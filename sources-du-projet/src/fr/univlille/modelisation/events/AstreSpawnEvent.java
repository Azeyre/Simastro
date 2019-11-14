package fr.univlille.modelisation.events;

import java.util.EventObject;

import fr.univlille.modelisation.models.Astre;

public class AstreSpawnEvent extends EventObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4469799038391471914L;
	private Astre astre;

    public AstreSpawnEvent(Object source, Astre astre) {
        super(source);

        this.astre = astre;
    }

    public Astre getAstre() {
        return astre;
    }

}
