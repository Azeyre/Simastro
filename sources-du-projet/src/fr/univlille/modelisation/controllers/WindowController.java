package fr.univlille.modelisation.controllers;

import fr.univlille.modelisation.models.Astre;
import fr.univlille.modelisation.views.Graphique;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

public class WindowController implements EventHandler<MouseEvent> {

	private Graphique g;
	private double lastX = 0, lastY = 0;
	private boolean dragged = false;

	public void setUnivers(Graphique gr){
		g = gr;
	}

	@Override
	public void handle(MouseEvent e) {
		if(e.getEventType().equals(MouseEvent.MOUSE_CLICKED) && !e.getTarget().equals(g.getInfos())) {
			System.out.println("Click : X: " + e.getX() + " ; Y: " + e.getY());
			double xMouse = e.getX(), yMouse = e.getY();
			double distance, dx, dy, rayon;
			Astre temp = null;
			for(Astre a: g.getMap().keySet()){
				rayon = a.getRayon() * g.getZoom();
				dx = xMouse - (g.deltaX + (g.getWidth() / 2) - (rayon / 2) + a.getPosition().getX() * g.getZoom());
				dy = yMouse - (g.deltaY + (g.getHeight() / 2) - (rayon / 2) + a.getPosition().getY() * g.getZoom());
				distance = Math.sqrt(dx * dx + dy * dy);
				if(distance < rayon) {
					temp = a;
				}
			}
			if(!dragged) g.affichageInfos(temp);
			dragged = false;
		} else if(e.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
			lastX = e.getX();
			lastY = e.getY();
			g.getScene().setCursor(Cursor.CLOSED_HAND);
		}  else if(e.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
			g.getScene().setCursor(Cursor.DEFAULT);
		}	else if(e.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
			dragged = true;
			g.enleveSuivi();
			g.deltaX -= lastX - e.getX();
			g.deltaY -= lastY - e.getY();
			lastX = e.getX();
			lastY = e.getY();
			g.moveBackground();
		}
	}
}
