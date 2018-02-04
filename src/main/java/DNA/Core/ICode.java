package DNA.Core;

import DNA.Wallets.ContractParameterType;

public interface ICode {
	public byte[] getCode(); 
	public ContractParameterType[] getParameterList();
	public ContractParameterType[] getReturnType();
}
