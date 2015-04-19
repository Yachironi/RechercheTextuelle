package tableau_suffixe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class SuffixArray {
	private MonolingualCorpus corpus;
	private ArrayList<Integer> suffixArray;

	public SuffixArray(MonolingualCorpus corpus) {
		this.setCorpus(corpus);
		suffixArray = new ArrayList<Integer>();
		for (int i = 0; i < corpus.getCorpus().size(); i=corpus.getFinPhrase(i)+1) {
			if (corpus.getCorpus().get(i) != 0) {
				//System.out.println(corpus.getSuffixFromPosition(i));
				ArrayList<Integer> encodedString = corpus
						.getEncodedString(corpus.getSuffixFromPosition(i));
				//System.out.println(encodedString);
				for (int j = 0 ; j < encodedString.size(); j++) {
					//suffixArray.addAll(encodedString.subList(0, j));
					System.out.println(encodedString.subList(j, encodedString.size()));
					suffixArray.add(encodedString.subList(j, encodedString.size()).get(0));
					
				}
				//System.out.println(encodedString.get(0));
				// suffixArray.add(corpus.getEncodedString(corpus.getSuffixFromPosition(i)));
			}
		}

		System.out.println(suffixArray);
		for (Iterator iterator = suffixArray.iterator(); iterator.hasNext();) {
			Integer integer = (Integer) iterator.next();
			//System.out.println(integer);
			System.out.println(corpus.getSuffixFromPosition(integer-1));
		}
		Comparator<Integer> comparator = new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return corpus.compareSuffixes(o1, o2);
			}
		};
		Collections.sort(suffixArray, comparator);
		//System.out.println(suffixArray);
	}

	public MonolingualCorpus getCorpus() {
		return corpus;
	}

	public void setCorpus(MonolingualCorpus corpus) {
		this.corpus = corpus;
	}
}
