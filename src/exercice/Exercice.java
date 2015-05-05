package exercice;

import grapheLexical.GrapheLexical;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.sun.java.swing.plaf.motif.resources.motif;

import tableau_suffixe.*;
import traducteur.*;

public class Exercice {

	// TODO il faut faire un filtre en deux etape : 1ere filtre : 20 % de mot
	// incconnu par phrase
	// 2eme filtre : cherche dans les phrases filte1 telque la somme du taux de
	// similarité entre les mots connus et les mots inconnus soit la plus grande
	// (car c'est la plus près )

	private GrapheLexical monGraphLex;
	private Traducteur monTrad;
	private ArrayList<String> phraseFiltre1;
	private ArrayList<String> phraseFiltre2;

	public GrapheLexical getMonGraphLex() {
		return monGraphLex;
	}

	public void setMonGraphLex(GrapheLexical monGraphLex) {
		this.monGraphLex = monGraphLex;
	}

	public Exercice(ArrayList<String> connaissanceInitial,
			ArrayList<String> corrpus) {
		/*
		 * TODO initialiser monGraphLex , ArrayList<String> connaissanceInitial
		 * à met dans les variable de GrapheLexical directement
		 */
		phraseFiltre1 = new ArrayList<String>();
		phraseFiltre2 = new ArrayList<String>();
		monTrad = new Traducteur("fra", "eng", "Files/testCorpus.txt",
				"Files/link.txt");
		if(!new File("GrapheLexical.ser").exists()){
		monGraphLex = new GrapheLexical(connaissanceInitial, corrpus);
		try {
			monGraphLex.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}else{
			try {
				monGraphLex = GrapheLexical.load();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
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
			ArrayList<String> corpus) {

		// TODO : a remplacer l'arrayList s par la list contenant tout les
		// lignes du corpus
		Map<String,Double> resultatTop = new HashMap<>();
		
		for (int i = 0; i < corpus.size(); i++) {
			Double tauxmotinconue = tauxMotInconnu(corpus.get(i));
			if ((tauxmotinconue <= (100 - pourcentage))	&& (tauxmotinconue > 0)) {
				// on
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
				resultatTop.put(corpus.get(i), tauxmotinconue);
				//phraseFiltre1.add(corpus.get(i));
			}
		}
		//System.out.println(resultatTop);
		int i=10;
		Map<String,Double> resultatTopNew= sortByValues(resultatTop);
		
		for(Map.Entry<String, Double> entry: resultatTopNew.entrySet()){
			if(--i>0)
			phraseFiltre1.add(entry.getKey());
			else
				break;
		}
	}

	
	public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
	    Comparator<K> valueComparator =  new Comparator<K>() {
	        public int compare(K k1, K k2) {
	            int compare = map.get(k2).compareTo(map.get(k1));
	            if (compare == 0) return 1;
	            else return -compare;
	        }
	    };
	    Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
	    sortedByValues.putAll(map);
	    return sortedByValues;
	}
	/* renvoye le taux de mot inconnu dans une phrase */

	public double tauxMotInconnu(String phrase) {
		double res = 1.0;

		String[] phraseEnMot = phrase.split("\\W+");

		int compteurMotInconnu = 0;
		int nbMotDansPhrase = phraseEnMot.length;

		for (int i = 0; i < nbMotDansPhrase; i++) {
			//System.out.println("mot = " + phraseEnMot[i]);
			if (monGraphLex.estMetrise(phraseEnMot[i], 7) == false) {
				//System.out.println(" mot inconnu : " + phraseEnMot[i]);
				compteurMotInconnu++;
			}
		}
		res = (double) compteurMotInconnu / nbMotDansPhrase;

		res = res * 100;
		//System.out.println(" res de " + phrase + " = " + res);
		return res;
	}

	public void appPhraseFiltre2(ArrayList<String> connaissanceInitial) {

	
		double tmp = 0.0;
		double max = 0.0;
		int plusProche = 0;
		if(phraseFiltre1.size()>0){
		max = sommeTauxSimilarite(phraseFiltre1.get(0));
		
		for (int i = 1; i < phraseFiltre1.size(); i++) {// on parcour tout les
														// phrases qu'on avait
														// filtré avec le filtre
														// 1
			tmp = sommeTauxSimilarite(phraseFiltre1.get(i));// si
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
	}

	// sépare une phrase en deux arrayListe, une arrayList de mot connu, et
	// l'autre inconnu qui sont passé en paramètre

	public void separationConnuInconnu(String phrase,
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
			if (monGraphLex.estMetrise(phraseEnMot[i], 7) == true) {
				motConnuDansPhrase.add(phraseEnMot[i]);
			} else {
				motInconnuDansPhrase.add(phraseEnMot[i]);
			}
		}
	}

	public ArrayList<String> motConnuDansPhrase(String phrase) {
		ArrayList<String> motConnuDansPhrase = new ArrayList<>();
		String[] phraseEnMot = phrase.split("\\W+");

		double res = 0.0;

		for (int i = 0; i < phraseEnMot.length; i++) {// on sépare la phrase en
			// 2 arrayList , 1
			// contenant les mots
			// connus et l'autre les
			// mots inconnus
			if (monGraphLex.estMetrise(phraseEnMot[i], 7) == true) {
				motConnuDansPhrase.add(phraseEnMot[i]);
			}
		}
		return motConnuDansPhrase;
	}

	public ArrayList<String> motInconnueDansPhrase(String phrase) {
		ArrayList<String> motInconnueDansPhrase = new ArrayList<>();
		String[] phraseEnMot = phrase.split("\\W+");

		double res = 0.0;

		for (int i = 0; i < phraseEnMot.length; i++) {// on sépare la phrase en
			// 2 arrayList , 1
			// contenant les mots
			// connus et l'autre les
			// mots inconnus
			if (monGraphLex.estMetrise(phraseEnMot[i], 7) == false) {
				motInconnueDansPhrase.add(phraseEnMot[i]);
			}
		}
		return motInconnueDansPhrase;
	}

	public double sommeTauxSimilarite(String phrase) {
		double res = 0;
		ArrayList<String> motConnuDansPhrase = new ArrayList<String>();
		ArrayList<String> motInconnuDansPhrase = new ArrayList<String>();

		//System.out.println("sommeTauxSimilarite");
		motInconnuDansPhrase = motInconnueDansPhrase(phrase);
		motConnuDansPhrase = motConnuDansPhrase(phrase);
		// fin de la séparation
		int parc=0;
		for (int i = 0; i < motInconnuDansPhrase.size(); i++) {
			for (int j = 0; j < motConnuDansPhrase.size(); j++) {
				// on calcule la somme du taux de similarite entre chaque mot
				// inconnus et tout les mot connus
				//System.out.println((++parc)+" / "+ motInconnuDansPhrase.size()* motConnuDansPhrase.size());
				res = res
						+ this.monGraphLex.similarite2Mots(
								motInconnuDansPhrase.get(i),
								motConnuDansPhrase.get(j));

			}
		}

		return res;
	}

	public void trouveMotVoisin(String mot) {

		// TODO trouver 3 ou 5 mot voisin du mot passé en paramètre
	}

	public static void main(String[] args) {
		ArrayList<String> connaissanceInitial = new ArrayList<String>();
		ArrayList<String> corpus = new ArrayList<String>();
		corpus.add("apples");
		corpus.add("i");
		corpus.add("yello");
		corpus.add("is");
		corpus.add("sun");
		corpus.add("black");
		corpus.add("car");
		corpus.add("my");
		corpus.add("dog");
		corpus.add("sister");
		corpus.add("father");
		corpus.add("beach");
		corpus.add("sea");
		corpus.add("tree");
		corpus.add("yellow");
		corpus.add("red");

		connaissanceInitial.add("apples");
		connaissanceInitial.add("i");
		connaissanceInitial.add("yello");
		connaissanceInitial.add("is");
		connaissanceInitial.add("sun");
		connaissanceInitial.add("black");
		connaissanceInitial.add("car");
		connaissanceInitial.add("my");

		Exercice ex = new Exercice(connaissanceInitial, corpus);
		ArrayList<String> arraylistPhrase = new ArrayList<>();
		arraylistPhrase.add("i love apples.");
		arraylistPhrase.add("i am a test.");
		arraylistPhrase.add("my dog is white.");
		arraylistPhrase.add("my car is black.");
		arraylistPhrase.add("my car is yellow.");

		ex.appPhraseFiltre1(30, 2, arraylistPhrase);
		System.out.println(" affiche les phrases aprèse 1ere filtrage :  "
				+ ex.getPhraseFiltre1());
		ex.appPhraseFiltre2(connaissanceInitial);

		System.out.println(" affiche les phrases aprèse 2eme filtrage :  "
				+ ex.getPhraseFiltre2());

		// System.out.println(ex.getMonGraphLex());
	}
}
