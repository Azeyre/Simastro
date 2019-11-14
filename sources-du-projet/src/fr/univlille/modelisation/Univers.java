package fr.univlille.modelisation;

import fr.univlille.modelisation.models.Astre;
import fr.univlille.modelisation.models.Fixe;
import fr.univlille.modelisation.views.Graphique;

import java.util.ArrayList;
import java.util.List;

public class Univers {

    public List<Astre> astres;
    private Graphique display;

    public double G;
	public double dt;
	public double fa;
	public double rayon;

    public Univers(Graphique display, double G, double dt, double fa, double rayon) {
        this.astres = new ArrayList<>();
        this.display = display;
        this.G = G;
        this.dt = dt;
        this.fa = fa;
        this.rayon = rayon;
    }

    public void add(Astre astre) {
        astres.add(astre);

        astre.setUnivers(this);
        astre.addListener(display);
    }

    public void remove(Astre astre) {
        astres.remove(astre);
    }

    void update() {
    	astres.forEach(astre -> astre.doPhysics());
    	astres.forEach(astre -> astre.setNextPos());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Astre astre : astres) {
            stringBuilder.append(astre.toString()).append("; ");
        }

        return stringBuilder.toString();
    }
    
    public double getDt() {
		return dt;
	}
    
    public double getFa() {
		return fa;
	}
    
    public double getG() {
		return G;
	}
    
    public double getRayon() {
		return rayon;
	}
    
    private Fixe getFixe(String name) {
		for(Astre a : astres) {
			if(a.getNom().equals(name)) {
				return (Fixe) a;
			}
		}
		return null;
	}
    
}