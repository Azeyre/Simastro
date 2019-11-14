package fr.univlille.modelisation;

public class Vector {
	private double x;
	private double y;

	//Constructeur pour etoiles dans background
	public Vector(double debutX, double debutY, double finX, double finY){
		this.x = (Math.random() * ((finX - debutX) + 1)) + debutX;
		this.y = (Math.random() * ((finY - debutY) + 1)) + debutY;
	}

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public void setX(double value) {
		this.x = value;
	}

	public void setY(double value) {
		this.y = value;
	}

	public void addVector(Vector v) {
		this.x += v.getX();
		this.y += v.getY();
	}

	public void multiply(double value) {
		this.x = this.x*value;
		this.y = this.y*value;
	}

	public double distance(Vector v) {
		double x = this.getX() - v.getX();
		double y = this.getY() - v.getY();
		return Math.sqrt( Math.pow(x, 2)+Math.pow(y,2) );
	}

	//return a normalize vector that give the direction between this and v
	public Vector direction(Vector v) {
		if(this.distance(v) == 0) {return new Vector(0,0);}
		double x = (this.getX() - v.getX())/this.distance(v);
		double y = (this.getY() - v.getY())/this.distance(v);
		return new Vector(x,y);
	}

	public double angle(Vector v) { // un angle entre 0 et 360

		return 0;
	}

	public Vector copy() {
		return new Vector(this.getX(),this.getY());
	}

	@Override
	public String toString() {
		return "("+this.x+","+this.y+")";
	}
}
