//////  12.07.2022 Cracow  /////////
//  Author: Jakub Adamczyk        ///
//  mail: quba.adamczyk@icloud.com ///
//  Blockchain Project              ///
//  Transaction                       ///
//////////////////////////////////////////
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

public class Transaction {
    /*
       a transaction is related to a wallet so we need to have some users that transfer cash to others

     */
    public float value;
    public PublicKey sender; //senders addr/public key
    public PublicKey recipient; //recipents addr key
    public byte[] signature; //to protect transaction from others
    public String transactionID;

    public ArrayList<TransactionInput> inputs;
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int transactions = 0; //how many transactions

    //constructor
    public Transaction(PublicKey from, PublicKey dest, float value, ArrayList<TransactionInput> inputs){
        this.value = value;
        this.inputs = inputs;
        this.recipient = dest;
        this.sender = from;
    }

    //calculate transaction hash
    private String calcHash(){
        transactions++; //to avoid same hash
        return algoUtils.applySha256(
            algoUtils.getStringFromKey(sender)+ algoUtils.getStringFromKey(recipient)+ value +transactions
                );
    }
    public void generateSignature(PrivateKey privateKey){
        String data = algoUtils.getStringFromKey(sender) + algoUtils.getStringFromKey(recipient)+ value;
        signature = algoUtils.applyECDSASignature(privateKey, data);
    }
    public boolean verifySignature(){
        String data = algoUtils.getStringFromKey(sender) + algoUtils.getStringFromKey(recipient)+ value;
        return algoUtils.verifyECDSASignature(sender,data,signature);
    }
    //verify if we can proceed a transaction
    public boolean processTransaction(){
        if(!verifySignature()) {
            System.out.println("Can't proceed your transaction!");
            return false;
        }
        for(TransactionInput i : inputs){
            i.UTXO = PlataoPlomo.UTXOs.get(i.transactionOutputID);
        }
        if(getInputsValue()<PlataoPlomo.minTransaction){
            System.out.println("Transaction input too small "+getInputsValue());
            System.out.println("Transaction must be grater than"+PlataoPlomo.minTransaction);
            return false;
        }
        //transaction outputs
        float leftOver = getInputsValue() -value;
        transactionID =calcHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionID));
        outputs.add(new TransactionOutput(this.sender,leftOver,transactionID));

        for(TransactionOutput o : outputs) {
            PlataoPlomo.UTXOs.put(o.id , o);
        }
        for(TransactionInput i :inputs){
            if(i.UTXO == null) continue;
            PlataoPlomo.UTXOs.remove(i.UTXO.id);
        }
        return true;
    }
    public float getInputsValue(){
        float total =0;
        for(TransactionInput i :inputs){
            if(i.UTXO == null) continue;
            total += i.UTXO.value;
        }
        return total;
    }
    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }
}


