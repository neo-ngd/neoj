package DNA.Wallets;

public class CoinRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 3406743448316144201L;

	public CoinRuntimeException(String message) {
		super(message);
	}
	
	public CoinRuntimeException(String message, Throwable ex) {
		super(message, ex);
	}
}
