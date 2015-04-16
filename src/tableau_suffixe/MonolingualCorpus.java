package tableau_suffixe;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class MonolingualCorpus {
	/**
	 * Attributs (à compléter)
	 */
	private ArrayList<String> corpus;				// contient tout le corpus
	private int nbMotTotal;							// Le nombre de mot dans le corpus
	private HashMap<String, Integer> dictionnaire;	// string = token, integer = valeur
	private HashMap<Integer, Integer> tab_token;	// int = valeur, int = position
	
	
	/**
	 * Constructeur (à compléter)
	 */
	public MonolingualCorpus(String fileName){
		dictionnaire = new HashMap<String, Integer>();
		tab_token = new HashMap<Integer, Integer>();
		if(!loadFromFile(fileName)){
			System.err.println("Erreur dans le chargement du fichier -" + fileName + "-");
			System.exit(0);
		}
		
	}

	/** Permet de charger le corpus, le segmenter en tokens 
	 *  et créer les structures de données associées
	 * @param fileName
	 * @return true si le chargement se fait correctement, false sinon
	 */
	public boolean loadFromFile(String fileName){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
			String ligne;
			String[] tab;
			int val_$$ = 0;
			int val = val_$$++;
			int pos = 0;
			
			// Ajout du caractère $$ dans le dictionnaire. On lui associe la valeur 0
			dictionnaire.put("$$", val_$$);
			
			// On lit une ligne
			while ((ligne=br.readLine())!=null){
				// Ajout de la ligne
				corpus.add(ligne);
				
				tab = ligne.split(" ");		// On parse la ligne
				
				// On lit le tableau parsé
				for(String token : tab){
					token = token.toLowerCase();
					// Caractère de fin de paragraphe $$
					if(token.equals("$$") && !tab_token.containsKey(val_$$)){
						// On ajoute la position du $$
						tab_token.put(val_$$, pos);
					}
					
					// Autre que caractère $$
					else{
						char firstLettre = token.charAt(0);
						/** A MODIFIER : ce if dit qu'on accepte que les tokens qui commence par
						 * un chiffre ou une lettre ==> Necessaire?
						 */
						if((firstLettre>='a' && firstLettre<='z') || (firstLettre>='0' && firstLettre<='9')){
						
							// Token non présent dans le dictionnaire
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
			setNbMotTotal(pos+1);
			return true;
		}
		catch(Exception e){
			System.out.println(e.toString());
			return false;
		}
	}
	
	public String getTokenAtPosition(int position){
		// Parcours de tab_token (valeur, position)
		for(Entry<Integer, Integer> entry : tab_token.entrySet()) {
		    Integer cle_tab_token = entry.getKey();
		    Integer valeur_tab_token = entry.getValue();
		    
		    // Cas ou on a trouvé la bonne position
		    if(valeur_tab_token == position){
		    	// Parcours du dictionnaire (token, valeur)
				for(Entry<String, Integer> entry2 : dictionnaire.entrySet()) {
				    String token = entry2.getKey();
				    Integer valeur_token = entry2.getValue();
				    
				    // Cas ou on a trouvé le token
				    if(valeur_tab_token == valeur_token){
				    	return token;
				    }
				}
		    }
		}
		return null;
	}
	
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
	
	public int compareSuffixes(int position1, int position2){
		return 0;
	}

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
	
}
