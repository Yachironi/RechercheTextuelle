package tableau_suffixe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.CompareGenerator;


public class SuffixArray {
	private MonolingualCorpus corpus;
	private ArrayList<Integer> suffixArray;
	private ArrayList<Integer> lcpArray;

	public SuffixArray(final MonolingualCorpus corpus) {
		this.setCorpus(corpus);
		suffixArray = new ArrayList<Integer>();
		for (int i = 0; i < corpus.getCorpus().size(); i++) {
			if (corpus.getCorpus().get(i) != 0) {
				suffixArray.add(i);
				/*
				 * System.out.println(i); String s =
				 * corpus.getSuffixFromPosition(i); ArrayList<Integer>
				 * encodedString = corpus
				 * .getEncodedPhrase(corpus.getSuffixFromPosition(i));
				 * System.out.println(corpus.getSuffixFromPosition(i));
				 * System.out.println(encodedString); for (int j = 0 ; j <
				 * encodedString.size(); j++) {
				 * //suffixArray.addAll(encodedString.subList(0, j));
				 * System.out.println(encodedString.subList(j,
				 * encodedString.size()));
				 * suffixArray.add(encodedString.subList(j,
				 * encodedString.size()).get(0));
				 * 
				 * }
				 */
				// System.out.println(encodedString.get(0));
				// suffixArray.add(corpus.getEncodedPhrase(corpus.getSuffixFromPosition(i)));
			}
		}

		/*
		 * System.out.println(suffixArray);
		 * 
		 * for(int i=0; i<suffixArray.size();i++){
		 * System.out.println(corpus.getSuffixFromPosition(suffixArray.get(i)));
		 * 
		 * }
		 */
		Comparator<Integer> comparator = new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				//return corpus.compareSuffixes(o1, o2);
				return corpus.compareSuffixesInt(o1, o2);
			}
		};
		Collections.sort(suffixArray, comparator);

		System.out.println("------- DEB SuffixArray -------");
		for (int i = 0; i < suffixArray.size(); i++)
			System.out
					.println(corpus.getSuffixFromPosition(suffixArray.get(i)));
		System.out.println("------- FIN SuffixArray -------");

		try {
			setLcp();
		} catch (TokenNotFoundException e) {
			e.printStackTrace();
		}
		/*
		 * for(int i=0;i<lcpArray.size();i++){
		 * System.out.println(lcpArray.get(i)); }
		 */
		System.out.println("------- DEB LCP --------");
		System.out.println(lcpArray);
		System.out.println("------- FIN LCP --------");

	}

	public MonolingualCorpus getCorpus() {
		return corpus;
	}

	public void setCorpus(MonolingualCorpus corpus) {
		this.corpus = corpus;
	}

	public void setLcp() throws TokenNotFoundException {
		lcpArray = new ArrayList<Integer>(suffixArray.size() + 1);
		lcpArray.add(0);
		for (int i = 1; i < suffixArray.size(); i++) {
			int tmp = 0;
			ArrayList<Integer> encodedString1 = corpus.getEncodedPhrase(corpus
					.getSuffixFromPosition(suffixArray.get(i - 1)));
			ArrayList<Integer> encodedString2 = corpus.getEncodedPhrase(corpus
					.getSuffixFromPosition(suffixArray.get(i)));

			int min = Math.min(encodedString1.size(), encodedString2.size());
			for (int j = 0; j < min; j++) {
				if (encodedString1.get(j) == encodedString2.get(j)) {
					tmp++;
				} else {
					lcpArray.add(tmp);
					break;
				}
			}
		}
		lcpArray.add(0);
	}



	/*
	 * ArrayList<Integer> getAllPositionsOfPhrase(String phrase) {
	 * ArrayList<Integer> resultatDeRecherche = new ArrayList<>(); try {
	 * ArrayList<Integer> encodedString = corpus.getEncodedPhrase(phrase);
	 * 
	 * for (int i = 0; i < suffixArray.size(); i++) { if
	 * (corpus.getDictionnaire().containsKey(
	 * corpus.getTokenAtPosition(suffixArray.get(i))) &&
	 * compareStringToSuffix(phrase, suffixArray.get(i)) >= 0) {
	 * resultatDeRecherche.add(suffixArray.get(i));
	 * System.out.println("Phrase demande = "+phrase);
	 * System.out.println("Phrases resultat = "
	 * +corpus.getPhraseFromPosition(suffixArray.get(i)));
	 * 
	 * int j = i + 1; while (lcpArray.get(j) >= encodedString.size()) {
	 * resultatDeRecherche.add(suffixArray.get(j)); j++; } break; } }
	 * 
	 * System.out.println("Encoded = " + encodedString);
	 * System.out.println("resultatDeRecherche = "+resultatDeRecherche); return
	 * resultatDeRecherche; } catch (TokenNotFoundException e) {
	 * e.printStackTrace(); return null; } }
	 */

	
	
	public ArrayList<Integer> rechercheDichotomique(String phrase ){
		int size = suffixArray.size();
		int debut = 0;
		int fin = size;
		int milieu = (fin-debut)/2;
		ArrayList<Integer> resultat = new ArrayList<Integer>();
		/*Dans le cas ou tableau de suffixe serai en fonction de la valeur, et non pas en fonction
		 * de l'order lexicographique
		 */
		ArrayList<Integer> encodedPhrase = new ArrayList<Integer>();
		try {
			encodedPhrase = corpus.getEncodedPhrase(phrase.toLowerCase());
		} catch (TokenNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int val;
		// il faut que fin - debut soit >= 2
		while(milieu >= 0){ // A VERIFIER AVEC JUJU
			// valeur est dans la partie de droite
		    //val = compareStringToSuffixe(suffixArray.get(milieu), phrase);
			val = compareToSuffix(milieu, encodedPhrase);
			
			if(val >0){
				debut = milieu;
			}
			// valeur est dans la partie de gauche
			else if(val < 0){
				fin = milieu;
				int m = debut + (fin-debut)/2;
			}
			// valeur a ete trouvee
			else {
				while(lcpArray.get(milieu)>=phrase.split(" ").length) 
					{
					resultat.add(milieu);
					milieu--;	
					}
				resultat.add(milieu);
				return resultat;	// milieu est la position ou se trouve valeur
			}
			milieu = debut + (fin-debut)/2;
		}
		
		return null;
	}
	
	
	ArrayList<Integer> getAllPositionsOfPhrase(String phrase) {
		ArrayList<Integer> resultat = new ArrayList<Integer>();
		try {
			ArrayList<Integer> encodedString = corpus.getEncodedPhrase(phrase
					.toLowerCase());
			/* On vérifie que tous les tokens appartiennent au dictionnaire
			 * Si ce n'est pas le cas, on peut sortir
			 */
			
			for(int i=0; i<encodedString.size();i++)
			{
				if(!corpus.getDictionnaire().containsValue(encodedString.get(0)))
					return null;
			}
			resultat = rechercheDichotomique(phrase);
			if(resultat == null){
				return null;
			}
			else{
			
			int tmp = resultat.get(0)+1;
			System.out.println("Val"+resultat.get(0));
			while(lcpArray.get(tmp)>=phrase.split(" ").length){
				resultat.add(tmp);
				tmp++;
			}
			ArrayList<Integer> finale = new ArrayList<Integer>();
			for(int i=0;i<resultat.size();i++){
				finale.add(suffixArray.get(resultat.get(i)));
			}
				return finale;
			}	
				
		} catch (TokenNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	/*
	 * Dans le cas ou le tableau de suffixe sera trier suivant la valeur
	 */
	int compareToSuffix(int position, ArrayList<Integer> phrase){
		/* En mode int */ 
		try {
			ArrayList<Integer> encodedSuffix = corpus.getEncodedPhrase(corpus.getSuffixFromPosition(suffixArray.get(position)));
			
			int min = Math.min(phrase.size(), encodedSuffix.size());
			for(int i=0;i<min;i++)
			{
				if(phrase.get(i)>encodedSuffix.get(i)) return 1;
				if(phrase.get(i)<encodedSuffix.get(i)) return -1; 
			}
			return 0;
		} catch (TokenNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	int compareStringToSuffixe(int position, String phrase){
		String suffixe = corpus.getSuffixFromPosition(position);
		
		int min = Math.min(suffixe.length(),phrase.length());
		for(int i=0; i<min;i++){
			if(phrase.charAt(i) > suffixe.charAt(i)) return 1;
			if (phrase.charAt(i) < suffixe.charAt(i)) return -1;
		}
		return 0;
	}
}
