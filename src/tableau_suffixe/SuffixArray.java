package tableau_suffixe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class SuffixArray {
	private MonolingualCorpus corpus;
	private ArrayList<Integer> suffixArray;

	public SuffixArray(MonolingualCorpus corpus) {
		this.setCorpus(corpus);
		suffixArray = new ArrayList<Integer>(corpus.getCorpus().size());
		for (int i = 0; i < corpus.getCorpus().size(); i++) {
			suffixArray.add(corpus.getEncodedString(corpus
					.getSuffixFromPosition(i)));
		}
		Comparator<Integer> comparator = new Comparator<Integer>() {
			
			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Stub de la méthode généré automatiquement
				return corpus.compareSuffixes(o1, o2);
			}
		};
		Collections.sort(suffixArray, comparator);
	}
	
	public MonolingualCorpus getCorpus() {
		return corpus;
	}

	public void setCorpus(MonolingualCorpus corpus) {
		this.corpus = corpus;
	}
}
