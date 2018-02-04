package DNA.Wallets;

import DNA.Fixed8;
import DNA.UInt160;
import DNA.UInt256;
import DNA.Core.TransactionInput;
import DNA.IO.Caching.ITrackable;
import DNA.IO.Caching.TrackState;

/**
 * Account's change, used to pay for the transaction
 * 
 * @author 12146
 *
 */
public class Coin implements ITrackable<TransactionInput> {
    public TransactionInput input;
    public UInt256 assetId;
    public Fixed8 value;
    public UInt160 scriptHash;

    //[NonSerialized]
    private String _address = null;
    public String address() {
        if (_address == null) {
            _address = Wallet.toAddress(scriptHash);
        }
        return _address;
    }

    //[NonSerialized]
    private CoinState state;
    public CoinState getState() {
        return state;
    }
    
    public void setState(CoinState value) {
        if (state != value) {
            state = value;
            ITrackable<TransactionInput> _this = this;
          	if (_this.getTrackState() == TrackState.None) {
                _this.setTrackState(TrackState.Changed);
          	}
        }
    }
    
    private TrackState trackState;
    @Override
    public TrackState getTrackState() {
        return trackState;
    }

    @Override
    public void setTrackState(TrackState state) {
    	trackState = state;
    }
    
    @Override
    public TransactionInput key() {
        return input;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
        	return true;
        }
        if (!(obj instanceof Coin)) {
        	return false;
        }
        return input.equals(((Coin) obj).input);
    }

    @Override
    public int hashCode() {
        return input.hashCode();
    }
}
