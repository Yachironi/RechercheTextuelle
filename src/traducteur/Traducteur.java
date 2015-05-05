package traducteur;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import tableau_suffixe.MonolingualCorpus;
import tableau_suffixe.SuffixArray;
import traducteur.CoupleInt;
import traducteur.ListCoupleInt;

public class Traducteur {
	private SuffixArray suffixArray_lang1;	// fr
	private SuffixArray suffixArray_lang2;	// eng
	private ListCoupleInt link;
	// Correspondances de la langue 1 vers la langue 2
	private ArrayList<ListCoupleInt> listCorrespondances_lang12;
	
	// Correspondances de la langue 2 vers la langue 1
	private ArrayList<ListCoupleInt> listCorrespondances_lang21;
	
	public Traducteur(String lang1, String lang2, String corpus, String link){
		suffixArray_lang1 = new SuffixArray(corpus, lang1);		
		suffixArray_lang2 = new SuffixArray(corpus, lang2);
		 
		String fileWriteStructurelink = "Files/structure_link";
		initLink(fileWriteStructurelink, link);
		//System.out.println(this.link);
		String fileName_correspondance_12 = "Files/correspondances_" + lang1 + "_" + lang2;
		String fileName_correspondance_21 = "Files/correspondances_" + lang2 + "_" + lang1;
		
		//listCorrespondances_lang12 = loadCorrespondances(fileName_correspondance_12);
		//listCorrespondances_lang21 = loadCorrespondances(fileName_correspondance_21);
	}

	/**
	 * Initialise la hashmap qui contient les liens entre les phrases des 2 corpus
	 * @param read_write_link
	 * @param load_link
	 */
	private void initLink(String read_write_link, String load_link) {
		//link = new HashMap<Integer, Integer>();
		link = new ListCoupleInt();
		// Lecture
		if(isWritenInFile(read_write_link)){
			if(!readLink(read_write_link)){
				System.err.println("Erreur dans la lecture des liens dans "
						+ "le fichier -" + read_write_link + "-");
				System.exit(0);
			}
		}
		// Ecriture
		else{
			if(!loadLink(load_link)){
				System.err.println("Erreur dans le chargement du fichier -" + load_link + "-");
				System.exit(0);
			}
			if(!writeLink(read_write_link)){
				System.err.println("Erreur dans l'ecriture des liens dans "
						+ "le fichier -" + read_write_link + "-");
				System.exit(0);
			}
		}
	}
	
