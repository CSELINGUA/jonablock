package jonablock;

import java.net.PortUnreachableException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;

    public HashMap<String, TransactionOutput> UTXOs = new HashMap<>(); // only UTXOs owned by this wallet

    public Wallet(){}

    public void generateKeyPair(){}
    //returns balance and stores the UTXO's
    public float getBalance(){
        float total = 0;
        for(Map.Entry<String, TransactionOutput> item:JonaBlock.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)){ // if output belongs to me (if coins belong to me)
                UTXOs.put(UTXO.id,UTXO); // add it to our list of unspent transaction.
                total += UTXO.value;

            }

        }
        return total;
    }
    //generate and returns a new transaction from this wallet.
    public Transaction sendFunds (PublicKey _recipient, float value){
        if(getBalance() < value){ // gather balance and check
            System.out.println("#Not Enough funds to send transaction, Transaction Discarded");
            return null;
        }
        //create arraylist of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<>();

        float total = 0;
        for(Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }
        Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input: inputs){
            UTXOs.remove(input.transactionOutputId);
        }
        return newTransaction;
    }



}
