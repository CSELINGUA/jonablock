package jonablock;

import java.security.PublicKey;

public class TransactionOutput {
    public String id;
    public PublicKey recipient; //also known as the owner of these data
    public float value; // the amount of coin / values they own
    public String parentTransactionId; // the id of the transaction this output was created in

    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applylSha256(StringUtil.getStringFromKey(recipient) + Float.toString(value) + parentTransactionId);

    }
        //Check if coin belongs to you
        public boolean isMine (PublicKey publicKey){
            return (publicKey == recipient);
        }


        //Returns true if new transaction could be created


}
