package NEO.Network.Rpc;

public class RpcException extends Exception
{
	private static final long serialVersionUID = -8558006777817318117L;
	
	public final int code;
	
	public RpcException(int code, String message)
	{
		super(message);
		this.code = code;
	}
}
