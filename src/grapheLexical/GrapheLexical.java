package grapheLexical;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

import utils.Paire;

class GrapheLexical {
	SimpleWeightedGraph<Paire<String, Integer>, DefaultWeightedEdge> completeGraph;
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
	GrapheLexical(ArrayList<String> connaissanceInitial,
			ArrayList<String> corrpus,String path) {
		
		completeGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		/* Initialisation de Word2Vec*/
		ClassPathResource resource = new ClassPathResource(
				"raw_sentences.txt");
		File f = null;
		try {
			f = resource.getFile() ;
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
			for (String s : cache.words()) {
				System.out.println(s);
			}
		}
		
		/* Fin Initialisation de Word2Vec*/
		
		
		
		for (int i = 0; i < connaissanceInitial.size(); i++) {
			completeGraph.addVertex(new Paire<String, Integer>(
					connaissanceInitial.get(i), 10));
		}
		for (int i = 0; i < corrpus.size(); i++) {
			completeGraph.addVertex(new Paire<String, Integer>(corrpus.get(i),
					0));
		}
		
		for (Paire<String, Integer> vertexSource : completeGraph.vertexSet()) {
			for (Paire<String, Integer> vertexDestination : completeGraph
					.vertexSet()) {
				if ((vertexDestination != vertexSource)
						&& (completeGraph.getEdge(vertexDestination,
								vertexSource) == null)) {
					DefaultWeightedEdge edge = completeGraph.addEdge(
							vertexSource, vertexDestination);
					completeGraph.setEdgeWeight(edge, vec.similarity(vertexSource.getFirst(),vertexDestination.getFirst()));

				}
				// TODO Remplacer 10 par la valeur donné par la bibliotheque
				// WORD2VEC
			}
		}
	}  
 
	// ajouter , enlever , voir la similarité entre 2 mots,
	// **** *

	/*
	 * return le taux de similarite entre 2 mot, -1 si il ne trouve pas, c'est a
	 * dire un des deux mots n'existe pas
	 */
	public double similarite2Mots(String mot1, String mot2) {

		for (DefaultWeightedEdge edge : completeGraph.edgeSet()) {
			if ((completeGraph.getEdgeSource(edge).getFirst().equals(mot1) && completeGraph
					.getEdgeTarget(edge).getFirst().equals(mot2))
					|| (completeGraph.getEdgeSource(edge).getFirst().equals(mot2) && completeGraph
.getEdgeTarget(edge)
							.getFirst().equals(mot1))) {

				return completeGraph.getEdgeWeight(edge);
			}
		}
		return -1;
	}

	/*
	 * update la valeur d'un mot du graph
	 */
	public void updateMot(String mot1, int val) {

		for (Paire<String, Integer> vertex : completeGraph.vertexSet()) {
			if (vertex.getFirst().equals(mot1)) {
				vertex.setSecond(val);
				System.out.println("update de "+ mot1 + " avec success : new val " + vertex.getSecond());
			}
		}
		
	}

	/* supprime un mot du graph */
	public boolean suppMot(String mot) {
		for (Paire<String, Integer> vertex : completeGraph.vertexSet()) {
			if (vertex.getFirst().equals(mot)) {
				completeGraph.removeVertex(vertex);
				System.out.println("le mot : " + mot
						+ " a été supprimé du graph.");
				return true;
			}
		}
		System.out.println("le mot : " + mot + " n'existe pas.");
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("GrapheLexical { \n");
		for (DefaultWeightedEdge edge : completeGraph.edgeSet()) {
			sb.append(completeGraph.getEdgeSource(edge) + " , "
					+ completeGraph.getEdgeTarget(edge) + " = "
					+ completeGraph.getEdgeWeight(edge) + "\n");
		}
		sb.append("}");
		return sb.toString();
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
		
		GrapheLexical graph = new GrapheLexical(connaissanceInitial, corrpus,"pathToCorpus");
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
				System.out.println(s);
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			String line;
			System.out.println("Print similarity");
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
