package fr.univlille.modelisation.views;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreationUnivers {

	/*
	 * Ouverture d'une fenetre permettant de creer un univers personnalise
	 */

	@SuppressWarnings("deprecation")
	public CreationUnivers(){
		try {
			Stage s = new Stage();
			URL url = new File(getClass().getResource("/res/fxml/creationUnivers.fxml").getFile()).toURL();
			Parent settings = FXMLLoader.load(url);
			s.setScene(new Scene(settings));
			s.initModality(Modality.APPLICATION_MODAL);
			s.setWidth(1000);
			s.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}