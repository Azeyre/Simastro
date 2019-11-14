package fr.univlille.modelisation.views.animations;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Animation {

	Animation next;
	
	AnimationType type;
	double[] positions;
	int frameDuration;
	
	public Animation(int frameDuration, AnimationType type, double[] positions) {
		this.frameDuration = frameDuration;
		this.type = type;
		this.positions = new double[positions.length];
		
		int i = 0;
		for(double position: positions) {
			this.positions[i] = position;
			i++;
		}
	}
	
	public GraphicsContext apply(GraphicsContext context) {
		context.setFill(Color.BLACK);
		
		switch (type) {
		case OVAL:
			context.fillOval(positions[0], positions[1], positions[2], positions[3]);
			break;
		case RECT:
			context.fillRect(positions[0], positions[1], positions[2], positions[3]);
			break;
		}
		
		frameDuration--;
		
		return context;
	}
	
	@Override
	public String toString() {
		return "["+type+
				","+frameDuration+"]";
	}
}
