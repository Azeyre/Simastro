package fr.univlille.modelisation.models;

import fr.univlille.modelisation.Vector;

public class Simule extends Astre {

	public Simule(String nom, Vector position, Vector vitesse, double masse) {
		super();
		this.nom = nom;
		this.position = position;
		this.vitesse = vitesse;
		this.masse = masse;
		this.nextPosition = position;
	}

	@Override
	public void doPhysics() {
		Astre collider = this.getCollider();
		if(collider != null){
			System.out.println("collide");
			Vector sommeForce = new Vector(0,0);
			Vector f1 = this.getVitesse();
			f1.multiply(this.getMasse());
			Vector f2 = collider.getVitesse();
			f2.multiply(collider.getMasse());
			
			sommeForce.addVector(f1);
			sommeForce.addVector(f2);
		}
		Vector Va = new Vector(0,0);
		double G = super.getUnivers().getG();
		for(Astre astre:getUnivers().astres) {
			if (astre.equals(this)){continue;}
			double masse = this.getMasse()*astre.getMasse();
			double dist = Math.pow(this.getPosition().distance(astre.getPosition()), 2);
			double a = (G * (masse/dist))/this.getMasse();
			Vector dir = astre.getPosition().direction(this.getPosition());
			dir.multiply(a);
			Va.addVector(dir);
			//System.out.println("a : "+a+"| masse : "+masse+"|dist : "+this.getPosition().distance(astre.getPosition())+"| dist2 : "+dist);
		}

		this.getVitesse().addVector(Va);
		this.setVitesse(this.getVitesse());

		this.nextPosition = this.position.copy();
		this.nextPosition.addVector(this.getVitesse());

		//System.out.println("next pos : "+this.nextPosition + "| vitesse : "+this.vitesse);

		/*a = (G * (m1*m2)/(d*d))/m1
		 * va = a * vnormal
		 *
		 *
		 */
		update();
	}

}
