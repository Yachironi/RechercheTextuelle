package traducteur;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import tableau_suffixe.MonolingualCorpus;
import tableau_suffixe.SuffixArray;

public class Traducteur {
	private SuffixArray suffixArray_lang1;	// fr
	private SuffixArray suffixArray_lang2;	// eng
	private HashMap<Integer, Integer> link;
	
	public Traducteur(String lang1, String lang2, String corpus, String link){
		suffixArray_lang1 = new SuffixArray(new MonolingualCorpus(corpus, lang1));		
		suffixArray_lang2 = new SuffixArray(new MonolingualCorpus(corpus, lang2));
		
		String fileName_link = "";
		initLink(fileName_link, link);
	}

	private void initLink(String read_write_link, String load_link) {
		link = new HashMap<Integer, Integer>();
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

	private boolean loadLink(String load_link) {
		return false;
		
	}

	private boolean readLink(String fileName) {
		try {
			// Ouverture du flux
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(fileName)));
			link = (HashMap<Integer, Integer>) ois.readObject();
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

	public HashMap<Integer, Integer> getLink() {
		return link;
	}

	public void setLink(HashMap<Integer, Integer> link) {
		this.link = link;
	}
}