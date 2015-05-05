package exercice;

import java.util.ArrayList;

import com.sun.java.swing.plaf.motif.resources.motif;

import tableau_suffixe.*;
import traducteur.*;

public class Exercice {

	// TODO il faut faire un filtre en deux etape : 1ere filtre : 20 % de mot
	// incconnu par phrase
	// 2eme filtre : cherche dans les phrases filte1 telque la somme du taux de
	// similarité entre les mots connus et les mots inconnus soit la plus grande
	// (car c'est la plus près )

	// private GrapheLexical monGraphLex;
	private Traducteur monTrad;
	private ArrayList<String> phraseFiltre1;
	private ArrayList<String> phraseFiltre2;

	public Exercice() {
		/*
		 * TODO initialiser monGraphLex , ArrayList<String> connaissanceInitial
		 * à met dans les variable de GrapheLexical directement
		 */
		phraseFiltre1 = new ArrayList<String>();
		phraseFiltre2 = new ArrayList<String>();
		monTrad = new Traducteur("fra", "eng", "Files/testCorpus.txt",
				"Files/link.txt");
		// monGraphLex = new GrapheLexical ();

	}

	public Traducteur getMonTrad() {
		return monTrad;
	}

	public void setMonTrad(Traducteur monTrad) {
		this.monTrad = monTrad;
	}

	public ArrayList<String> getPhraseFiltre1() {
		return phraseFiltre1;
	}

	public void setPhraseFiltre1(ArrayList<String> phraseFiltre1) {
		this.phraseFiltre1 = phraseFiltre1;
	}

	public ArrayList<String> getPhraseFiltre2() {
		return phraseFiltre2;
	}

	public void setPhraseFiltre2(ArrayList<String> phraseFiltre2) {
		this.phraseFiltre2 = phraseFiltre2;
	}

	/*
	 * remplit l'arrayList phrasePourExo de phrase avec un taux de mot inconnu
	 * inférieur à un pourcentage qui est passé en paramètre
	 */
	public void appPhraseFiltre1(int pourcentage, int nbligneMax,
			ArrayList<String> connaissanceInitial) {

		ArrayList<String> s = new ArrayList<String>();
		String s1 = new String("I love apple.");
		s1 = s1.toUpperCase();
		s.add(s1);
		String s2 = new String("Daro is great.");
		s2 = s2.toUpperCase();
		s.add(s2);
		String s3 = new String("I am a test.");
		s3 = s3.toUpperCase();
		s.add(s3);
		String s4 = new String("My car is black and white.");
		s4 = s4.toUpperCase();
		s.add(s4);
		String s5 = new String("The sun is yello, and it is big.");
		s5 = s5.toUpperCase();
		s.add(s5);
		// TODO : a remplacer l'arrayList s par la list contenant tout les
		// lignes du corpus

		for (int i = 0; i < 5; i++) {
			if (tauxMotInconnu(s.get(i), connaissanceInitial) < pourcentage) {// on
																				// ajoute
																				// la
																				// phrase
																				// si
																				// le
																				// taux
																				// de
																				// mot
																				// inconnu
																				// est
																				// inférieur
																				// à
																				// 20
				// if(
				// phraseFiltre1.contains(monTrad.getSuffixArray_lang2().getCorpus().getPhraseFromLine(i))==false)//
				// on ajoute si il n'y a pas de redondance de la même phrase

				phraseFiltre1.add(s.get(i));

			}
		}
	}

	/* renvoye le taux de mot inconnu dans une phrase */

	public double tauxMotInconnu(String phrase,
			ArrayList<String> connaissanceInitial) {
		double res = 1.0;

		String[] phraseEnMot = phrase.split("\\W+");

		int compteurMotInconnu = 0;
		int nbMotDansPhrase = phraseEnMot.length;

		for (int i = 0; i < nbMotDansPhrase; i++) {
			System.out.println("mot = " + phraseEnMot[i]);
			if (connaissanceInitial.contains(phraseEnMot[i]) == false) {
				System.out.println(" mot inconnu : " + phraseEnMot[i]);
				compteurMotInconnu++;
			}
		}
		res = (double) compteurMotInconnu / nbMotDansPhrase;

		res = res * 100;
		System.out.println(" res de " + phrase + " = " + res);
		return res;
	}

