package tableau_suffixe;

public class SuffixArray {
	private MonolingualCorpus corpus;
	
	public SuffixArray(MonolingualCorpus corpus){
		this.setCorpus(corpus);
	}

	public MonolingualCorpus getCorpus() {
		return corpus;
	}

	public void setCorpus(MonolingualCorpus corpus) {
		this.corpus = corpus;
	}
}
