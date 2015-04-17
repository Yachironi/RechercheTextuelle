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
	private ArrayList<String> corpus;				// contient tout le corpus (ensemble de lignes)
	private int nbMotTotal;							// Le nombre de mot dans le corpus
	private HashMap<String, Integer> dictionnaire;	// string = token, integer = valeur
	private HashMap<Integer, Integer> tab_token;	// int = valeur, int = position
	private String langue;
	
	
	/**
	 * Constructeur
	 **/
	public MonolingualCorpus(String fileName, String langue){
		this.setLangue(langue);
		dictionnaire = new HashMap<String, Integer>();
		tab_token = new HashMap<Integer, Integer>();
		if(!loadFromFile(fileName, langue)){
			System.err.println("Erreur dans le chargement du fichier -" + fileName + "-");
			//System.exit(0);
		}
		
	}

	/** Permet de charger le corpus, le segmenter en tokens 
	 *  et cr�er les structures de donn�es associ�es
	 * @param fileName
	 * @return true si le chargement se fait correctement, false sinon
	 */
	public boolean loadFromFile(String fileName, String langue){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
			String ligne, token, reelleLigne;
			String[] tab;
			int val_$$ = 0;			// valeur num�rique du caract�re de fin
			int val = val_$$++;		// valeur num�rique des autres caract�res
			int pos = 0;			// position du token dans le corpus
			int i;
			
			// Ajout du caract�re $$ dans le dictionnaire. On lui associe la valeur 0
			dictionnaire.put("$$", val_$$);
			
			// On lit une ligne
			while ((ligne=br.readLine())!=null){
				// On parse la ligne en enlevant les espaces
				tab = ligne.split(" ");
				System.out.println(ligne);
				System.out.println("size "+tab.length);
				// On prend en compte que les lignes de la langue
				if(tab[1].equals(langue)){
					
					// On souhaite ajouter la "vraie" ligne a l'attribut corpus 
					// (sans le int et la langue)
					reelleLigne = reelleLigne(ligne, tab);
					
					// Si c'est null --> erreur
					if(reelleLigne == null){
						System.err.println("reelleLigne est null");
						System.exit(0);
					}
					
					// On remplit la variable corpus
					corpus.add(reelleLigne);
					
					// On lit le tableau pars�
					
					/**
					 *Remarque : on commence a i=2 car a 
					 * - i=0 : c'est un integer (on en fait rien??)
					 * - i=1 : c'est la langue
					 */
					for(i=2; i<tab.length; i++){
						
						// On transforme la chaine en minuscule
						token = tab[i].toLowerCase();
						
						// Si on tombe sur le caract�re de fin de paragraphe $$
						if(token.equals("$$") && !tab_token.containsKey(val_$$)){
							// On ajoute la position du $$
							tab_token.put(val_$$, pos);
						}
						
						// Autre que caract�re $$
						else{
							char firstLettre = token.charAt(0);
							/** A MODIFIER : ce if dit qu'on accepte que les tokens qui commence par
							 * un chiffre ou une lettre ==> Necessaire?
							 */
							// Le token doit commencer par une lettre ou un chiffre
							if((firstLettre>='a' && firstLettre<='z') || (firstLettre>='0' && firstLettre<='9')){
							
								// On enleve un caract�re de ponctuation ou autre s'il y
								// en a un a la fin du token
								token = enlevePonctuationEnFinDeToken(token);
															
								// Token non pr�sent dans le dictionnaire
								if(!dictionnaire.containsKey(token)){
									// Ajout du token dans le dictionnaire (avec sa valeur)
									dictionnaire.put(token , val);
									
									// Ajout de la position dans le tableau de token
									tab_token.put(val, pos);
									val++;
								}
							}	
						}
						pos++;
					}
				}
			}
			setNbMotTotal(pos+1);
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
		// Parcours de tab_token (valeur, position)
		for(Entry<Integer, Integer> entry : tab_token.entrySet()) {
		    Integer cle_tab_token = entry.getKey();
		    Integer valeur_tab_token = entry.getValue();
		    
		    // Cas ou on a trouv� la bonne position
		    if(valeur_tab_token == position){
		    	// Parcours du dictionnaire (token, valeur)
				for(Entry<String, Integer> entry2 : dictionnaire.entrySet()) {
				    String token = entry2.getKey();
				    Integer valeur_token = entry2.getValue();
				    
				    // Cas ou on a trouv� le token
				    if(valeur_tab_token == valeur_token){
				    	return token;
				    }
				}
		    }
		}
		return null;
	}
	
	/**
	 * Un suffixe est le token present a la position passee en parametre, et se termine 
	 * jusqu'a la fin du paragraphe (visible grace au caractere $$)
	 * @param position
	 * @return le suffixe present a la position passee en parametre dans le corpus
	 */
	public String getSuffixFromPosition(int position){
		String suffixe = "";
		int i, j;
		int nbMots = 0;
		for(i=0; i<corpus.size(); i++){
			nbMots += corpus.get(i).split(" ").length;
			// Le mot est dans cette ligne
			if(position<=nbMots){
				String[] tab = corpus.get(i).split(" ");
				int pos = nbMots-position;
				
				// On parcourt la ligne
				for(j=pos; j<tab.length; j++){
					// Si on tombe sur la fin du paragraphe, on renvoie le suffixe
					if(tab[j].equals("$$")){
						return suffixe.substring(0, suffixe.length()-2);
					}
					suffixe = suffixe + tab[j] + " ";
				}
				// Si on sort de cette boucle, la ligne est fini mais pas le paragraphe :
				//    il faut continuer aux lignes suivantes jusqu'a trouver $$
				for(i=i+1; i<corpus.size(); i++){
					tab = corpus.get(i).split(" ");
					for(j=0; j<tab.length; j++){
						// Si on tombe sur la fin du paragraphe, on renvoie le suffixe
						if(tab[j].equals("$$")){
							return suffixe.substring(0, suffixe.length()-2);
						}
						suffixe += tab[j];
					}
				}
			}
		}
		if(!suffixe.equals("")){
			return suffixe.substring(0, suffixe.length()-2);
		}
		return null;
	}
	
	/**
	 * 
	 * @param position1
	 * @param position2
	 * @return
	 */
	public int compareSuffixes(int position1, int position2){
		return 0;
	}
	
	
	
	/**
	 * Methodes auxiliaires
	 */
	
	/**
	 * 
	 * @param ligne
	 * @param tab
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

	public HashMap<Integer, Integer> getTab_token() {
		return tab_token;
	}

	public void setTab_token(HashMap<Integer, Integer> tab_token) {
		this.tab_token = tab_token;
	}

	public int getNbMotTotal() {
		return nbMotTotal;
	}

	public void setNbMotTotal(int nbMotTotal) {
		this.nbMotTotal = nbMotTotal;
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
		 */
		System.out.println("-----------Tab_token------------");
		for(Entry<Integer, Integer> entry2 : test.getTab_token().entrySet()) {
		    System.out.println("(" + entry2.getKey() + ", " + entry2.getValue() + ")");
		}
		System.out.println("--------------------------------");
	}
}
