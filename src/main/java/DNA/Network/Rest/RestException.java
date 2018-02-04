package DNA.Network.Rest;

public class RestException extends Exception {
	private static final long serialVersionUID = -8558006777817318117L;
	
	public RestException(String message) {
		super(message);
	}
}