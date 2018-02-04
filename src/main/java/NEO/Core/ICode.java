package NEO.Core;

import NEO.Wallets.ContractParameterType;

public interface ICode {
	public byte[] getCode(); 
	public ContractParameterType[] getParameterList();
	public ContractParameterType[] getReturnType();
}
