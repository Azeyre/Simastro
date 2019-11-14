package fr.univlille.modelisation.models;

import fr.univlille.modelisation.Vector;
import fr.univlille.modelisation.views.Representation;

public class Vaisseau extends Astre {

	private boolean left = false,
			right = false,
			up = false,
			down = false;

	private double pprincipal, pretro;
	private double angle;
	private double[] pointsX, pointsY;


	public Vaisseau(String nom, Vector position, Vector vitesse, double pprincipal, double pretro, double masse) {
		super();
		this.nom = nom;
		this.position = position;
		this.vitesse = vitesse;
		this.masse = masse;
		this.nextPosition = position;
		this.pprincipal = pprincipal;
		this.pretro = pretro;
		this.pointsX = new double[5];
		this.pointsY = new double[5];
		this.angle = 0;
	}

	@Override
	public void setRepresentation() {
		this.representation = Representation.VAISSEAU;
	}

	@Override
	public void doPhysics() {
		Vector Va = new Vector(0, 0);
		double G = super.getUnivers().getG();
		for (Astre astre : getUnivers().astres) {
			if (astre.equals(this)) {
				continue;
			}
			double masse = this.getMasse() * astre.getMasse();
			double dist = Math.pow(this.getPosition().distance(astre.getPosition()), 2);
			double a = (G * (masse / dist)) / this.getMasse();
			Vector dir = astre.getPosition().direction(this.getPosition());
			dir.multiply(a);
			Va.addVector(dir);
		}
		
		//vaisseau déplacement ( à enlever en cas d'erreur )
		if (left){
		    this.angle = this.angle-0.1;
		}
		if (right){
		    this.angle = this.angle+0.1;
		}
			
		Vector vDirection = new Vector(Math.cos((this.angle%360)/Math.PI),Math.sin((this.angle%360)/Math.PI));
		vDirection.multiply(2); // a remplacer par force 
		
		if(up){
		    this.getVitesse().addVector(vDirection);
		}
		if(down){
		    vDirection.multiply(-1);
		    this.getVitesse().addVector(vDirection);
		}
		//fin déplacement 

		this.getVitesse().addVector(Va);
		this.setVitesse(this.getVitesse());

		this.nextPosition = this.position.copy();
		this.nextPosition.addVector(this.getVitesse());

		// System.out.println("next pos : "+this.nextPosition + "| vitesse :
		// "+this.vitesse);

		/*
		 * a = (G * (m1*m2)/(d*d))/m1 va = a * vnormal
		 *
		 *
		 */
		//angle = (angle + 0.1) % 360;
		update();
	}

	public void direction(boolean up, boolean down, boolean left, boolean right){
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
	}

	public double getAngle() {
		return angle;
	}

	public double[] getAffichageX() {
		pointsX[0] = position.getX();
		pointsX[1] = position.getX() + 20;
		pointsX[2] = position.getX() + 30;
		pointsX[3] = position.getX() + 20;
		pointsX[4] = position.getX();
		return pointsX;
	}

	public double[] getAffichageY() {
		pointsY[0] = position.getY();
		pointsY[1] = position.getY();
		pointsY[2] = position.getY() + 10;
		pointsY[3] = position.getY() + 20;
		pointsY[4] = position.getY() + 20;
		return pointsY;
	}

	@Override
	public boolean isVaisseau(){
		return true;
	}
	
	public double getPpPrincipal() {
		return pprincipal;
	}
	
	public double getPpRetro() {
		return pretro;
	}
}