	public void appPhraseFiltre2(ArrayList<String> connaissanceInitial) {

		double tmp = 0.0;
		double max = 0.0;
		int plusProche = 0;
		max = sommeTauxSimilarite(phraseFiltre1.get(0), connaissanceInitial);

		for (int i = 1; i < phraseFiltre1.size(); i++) {// on parcour tout les
														// phrases qu'on avait
														// filtré avec le filtre
														// 1
			tmp = sommeTauxSimilarite(phraseFiltre1.get(i), connaissanceInitial);// si
																					// les
																					// mots
																					// inconnuc
																					// de
																					// la
																					// phrase
																					// actuelle
																					// contient
																					// un
																					// taux
																					// de
																					// similarité
																					// plus
																					// élevé
																					// alors
																					// on
																					// le
																					// considère
																					// étant
																					// le
																					// max
																					// actuel
			if (tmp > max) {
				plusProche = i;// on sauvegarde l'index de la phrase ( de
								// phraseFiltre1 ).
				max = tmp;
			}
		}
		phraseFiltre2.add(phraseFiltre1.get(plusProche));// on ajoute la phrase
															// qui on a trouvé
															// dans l'arrayList
															// phraseFiltre2
	}

	// sépare une phrase en deux arrayListe, une arrayList de mot connu, et
	// l'autre inconnu qui sont passé en paramètre

	public void separationConnuInconnu(String phrase,
			ArrayList<String> connaissanceInitial,
			ArrayList<String> motInconnuDansPhrase,
			ArrayList<String> motConnuDansPhrase) {

		String[] phraseEnMot = phrase.split("\\W+");

		double res = 0.0;

		// TODO
		for (int i = 0; i < phraseEnMot.length; i++) {// on sépare la phrase en
			// 2 arrayList , 1
			// contenant les mots
			// connus et l'autre les
			// mots inconnus
			if (connaissanceInitial.contains(phraseEnMot[i]) == true) {
				motConnuDansPhrase.add(phraseEnMot[i]);
			} else {
				motInconnuDansPhrase.add(phraseEnMot[i]);
			}
		}
	}

	public double sommeTauxSimilarite(String phrase,
			ArrayList<String> connaissanceInitial) {

		ArrayList<String> motConnuDansPhrase = new ArrayList<String>();// contiendra
																		// tout
																		// les
																		// mot
																		// connus
																		// dans
																		// la
																		// phrase
		ArrayList<String> motInconnuDansPhrase = new ArrayList<String>();//

		separationConnuInconnu(phrase, connaissanceInitial,
				motInconnuDansPhrase, motConnuDansPhrase);
		// fin de la séparation

		for (int i = 0; i < motInconnuDansPhrase.size(); i++) {
			for (int j = 0; j < motInconnuDansPhrase.size(); j++) {
				// on calcule la somme du taux de similarite entre chaque mot
				// inconnus et tout les mot connus
				// res = res +
				// this.monGraphLex.similarite2Mots(motInconnuDansPhrase.get(i),motConnuDansPhrase.get(j));

			}
		}

		return res;
	}

	public void trouveMotVoisin(String mot) {

		// TODO trouver 3 ou 5 mot voisin du mot passé en paramètre
	}

	public static void main(String[] args) {
		ArrayList<String> connaissanceInitial = new ArrayList<String>();
		connaissanceInitial.add("APPLE");
		connaissanceInitial.add("I");
		connaissanceInitial.add("YELLO");
		connaissanceInitial.add("IS");
		connaissanceInitial.add("SUN");
		connaissanceInitial.add("BLACK");
		connaissanceInitial.add("CAR");
		connaissanceInitial.add("MY");

		Exercice ex = new Exercice();
		ex.getPhraseFiltre1(34, 2, connaissanceInitial);
		System.out.println(" affiche les phrases aprèse 1ere filtrage :  "
				+ ex.getPhraseFiltre1());
	}
}
