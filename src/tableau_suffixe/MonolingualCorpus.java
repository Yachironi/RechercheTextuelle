package tableau_suffixe;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class MonolingualCorpus {
	/**
	 * Attributs (à compléter)
	 */
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
						/** A MODIFIER **/
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
			return true;
		}
		catch(Exception e){
			System.out.println(e.toString());
			return false;
		}
	}
	
	public String getTokenAtPosition(int position){
		return null;
	}
	
	public String getSuffixFromPosition(int position){
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
	
}
