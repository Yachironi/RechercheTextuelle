package tableau_suffixe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import traducteur.CoupleInt;
import traducteur.ListCoupleInt;

public class MonolingualCorpus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5891220684242552315L;

	/**
	 * Attributs
	 */
	// contient tout le corpus (represente par leur valeur). L'indice = position
	// du mot
	private ArrayList<Integer> corpus;

	// string = token, integer = valeur du token
	private HashMap<String, Integer> dictionnaire;

	// int = valeur, liste de int = identifiant de la phrase ou est present le
	// token
	private HashMap<Integer, ArrayList<Integer>> tab_token;

	private ListCoupleInt tab_line;
	
	private HashMap<Integer, String> listPhrasesReelles;	// va nous permettre d'afficher les vraies phrases
	
	// Langue du corpus
	private String langue;
	private static int val_$$ = 0; // valeur numerique du caractere de fin

	private String fileName_structure;

	/**
	 * Constructeur
	 **/
	public MonolingualCorpus(String fileName, String langue) {
		fileName_structure = fileName + "_" + "structures_" + langue;
		this.setLangue(langue);
		dictionnaire = new HashMap<String, Integer>();
		tab_token = new HashMap<Integer, ArrayList<Integer>>();
		corpus = new ArrayList<Integer>();
		tab_line = new ListCoupleInt();
		listPhrasesReelles = new HashMap<Integer, String>();
		
		// Cas ou il existe un fichier contenant les structures
		if (isWritenInFile(fileName_structure)) {
			System.out.println("Ce fichier a deja ete serialize : LECTURE");
			System.out.println("debut read");
			// Lecture
			if (!readStructuresInFile(fileName_structure)) {
				System.err
						.println("Erreur dans la lecture des structures dans "
								+ "le fichier -" + fileName_structure + "-");
				System.exit(0);
			}
			System.out.println("fin read");
		}

		// Cas ou il n'existe pas de fichier contenant les structures
		else {
			System.out.println("Ce fichier n'a pas deja ete serialize : ECRITURE");
			System.out.println("Debut load");
			// Chargement du corpus
			if (!loadFromFile(fileName, langue)) {
				System.err.println("Erreur dans le chargement du fichier -"
						+ fileName + "-");
				System.exit(0);
			}
			System.out.println("Fin load\nDebut write");
			// Ecriture des structures
			if (!writeStructuresInFile(fileName_structure)) {
				System.err
						.println("Erreur dans l'ecriture des structures dans "
								+ "le fichier -" + fileName_structure + "-");
				System.exit(0);
			}
			System.out.println("Fin write");
		}

	}


	public boolean loadFromFile(String fileName, String langue) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
			String ligne, token;
			String[] tab;
			int val = val_$$ + 1; // valeur numerique des autres caracteres
			int i, id_ligne, val_token;

			// Ajout du caractere de fin de paragraphe dans le dico
			dictionnaire.put("$$", val_$$);

			while ((ligne = br.readLine()) != null) {
				
				tab = tokenize(ligne);
				//id_ligne = Integer.parseInt(tab[0]);
				
				// On prend que les donnees de la langue choisie
				if (tab.length>1 && tab[1].equals(langue)) {
					id_ligne = Integer.parseInt(tab[0]);
					// on ajoute la phrase reelle
					//listPhrasesReelles.put(id_ligne, reelleLigne(ligne, tab));
					listPhrasesReelles.put(id_ligne, reelleLigne(ligne, tab));

					// On lit le tableau parse (on commence a i=2 car on prend
					// pas le int et la langue
					for (i = 2; i < tab.length; i++) {
						// On transforme la chaine en minuscule
						token = tab[i].toLowerCase();
						
						// ajout du couple (position du 1er mot de la ligne, 
						//	numero de la ligne)
						if(i==2){
							tab_line.add(new CoupleInt(corpus.size(), id_ligne));

						}
						
						// On ajoute le token au dictionnaire s'il n'y ait pas
						if (!dictionnaire.containsKey(token)) {
							dictionnaire.put(token, val);
							val++;
						}

						// On ajoute l'identifiant de la ligne
						// Cas ou tab_token contient la valeur du token
						val_token = dictionnaire.get(token);
						if (tab_token.containsKey(val_token)) {
							tab_token.get(val_token).add(id_ligne);
						}
						// Cas ou tab_token ne contient pas la valeur du token
						else {
							tab_token.put(val_token, new ArrayList<Integer>());
							tab_token.get(val_token).add(id_ligne);
						}

						// Ajout du token (via sa valeur) dans le corpus
						corpus.add(val_token);
					}

					// On ajoute le caractere de fin de ligne $$
					corpus.add(val_$$);
					if (tab_token.containsKey(val_$$)) {
						tab_token.get(val_$$).add(id_ligne);
					} 
					else {
						tab_token.put(val_$$, new ArrayList<Integer>());
						tab_token.get(val_$$).add(id_ligne);
					}
				}
			}
			return true;
		} catch (Exception e) {
			
			System.out.println(getPhraseFromPosition(corpus.size()-2));
			
			System.out.println(e.toString());
			return false;
		}
	}

	/**
	 * 
	 * @param position
	 * @return le token present dans le corpus a la position passee en parametre
	 */
	public String getTokenAtPosition(int position) {
		// Message d'erreur si position est incorrecte
		if (position >= corpus.size()) {
			System.err.println("La position n'est pas dans le corpus");
			System.exit(0);
		}

		// On recupere la valeur du token
		int val_token = corpus.get(position);
		// On cherche le token dans le dictionnaire
		for (Entry<String, Integer> entry : dictionnaire.entrySet()) {
			if (entry.getValue() == val_token) {
				return entry.getKey();
			}
		}

		// Si on arrive ici : probleme
		System.err.println("La valeur du token -" + val_token
				+ "- a la position -" + position
				+ "- n'est pas dans le dictionnaire");
		return null;
	}

	/**
	 * Un suffixe est le token present a la position passee en parametre, et se
	 * termine jusqu'a la fin du paragraphe (visible grace au caractere $$)
	 * 
	 * @param position
	 * @return le suffixe present a la position passee en parametre dans le
	 *         corpus
	 */
	public String getSuffixFromPosition(int position) {
		// Message d'erreur si position est incorrecte
		if (position >= corpus.size()) {
			System.err.println("La position n'est pas dans le corpus");
			System.exit(0);
		}
		if (corpus.get(position) == val_$$) {
			return "";
		}

		int pos_debut_phrase = position;
		int pos_fin_phrase = getFinPhrase(position);
		String suffixe = "";
		for (int i = pos_debut_phrase; i < pos_fin_phrase - 1; i++) {
			suffixe = suffixe + getTokenAtPosition(i) + " ";
		}
		return suffixe.substring(0, suffixe.length() - 1);
	}

	/**
	 * Renvoie la phrase entiere auquel appartient le token present a la position
	 * passee en parametre
	 * @param position
	 * @return
	 */
	public String getPhraseFromPosition(int position) {
		int pos_debut_phrase = getDebutPhrase(position);
		int pos_fin_phrase = getFinPhrase(position);
		String suffixe = "";
		for (int i = pos_debut_phrase; i < pos_fin_phrase - 1; i++) {
			suffixe = suffixe + getTokenAtPosition(i) + " ";
		}
		return suffixe.substring(0, suffixe.length() - 1);
	}
	
	/**
	 * Permet de recuperer la phrase present a la ligne passee en parametre
	 * @param ligne
	 * @return
	 */
	public String getPhraseFromLine(int ligne){
		int position = tab_line.get_i1(ligne);
		if(position != -1){
			return getPhraseFromPosition(position);
		}
		else{
			return null;
		}
	}

	/**
	 * 
	 * @param position1
	 * @param position2
	 * @return
	 */
	public int compareSuffixes(int position1, int position2) {
		String chaine1 = getSuffixFromPosition(position1);
		String chaine2 = getSuffixFromPosition(position2);
		if (chaine1.equals(chaine2) || position1 == position2)
			return 0;
		int min = Math.min(chaine1.length(), chaine2.length());
		chaine1.toLowerCase();
		chaine2.toLowerCase();
		for (int i = 0; i < min; i++) {
			if (chaine1.charAt(i) > chaine2.charAt(i))
				return 1;
			else if (chaine1.charAt(i) < chaine2.charAt(i))
				return -1;
		}
		if (chaine1.length() > chaine2.length())
			return 1;
		else
			return -1;
	}
	
	/*
	 * On ne trie non plus suivant l'ordre lexicographique, mais suivant la valeur 
	 */
	
	public int compareSuffixesInt(int position1, int position2){

		try {
			String chaine1 = getSuffixFromPosition(position1);
			String chaine2 = getSuffixFromPosition(position2);
			if(position1==position2 || chaine1.equals(chaine2)) return 0;
			ArrayList<Integer> encodedPosition1 = getEncodedPhrase(chaine1);
			ArrayList<Integer> encodedPosition2 = getEncodedPhrase(chaine2);
			int min = Math.min(encodedPosition1.size(), encodedPosition2.size());
			for(int i=0;i<min;i++){
				if(encodedPosition1.get(i) > encodedPosition2.get(i)) return 1;
				else if (encodedPosition1.get(i) < encodedPosition2.get(i)) return -1;
			}
			
			
			if (encodedPosition1.size() > encodedPosition2.size())
				return 1;
			else
				return -1;
		
		
		} catch (TokenNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
		
	}

	/**
	 * Methodes auxiliaires
	 */

	/**
	 * 
	 * @param chaine
	 * @return
	 */
	public String[] tokenize(String chaine) {
		return chaine.split("\\W+");
	}

	/**
	 * Recupere la position de la fin de la phrase
	 * 
	 * @param position
	 * @return
	 */
	public int getFinPhrase(int position) {
		// Message d'erreur si position est incorrecte
		if (position >= corpus.size()) {
			System.err.println("La position n'est pas dans le corpus");
			return -1;
		}

		int i = position;
		int size = corpus.size();
		while (i < size && corpus.get(i) != val_$$) {
			i++;
		}
		if (i == size) {
			return i;
		}
		return i + 1;
	}

	/**
	 * Recupere la position du debut de la phrase
	 * 
	 * @param position
	 * @return
	 */
	public int getDebutPhrase(int position) {
		// Message d'erreur si position est incorrecte
		if (position >= corpus.size()) {
			System.err.println("La position n'est pas dans le corpus");
			return -1;
		}

		int i = position;
		while (i > 0 && corpus.get(i) != val_$$) {
			i--;
		}
		if (i == 0) {
			return i;
		}
		return i + 1;
	}

	/**
	 * Ecrit les structures dans un fichier
	 * 
	 * @param fileName
	 * @return true si l'ecriture s'est bien passee, false sinon
	 */
	public boolean writeStructuresInFile(String fileName) {
		try {
			// Ouverture flux
			File f = new File(fileName);
			f.createNewFile();
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(f));

			// sï¿½rialization de l'objet
			oos.writeObject(corpus);
			oos.writeObject(dictionnaire);
			oos.writeObject(tab_token);
			oos.writeObject(tab_line);
			oos.writeInt(val_$$);
			oos.writeObject(listPhrasesReelles);
			
			// Fermeture flux
			oos.close();
			return true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Lis les structures dans un fichier et les remplit dans les attributs
	 * 
	 * @param fileName
	 * @return true si la lecture s'est bien passee, false sinon
	 */
	public boolean readStructuresInFile(String fileName) {
		try {
			// Ouverture du flux
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					new File(fileName)));

			corpus = (ArrayList<Integer>) ois.readObject();
			dictionnaire = (HashMap<String, Integer>) ois.readObject();
			tab_token = (HashMap<Integer, ArrayList<Integer>>) ois.readObject();
			tab_line = (ListCoupleInt) ois.readObject();
			val_$$ = ois.readInt();
			listPhrasesReelles= (HashMap<Integer, String>) ois.readObject();

			// Fermeture du flux
			ois.close();

			return true;
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * permet de savoir si il y a le fichier qui contient les structures
	 * 
	 * @param fileName
	 * @return true si un tel fichier existe, false sinon
	 */
	public boolean isWritenInFile(String fileName) {
		File f = new File(fileName);
		return f.exists();
	}

	/**
	 * 
	 * @param phrase
	 * @return
	 * @throws TokenNotFoundException 
	 */
	public ArrayList<Integer> getEncodedPhrase(String phrase) throws TokenNotFoundException {
		String[] tokens = tokenize(phrase);
		ArrayList<Integer> resultat = new ArrayList<>();
		for (int i = 0; i < tokens.length; i++) {
			if (dictionnaire.get(tokens[i]) != null) {
				resultat.add(dictionnaire.get(tokens[i].toLowerCase()));
			} 
			else {
				throw new TokenNotFoundException(tokens[i]);
			}
		}
		
		return resultat;
	}

	/**
	 * 
	 * @param ligne
	 * @param tab
	 *            Permet d'enlever des caractï¿½res de ponctuation eventuellement
	 *            present
	 * @return un string, la "vraie" ligne, sans l'integer et la langue au dï¿½but
	 *         de la ligne
	 */
	private String reelleLigne(String ligne, String[] tab) {
		int i = 0;
		String lang = tab[1];
		int size = ligne.length();

		// On avance jusqu'a trouver la premiere lettre de la langue
		while (i < size && ligne.charAt(i) != lang.charAt(0)) {
			i++;
		}

		// Ici, i est a la premiere lettre de la langue normalement
		int size_lang = lang.length();
		int j = 0;

		// On verifie bien qu'on est bien au mot de la langue
		while (i < size && j < size_lang && ligne.charAt(i) == lang.charAt(j)) {
			i++;
			j++;
		}
		// Si c'est vrai, alors c'ï¿½tait bien la langue
		if (j == size_lang) {
			// On enleve les espaces
			while (i < size && !isValidChar(ligne.charAt(i))) {
				i++;
			}
			// Arrive ici, c'est le debut de la "vraie" ligne
			return ligne.substring(i, size - 1);
		}
		// Si on arrive ici, c'est qu'il y a eu une erreur
		System.err.println("Erreur dans reelleLigne");
		return null;
	}
	
	public boolean isValidChar(char c){
		return (c>='a'&& c<='z') || (c>='A' && c<='Z') || (c>='0' && c<='9');
	}
	
	public String getReellePhrase(int ligne){
		return listPhrasesReelles.get(ligne);
	}

	/**
	 * Permet d'enlever des caractï¿½res de ponctuation eventuellement present a
	 * la fin du token
	 * 
	 * @param token
	 * @return string (token) sans la ponctuation a la fin du token "initial"
	 
	private String enlevePonctuationEnFinDeToken(String token) {
		char end = token.charAt(token.length() - 1);
		if (!((end >= 'a' && end <= 'z') || (end >= '0' && end <= '9'))) {
			return enlevePonctuationEnFinDeToken(token.substring(0,
					token.length() - 1));
		}
		return token;
	}
	*/

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


	public ListCoupleInt getTab_line() {
		return tab_line;
	}

	public void setTab_line(ListCoupleInt tab_line) {
		this.tab_line = tab_line;
	}

	public ArrayList<Integer> getCorpus() {
		return corpus;
	}

	public void setCorpus(ArrayList<Integer> corpus) {
		this.corpus = corpus;
	}
	
	public int getValFinParagraphe(){
		return val_$$;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public HashMap<Integer, String> getListPhrasesReelles() {
		return listPhrasesReelles;
	}


	public void setListPhrasesReelles(HashMap<Integer, String> listPhrasesReelles) {
		this.listPhrasesReelles = listPhrasesReelles;
	}


	/**
	 * Main pour tester cette classe AVEC LES DONNEES DE TATOEBA UNIQUEMENT
	 */
	public static void main(String[] args) {
		String fileName = "Files/sentences.csv"; // A CHANGER AVANT DE TESTER
		
		System.out.println("Lancement de MonolingualCorpus");
		MonolingualCorpus mc = new MonolingualCorpus(fileName, "fra");
		
	}
	
	
}
