package tableau_suffixe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

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
				return corpus.compareSuffixes(o1, o2);
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

	int compareStringToSuffix(String phrase, Integer suffixPosition) {

		return suffixPosition;
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

	ArrayList<Integer> getAllPositionsOfPhrase(String phrase) {
		Set<Integer> resultat = new HashSet<Integer>();
		try {
			ArrayList<Integer> encodedString = corpus.getEncodedPhrase(phrase
					.toLowerCase());

			for (int i = 0; i < suffixArray.size(); i++) {
				// System.out.println("Phrase["+i+"] = "+corpus.getSuffixFromPosition(i)+" | "+"Au revoir et à demain .");

				if (corpus.getEncodedPhrase(corpus.getSuffixFromPosition(i))
						.containsAll(encodedString)) {
					// System.out.println("trouvé");
					// System.out.println(corpus.getPhraseFromPosition(i));
					
					
					// TODO Ajouter le numéro de la ligne ou on trouve le token au lieu de la position dans la tablea du suffix
					resultat.add(i);
				}
			}
			return new ArrayList<>(resultat);
		} catch (TokenNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
