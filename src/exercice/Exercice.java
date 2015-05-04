package exercice;

import java.util.ArrayList;

import grapheLexical.*;
import tableau_suffixe.*;
import traducteur.*;

public class Exercice {

	// TODO il faut faire un filtre en deux etape : 1ere filtre : 20 % de mot
	// incconnu par phrase
	// 2eme filtre : cherche ce 30 % de mot inconnu selon le domaine le plus
	// imposant

	private GrapheLexical monGraphLex;
	private Traducteur monTrad;
	private ArrayList<String> phraseFiltre1;
	private ArrayList<String> phraseFiltre2;

	public Exercice() {
		ArrayList<String>  phraseFiltre1 = new ArrayList<String>();
		ArrayList<String>  phraseFiltre2 = new ArrayList<String>();
		monTrad = new Traducteur("fra", "eng", "Files/testCorpus.txt",
				"Files/link.txt");
		
		/* TODO initialiser monGraphLex , ArrayList<String> connaissanceInitial à met dans les variable de GrapheLexical directement */
	}

    /*remplit l'arrayList phrasePourExo de phrase avec un taux de mot inconnu inférieur à un pourcentage qui est passé en paramètre  */
	public void getPhraseFiltre1( int pourcentage, int nbligneMax, ArrayList<String> connaissanceInitial) {

		for(int i=0; i< nbligneMax;i++ ){
													
			 if(tauxMotInconnu(monTrad.getSuffixArray_lang2().getCorpus().getPhraseFromLine(i), connaissanceInitial) < pourcentage){//on ajoute la phrase si le taux de mot inconnu est inférieur à 20
				 if( phraseFiltre1.contains(monTrad.getSuffixArray_lang2().getCorpus().getPhraseFromLine(i))==false)// on ajoute si il n'y a pas de redondance de la même phrase
					 phraseFiltre1.add(monTrad.getSuffixArray_lang1().getCorpus().getPhraseFromLine(i));
			 }
		}		 
	}

	
	
	/* renvoye le taux de mot inconnu dans une phrase */

	public double tauxMotInconnu(String phrase,
			ArrayList<String> connaissanceInitial) {
		String[] phraseEnMot = phrase.split("\\W+");
		;
		int compteurMotInconnu = 0;
		int nbMotDansPhrase = phraseEnMot.length;
		for (int i = 0; i < nbMotDansPhrase; i++) {
			if (connaissanceInitial.contains(phraseEnMot[i]) == false) {
				compteurMotInconnu++;
			}
		}
		return (compteurMotInconnu / nbMotDansPhrase);
	}

	public static void main(String[] args) {
		Exercice ex = new Exercice();
	}
}
