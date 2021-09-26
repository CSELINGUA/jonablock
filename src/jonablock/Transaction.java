package jonablock;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    public String transactionId; // this is also the hash of the transaction
    public PublicKey sender;  // sender address / public key
    public PublicKey recipient; // Recipient's address / public key
    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();


    // a rough count of how many transactions have been generated
    private static int sequence = 0;

    //constructor


    public Transaction(PublicKey sender, PublicKey recipient, float value, ArrayList<TransactionInput> inputs) {
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculateHash(){
        sequence++;
        return StringUtil.applylSha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient)+
                        Float.toString(value) + sequence
        );
    }
    public void generateSignature (PrivateKey privateKey){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)+Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey,data);


    }
    public boolean verifySignature(){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);

    }


    public boolean processTransaction(){

        if(verifySignature() == false){
            System.out.println("#Transactioni Signature failed to verify");
            return false;

        }
        for(TransactionInput i : inputs){
            i.UTXO = JonaBlock.UTXOs.get(i.transactionOutputId);
        }

        //check if transaction is valid:
        if(getInputsValue() < JonaBlock.minimumTransaction){
            System.out.println("#Transaction Inputs too small: "+ getInputsValue());
            return false;
        }

        //generate transaction outputs:
        float leftOver = getInputsValue() - value;  // get value of inputs then the left over change:
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, value,transactionId)); //send value to recipient
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));// send the left over 'change' back to sender

        //add outputs to Unspent list
        for(TransactionOutput o: outputs){
            JonaBlock.UTXOs.put(o.id, o);
        }

        //remove transaction inputs for UTXO lists as spent:
        for(TransactionInput i: inputs){
            if(i.UTXO == null) continue;   // if Transaction can't be found skip it
            JonaBlock.UTXOs.remove(i.UTXO.id);
        }
        return true;
    }

    //returns sum of inputs(UTXOs) values
    public float getInputsValue(){
        float total = 0;
        for(TransactionInput i : inputs){
            if(i.UTXO == null) continue; // if Transaction can't be found skip it
            total += i.UTXO.value;
        }
        return total;
    }
    //rturns sum of outputs:
    public float getOutputsValue(){
        float total = 0;
        for(TransactionOutput o: outputs){
            total += o.value;
        }
        return total;

    }
}
