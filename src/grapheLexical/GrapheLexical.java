package grapheLexical;

import java.util.ArrayList;
import java.util.HashSet;
import nlp.smt.tools.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import utils.Paire;

/*
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.SerializationUtils;
import org.springframework.core.io.ClassPathResource;
*/

public class GrapheLexical {
	SimpleWeightedGraph<Paire<String, Integer>, DefaultWeightedEdge> completeGraph;

	/**
	 * Constructeur du graph de connaissance
	 * 
	 * @param connaissanceInitial
	 *            Tableau de toutes les mots maitrisé par l'utilisateur
	 */
	GrapheLexical(ArrayList<String> connaissanceInitial,
			ArrayList<String> corrpus) {
		completeGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
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
					completeGraph.setEdgeWeight(edge, 10);

				}
				// TODO Remplacer 10 par la valeur donné par la bibliotheque
				// WORD2VEC
			}
		}
	}

	// ajouter , enlever , voir la similarité entre 2 mots,

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
	 * update un nouveau mot dans le graph
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

	
	
	
	
	
	
/*
	
	public LexicalGraph(Word2Vec vec ) {
		  
		VocabCache cache = vec.getCache();
		HashSet<String> dejaVu = new HashSet<String>();
		
		for (String a : cache.words()) {
			for (String b : cache.words()) {
				if (!dejaVu.contains(b)) {
					SimilarityValue sv = new SimilarityValue(a, b, vec.similarity(a, b));
					arcs.add(sv);
				}
			}
			dejaVu.add(a);
		}
	}
	
	public void print() {
		 SentenceIterator iter;
		 TokenizerFactory tokenizer;
		 VocabCache cache;
		 Word2Vec vec;
		 
		 tokenizer = new DefaultTokenizerFactory();
		 iter = new LineSentenceIterator();
		 vec = new Word2Vec.Builder().minWordFrequency(5).vocabCache(cache)
				.windowSize(5)
				.layerSize(100).iterate(iter).tokenizerFactory(tokenizer)
				.build();

		String mot1 = new String("test");
		String mot2 = new String("tester");
		System.out.printf("similarite entre test et tester =  %f \n", vec.similarity(mot1, mot2));
		 
	}
	
	
	*/
	
	
	
	
	
	
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
}
