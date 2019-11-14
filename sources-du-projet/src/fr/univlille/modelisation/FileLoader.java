package fr.univlille.modelisation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.univlille.modelisation.models.Astre;
import fr.univlille.modelisation.models.Cercle;
import fr.univlille.modelisation.models.Ellipse;
import fr.univlille.modelisation.models.Fixe;
import fr.univlille.modelisation.models.Simule;
import fr.univlille.modelisation.models.Vaisseau;

public class FileLoader {

	private double G;
	private double dt;
	private int fa;
	private int rayon;
	private List<Astre> entites;
	private Vaisseau v;

	public FileLoader(String urlFile, boolean universPerso)
			throws FichierIllisible, NumberFormatException, ArrayIndexOutOfBoundsException, IOException {
		BufferedReader br = null;
		try {
			entites = new ArrayList<Astre>();
			InputStream in = null;
			if(universPerso) {
				in = new FileInputStream(new File(urlFile));
			} else in = getClass().getResourceAsStream("/res/fichiers/"+urlFile+".astro");
			br = new BufferedReader(new InputStreamReader(in));
			String st;

			while ((st = br.readLine()) != null) {
				System.out.println(st);
				if (!st.contains("#")) {
					if (st.contains("PARAMS")) {
						String[] params = st.split(" "); // PARAMS G=0.01 dt=20 rayon=500
						if(params.length != 5) throw new FichierIllisible();
						G = Double.valueOf(params[1].split("=")[1]); // params[1] = G=0.01
						dt = Double.valueOf(params[2].split("=")[1]) * 1000; // params[2] = dt=20
						fa = Integer.valueOf(params[3].split("=")[1]); //params[3] = fa=1
						rayon = Integer.valueOf(params[4].split("=")[1]); // params[4] = rayon=500
					} else if (st.contains(":")) {
						double vx, vy, pprincipal, pretro, periode;
						String[] params = st.split(" ");
						String objNom = params[0];
						objNom = (new StringBuilder(objNom)).deleteCharAt(objNom.length() - 1).toString();
						String entite = params[1];
						double masse = Double.valueOf(params[2].split("=")[1]);
						double x = Double.valueOf(params[3].split("=")[1]);
						double y = Double.valueOf(params[4].split("=")[1]);
						if(entite.equals("Fixe")) {
							entites.add(new Fixe(objNom, new Vector(x, y), masse));
						} else if(entite.startsWith("Simul")) {
							vx = Double.valueOf(params[5].split("=")[1]);
							vy = Double.valueOf(params[6].split("=")[1]);
							entites.add(new Simule(objNom, new Vector(x, y), new Vector(vx, vy), masse));
						} else if(entite.equals("Vaisseau")) {
							vx = Double.valueOf(params[5].split("=")[1]);
							vy = Double.valueOf(params[6].split("=")[1]);
							pprincipal = Double.valueOf(params[7].split("=")[1]);
							pretro = Double.valueOf(params[8].split("=")[1]);
							v = new Vaisseau(objNom, new Vector(x, y), new Vector(vx, vy), pprincipal, pretro, masse);
							entites.add(v);
						} else if(entite.equals("Cercle")) {
							Astre centre = null;
							System.out.println("Entites : ");
							for(Astre a : entites) {
								if(a.getNom().equals(params[5].split("=")[1])) centre = a;
							}
							periode = Double.valueOf(params[6].split("=")[1]);
							entites.add(new Cercle(objNom, centre, x, y, masse, periode));
							if(centre == null) throw new FichierIllisible();
						} else if(entite.equals("Ellipse")) {
							Fixe premFoyer = null;
							Fixe deuxFoyer = null;
							for(Astre a : entites) {
								if(a.getNom().equals(params[5].split("=")[1])) premFoyer = (Fixe) a;
								if(a.getNom().equals(params[6].split("=")[1])) deuxFoyer = (Fixe) a;
							}
							periode = Double.valueOf(params[7].split("=")[1]);
							entites.add(new Ellipse(objNom, x, y, premFoyer, deuxFoyer, masse, periode));
							if(premFoyer == null || deuxFoyer == null) throw new FichierIllisible();
						}
					}
				}
			}
			if(entites.size() == 0) throw new FichierIllisible();
		} finally {
			try {
				br.close();
			} catch (IOException e) {}
		}
	}

	public List<Astre> getEntites() {
		return entites;
	}

	public double getDt() {
		return dt;
	}

	public int getFa() {
		return fa;
	}

	public double getG() {
		return G;
	}

	public int getRayon() {
		return rayon;
	}

	public Vaisseau getVaisseau(){
		return v;
	}
}
