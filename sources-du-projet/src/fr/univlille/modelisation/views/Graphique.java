package fr.univlille.modelisation.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.univlille.modelisation.FichierIllisible;
import fr.univlille.modelisation.FileLoader;
import fr.univlille.modelisation.Physique;
import fr.univlille.modelisation.Univers;
import fr.univlille.modelisation.Vector;
import fr.univlille.modelisation.controllers.ScrollController;
import fr.univlille.modelisation.controllers.VaisseauController;
import fr.univlille.modelisation.controllers.WindowController;
import fr.univlille.modelisation.events.AstreMoveEvent;
import fr.univlille.modelisation.events.AstreSpawnEvent;
import fr.univlille.modelisation.events.UniversListener;
import fr.univlille.modelisation.models.Astre;
import fr.univlille.modelisation.models.Vaisseau;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Graphique extends Application implements UniversListener {

	private static double WIDTH = 1000, HEIGHT = 600;

	private double zoom = 1.0;
	public double deltaX = 0, deltaY = 0;
	private Map<Astre, Vector> map = new HashMap<>();

	private Canvas canvas, infos, etoiles;
	private VBox options;
	private GraphicsContext gc, gcInfos, gcEtoiles;
	private StackPane root;
	private Scene scene;

	private List<Astre> entites;
	private double dt;
	private double G;
	private int rayon;
	private int fa;
	private Univers univers;
	private Vaisseau v;

	private Astre selected;
	private CheckMenuItem itemSuivre;
	private MenuItem itemCentre;
	private boolean menuGauche;

	private Physique physique;
	private String affichage;
	private double milliseconds = 0;
	private int secondes = 0, minutes = 0, heures = 0;

	private HashMap<Vector, Double> posEtoiles;
	private final int etoilesMin = 500;
	private final int offsetEtoilesTaille = 4, tailleEtoilesMin = 1;
	private double debutX, finX, debutY, finY;
	private double generationX, generationY;

	private Thread th;
	private WindowController winControl;
	private String nomFile;

	public Graphique(String urlFile, boolean universPerso) throws NumberFormatException, FichierIllisible, IOException {
		nomFile = urlFile;
		FileLoader fileLoader = new FileLoader(urlFile, universPerso);
		entites = fileLoader.getEntites();
		G = fileLoader.getG();
		dt = fileLoader.getDt();
		rayon = fileLoader.getRayon();
		fa = fileLoader.getFa();
		v = fileLoader.getVaisseau();
		univers = new Univers(this, G, dt, fa, rayon);
		zoom = (WIDTH / 2) / rayon;
		fileLoader.getEntites().forEach(Astre -> {
			univers.add(Astre);
		});
		winControl = new WindowController();
		winControl.setUnivers(this);
		physique = new Physique(univers);
		th = new Thread(physique);
		th.start();
	}

	public Graphique(String urlFile) throws NumberFormatException, FichierIllisible, IOException {
		this(urlFile, false);
	}

	@Override
	public void start(Stage primaryStage) {
		Timeline timeline;

		canvas = new Canvas(WIDTH, HEIGHT);
		infos = new Canvas(WIDTH / 3 + 50, HEIGHT / 6);
		etoiles = new Canvas(WIDTH, HEIGHT);
		gc = canvas.getGraphicsContext2D();
		gcInfos = infos.getGraphicsContext2D();
		gcEtoiles = etoiles.getGraphicsContext2D();

		/*menuButton = new Button(">");
		menuButton.setId("fleche");
		Button play = new Button("Play");
		Button stop = new Button("Stop");
		centre = new Button("Centrer");
		suiviButton = new ToggleButton("Suivre");*/

		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("Fichier");
        Menu timerMenu = new Menu("Timer");
        Menu affichageMenu = new Menu("Affichage");

        MenuItem itemNouveau = new MenuItem("Nouveau");
        MenuItem itemOuvrir = new MenuItem("Ouvrir");
        fileMenu.getItems().addAll(itemNouveau, itemOuvrir);

        RadioMenuItem itemPlay = new RadioMenuItem("Play");
        RadioMenuItem itemPause = new RadioMenuItem("Pause");
        ToggleGroup group = new ToggleGroup();
        itemPlay.setToggleGroup(group);
        itemPause.setToggleGroup(group);
        itemPlay.setSelected(true);
        timerMenu.getItems().addAll(itemPlay, itemPause);

        itemSuivre = new CheckMenuItem("Suivre");
        itemCentre = new MenuItem("Centre");
        CheckMenuItem itemMenu = new CheckMenuItem("Menu");
        affichageMenu.getItems().addAll(itemSuivre, itemCentre, itemMenu);
        menuBar.getMenus().addAll(fileMenu, timerMenu, affichageMenu);

		root = new StackPane();
		root.setBackground(new Background(new BackgroundFill(Color.rgb(40, 40, 40), CornerRadii.EMPTY, Insets.EMPTY)));

		/*StackPane.setAlignment(menuButton, Pos.CENTER_LEFT);
		StackPane.setAlignment(hbox, Pos.TOP_RIGHT);*/

		StackPane.setAlignment(menuBar, Pos.TOP_LEFT);
		StackPane.setMargin(infos, new Insets(20, 20, 20, 20));
		StackPane.setAlignment(infos, Pos.BOTTOM_RIGHT);

		root.getChildren().addAll(etoiles, canvas, menuBar, infos);
		root.setPrefSize(WIDTH, HEIGHT);
		scene = new Scene(root);

		/*
		 * Evenement d'auto resize
		 */
		canvas.widthProperty().bind(primaryStage.widthProperty());
		canvas.heightProperty().bind(primaryStage.heightProperty());

		canvas.widthProperty().addListener((observable, oldVal, newVal) -> {
			WIDTH = (double) newVal;
			zoom = (WIDTH / 2) / rayon;
		});

		canvas.heightProperty().addListener((observable, oldVal, newVal) -> {
			HEIGHT = (double) newVal;
			zoom = (WIDTH / 2) / rayon;
		});

		/*
		 * Evenement de souris sur la fenetre
		 */
		scene.addEventHandler(MouseEvent.MOUSE_CLICKED, winControl);
		scene.addEventHandler(ScrollEvent.ANY, new ScrollController(this));
		scene.addEventHandler(MouseEvent.MOUSE_PRESSED, winControl);
		scene.addEventHandler(MouseEvent.MOUSE_RELEASED, winControl);
		scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, winControl);

		/*
		 * Ajout des controllers vaisseau s'il existe un vaisseau
		 */
		if (v != null) {
			VaisseauController vc = new VaisseauController(v);
			scene.addEventHandler(KeyEvent.KEY_PRESSED, vc);
			scene.addEventHandler(KeyEvent.KEY_RELEASED, vc);
		}

		scene.getStylesheets().add("/res/css/sceneStyle.css");

		primaryStage.setMaxWidth(1920);
		primaryStage.setMaxHeight(1080);
		primaryStage.setTitle("SIMASTRO " + nomFile);
		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setOnCloseRequest(t -> {
			physique.stop();
			th.interrupt();
			primaryStage.close();
		});

		/*
		 * Timer JavaFx qui fait l'affichage
		 */
		timeline = new Timeline(new KeyFrame(Duration.millis(univers.getDt()), new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				display();
			}
		}));

		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();

		/*
		 * MENU A GAUCHE
		 */
		options = new VBox();
		options.setMaxSize(WIDTH / 4, HEIGHT);
		options.setBackground(new Background(new BackgroundFill(Color.rgb(0, 50, 150), null, null)));
		StackPane.setAlignment(options, Pos.TOP_LEFT);

		/*backButton = new Button("<");
		backButton.setId("fleche");
		StackPane.setAlignment(backButton, Pos.CENTER_LEFT);
		StackPane.setMargin(backButton, new Insets(0,0,0,root.getWidth() / 4 > 300 ? 300 : root.getWidth() / 4));*/

		root.heightProperty().addListener((event) -> {
			options.setMaxHeight(root.getHeight());
			options.setMaxWidth(root.getWidth() / 4 > 300 ? 300 : root.getWidth() / 4);
		});

		/*menuButton.setOnAction(e -> {
			menuGauche = true;
			root.getChildren().removeAll(menuButton);
			root.getChildren().addAll(options, backButton);
		});
		backButton.setOnAction(e -> {
			menuGauche = false;
			root.getChildren().removeAll(options, backButton);
			root.getChildren().addAll(menuButton);
		});


		// TAILLE HBOX
		hbox.setMaxSize(play.getWidth() + stop.getWidth() + centre.getWidth() + suiviButton.getWidth() + 80, play.getHeight());*/

		itemMenu.setOnAction(e -> {
			if(itemMenu.isSelected()) {
				menuGauche = true;
				root.getChildren().add(1, options);
			} else {
				menuGauche = false;
				root.getChildren().remove(options);
			}
		});
		itemNouveau.setOnAction(e -> new CreationUnivers());
		itemOuvrir.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Astro files (*.astro)", "*.astro");
			fileChooser.getExtensionFilters().add(extFilter);
			File temp = fileChooser.showOpenDialog(primaryStage);
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
		});

		/*
		 * Mettre en pause uniquement le thread de physique sinon bloque le canvas
		 */

		itemPlay.setOnAction(e -> physique.resume());
		itemPause.setOnAction(e -> physique.pause());

		/*
		 * Reviens au centre du monde en appuyant sur le bouton centre
		 */
		itemCentre.setOnAction(e -> {
			itemSuivre.setSelected(false);
			deltaX = 0;
			deltaY = 0;
		});

		/*
		 * CHOIX PLANETE GRACE AUX FLECHES
		 */
		infos.setOnMouseClicked(e -> {
			boolean suivre = false;
			if(itemSuivre.isSelected()) suivre = true;
			int idx = 0;
			if((e.getX() > 0 && e.getX() <= 35) && (e.getY() > 0 && e.getY() <= infos.getHeight())) {
				for(int i = 0; i < entites.size(); i++) {
					if(entites.get(i).equals(selected)) {
						idx = i;
						break;
					}
				}
				if(idx == 0) idx = entites.size();
				selected = entites.get(idx-1);
				if(suivre) itemSuivre.setSelected(true);
			} else if((e.getX() > infos.getWidth() - 35 && e.getX() <= infos.getWidth())
					&& (e.getY() > 0 && e.getY() <= infos.getHeight())) {
				for(int i = 0; i < entites.size(); i++) {
					if(entites.get(i).equals(selected)) {
						idx = i;
						break;
					}
				}
				if(idx == entites.size() - 1) idx = -1;
				selected = entites.get(idx+1);
				if(suivre) itemSuivre.setSelected(true);
			}
		});
		initBackground();

		primaryStage.setMinWidth(600);
		primaryStage.setMinHeight(400);
		System.out.println("4");
	}

	private void display() {
		/*
		 * Clear les 2 canvas
		 */
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gcInfos.clearRect(0, 0, infos.getWidth(), infos.getHeight());

		/*
		 * AFFICHAGE TIMER ET POSITION ACTUELLE
		 */
		DecimalFormat f = new DecimalFormat();
		f.setMaximumFractionDigits(0);
		NumberFormat formatter = new DecimalFormat("00");

		milliseconds = physique.getTime() % 1000;
		secondes = (int) (physique.getTime() / 1000) % 60 ;
		minutes = (int) ((physique.getTime() / (1000*60)) % 60);
		heures = (int) ((physique.getTime() / (1000*60*60)) % 24);
		affichage = "H:" + formatter.format(heures) + " M:" + formatter.format(minutes) + " S:" + formatter.format(secondes)
		+ " M:" + f.format(milliseconds);


		gc.setFill(Color.WHITE);
		gc.fillText(affichage, canvas.getWidth() / 2 - 90, 60);
		if(!menuGauche) {
			gc.fillText("X:" + f.format(deltaX) + " ; Y:" + f.format(deltaY), 10, canvas.getHeight() - 30);
		} else if(menuGauche) {
			gc.fillText("X:" + f.format(deltaX) + " ; Y:" + f.format(deltaY), options.getWidth() + 10, canvas.getHeight() - 30);
		}


		/*
		 * AFFICHAGE DES INFORMATIONS SUR L'ASTRE SELECTIONNE
		 */
		f.setMaximumFractionDigits(2);
		double rayon;
		if (selected != null) {
			// HALLO VERT AUTOUR DE LA PLANETE
			rayon = selected.getRayon() * zoom * 1.5;
			gc.setFill(Color.DARKGREEN);
			gc.fillOval(deltaX + (canvas.getWidth() / 2) - (rayon / 2) + selected.getPosition().getX() * zoom,
					deltaY + (canvas.getHeight() / 2) - (rayon / 2) + selected.getPosition().getY() * zoom, rayon,
					rayon);
			// AFFICHAGE INFO DE LA PLANETE
			double rayonInfos = selected.getRayon();
			gcInfos.setFill(Color.rgb(0, 75, 200));
			gcInfos.fillRoundRect(0, 0, infos.getWidth(), infos.getHeight(), 25, 25);
			// DESSINS FLECHES
			gcInfos.setStroke(Color.rgb(0, 50, 150));
			gcInfos.setLineWidth(2);
			gcInfos.strokeLine(35, 0, 35, infos.getHeight());
			gcInfos.strokeLine(infos.getWidth() - 35, 0,infos.getWidth() - 35, infos.getHeight());

			gcInfos.setFill(Color.rgb(0, 50, 150));
			gcInfos.fillPolygon(new double[]{10, 25, 25},
	                new double[]{infos.getHeight() / 2, infos.getHeight() / 2 - 10, infos.getHeight() / 2 + 10}, 3);
			gcInfos.fillPolygon(new double[]{infos.getWidth() - 10, infos.getWidth() - 25, infos.getWidth() - 25},
	                new double[]{infos.getHeight() / 2, infos.getHeight() / 2 - 10, infos.getHeight() / 2 + 10}, 3);
			// NOM ET MASSE
			gcInfos.setFill(Color.WHITE);
			gcInfos.fillText(selected.getNom() + "    ( " + selected.getMasse() + " kg )", infos.getWidth() / 3 + 25, infos.getHeight() / 4);
			gcInfos.fillText("PosX : " + f.format(selected.getPosition().getX()), infos.getWidth() / 3, (infos.getHeight() / 4) + 20);
			gcInfos.fillText("PosY : " + f.format(selected.getPosition().getY()), infos.getWidth() / 3, (infos.getHeight() / 4) + 40);
			// VITESSE EN METRES PAR SECONDE
			gcInfos.fillText("Vitesse : " + f.format(selected.getVitesseMetre()) + " m/s", infos.getWidth() / 3, (infos.getHeight() / 4) + 60);
			gcInfos.setFill(selected.getRepresentation().getCouleur());
			// DESSIN DE LA PLANETE DANS LES INFOS
			gcInfos.fillOval(infos.getWidth() / 5 - (rayonInfos) + 5, infos.getHeight() / 2 - (rayonInfos), rayonInfos * 2,
					rayonInfos * 2);

			if(itemSuivre.isSelected()) {
				deltaX = -selected.getPosition().getX() * zoom;
				deltaY = -selected.getPosition().getY() * zoom;
			}
		}

		/*
		 * AFFICHAGE DES ASTRES ET DE LEUR TRAINEE
		 */
		for (Astre astre : map.keySet()) {
			rayon = astre.getRayon() * zoom;

			for (Vector v : astre.getTrail()) {
				if (v != null) {
					gc.setFill(Color.rgb(255, 0, 0, 0.05));
					gc.fillOval(deltaX + (canvas.getWidth() / 2) - (rayon / 4) + v.getX() * zoom,
							deltaY + (canvas.getHeight() / 2) - (rayon / 4) + v.getY() * zoom, rayon / 2, rayon / 2);
				}
			}

			double x = deltaX + (canvas.getWidth() / 2) - (rayon / 2) + astre.getPosition().getX() * zoom;
			double y = deltaY + (canvas.getHeight() / 2) - (rayon / 2) + astre.getPosition().getY() * zoom;

			gc.setFill(astre.getRepresentation().getCouleur());
			gc.fillOval(x, y, rayon, rayon);
		}

		if(itemSuivre.isSelected()) moveBackground();
		// AFFICHER LA MAP EN FONCTION DE LA VISION
	}

	public void affichageInfos(Astre a) {
		DecimalFormat f = new DecimalFormat();
		f.setMaximumFractionDigits(2);
		selected = a;
	}

	@Override
	public void updateAstre(AstreMoveEvent event) {
		map.replace(event.getAstre(),
				new Vector(WIDTH / 2 - event.getPosition().getX(), HEIGHT / 2 - event.getPosition().getY()));
	}

	@Override
	public void spawnAstre(AstreSpawnEvent event) {
		map.put(event.getAstre(), new Vector(WIDTH / 2 - event.getAstre().getPosition().getX(),
				HEIGHT / 2 - event.getAstre().getPosition().getY()));
	}

	public Scene getScene() {
		return scene;
	}

	public Map<Astre, Vector> getMap() {
		return map;
	}

	public void zoomIn() {
		if (zoom < 10)
			zoom += 0.1;
	}

	public void zoomOut() {
		if (zoom > 0.10)
			zoom -= 0.1;
	}

	public double getZoom() {
		return zoom;
	}

	public double getWidth() {
		return canvas.getWidth();
	}

	public double getHeight() {
		return canvas.getHeight();
	}

	public void enleveSuivi() {
		itemSuivre.setSelected(false);
	}

	public Canvas getInfos() {
		return infos;
	}

	private void initBackground(){
		posEtoiles = new HashMap<>();
		debutX = - WIDTH * 2;
		debutY = - HEIGHT * 2;
		finX = WIDTH * 2;
		finY = HEIGHT * 2;
		for(int i = 0 ; i < etoilesMin ; i++){
			posEtoiles.put(
					new Vector(debutX, debutY, finX, finY),
					(Math.random() * offsetEtoilesTaille) + tailleEtoilesMin);
		}
		generationX = univers.getRayon();
		generationY = univers.getRayon();
		moveBackground();
	}

	public void moveBackground(){
		/*
		 * Generation des etoiles en background en fonction de la position actuelle
		 */
		double r = univers.getRayon() * (1 / zoom);
		System.out.println("rayon: "+ r);

		gcEtoiles.clearRect(0, 0, etoiles.getWidth(), etoiles.getHeight());
		gcEtoiles.setFill(Color.WHITE);
		double rayonEtoiles;
		for(Vector v: posEtoiles.keySet()){
			rayonEtoiles = posEtoiles.get(v);
			gcEtoiles.fillOval(
					deltaX + v.getX() * zoom + WIDTH / 2,
					deltaY + v.getY() * zoom + HEIGHT / 2,
					rayonEtoiles * (1 + Math.log(zoom)),
					rayonEtoiles * (1 + Math.log(zoom)));
		}
	}
}