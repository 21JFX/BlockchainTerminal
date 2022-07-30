//////  12.07.2022 Cracow  /////////
//  Author: Jakub Adamczyk        ///
//  mail: quba.adamczyk@icloud.com ///
//  Blockchain Project              ///
//  Class for a Block structure       ///
//////////////////////////////////////////

import java.util.ArrayList;
import java.util.Date;

public class Block {
    private String hash;
    public String previousHash;
    public long timeStamp;
    public String merkleRoot;
    private int nonce;
    public ArrayList<Transaction> transactions = new ArrayList<>();

    public Block(String previousHash){
        //this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateBlockHash();
    }

    //setters and getters, rest of them not used, so I deleted it
    public String getHash(){
        return hash;
    }
    public String calculateBlockHash() {
        return algoUtils.applySha256(previousHash+ timeStamp +nonce+merkleRoot);
    }
    public void mineBlock(int difficulty){
        merkleRoot = algoUtils.getMerkleRoot(transactions);
        String target = algoUtils.getDificultyString(difficulty);
        while(!hash.substring(0,difficulty).equals(target)){
            nonce++;
            hash = calculateBlockHash();
        }
        System.out.println("Block mined!:" +hash);
    }

    //validation func
    public static Boolean isBchValid(ArrayList<Block>blockchain){
        Block currBlock;
        Block prevBlock;
        String hashTarget = new String(new char[PlataoPlomo.difficulty]).replace('\0','0');

        for(int i=1; i< blockchain.size(); i++){
            currBlock = blockchain.get(i);
            prevBlock = blockchain.get(i-1);
        if(!prevBlock.hash.equals(currBlock.previousHash)) {
            System.out.println("Previous hashes not equal");
            return false;
        }
        if(!prevBlock.hash.equals(currBlock.calculateBlockHash())){
            System.out.println("Current hashes not equal");
            return false;
        }
        if(!currBlock.hash.substring(0,PlataoPlomo.difficulty).equals(hashTarget)){
            System.out.println("The block hasn't been mined");
            return false;
        }
        }

        return true;
    }
    //adding transaction to a block
    public boolean addTransaction(Transaction transaction){
        if(transaction == null) return false;
        if(transactions.size()>10){
            System.out.println("Transactions limit for a block is 10! Transaction not added to a block");
            return false;
        }
        if ((!"0".equals(previousHash)) && (!transaction.processTransaction())) {
            System.out.println("Transaction failed");
            return false;
        }
        transactions.add(transaction);
        System.out.println("Transaction added to a block");
        return true;
    }

}
