package fr.univlille.modelisation.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import fr.univlille.modelisation.FichierIllisible;
import fr.univlille.modelisation.Vector;
import fr.univlille.modelisation.models.Astre;
import fr.univlille.modelisation.models.Cercle;
import fr.univlille.modelisation.models.Ellipse;
import fr.univlille.modelisation.models.Fixe;
import fr.univlille.modelisation.models.Simule;
import fr.univlille.modelisation.models.Vaisseau;
import fr.univlille.modelisation.views.Graphique;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FenetreUnivers {

	private static final String regexNombrePositif = "[0-9]*[\\.]?[0-9]*";
	private static final String regexNombreRelatif = "[\\-]?[0-9]*[\\.]?[0-9]*";

	@FXML private Label txtError;

	@FXML private TextField g;
	@FXML private TextField dt;
	@FXML private TextField fa;
	@FXML private TextField rayon;

	@FXML private ChoiceBox<String> choiceBox;
	@FXML private TextField nom;
	@FXML private TextField masse;
	@FXML private TextField posX;
	@FXML private TextField posY;
	@FXML private HBox hVitesse;
	@FXML private HBox hEllipse;
	@FXML private HBox hCercle;
	@FXML private HBox hVaisseau;
	@FXML private TextField vitX;
	@FXML private TextField vitY;
	@FXML private ChoiceBox<String> foyer1;
	@FXML private ChoiceBox<String> foyer2;
	@FXML private TextField periodeEllipse;
	@FXML private ChoiceBox<String> choiceAstre;
	@FXML private TextField periodeCercle;
	@FXML private TextField ppPrincipal;
	@FXML private TextField ppRetro;

	@FXML private Button save;
	@FXML private TextField chemin;
	@FXML private TableView<TempAstre> tableView;

	private ArrayList<TextField> paramUnivers = new ArrayList<>();
	private ArrayList<TextField> paramFixe = new ArrayList<>();
	private ArrayList<TextField> paramCercle = new ArrayList<>();
	private ArrayList<TextField> paramEllipse = new ArrayList<>();
	private ArrayList<TextField> paramSimule = new ArrayList<>();
	private ArrayList<TextField> paramVaisseau = new ArrayList<>();
	private ArrayList<TextField> paramSelected;

	private ArrayList<Astre> astreAjoute = new ArrayList<>();
	private ArrayList<Fixe> fixeAjoute = new ArrayList<>();
	private ArrayList<Cercle> cercleAjoute = new ArrayList<>();
	private ArrayList<Ellipse> ellipseAjoute = new ArrayList<>();
	private ArrayList<Simule> simuleAjoute = new ArrayList<>();
	private Vaisseau vaisseauAjoute = null;

	private File file;

	private final String cercle = "Cercle", ellipse = "Ellipse", fixe = "Fixe", simule = "Simule", vaisseau = "Vaiseau";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initialize() {
		chemin.setEditable(false);

		paramUnivers.add(g);
		paramUnivers.add(dt);
		paramUnivers.add(fa);
		paramUnivers.add(rayon);

		paramFixe.add(masse);
		paramFixe.add(posX);
		paramFixe.add(posY);

		paramCercle.addAll(paramFixe);
		paramCercle.add(periodeCercle);

		paramEllipse.addAll(paramFixe);
		paramEllipse.add(periodeEllipse);
		paramEllipse.add(vitX);
		paramEllipse.add(vitY);

		paramSimule.addAll(paramFixe);
		paramSimule.add(vitX);
		paramSimule.add(vitY);

		paramVaisseau.addAll(paramFixe);
		paramVaisseau.add(vitX);
		paramVaisseau.add(vitY);
		paramVaisseau.add(ppPrincipal);
		paramVaisseau.add(ppRetro);

		g.textProperty().addListener(new InputValue(g, regexNombrePositif));
		dt.textProperty().addListener(new InputValue(dt, regexNombrePositif));
		fa.textProperty().addListener(new InputValue(fa, regexNombrePositif));
		rayon.textProperty().addListener(new InputValue(rayon, regexNombrePositif));

		masse.textProperty().addListener(new InputValue(masse, regexNombrePositif));
		posX.textProperty().addListener(new InputValue(posX, regexNombreRelatif));
		posY.textProperty().addListener(new InputValue(posY, regexNombreRelatif));
		vitX.textProperty().addListener(new InputValue(vitX, regexNombreRelatif));
		vitY.textProperty().addListener(new InputValue(vitY, regexNombreRelatif));
		periodeEllipse.textProperty().addListener(new InputValue(periodeEllipse, regexNombrePositif));
		periodeCercle.textProperty().addListener(new InputValue(periodeCercle, regexNombrePositif));
		ppPrincipal.textProperty().addListener(new InputValue(ppPrincipal, regexNombrePositif));
		ppRetro.textProperty().addListener(new InputValue(ppRetro, regexNombrePositif));

		// Cercle et Ellipse pas disponible des le depart car besoin d'autres
		// astres pour etre ajouter
		choiceAstre.getItems().add("");
		foyer1.getItems().add("");
		foyer2.getItems().add("");

		ObservableList<String> astres = FXCollections.observableArrayList(fixe, vaisseau, simule);
		choiceBox.getItems().addAll(astres);
		choiceBox.getSelectionModel().selectFirst();
		choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
				lockAll();
				clearText();
				if (choiceBox.getItems().get((Integer) number2).equals(cercle)) {
					unlock(hCercle);
					paramSelected = paramCercle;
				} else if (choiceBox.getItems().get((Integer) number2).equals(ellipse)) {
					unlock(hVitesse);
					unlock(hEllipse);
					paramSelected = paramEllipse;
				} else if (choiceBox.getItems().get((Integer) number2).equals(vaisseau)) {
					unlock(hVitesse);
					unlock(hVaisseau);
					paramSelected = paramVaisseau;
				} else if (choiceBox.getItems().get((Integer) number2).equals(simule)) {
					unlock(hVitesse);
					paramSelected = paramSimule;
				} else paramSelected = paramFixe;

			}
		});
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		TableColumn colType = new TableColumn<>("Type");
		colType.setCellValueFactory(new PropertyValueFactory<>("type"));
		TableColumn colNom = new TableColumn<>("Nom");
		colNom.setCellValueFactory(new PropertyValueFactory<>("nomA"));
		TableColumn colMasse = new TableColumn<>("Masse");
		colMasse.setCellValueFactory(new PropertyValueFactory<>("masseA"));
		TableColumn colPosX = new TableColumn<>("Pos X");
		colPosX.setCellValueFactory(new PropertyValueFactory<>("posx"));
		TableColumn colPosY = new TableColumn<>("Pos Y");
		colPosY.setCellValueFactory(new PropertyValueFactory<>("posy"));
		TableColumn colVitX = new TableColumn<>("Vit X");
		colVitX.setCellValueFactory(new PropertyValueFactory<>("vitx"));
		TableColumn colVitY = new TableColumn<>("Vit Y");
		colVitY.setCellValueFactory(new PropertyValueFactory<>("vity"));
		TableColumn colFoyer1 = new TableColumn<>("Foyer 1");
		colFoyer1.setCellValueFactory(new PropertyValueFactory<>("f1"));
		TableColumn colFoyer2 = new TableColumn<>("Foyer 2");
		colFoyer2.setCellValueFactory(new PropertyValueFactory<>("f2"));
		TableColumn colPeriodeEllipse = new TableColumn<>("Periode El");
		colPeriodeEllipse.setCellValueFactory(new PropertyValueFactory<>("perEl"));
		TableColumn colCentre = new TableColumn<>("Centre");
		colCentre.setCellValueFactory(new PropertyValueFactory<>("centre"));
		TableColumn colPeriodeCercle = new TableColumn<>("Periode Ce");
		colPeriodeCercle.setCellValueFactory(new PropertyValueFactory<>("perCe"));
		TableColumn colPpPrincipal = new TableColumn<>("PP Pr");
		colPpPrincipal.setCellValueFactory(new PropertyValueFactory<>("ppPr"));
		TableColumn colPpRetro = new TableColumn<>("PP Re");
		colPpRetro.setCellValueFactory(new PropertyValueFactory<>("ppRe"));
		tableView.getColumns().addAll(colType, colNom, colMasse, colPosX, colPosY, colVitX, colVitY, colFoyer1,
				colFoyer2, colPeriodeEllipse, colCentre, colPeriodeCercle, colPpPrincipal, colPpRetro);
		tableView.setPlaceholder(new Label("Aucun astre ajouté."));

		save.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Astro files (*.astro)", "*.astro");
			fileChooser.getExtensionFilters().add(extFilter);
			file = fileChooser.showSaveDialog((Stage) save.getScene().getWindow());
			if (file != null) {
				chemin.setText("" + file.getAbsolutePath());
			}
		});
		txtError.setTextFill(Color.RED);
		clearText();
		paramSelected = paramFixe;
	}

	public void ajout() {
		for(TextField txtF: paramSelected){
			System.out.println("Param : " + txtF.getText());
		}

		boolean erreur = false;
		if (checkValueText(nom) && astreExistant(nom.getText())) {
			System.out.println("Erreur nom");
			erreur = true;
		}
		for (TextField t : paramSelected) {
			if (t.equals(posX) || t.equals(posY) || t.equals(vitX) || t.equals(vitY)) {
				if (!checkValueText(t)) {
					System.out.println("Erreur value 1");
					erreur = true;
				}
			} else {
				if (!checkValueInteger(t, 0)) {
					System.out.println("Erreur value 2");
					erreur = true;
				}
			}
		}
		if(paramSelected.equals(paramCercle)){
			if(choiceAstre.getSelectionModel().getSelectedIndex() == 0) {
				choiceAstre.setStyle("-fx-background-color: #F00");
				erreur = true;
			}
		}
		if(paramSelected.equals(paramEllipse)) {
			if(foyer1.getSelectionModel().getSelectedIndex() == 0) {
				foyer1.setStyle("-fx-background-color: #F00");
				erreur = true;
			}
			if(foyer2.getSelectionModel().getSelectedIndex() == 0 || foyer2.getSelectionModel().getSelectedItem().equals(foyer1.getSelectionModel().getSelectedItem())) {
				foyer2.setStyle("-fx-background-color: #F00");
				erreur = true;
			}
		}
		System.out.println(erreur);
		if (!erreur) {
			System.out.println("Ajout");

			String typeAstre = "";
			String nomAstre = nom.getText();
			double x = Double.valueOf(posX.getText());
			double y = Double.valueOf(posY.getText());
			Vector pos = new Vector(x, y);
			double masseD = Double.valueOf(masse.getText());
			TempAstre t = null;
			if(choiceBox.getSelectionModel().getSelectedItem().equals(fixe)){
				Fixe fixe = new Fixe(nomAstre, pos, masseD);
				astreAjoute.add(fixe);
				fixeAjoute.add(fixe);
				typeAstre = "Fixe";
			}
			else if(choiceBox.getSelectionModel().getSelectedItem().equals(simule)){
				double vitXTemp = Double.valueOf(vitX.getText());
				double vitYTemp = Double.valueOf(vitY.getText());
				Vector v = new Vector(vitXTemp, vitYTemp);
				Simule simule = new Simule(nomAstre, pos, v, masseD);
				astreAjoute.add(simule);
				simuleAjoute.add(simule);
				typeAstre = "Simule";
			}
			else if(choiceBox.getSelectionModel().getSelectedItem().equals(cercle)){
				Astre centre = astreAjoute.get(choiceAstre.getSelectionModel().getSelectedIndex() - 1);
				double per = Double.valueOf(periodeCercle.getText());
				Cercle c = new Cercle(nomAstre, centre, x, y, masseD, per);
				astreAjoute.add(c);
				cercleAjoute.add(c);
				typeAstre = "Cercle";
			}
			else if(choiceBox.getSelectionModel().getSelectedItem().equals(ellipse)){
				double per = Double.valueOf(periodeEllipse.getText());
				Fixe f1 = fixeAjoute.get(foyer1.getSelectionModel().getSelectedIndex() - 1);
				Fixe f2 = fixeAjoute.get(foyer2.getSelectionModel().getSelectedIndex() - 1);
				Ellipse el = new Ellipse(nomAstre, x, y, f1, f2, masseD, per);
				astreAjoute.add(el);
				ellipseAjoute.add(el);
				typeAstre = "Ellipse";
			} else {
				double vitXTemp = Double.valueOf(vitX.getText());
				double vitYTemp = Double.valueOf(vitY.getText());
				Vector v = new Vector(vitXTemp, vitYTemp);
				double ppp = Double.valueOf(ppPrincipal.getText());
				double ppr = Double.valueOf(ppRetro.getText());
				vaisseauAjoute = new Vaisseau(nomAstre, pos, v, ppp, ppr, masseD);
				astreAjoute.add(vaisseauAjoute);
				typeAstre = "Vaisseau";
				choiceBox.getItems().remove(1);
			}
			t = new TempAstre(typeAstre);
			tableView.getItems().add(t);

			int index = choiceAstre.getItems().size();

			for(Astre a: astreAjoute){
				choiceAstre.getItems().add(a.getNom());
			}
			for(int i = 1 ; i < index ; i++){
				choiceAstre.getItems().remove(i);
			}

			index = foyer1.getItems().size();

			for(Fixe f: fixeAjoute){
				foyer1.getItems().add(f.getNom());
				foyer2.getItems().add(f.getNom());
			}
			for(int i = 1 ; i < index ; i++){
				foyer1.getItems().remove(i);
				foyer2.getItems().remove(i);
			}

			if (astreAjoute.size() == 1)
				choiceBox.getItems().add(cercle);
			else if (fixeAjoute.size() == 2)
				choiceBox.getItems().add(ellipse);

		}
	}

	public void apply() throws NumberFormatException, FichierIllisible, IOException {
		txtError.setText("");
		boolean erreur = false;
		for(TextField t: paramUnivers){
			if(!checkValueInteger(t, 0)){
				erreur = true;
			}
		}
		if(!erreur && tableView.getItems().size() > 0 && file != null){
			String s = "# Creation Univers Personnalise\n";
			s += "PARAMS G=" + g.getText() + " dt=" + dt.getText() + " fa=" + fa.getText() + " rayon=" + rayon.getText() + "\n";
			for(Fixe f: fixeAjoute) {
				s += f.getNom() + ": Fixe masse=" + Double.valueOf(f.getMasse()) + " posx=" + Double.valueOf(f.getPosition().getX()) + " posy=" + Double.valueOf(f.getPosition().getY()) + "\n";
			}
			for(Cercle c: cercleAjoute) {
				s += c.getNom() + ": Cercle masse=" + Double.valueOf(c.getMasse()) + " posx=" + Double.valueOf(c.getPosition().getX()) + " posy=" + Double.valueOf(c.getPosition().getY()) + " centre=" + c.getCentreNom() + " periode=" + Double.valueOf(c.getPeriode()) + "\n";
			}
			for(Simule si: simuleAjoute) {
				s += si.getNom() + ": Simulé masse=" + Double.valueOf(si.getMasse()) + " posx=" + Double.valueOf(si.getPosition().getX()) + " posy=" + Double.valueOf(si.getPosition().getY()) + " vx=" + Double.valueOf(si.getVitesse().getX()) + " vy=" + Double.valueOf(si.getVitesse().getY()) + "\n";
			}
			for(Ellipse e: ellipseAjoute) {
				s += e.getNom() + ": Ellipse masse=" + Double.valueOf(e.getMasse()) + " posx=" + Double.valueOf(e.getPosition().getX()) + " posy=" + Double.valueOf(e.getPosition().getY()) +
						" premfoyer=" + e.getFoyer1Nom() + " deuxfoyer=" + e.getFoyer2Nom() + " periode=" + Double.valueOf(e.getPeriode()) + "\n";
			}
			if(vaisseauAjoute != null) {
				s += vaisseauAjoute.getNom() + ": Vaisseau masse=" + Double.valueOf(vaisseauAjoute.getMasse()) +
						" posx=" + Double.valueOf(vaisseauAjoute.getPosition().getX()) + " posy=" + Double.valueOf(vaisseauAjoute.getPosition().getY()) +
						" vx=" + Double.valueOf(vaisseauAjoute.getVitesse().getX()) + " vy=" + Double.valueOf(vaisseauAjoute.getVitesse().getY()) +
						" pprincipal=" + Double.toString(vaisseauAjoute.getPpPrincipal()) + " pretro=" + Double.toString(vaisseauAjoute.getPpRetro()) + "\n";
			}
			saveTextToFile(s, file);
			Graphique g = new Graphique(file.getAbsolutePath(), true);
			g.start(new Stage());
			((Stage) nom.getScene().getWindow()).close();
		} else {
			if(tableView.getItems().size() > 0) txtError.setText("Il faut ajouter un astre.");
			else txtError.setText("Choisir un fichier de destination.");
		}
	}

	private static class InputValue implements ChangeListener<String> {
		private final TextField txtField;
		private final String regex;

		public InputValue(TextField txtField, String regex) {
			this.txtField = txtField;
			this.regex = regex;
		}

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			if (!newValue.matches(regex))
				txtField.setText(oldValue);
		}
	}

	private static void saveTextToFile(String content, File file) {
		try {
			PrintWriter writer;
			writer = new PrintWriter(file);
			writer.println(content);
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void lockAll() {
		hVitesse.setDisable(true);
		hCercle.setDisable(true);
		hEllipse.setDisable(true);
		hVaisseau.setDisable(true);
	}

	private void unlock(Parent p) {
		p.setDisable(false);
	}

	private void clearText(){
		vitX.setText("");
		vitX.setStyle("-fx-control-inner-background: #FFF");
		vitY.setText("");
		vitY.setStyle("-fx-control-inner-background: #FFF");
		periodeCercle.setText("");
		periodeCercle.setStyle("-fx-control-inner-background: #FFF");
		periodeEllipse.setText("");
		periodeEllipse.setStyle("-fx-control-inner-background: #FFF");
		ppPrincipal.setText("");
		ppPrincipal.setStyle("-fx-control-inner-background: #FFF");
		ppRetro.setText("");
		ppRetro.setStyle("-fx-control-inner-background: #FFF");
		choiceAstre.getSelectionModel().selectFirst();
		foyer1.getSelectionModel().selectFirst();
		foyer2.getSelectionModel().selectFirst();
	}

	private boolean astreExistant(String n){
		int index = 0;
		while(index < astreAjoute.size()){
			if(astreAjoute.get(index).getNom().equals(n)){
				nom.setStyle("-fx-control-inner-background: #F00");
				return true;
			}
			index++;
		}
		return false;
	}

	private boolean checkValueInteger(TextField t, int value) {
		if (!t.getText().isEmpty() && Double.valueOf(t.getText()) > value) {
			t.setStyle("-fx-control-inner-background: #FFF");
			return true;
		}
		txtError.setText("Mauvaise valeur (> 0): " + t.getId());
		t.setStyle("-fx-control-inner-background: #F00");
		return false;
	}

	private boolean checkValueText(TextField t) {
		if (!t.getText().isEmpty()) {
			t.setStyle("-fx-control-inner-background: #FFF");
			return true;
		}
		txtError.setText("Mauvaise valeur : " + t.getId());
		t.setStyle("-fx-control-inner-background: #F00");
		return false;
	}

	 public class TempAstre {
	        private String type = "";
	        private String nomA = "";
	        private String masseA = "";
	        private String posx = "";
	        private String posy = "";
	        private String vitx = "";
	        private String vity = "";
	        private String f1 = "";
	        private String f2 = "";
	        private String perEL = "";
	        private String centre = "";
	        private String perCe = "";
	        private String ppPr = "";
	        private String ppRe = "";

			public TempAstre(String type) {
				this.type = type;
				this.nomA = nom.getText();
				this.masseA = masse.getText();
				this.posx = posX.getText();
				this.posy = posY.getText();
				if(!vitX.getText().isEmpty()) this.vitx = vitX.getText();
				if(!vitY.getText().isEmpty()) this.vity = vitY.getText();
				if(!foyer1.getItems().isEmpty()) this.f1 = foyer1.getValue().toString();
				if(!foyer2.getItems().isEmpty()) this.f2 = foyer2.getValue().toString();
				if(!periodeEllipse.getText().isEmpty()) this.perEL = periodeEllipse.getText();
				if(!choiceAstre.getItems().isEmpty()) this.centre = choiceAstre.getValue().toString();
				if(!periodeCercle.getText().isEmpty()) this.perCe = periodeCercle.getText();
				if(!ppPrincipal.getText().isEmpty()) this.ppPr = ppPrincipal.getText();
				if(!ppRetro.getText().isEmpty()) this.ppRe = ppRetro.getText();
			}

			public String getType() {
				return type;
			}

			public String getNomA() {
				return nomA;
			}

			public String getMasseA() {
				return masseA;
			}

			public String getPosx() {
				return posx;
			}

			public String getPosy() {
				return posy;
			}

			public String getVitx() {
				return vitx;
			}

			public String getVity() {
				return vity;
			}

			public String getF1() {
				return f1;
			}

			public String getF2() {
				return f2;
			}

			public String getPerEL() {
				return perEL;
			}

			public String getCentre() {
				return centre;
			}

			public String getPerCe() {
				return perCe;
			}

			public String getPpPr() {
				return ppPr;
			}

			public String getPpRe() {
				return ppRe;
			}

			public void setType(String type) {
				this.type = type;
			}

			public void setNomA(String nomA) {
				this.nomA = nomA;
			}

			public void setMasseA(String masseA) {
				this.masseA = masseA;
			}

			public void setPosx(String posx) {
				this.posx = posx;
			}

			public void setPosy(String posy) {
				this.posy = posy;
			}

			public void setVitx(String vitx) {
				this.vitx = vitx;
			}

			public void setVity(String vity) {
				this.vity = vity;
			}

			public void setF1(String f1) {
				this.f1 = f1;
			}

			public void setF2(String f2) {
				this.f2 = f2;
			}

			public void setPerEL(String perEL) {
				this.perEL = perEL;
			}

			public void setCentre(String centre) {
				this.centre = centre;
			}

			public void setPerCe(String perCe) {
				this.perCe = perCe;
			}

			public void setPpPr(String ppPr) {
				this.ppPr = ppPr;
			}

			public void setPpRe(String ppRe) {
				this.ppRe = ppRe;
			}

	    }
}
