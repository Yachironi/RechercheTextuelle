package tableau_suffixe;

public class TokenNotFoundException extends Exception {

	public TokenNotFoundException() {
		super("Le token n'existe pas dans le corpus");
	}

	public TokenNotFoundException(String message) {
		super("Le token "+message+" n'existe pas dans le corpus.");
	}
	
}
