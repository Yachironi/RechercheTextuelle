package grapheLexical;

import java.util.ArrayList;

import org.deeplearning4j.word2vec.Word2Vec;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import utils.Paire;

class GrapheLexical {
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
		GrapheLexical graph = new GrapheLexical(connaissanceInitial, corrpus);

		System.out.println(graph);

		Word2Vec vec = new Word2Vec(corrpus);
		vec.setLayerSize(300);
		vec.setWindow(5);
		
		System.out.println(vec.similarity("lion", "lion"));
		System.out.println(vec.similarity("lion", "cub"));
		System.out.println(vec.similarity("lion", "lioness"));
		System.out.println(vec.similarity("lion", "cat"));
		System.out.println("FIN");
	}
}
