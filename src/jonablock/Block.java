package jonablock;

import java.util.ArrayList;
import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>(); // our data will be  a simple message - certificate data

    private long timeStamp; // as number of milliseconds since 1/1/1970.
    private int nonce;



    //Block Construcor
    public Block(String previousHash) {
        this.previousHash = previousHash;

        this.timeStamp = new Date().getTime();

        this.hash = calculateHash(); //making sure we do this after we set the other values
    }

    //calculate new Hash based on blocks contents
    public String calculateHash(){
        String calculatedhash = StringUtil.applylSha256(
                previousHash+
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot);
        // create a string with difficulty * "0"
        return calculatedhash;
    }

    //Increases nonce value until hash target is reached.
    public void mineBlock(int difficulty){
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = StringUtil.getDificultyString(difficulty); //create a string with difficulty * "0"

//        String target = new String (new char[difficulty]).replace('\0','0');
        while(!hash.substring(0, difficulty).equals(target)){
            nonce++;
            hash = calculateHash();
        }
        System.out.println("jonablock.Block Mined!!! : " + hash);
    }

    //add transactions to this block
    public boolean addTransaction(Transaction transaction){
        //process transation and check if valid, unless block is genesis block then ignore.
        if(transaction == null) return false;
        if((previousHash != "0")){
            if((transaction.processTransaction() != true)){
                System.out.println("transaction faild to process, Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }
}