	/**
	 * Ecrit dans un fichier les phrases des 2 corpus en parallele
	 * @param fileLang1
	 * @param fileLang2
	 * @return
	 */
	/**
	 * TODO : verifier s'il faut faire newLine + flush
	 */
	public boolean writePhrasesInParallel(String fileLang1, String fileLang2){
		try {
			BufferedWriter bw_lang1 = new BufferedWriter(new FileWriter(fileLang1));
			BufferedWriter bw_lang2 = new BufferedWriter(new FileWriter(fileLang2));
			
			int id_ligne;
			String phrase;
			
			boolean res = true;
			for(CoupleInt c : link){
				// On s'occupe de la phrase du 1er int
				id_ligne = c.getI1();
				phrase = suffixArray_lang1.getCorpus().getPhraseFromLine(id_ligne);
				// cas ou la ligne est presente dans le corpus lang1
				if(phrase != null){
					bw_lang1.write(phrase);
					bw_lang1.newLine();
					bw_lang1.flush();
					
					// on s'occupe de la phrase du 2eme int : elle devrait
					// appartenir au corpus lang2
					id_ligne = c.getI2();
					phrase = suffixArray_lang2.getCorpus().getPhraseFromLine(id_ligne);
					if(phrase != null){
						bw_lang2.write(phrase);
						bw_lang2.newLine();
						bw_lang2.flush();
					}
					else{
						System.err.println("Erreur au niveau du CoupleInt " + c + " car la phrase "
								+ c.getI2() + " n'appartient pas au corpus "
										+ suffixArray_lang2.getCorpus().getLangue());
						res = false;
					}
				}
				else{
					phrase = suffixArray_lang2.getCorpus().getPhraseFromLine(id_ligne);
					// cas ou la ligne est presente dans le corpus lang2
					if(phrase != null){
						bw_lang2.write(phrase);
						bw_lang2.newLine();
						bw_lang2.flush();
						
						// on s'occupe de la phrase du 2eme int : elle devrait
						// appartenir au corpus lang1
						id_ligne = c.getI2();
						phrase = suffixArray_lang1.getCorpus().getPhraseFromLine(id_ligne);
						if(phrase != null){
							bw_lang1.write(phrase);
							bw_lang1.newLine();
							bw_lang1.flush();
						}
						else{
							System.err.println("Erreur au niveau du CoupleInt " + c + " car la phrase "
									+ c.getI2() + " n'appartient pas au corpus "
											+ suffixArray_lang1.getCorpus().getLangue());
							res = false;
						}
					}
					// Erreur : la ligne n'appartient a aucun des corpus
					else{
						System.err.println("Erreur au niveau du CoupleInt " + c + " car la phrase "
								+ c.getI1() + " n'appartient a aucun corpus");
						res = false;
					}
				}
				
			}
		  
		 	bw_lang1.close();
		 	bw_lang2.close();
		 	return res;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
		
	/**
	 * Ecrit la hashmap dans un fichier
	 * @param fileName
	 * @return
	 */
	private boolean writeLink(String fileName) {
		try {
			// Ouverture flux
			File f = new File(fileName);
			f.createNewFile();
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));

			// serialization de l'objet
			oos.writeObject(link);

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
	 * Chargement des liens dans le fichier fourni par tatoeba
	 * @param fileName
	 * @return
	 */
	private boolean loadLink(String fileName) {
		try {
			System.out.println("on load");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), "UTF8"));
			String ligne;
			String[] tab;
			
			// On parcours l'ensemble du fichier link
			int i=0;
			while ((ligne = br.readLine()) != null) {
				 
				// On parse la ligne en enlevant les espaces
				StringTokenizer tokenize = new StringTokenizer(ligne);
				int line1 = Integer.parseInt(tokenize.nextToken());
				int line2 = Integer.parseInt(tokenize.nextToken());

				// Condition pour ajouter le couple d'entier : il faut que les phrases liees
				// appartiennent aux 2 corpus
				if(!link.contains_couple(new CoupleInt(line1, line2)) && !link.contains_couple(new CoupleInt(line2, line1)))
					
					if((suffixArray_lang1.getCorpus().getTab_line().contains_i2(line1)
							&& suffixArray_lang2.getCorpus().getTab_line().contains_i2(line2)
							||
					   (suffixArray_lang2.getCorpus().getTab_line().contains(line1)
							&& suffixArray_lang1.getCorpus().getTab_line().contains(line2)))){	 
						link.add(new CoupleInt(line1, line2));	
					}	
				}
			
			return true;
		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}
		
	}

	/**
	 * Charge la hashmap ecrite dans un fichier
	 * @param fileName
	 * @return
	 */
	private boolean readLink(String fileName) {
		try {
			// Ouverture du flux
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(fileName)));
			link = (ListCoupleInt) ois.readObject();
			// Fermeture du flux
			ois.close();

			return true;
		} catch (IOException | ClassNotFoundException e) {
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
	
	/** Traduit tokens de la lang1 vers la lang2
	 * Phrase peut etre qu'un seul mot ou un groupe de mot
	 * @param phrase
	 * @return
	 */
	//public ArrayList<String> traduct(String phrase, String lang1, String lang2){
	public TreeMap<String, ArrayList<String>> traduct(String phrase, String lang1, String lang2){
		ArrayList<String> resultat_traduction = new ArrayList<String>();
		
		// Contient les id des phrases de la recherche dans la langue initiale
		ArrayList<Integer> list_IdPhrases;
		
		// Contient les id des phrases de la recherche dans la langue traduite
		ArrayList<Integer> list_IdPhrases_traduit = new ArrayList<Integer>();
		
		ArrayList<ListCoupleInt> correspondances = new ArrayList<ListCoupleInt>();
		
		CoupleInt int_and_pos;
		
		ArrayList<String> phrasesReellesLangTrad1 = new ArrayList<String>();
		ArrayList<String> phrasesReellesLangTrad2 = new ArrayList<String>();
		
		HashMap<String, ArrayList<String>> listResultats = new HashMap<String, ArrayList<String>>();
		
		int nbResultats = 0;
		
		// Cas ou on veut traduire de suffixArray_lang1 -> suffixArray_lang2
		if(suffixArray_lang1.getCorpus().getLangue().equals(lang1) && 
				suffixArray_lang2.getCorpus().getLangue().equals(lang2)){
			list_IdPhrases = suffixArray_lang1.getAllPositionsOfPhrase(phrase);
			System.out.println(list_IdPhrases);
			// On effectue la traduction
			for(Integer pos : list_IdPhrases){
				nbResultats++;
				phrasesReellesLangTrad1.add(suffixArray_lang1.getCorpus().getReellePhrase(pos));
				System.out.println(phrasesReellesLangTrad1);
				int_and_pos = link.getOtherIntAndPosition(pos);
				System.out.println(int_and_pos);
				if(int_and_pos != null){
					phrasesReellesLangTrad2.add(suffixArray_lang2.getCorpus().getReellePhrase(int_and_pos.getI1()));
					//list_IdPhrases_traduit.add(int_and_pos.getI1());
					//correspondances.add(listCorrespondances_lang12.get(int_and_pos.getI2()));
				}
			}
					
			/**
			 * Remarque : pas besoin de �a grace a phrasesReellesLangTrad2 qui est la traduct?
			 
			int position_corpus;
			String phraseAtPosi;
			for(Integer pos : list_IdPhrases_traduit)
			{
				 System.out.println(pos);
				 position_corpus = suffixArray_lang2.getCorpus().getTab_line().get_i1(pos);
				 System.out.println(position_corpus);
				 phraseAtPosi = suffixArray_lang2.getCorpus().getSuffixFromPosition(position_corpus);
				 resultat_traduction.add(phraseAtPosi);
			}
			*/
			// On trie de la plus pertinente a la moins pertinente
			//Collections.sort(resultat_traduction, new ComparatorResultTraduction());
			
			// Recherche des mots a surlignes
			if(nbResultats!= phrasesReellesLangTrad1.size() || nbResultats!=phrasesReellesLangTrad2.size() || nbResultats!=correspondances.size()){
				System.err.println("Erreur dans le nb de resultats de la traduction");
			}
			ArrayList<ArrayList<String>> listMotsASurligner = new ArrayList<ArrayList<String>>();
			for(int i=0; i<nbResultats; i++){
				listMotsASurligner.add(getMotsASurligner(phrase, phrasesReellesLangTrad1.get(i), phrasesReellesLangTrad2.get(i), correspondances.get(i)));
			}
			
			/**
			 * Ici, phrasesReellesLangTrad2 = resultat de la traduction
			 * listMotsASurligner = la liste de la liste des mots a surligner
			 */
			/*
			 * Il gaut trier phrasesRellesLangTrad2 de la + a la - pertinente
			 *  PB : il faut aussi trier listMotsASurligner dans le meme ordre..
			 */
			for(int i=0; i<nbResultats; i++){
				listResultats.put(phrasesReellesLangTrad2.get(i), listMotsASurligner.get(i));
			}
			// On trie la hashmap
			return doSort(listResultats);
			
			//return resultat_traduction;
			//return phrasesReellesLangTrad2;
		}
		
		// Cas ou on veut traduire de suffixArray_lang2 -> suffixArray_lang1
		else if(suffixArray_lang2.getCorpus().getLangue().equals(lang1) && 
				suffixArray_lang1.getCorpus().getLangue().equals(lang2)){
			list_IdPhrases = suffixArray_lang2.getAllPositionsOfPhrase(phrase);
			
			// On effectue la traduction
			for(Integer pos : list_IdPhrases){
				phrasesReellesLangTrad1.add(suffixArray_lang2.getCorpus().getReellePhrase(pos));
				int_and_pos = link.getOtherIntAndPosition(pos);
				if(int_and_pos != null){
					phrasesReellesLangTrad2.add(suffixArray_lang1.getCorpus().getReellePhrase(int_and_pos.getI1()));
					list_IdPhrases_traduit.add(int_and_pos.getI1());
					correspondances.add(listCorrespondances_lang21.get(int_and_pos.getI2()));
				}
			}
			
			/**
			 * Remarque : pas besoin de �a grace a phrasesReellesLangTrad2 qui est la traduct?
			 
			int position_corpus;
			String phraseAtPosi;
			for(Integer pos : list_IdPhrases_traduit)
			{
				 position_corpus = suffixArray_lang1.getCorpus().getTab_line().get_i1(pos);
				 phraseAtPosi = suffixArray_lang1.getCorpus().getSuffixFromPosition(position_corpus);
				 resultat_traduction.add(phraseAtPosi);
			}	
			*/
			// On trie de la plus pertinente a la moins pertinente
			//Collections.sort(resultat_traduction, new ComparatorResultTraduction());

			// Recherche des mots a surlignes
			if(nbResultats!= phrasesReellesLangTrad1.size() || nbResultats!=phrasesReellesLangTrad2.size() || nbResultats!=correspondances.size()){
				System.err.println("Erreur dans le nb de resultats de la traduction");
			}
			ArrayList<ArrayList<String>> listMotsASurligner = new ArrayList<ArrayList<String>>();
			for(int i=0; i<nbResultats; i++){
				listMotsASurligner.add(getMotsASurligner(phrase, phrasesReellesLangTrad1.get(i), phrasesReellesLangTrad2.get(i), correspondances.get(i)));
			}
			
			/**
			 * Ici, phrasesReellesLangTrad2 = resultat de la traduction
			 * listMotsASurligner = la liste de la liste des mots a surligner
			 */
			/*
			 * Il gaut trier phrasesRellesLangTrad2 de la + a la - pertinente
			 *  PB : il faut aussi trier listMotsASurligner dans le meme ordre..
			 */
			for(int i=0; i<nbResultats; i++){
				listResultats.put(phrasesReellesLangTrad2.get(i), listMotsASurligner.get(i));
			}
			// On trie la hashmap
			return doSort(listResultats);
			
			
			//return resultat_traduction;
			//return phrasesReellesLangTrad2;
			
		}
		// Probleme
		else{
			System.err.println("Erreur : les langues sont differentes des langues des corpus");
		}
		return null;
	}
	
	/**
	 * Trie le resultat  de la traduction de la + pertinente a la - pertinente
	 * @param map
	 * @return
	 */
	public TreeMap<String, ArrayList<String>> doSort(HashMap<String, ArrayList<String>> map) {
		 TreeMap<String, ArrayList<String>> sortedMap = new TreeMap<String, ArrayList<String>>(new Comparator<String>(){
             @Override
             public int compare(String o1, String o2) {
            	if(o1.length() < o2.length()){
            		return -1;
         		}
         		else if(o1.length() > o2.length()){
         			return 1;
         		}
         		else{
         			return o1.compareTo(o2);
         		}
             }
         });
		 sortedMap.putAll(map);
		 return sortedMap;
	}
	
	
	public ArrayList<ListCoupleInt> loadCorrespondances(String fileName){
		ArrayList<ListCoupleInt> list = new ArrayList<ListCoupleInt>();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
			String ligne;
			String[] tab;
			ListCoupleInt list_ligne = new ListCoupleInt();

			while ((ligne = br.readLine()) != null) {
				list_ligne = new ListCoupleInt();
				tab = ligne.split("[ -]");
				int i;
				int size = tab.length;
				for(i=0; i<size; i+=2){
					list_ligne.add(new CoupleInt(Integer.parseInt(tab[i]), Integer.parseInt(tab[i+1])));
				}
				list.add(list_ligne);
			}
		} 
		catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
		
	
	public ArrayList<String> getMotsASurligner(String recherche, String phraseLang1, String phraseLang2, ListCoupleInt correspondance){
		ArrayList<String> res = new ArrayList<String>();
		
		String[] recherche_tokenize = MonolingualCorpus.tokenize(recherche);
		String[] phraseLang1_tokenize = MonolingualCorpus.tokenize(phraseLang1);
		String[] phraseLang2_tokenize = MonolingualCorpus.tokenize(phraseLang2);

		int pos, pos_correspondance;
		for(String token : recherche_tokenize){
			// on obtient la position dans la correspondance (partie gauche)
			pos = searchStringInTab(token, phraseLang1_tokenize);
			if(pos != -1){
				// on obtient la position dans la correspondance (partie droite)
				pos_correspondance = searchPositionCorrespondance(pos, correspondance);
				/**
				 * TODO A VERIFIER : ici on prend tout, meme si il y a 0-0 et 0-1
				 * --> Si il faut prendre uniquement 0-1, il faut regarder dans 
				 * searchPositionCorrespondance si le prochain c.getI1() n'a pas la meme valeur
				 * que i1 (passe en parametre)
				 * ------> peut-etre que ce n'est pas une bonne idee de choisir le dernier
				 * (0-1) car en francais un mot peut etre egal a 2 mots en anglais..
				 */
				res.add(phraseLang2_tokenize[pos_correspondance]);
			}
		}

		return res;
	}
	
	public int searchStringInTab(String string, String[] tab){
		int pos=0;
		for(String s : tab){
			if(s.equals(string)){
				return pos;
			}
			pos++;
		}
		return -1;
	}
	
	public int searchPositionCorrespondance(int i1, ListCoupleInt list){
		for(CoupleInt c : list){
			if(c.getI1() == i1){
				return c.getI2();
			}
		}
		return -1;
	}
	

	
	/**
	 * Getter & Setter
	 */
	

	public SuffixArray getSuffixArray_lang1() {
		return suffixArray_lang1;
	}

	public void setSuffixArray_lang1(SuffixArray suffixArray_lang1) {
		this.suffixArray_lang1 = suffixArray_lang1;
	}

	public SuffixArray getSuffixArray_lang2() {
		return suffixArray_lang2;
	}

	public void setSuffixArray_lang2(SuffixArray suffixArray_lang2) {
		this.suffixArray_lang2 = suffixArray_lang2;
	}

	public ListCoupleInt getLink() {
		return link;
	}

	public void setLink(ListCoupleInt link) {
		this.link = link;
	}
	

	public ArrayList<ListCoupleInt> getListCorrespondances_lang12() {
		return listCorrespondances_lang12;
	}

	public void setListCorrespondances_lang12(
			ArrayList<ListCoupleInt> listCorrespondances_lang12) {
		this.listCorrespondances_lang12 = listCorrespondances_lang12;
	}

	public ArrayList<ListCoupleInt> getListCorrespondances_lang21() {
		return listCorrespondances_lang21;
	}

	public void setListCorrespondances_lang21(
			ArrayList<ListCoupleInt> listCorrespondances_lang21) {
		this.listCorrespondances_lang21 = listCorrespondances_lang21;
	}
	

	public static void main(String[] args) {
	
		Traducteur test = new Traducteur("fra","eng","Files/testCorpus.txt","Files/link.txt");
		test.writePhrasesInParallel("Files/testFr.txt","Files/testEng.txt");
		//System.out.println(test.traduct("je", "fra", "eng"));
	
	}
}
