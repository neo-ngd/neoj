package DNA.Wallets;

public class CoinException extends Exception {
	private static final long serialVersionUID = 3406743448316144201L;

	public CoinException(String message) {
		super(message);
	}
	
	public CoinException(String message, Throwable ex) {
		super(message, ex);
	}
}
