package tableau_suffixe;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class MonolingualCorpus {
	/**
	 * Attributs
	 */
	// contient tout le corpus (represente par leur valeur). L'indice = position du mot
	private ArrayList<Integer> corpus;
	
	// string = token, integer = valeur du token
	private HashMap<String, Integer> dictionnaire;
	
	// int = valeur, liste de int = identifiant de la phrase ou est present le token
	private HashMap<Integer, ArrayList<Integer>> tab_token;
	
	// Langue du corpus
	private String langue;
	
	private static int val_$$ = 0;			// valeur numerique du caractere de fin

	
	
	/**
	 * Constructeur
	 **/
	public MonolingualCorpus(String fileName, String langue){
		this.setLangue(langue);
		dictionnaire = new HashMap<String, Integer>();
		tab_token = new HashMap<Integer, ArrayList<Integer>>();
		if(!loadFromFile(fileName, langue)){
			System.err.println("Erreur dans le chargement du fichier -" + fileName + "-");
			//System.exit(0);
		}
		
	}
	
	public boolean loadFromFile(String fileName, String langue){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
			String ligne, token, reelleLigne;
			String[] tab;
			int val = val_$$++;		// valeur numerique des autres caracteres
			int pos = 0;			// position du token dans le corpus
			int i, id_ligne, val_token;
			
			// Ajout du caractere $$ dans le dictionnaire. On lui associe la valeur 0
			dictionnaire.put("$$", val_$$);
			// On parcours l'ensemble du corpus
			while ((ligne=br.readLine())!=null){
				// On parse la ligne en enlevant les espaces
				tab = ligne.split(" ");
				// On prend que les donnees de la langue choisie
				if(tab[1].equals(langue)){
					id_ligne = Integer.parseInt(tab[0]);

					// On lit le tableau parse (on commence a i=2 car on prend pas le int
					// et la langue
					for(i=2; i<tab.length; i++){
						
						// On transforme la chaine en minuscule
						token = tab[i].toLowerCase();
						
						// On ajoute le token au dictionnaire s'il n'y ait pas
						if(!dictionnaire.containsKey(token)){
							dictionnaire.put(token , val);
							val++;
						}
						
						// On ajoute l'identifiant de la ligne
						// Cas ou tab_token contient la valeur du token
						val_token = dictionnaire.get(token);
						if(tab_token.containsKey(val_token)){
							tab_token.get(val_token).add(id_ligne);
						}
						// Cas ou tab_token ne contient pas la valeur du token
						else{
							tab_token.put(val_token, new ArrayList<Integer>(id_ligne));
						}
						
						// Ajout du token (via sa valeur) dans le corpus
						corpus.add(val_token);
					}
					
					// On ajoute le caractere de fin de ligne $$
					corpus.add(val_$$);
					if(tab_token.containsKey(val_$$)){
						tab_token.get(val_$$).add(id_ligne);
					}
					else{
						tab_token.put(val_$$, new ArrayList<Integer>(id_ligne));
					}
				}
			}
			return true;
		}
		catch(Exception e){
			System.out.println(e.toString());
			return false;
		}
	}

	/**
	 * 
	 * @param position
	 * @return le token present dans le corpus a la position passee en parametre
	 */
	public String getTokenAtPosition(int position){
		// Message d'erreur si position est incorrecte
		if(position>=corpus.size()){
			System.err.println("La position n'est pas dans le corpus");
			System.exit(0);
		}
		
		// On recupere la valeur du token
		int val_token = corpus.get(position);
		
		// On cherche le token dans le dictionnaire
		for(Entry<String, Integer> entry : dictionnaire.entrySet()){
			if(entry.getValue() == val_token){
				return entry.getKey();
			}
		}
		
		// Si on arrive ici : probleme
		System.err.println("La valeur du token -" + val_token + "- a la position -"
				+ position + "- n'est pas dans le dictionnaire");
		return null;
	}
	
	/**
	 * Un suffixe est le token present a la position passee en parametre, et se termine 
	 * jusqu'a la fin du paragraphe (visible grace au caractere $$)
	 * @param position
	 * @return le suffixe present a la position passee en parametre dans le corpus
	 */
	public String getSuffixFromPosition(int position){
		int pos_debut_phrase = getDebutPhrase(position);
		int pos_fin_phrase = getFinPhrase(position);
		String suffixe = "";
		for(int i=pos_debut_phrase; i<pos_fin_phrase; i++){
			suffixe = suffixe + getTokenAtPosition(corpus.get(i)) + " ";
		}
		return suffixe.substring(0, suffixe.length()-2);
	}

	private int getFinPhrase(int position) {
		// Message d'erreur si position est incorrecte
		if(position>=corpus.size()){
			System.err.println("La position n'est pas dans le corpus");
			return -1;
		}
		
		int i=position;
		int size = corpus.size();
		while(i<size && corpus.get(i) != val_$$){
			i++;
		}
		if(i==size){
			return i;
		}
		return i+1;
	}

	private int getDebutPhrase(int position) {
		// Message d'erreur si position est incorrecte
		if(position>=corpus.size()){
			System.err.println("La position n'est pas dans le corpus");
			return -1;
		}
		
		int i=position;
		while(i>0 && corpus.get(i) != val_$$){
			i--;
		}
		if(i==0){
			return i;
		}
		return i+1;
	}

	/**
	 * 
	 * @param position1
	 * @param position2
	 * @return
	 */
	public int compareSuffixes(int position1, int position2){
		String chaine1 = getSuffixFromPosition(position1);
		String chaine2 = getSuffixFromPosition(position2);
		if(chaine1.equals(chaine2) || position1 == position2 ) return 0;
		int min = Math.min(chaine1.length(),chaine2.length());
		chaine1.toLowerCase();
		chaine2.toLowerCase();
		for(int i=0; i<min; i++){		
			if(chaine1.charAt(i) > chaine2.charAt(i)) return 1;
			else if(chaine1.charAt(i) < chaine2.charAt(i)) return -1;
		}
		if(chaine1.length()>chaine2.length()) return 1;
		else return -1;
	}
	
	
	
	/**
	 * Methodes auxiliaires
	 */
	
	public int getEncodedString(String token){
		return 0;
	}
	
	/**
	 * 
	 * @param ligne
	 * @param tab
	 * Permet d'enlever des caract�res de ponctuation eventuellement present 
	 * @return un string, la "vraie" ligne, sans l'integer et la langue au d�but de la ligne
	 */
	private String reelleLigne(String ligne, String[] tab) {
		int i=0;
		String lang = tab[1];
		int size = ligne.length();
		
		// On avance jusqu'a trouver la premiere lettre de la langue
		while(i<size && ligne.charAt(i) != lang.charAt(0)){
			i++;
		}
		
		// Ici, i est a la premiere lettre de la langue normalement
		int size_lang = lang.length();
		int j = 0;
		
		// On verifie bien qu'on est bien au mot de la langue
		while(i<size && j<size_lang && ligne.charAt(i) == lang.charAt(j)){
			i++;
			j++;
		}
		// Si c'est vrai, alors c'�tait bien la langue
		if(j == size_lang){
			// On enleve les espaces
			while(i<size && ligne.charAt(i) == ' '){
				i++;
			}
			// Arrive ici, c'est le debut de la "vraie" ligne
			return ligne.substring(i, size-1);
		}
		// Si on arrive ici, c'est qu'il y a eu une erreur
		return null;
	}

	/**
	 * Permet d'enlever des caract�res de ponctuation eventuellement present 
	 * a la fin du token
	 * @param token
	 * @return string (token) sans la ponctuation a la fin du token "initial"
	 */
	private String enlevePonctuationEnFinDeToken(String token) {
		char end = token.charAt(token.length()-1);
		if(!((end>='a' && end<='z')||(end>='0' && end<='9'))){
			return enlevePonctuationEnFinDeToken(token.substring(0, token.length()-2));
		}
		return token;
	}
	
	
	/**
	 * Get & Set
	 */

	public HashMap<String, Integer> getDictionnaire() {
		return dictionnaire;
	}

	public void setDictionnaire(HashMap<String, Integer> dictionnaire) {
		this.dictionnaire = dictionnaire;
	}

	public HashMap<Integer, ArrayList<Integer>> getTab_token() {
		return tab_token;
	}

	public void setTab_token(HashMap<Integer, ArrayList<Integer>> tab_token) {
		this.tab_token = tab_token;
	}

	public String getLangue() {
		return langue;
	}

	public void setLangue(String langue) {
		this.langue = langue;
	}
	
	
	/**
	 * Main pour tester cette classe AVEC LES DONNEES DE TATOEBA UNIQUEMENT
	 */
	public static void main(String[] args){
		String fileName = "test.csv";	// A CHANGER AVANT DE TESTER
		MonolingualCorpus test = new MonolingualCorpus(fileName, "fra");
		
		// Si on arrive jusqu'ici, c'est que le load n'a pas g�n�rer d'erreur
		
		/**
		 * Test de getTokenAtPosition
		 */
		int position = 4;	// A CHANGER EN FONCTION DU FICHIER
		System.out.println("Test de getTokenAtPosition a la position " + position);
		System.out.println("---> " + test.getTokenAtPosition(position));
		
		/**
		 * Test de getSuffixFromPosition
		 */
		position = 4;	// A CHANGER EN FONCTION DU FICHIER
		System.out.println("Test de getSuffixFromPosition a la position " + position);
		System.out.println("---> " + test.getSuffixFromPosition(position));
		
		/**
		 * Test de compareSuffixes
		 */
		
		
		/**
		 * Affichage du dictionnaire
		 */
		System.out.println("---------Dictionnaire------------");
		for(Entry<String, Integer> entry : test.getDictionnaire().entrySet()) {
		    System.out.println("(" + entry.getKey() + ", " + entry.getValue() + ")");
		}
		System.out.println("---------------------------------");
		
		
		/**
		 * Affichage de tab_token
		 
		System.out.println("-----------Tab_token------------");
		for(Entry<Integer, Integer> entry2 : test.getTab_token().entrySet()) {
		    System.out.println("(" + entry2.getKey() + ", " + entry2.getValue() + ")");
		}
		System.out.println("--------------------------------");
		*/
	}

	public ArrayList<Integer> getCorpus() {
		return corpus;
	}

	public void setCorpus(ArrayList<Integer> corpus) {
		this.corpus = corpus;
	}
}
