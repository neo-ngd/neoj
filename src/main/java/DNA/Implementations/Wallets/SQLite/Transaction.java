package DNA.Implementations.Wallets.SQLite;

import java.sql.Date;

class Transaction {
    public byte[] hash;
    public byte type;
    public byte[] rawData;
    public int height;
    public Date time;
}
