package fr.univlille.modelisation.controllers;

import fr.univlille.modelisation.views.Graphique;
import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;

public class ScrollController implements EventHandler<ScrollEvent>{

	private Graphique g;

	public ScrollController(Graphique g){
		this.g = g;
	}

	@Override
	public void handle(ScrollEvent event) {
		if(event.getDeltaY() > 0) {
			g.zoomIn();
		} else g.zoomOut();
		g.moveBackground();
	}

}
