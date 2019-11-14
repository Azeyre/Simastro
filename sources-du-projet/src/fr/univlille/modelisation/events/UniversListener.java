package fr.univlille.modelisation.events;

import java.util.EventListener;

import fr.univlille.modelisation.events.AstreMoveEvent;

public interface UniversListener extends EventListener {
	void updateAstre(AstreMoveEvent event);
	void spawnAstre(AstreSpawnEvent event);
}
