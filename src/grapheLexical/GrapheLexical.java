package grapheLexical;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.SerializationUtils;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.springframework.core.io.ClassPathResource;

import akka.japi.Pair;
import scala.annotation.varargs;
import utils.Paire;

public class GrapheLexical {
	SimpleWeightedGraph<Paire<String, Integer>, DefaultWeightedEdge> completeGraph;
	HashMap<String, Integer> graphe;
	private SentenceIterator iter;
	private TokenizerFactory tokenizer;
	private Word2Vec vec;
	public final static String VEC_PATH = "vec2.ser";
	public final static String CACHE_SER = "cache.ser";

	/**
	 * Constructeur du graph de connaissance
	 * 
	 * @param connaissanceInitial
	 *            Tableau de toutes les mots maitrisé par l'utilisateur
	 */
	public GrapheLexical(ArrayList<String> connaissanceInitial,
			ArrayList<String> corrpus) {

		completeGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		graphe = new HashMap<>();
		/* Initialisation de Word2Vec */
		System.out.println("===== Initialisation de Word2Vec ======");
		ClassPathResource resource = new ClassPathResource("phrasesENsaved.txt");
		File f = null;
		try {
			f = resource.getFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.iter = new LineSentenceIterator(new File(f.getAbsolutePath()));
		tokenizer = new DefaultTokenizerFactory();
		VocabCache cache;

		if (vec == null && !new File(VEC_PATH).exists()) {
			cache = new InMemoryLookupCache.Builder().lr(2e-5)
					.vectorLength(100).build();
			vec = new Word2Vec.Builder().vocabCache(cache).windowSize(5)
					.layerSize(100).iterate(iter).tokenizerFactory(tokenizer)
					.build();
			vec.setCache(cache);
			vec.fit();
			SerializationUtils.saveObject(vec, new File(VEC_PATH));
			SerializationUtils.saveObject(cache, new File(CACHE_SER));
		} else {
			vec = SerializationUtils.readObject(new File(VEC_PATH));
			cache = SerializationUtils.readObject(new File(CACHE_SER));
			vec.setCache(cache);
			System.out
					.println("--------------------------------------------------");

		}

		// for(String s : cache.words())
		// System.out.println("Vocab "+s);
		System.out.println("===== Initialisation de Word2Vec ======");

		/* Fin Initialisation de Word2Vec */

		System.out.println("===== Creation de graph de connaissance ======");
		for (int i = 0; i < connaissanceInitial.size(); i++) {
			completeGraph.addVertex(new Paire<String, Integer>(
					connaissanceInitial.get(i), 10));
			graphe.put(connaissanceInitial.get(i), 10);
		}
		System.out.println("===== Creation de graph de connaissance ======");


	}

	// ajouter , enlever , voir la similarité entre 2 mots,
	// **** *

	/*
	 * return le taux de similarite entre 2 mot, -1 si il ne trouve pas, c'est a
	 * dire un des deux mots n'existe pas
	 */
	public double similarite2Mots(String mot1, String mot2) {
		/*
		 * for (DefaultWeightedEdge edge : completeGraph.edgeSet()) { if
		 * ((completeGraph.getEdgeSource(edge).getFirst().equals(mot1) &&
		 * completeGraph .getEdgeTarget(edge).getFirst().equals(mot2)) ||
		 * (completeGraph.getEdgeSource(edge).getFirst() .equals(mot2) &&
		 * completeGraph.getEdgeTarget(edge) .getFirst().equals(mot1))) {
		 * 
		 * return completeGraph.getEdgeWeight(edge); } }
		 */
		return vec.similarity(mot1, mot2);
	}

	/*
	 * update la valeur d'un mot du graph
	 */
	public void updateMot(String mot1, int val) {
		graphe.put(mot1, val);
	}

	/* Nearest word to string */
	public Collection<String> nearest(String mot, int n) {
		return this.vec.wordsNearest(mot, n);
	}

	/**
	 * 
	 * @param mot
	 * @param niveaux
	 * @return contient ou pas
	 */
	public boolean estMetrise(String mot, int niveaux) {
		if (graphe.containsKey(mot) && graphe.get(mot) >= niveaux) {
			return true;
		} else {
			return false;
		}
	}

	/* supprime un mot du graph */
	public boolean suppMot(String mot) {
		if (graphe.containsKey(mot)) {
			graphe.remove(graphe.get(mot));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return graphe.toString() ;
	}

	public static void main(String[] args) {
		ArrayList<String> connaissanceInitial = new ArrayList<String>() {
			{
				add("elephant");
				add("gazelle");
			}
		};
		ArrayList<String> corrpus = new ArrayList<String>() {
			{
				add("lion");
				add("zebre");
				add("cahier");
				add("enfant");
				add("zoo");
			}
		};

		GrapheLexical graph = new GrapheLexical(connaissanceInitial, corrpus);
		graph.updateMot("zoo", 100);

		System.out.println(graph);

		System.out.println("similarite entre 2 mot : elephant et lion = "
				+ graph.similarite2Mots("elephant", "lion"));

	}

	public void train() throws Exception {
		VocabCache cache;
		if (vec == null && !new File(VEC_PATH).exists()) {
			cache = new InMemoryLookupCache.Builder().lr(2e-5)
					.vectorLength(100).build();
			vec = new Word2Vec.Builder().vocabCache(cache).windowSize(5)
					.layerSize(100).iterate(iter).tokenizerFactory(tokenizer)
					.build();
			vec.setCache(cache);
			vec.fit();
			SerializationUtils.saveObject(vec, new File(VEC_PATH));
			SerializationUtils.saveObject(cache, new File(CACHE_SER));
		} else {
			vec = SerializationUtils.readObject(new File(VEC_PATH));
			cache = SerializationUtils.readObject(new File(CACHE_SER));
			vec.setCache(cache);
			for (String s : cache.words()) {
				// System.out.println(s);
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			String line;
			//System.out.println("Print similarity");
			while ((line = reader.readLine()) != null) {
				String[] split = line.split(",");
				if (cache.indexOf(split[0]) < 0) {
					System.err.println("Word " + split[0] + " not in vocab");
					continue;
				}
				if (cache.indexOf(split[01]) < 0) {
					System.err.println("Word " + split[1] + " not in vocab");
					continue;
				}
				System.out.println(vec.similarity(split[0], split[1]));
			}
		}
	}
}
