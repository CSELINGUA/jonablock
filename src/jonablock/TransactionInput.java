package jonablock;

public class TransactionInput {

    public String transactionOutputId; // reference to TransactionOutputs -> transactionId
    public TransactionOutput UTXO;  // contains the unspent transaction

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }


}
