package fr.univlille.modelisation.controllers;

import fr.univlille.modelisation.models.Vaisseau;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class VaisseauController implements EventHandler<KeyEvent> {

	private Vaisseau v;
	private boolean up, down, left, right;

	public VaisseauController(Vaisseau v){
		this.v = v;
	}

	@Override
	public void handle(KeyEvent event) {
		if(event.getEventType().equals(KeyEvent.KEY_PRESSED)){
			direction(event.getCode());
		}else if(event.getEventType().equals(KeyEvent.KEY_RELEASED)) {
			direction(event.getCode(), false);
		}

		event.consume();
	}

	private void direction(KeyCode key){
		direction(key, true);
	}

	private void direction(KeyCode key, boolean active) {
		switch(key){
			case LEFT:
				if(left != active) left = active;
				break;
			case RIGHT:
				if(right != active) right = active;
				break;
			case UP:
				if(up != active) up = active;
				break;
			case DOWN:
				if(down != active) down = active;
				break;
			default:
				break;
		}

		v.direction(up, down, left, right);
	}


}