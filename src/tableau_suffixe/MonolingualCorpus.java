package tableau_suffixe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class MonolingualCorpus implements Serializable{
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

	// Langue du corpus
	private String langue;
	private static int val_$$ = 0; // valeur numerique du caractere de fin
	private InputStream is;
	private TokenizerModel model;
	private Tokenizer tokenizer;
	
	private String fileName_structure;

	/**
	 * Constructeur
	 **/
	public MonolingualCorpus(String fileName, String langue) {
		fileName_structure = fileName + "_" + "structures_" + langue + ".txt";
		this.setLangue(langue);
		dictionnaire = new HashMap<String, Integer>();
		tab_token = new HashMap<Integer, ArrayList<Integer>>();
		corpus = new ArrayList<Integer>();
		try {
			is = new FileInputStream("fr-token.bin");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		model = null;
		try {
			model = new TokenizerModel(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tokenizer = new TokenizerME(model);
		// Cas ou il existe un fichier contenant les structures
		if(isWritenInFile(fileName_structure)){
			System.out.println("Ce fichier a deja ete serialize : LECTURE");
			// Lecture
			if(!readStructuresInFile(fileName_structure)){
				System.err.println("Erreur dans la lecture des structures dans "
						+ "le fichier -" + fileName_structure + "-");
				System.exit(0);
			}
		}
		
		// Cas ou il n'existe pas de fichier contenant les structures
		else{
			System.out.println("Ce fichier n'a pas deja ete serialize : ECRITURE");
			// Chargement du corpus
			if(!loadFromFile(fileName, langue)) {
				System.err.println("Erreur dans le chargement du fichier -"
						+ fileName + "-");
				System.exit(0);
			}
			// Ecriture des structures
			if(!writeStructuresInFile(fileName_structure)){
				System.err.println("Erreur dans l'ecriture des structures dans "
						+ "le fichier -" + fileName_structure + "-");
				System.exit(0);
			}
		}
		
		

	}

	public boolean loadFromFile(String fileName, String langue) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), "UTF8"));
			LineNumberReader lineNumberReader = new LineNumberReader(
					new FileReader(fileName));
			lineNumberReader.skip(Long.MAX_VALUE);
			int lines = lineNumberReader.getLineNumber();
			String ligne, token;
			String[] tab;
			int val = val_$$ + 1; // valeur numerique des autres caracteres
			int i, id_ligne, val_token;

			// Mise en place de la tokenisation
			

			// Ajout du caractere $$ dans le dictionnaire. On lui associe la
			// valeur 0
			dictionnaire.put("$$", val_$$);

			int parsedLines=0;
			// On parcours l'ensemble du corpus
			while ((ligne = br.readLine()) != null) {
				// On parse la ligne en enlevant les espaces
				tab = tokenize(ligne);
				// On prend que les donnees de la langue choisie
				parsedLines++;
				if (tab[1].equals(langue)) {
					id_ligne = Integer.parseInt(tab[0]);

					// On lit le tableau parse (on commence a i=2 car on prend
					// pas le int
					// et la langue
					for (i = 2; i < tab.length; i++) {
						// On transforme la chaine en minuscule
						token = tab[i].toLowerCase();

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
							// System.out.println("token = " + token +
							// " ligne = " + id_ligne);
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
					} else {
						tab_token.put(val_$$, new ArrayList<Integer>());
						tab_token.get(val_$$).add(id_ligne);
					}
				}
				//System.out.println(parsedLines+" / "+lines);
			}
			is.close();
			return true;
		} catch (Exception e) {
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
		if(corpus.get(position) == val_$$){
			return "";
		}
		
		int pos_debut_phrase = position;
		int pos_fin_phrase = getFinPhrase(position);
		String suffixe = "";
		for (int i = pos_debut_phrase; i < pos_fin_phrase-1; i++) {		
			suffixe = suffixe + getTokenAtPosition(i) + " ";			
		}
		return suffixe.substring(0, suffixe.length()-1);
	}
	
	public String getPhraseFromPosition(int position) {
		int pos_debut_phrase = getDebutPhrase(position);
		int pos_fin_phrase = getFinPhrase(position);
		String suffixe = "";
		for (int i = pos_debut_phrase; i < pos_fin_phrase-1; i++) {			
			suffixe = suffixe + getTokenAtPosition(i) + " ";			
		}
		return suffixe.substring(0, suffixe.length()-1);
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

	/**
	 * Methodes auxiliaires
	 */

	/**
	 * 
	 * @param chaine
	 * @return
	 */
	public String[] tokenize(String chaine) {
		return tokenizer.tokenize(chaine);
	}

	
	/**
	 * Recupere la position de la fin de la phrase
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
	 * @param position
	 * @return
	 */
	private int getDebutPhrase(int position) {
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
	 * @param fileName
	 * @return true si l'ecriture s'est bien passee, false sinon
	 */
	public boolean writeStructuresInFile(String fileName){
		try {
			// Ouverture flux
			File f = new File(fileName);
			f.createNewFile();
			ObjectOutputStream oos =  new ObjectOutputStream(new FileOutputStream(f)) ;
			
			// sérialization de l'objet
			oos.writeObject(corpus);
	    	oos.writeObject(dictionnaire);
	    	oos.writeObject(tab_token);
	    	oos.writeInt(val_$$);
	    	
	    	// Fermeture flux
	    	oos.close();
			return true;

		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		/*
		try{
			
			FileWriter fw = new FileWriter(adressedufichier, true);
			
			// le BufferedWriter output auquel on donne comme argument le FileWriter fw cree juste au dessus
			BufferedWriter output = new BufferedWriter(fw);
			
			//on marque dans le fichier ou plutot dans le BufferedWriter qui sert comme un tampon(stream)
			output.write(texte);
			//on peut utiliser plusieurs fois methode write
			
			output.flush();
			//ensuite flush envoie dans le fichier, ne pas oublier cette methode pour le BufferedWriter
			
			output.close();
			//et on le ferme
			System.out.println("fichier créé");
		}
		catch(IOException ioe){
			System.out.print("Erreur : ");
			ioe.printStackTrace();
			}

	}
	
	*/
		
		/*
		
		FileWriter writer = null;
		String texte = "texte à insérer à la fin du fichier";
		try{
		     writer = new FileWriter("fichier.txt", true);
		     writer.write(texte,0,texte.length());
		}catch(IOException ex){
		    ex.printStackTrace();
		}finally{
		  if(writer != null){
		     try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		}
		
		*/
		
		/*
		
		try {
			FileWriter f = new FileWriter("Files/structures.txt");
			PrintWriter pw = new PrintWriter(f);
			
			// Ecriture du corpus
			pw.println("corpus");
			for(Integer i : corpus){
				pw.println(i.toString());
			}
			
			
			// Ecriture du dictionnaire
			pw.println("dictionnaire");
			for (Entry<String, Integer> entry : dictionnaire.entrySet()) {
				pw.println(entry.getKey() + " " + entry.getValue().toString());
			}
			
			// Ecriture de tab_token
			pw.println("tab_token");
			String chaine = "";
			for (Entry<Integer, ArrayList<Integer>> entry2 : tab_token.entrySet()) {
				chaine = entry2.getKey().toString();
				for(Integer j : entry2.getValue()){
					chaine = chaine + " " + j.toString();
				}
				pw.println(chaine);
			}
			pw.close();
			return true;
		} catch (IOException e) {
			return false;
		}
		*/
	}
	
	/**
	 * Lis les structures dans un fichier et les remplit dans les attributs
	 * @param fileName
	 * @return true si la lecture s'est bien passee, false sinon
	 */
	public boolean readStructuresInFile(String fileName){
		try {
			// Ouverture du flux
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(fileName)));
			
			corpus = (ArrayList<Integer>) ois.readObject();
		    dictionnaire = (HashMap<String, Integer>) ois.readObject();
		    tab_token = (HashMap<Integer, ArrayList<Integer>>) ois.readObject();
		    val_$$ = ois.readInt();
		    
		    // Fermeture du flux
		    ois.close();
		    
		    return true;
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}	       

		/*
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), "UTF8"));
		
			String ligne;
			String[] tab;
			
			// Lecture de la structure corpus
			if((ligne = br.readLine()) != null && ligne.equals("corpus")){
				while ((ligne = br.readLine()) != null && !ligne.equals("dictionnaire")) {
					corpus.add(Integer.parseInt(ligne));
				}
			}
			else{
				System.err.println("Erreur dans la lecture du fichier -" + fileName + "- : "
						+ " la ligne corpus n'est pas presente");
				System.exit(0);
			}
			
			// Lecture de la structure dictionnaire
			if((ligne = br.readLine()) != null && ligne.equals("dictionnaire")){
				while ((ligne = br.readLine()) != null && !ligne.equals("tab_token")) {
					tab = ligne.split(" ");
					dictionnaire.put(tab[0], Integer.parseInt(tab[1]));
				}
			}
			else{
				System.err.println("Erreur dans la lecture du fichier -" + fileName + "- : "
						+ " la ligne dictionnaire n'est pas presente");
				System.exit(0);
			}
			
			if((ligne = br.readLine()) != null && ligne.equals("tab_token")){
				while ((ligne = br.readLine()) != null) {
					tab = ligne.split(" ");
					int l = tab.length;
					ArrayList<Integer> list_positions = new ArrayList<Integer>();
					for(int i=1; i<l; i++){
						list_positions.add(Integer.parseInt(tab[i]));
					}
					tab_token.put(Integer.parseInt(tab[0]), list_positions);
				}
			}
			else{
				System.err.println("Erreur dans la lecture du fichier -" + fileName + "- : "
						+ " la ligne tab_token n'est pas presente");
				System.exit(0);
			}
			
			
			return true;
		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}
		*/
	}
	
	/**
	 * permet de savoir si il y a le fichier qui contient les structures
	 * @param fileName
	 * @return true si un tel fichier existe, false sinon
	 */
	public boolean isWritenInFile(String fileName){
		File f = new File(fileName);
		return f.exists();
	}
	
	/**
	 * 
	 * @param phrase
	 * @return
	 */
	public ArrayList<Integer> getEncodedPhrase(String phrase) {
		String[] tokens= tokenize(phrase);
		ArrayList<Integer> resultat = new ArrayList<>();
		for (int i = 0; i < tokens.length; i++) {
			resultat.add(dictionnaire.get(tokens[i]));	
		}
		System.out.println("ici pas de soucis");
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
			while (i < size && ligne.charAt(i) == ' ') {
				i++;
			}
			// Arrive ici, c'est le debut de la "vraie" ligne
			return ligne.substring(i, size - 1);
		}
		// Si on arrive ici, c'est qu'il y a eu une erreur
		return null;
	}

	/**
	 * Permet d'enlever des caractï¿½res de ponctuation eventuellement present a
	 * la fin du token
	 * 
	 * @param token
	 * @return string (token) sans la ponctuation a la fin du token "initial"
	 */
	private String enlevePonctuationEnFinDeToken(String token) {
		char end = token.charAt(token.length() - 1);
		if (!((end >= 'a' && end <= 'z') || (end >= '0' && end <= '9'))) {
			return enlevePonctuationEnFinDeToken(token.substring(0,
					token.length() - 1));
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
	public static void main(String[] args) {
		String fileName = "Files/test_bug.csv"; // A CHANGER AVANT DE TESTER
		MonolingualCorpus test = new MonolingualCorpus(fileName, "fra");
		// Si on arrive jusqu'ici, c'est que le load n'a pas gï¿½nï¿½rer d'erreur

		/**
		 * Test de getTokenAtPosition
		 */
		/*int position = 0; // A CHANGER EN FONCTION DU FICHIER
		System.out.println("Test de getTokenAtPosition a la position "
				+ position);
		System.out.println("---> " + test.getTokenAtPosition(position));
		System.out.println("Test de getSuffixFromPosition a la position "
				+ position);
		System.out.println("---> " + test.getSuffixFromPosition(position));

		/**
		 * Test de getSuffixFromPosition
		 * 
		 */
		/*position = 3; // A CHANGER EN FONCTION DU FICHIER
		System.out.println("Test de getTokenAtPosition a la position "
				+ position);
		System.out.println("---> " + test.getTokenAtPosition(position));
		
		System.out.println("Test de getSuffixFromPosition a la position "
				+ position);
		System.out.println("---> " + test.getSuffixFromPosition(position));
		

		/**
		 * Test de getPhraseFromPosition
		 */
		/*position = 3; // A CHANGER EN FONCTION DU FICHIER
		System.out.println("Test de getPhraseFromPosition a la position "
				+ position);
		System.out.println("---> " + test.getPhraseFromPosition(position));
		*/
		
		System.out.println("---------Dictionnaire------------");
		for (Entry<String, Integer> entry : test.getDictionnaire().entrySet()) {
			System.out.println("(" + entry.getKey() + ", " + entry.getValue()
					+ ")");
		}
		System.out.println("---------------------------------");

		SuffixArray test1 = new SuffixArray(test);

		/**
		 * Test de compareSuffixes
		 */

		/**
		 * Affichage du dictionnaire
		 
		 
		System.out.println("---------Dictionnaire------------");
		for (Entry<String, Integer> entry : test.getDictionnaire().entrySet()) {
			System.out.println("(" + entry.getKey() + ", " + entry.getValue()
					+ ")");
		}
		System.out.println("---------------------------------");
		*/

		/**
		 * Affichage de tab_token
		 * 
		 
		 System.out.println("-----------Tab_token------------");
		 for(Entry<Integer, ArrayList<Integer>> entry2 : test.getTab_token().entrySet()){
			 System.out.println("(" + entry2.getKey() + ", " + entry2.getValue().toString()
					 + ")"); 
		 } 
		 System.out.println("--------------------------------");
		 */
		
		 /*
		System.out.println(test.getCorpus());
		SuffixArray suffixArray = new SuffixArray(test);
		*/
	}

	public ArrayList<Integer> getCorpus() {
		return corpus;
	}

	public void setCorpus(ArrayList<Integer> corpus) {
		this.corpus = corpus;
	}
	
}
