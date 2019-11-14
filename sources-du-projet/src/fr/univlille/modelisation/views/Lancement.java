package fr.univlille.modelisation.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.univlille.modelisation.FichierIllisible;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sun.security.mscapi.PRNG;

public class Lancement extends Application {
	Stage principalStage = new Stage();

	public void start(Stage stage) throws Exception {
		InnerShadow IS = new InnerShadow();

		VBox root = new VBox();
		Image im = new Image(getClass().getResourceAsStream("/res/img/solar_system.jpg"));
		root.setBackground(new Background(new BackgroundImage(im, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

		Label name = new Label("SIMASTRO");
		name.setFont(new Font("Cambria", 80));
		name.setTextFill(Color.WHITE);
		name.setEffect(IS);

		ComboBox<Label> fichier = new ComboBox<>();
		ArrayList<Label> aaa = new ArrayList<>();
		try (final InputStream is = getClass().getResourceAsStream("/res/fichiers")) {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String st;

			while ((st = br.readLine()) != null) {
				System.out.println(st);
				aaa.add(new Label(st));
			}
		}
		fichier.getItems().setAll(aaa);
		fichier.setValue(new Label("05_Test"));
		fichier.setId("SelectionFichier");

		Label nouveau = new Label("Nouveau");
		nouveau.setFont(new Font("Cambria", 40));
		nouveau.setCursor(Cursor.HAND);
		nouveau.setId("Menu");

		Label debut = new Label("Demarrer");
		debut.setFont(new Font("Cambria", 40));
		debut.setCursor(Cursor.HAND);
		debut.setId("Menu");

		Label charger = new Label("Charger");
		charger.setFont(new Font("Cambria", 40));
		charger.setCursor(Cursor.HAND);
		charger.setId("Menu");

		Label quitter = new Label("Quitter");
		quitter.setFont(new Font("Cambria", 40));
		quitter.setCursor(Cursor.HAND);
		quitter.setId("Menu");

		root.getChildren().addAll(name, fichier, debut, nouveau, charger, quitter);
		root.setAlignment(Pos.TOP_CENTER);
		VBox.setMargin(name, new Insets(50, 0, 150, 0));

		Scene scene = new Scene(root, 1000, 600);
		scene.getStylesheets().add(getClass().getResource("/res/styleLancement.css").toExternalForm());
		principalStage.setScene(scene);
		principalStage.setTitle("SIMASTRO");
		principalStage.show();

		// Gestion Evenement
		debut.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				// Ajouter l'option pour changer d'image
				Graphique g;
				try {
					g = new Graphique(fichier.getSelectionModel().getSelectedItem().getText());
					g.start(new Stage());
					principalStage.close();
				} catch (FileNotFoundException e1){
					final JPanel panel = new JPanel();
				    JOptionPane.showMessageDialog(panel, "Le fichier n'existe pas", "Erreur", JOptionPane.ERROR_MESSAGE);
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException | FichierIllisible e1){
					final JPanel panel = new JPanel();
				    JOptionPane.showMessageDialog(panel, "Parametre(s) manquant(s)", "Erreur", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e1) {
					final JPanel panel = new JPanel();
				    JOptionPane.showMessageDialog(panel, "Erreur lors de la lecture du fichier", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		nouveau.setOnMouseClicked(e -> new CreationUnivers());

		charger.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Astro files (*.astro)", "*.astro");
				fileChooser.getExtensionFilters().add(extFilter);
				File temp = fileChooser.showOpenDialog(principalStage);
				if (temp != null) {
					Graphique g;
					try {
						g = new Graphique(temp.getAbsolutePath(), true);
						g.start(new Stage());
					} catch (FileNotFoundException e1){
						final JPanel panel = new JPanel();
					    JOptionPane.showMessageDialog(panel, "Le fichier n'existe pas", "Erreur", JOptionPane.ERROR_MESSAGE);
					} catch (NumberFormatException | ArrayIndexOutOfBoundsException | FichierIllisible e1){
						final JPanel panel = new JPanel();
					    JOptionPane.showMessageDialog(panel, "Parametre(s) manquant(s)", "Erreur", JOptionPane.ERROR_MESSAGE);
					} catch (IOException e1) {
						final JPanel panel = new JPanel();
					    JOptionPane.showMessageDialog(panel, "Erreur lors de la lecture du fichier", "Erreur", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		quitter.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				principalStage.close();
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
