//////  12.07.2022 Cracow  /////////
//  Author: Jakub Adamczyk        ///
//  mail: quba.adamczyk@icloud.com ///
//  Blockchain Project              ///
//  Wallet                            ///
//////////////////////////////////////////

import java.security.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.sql.Array;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;

public class Wallet {
    public PrivateKey privateKey; //protected 
    public PublicKey publicKey;
    public Map<String, TransactionOutput> UTXOs = new HashMap<>(); //final
    public static List<Wallet> wallets = new ArrayList<>(); //final
    Wallet(){
        generateKeyPair();
    }

    public void generateKeyPair() {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG"); //public static final STRING
            ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("B-571");
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }catch (Exception e){
            throw new RuntimeException(e); //logging
        }
    }

    //as the name says
    public float getBalance(){ //big decimal 
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : PlataoPlomo.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isOwned(publicKey)){
                UTXOs.put(UTXO.id,UTXO);
                total += UTXO.value;
            }
        }
        return total;
    }
    public Transaction sendFunds(PublicKey _recipient, float value){
        if(getBalance()<value){
            System.out.println("Not enough funds to make a transaction"); //logger
            return null;
        }
        List<TransactionInput> inputs = new ArrayList<>(); //set size map.entrySet(setSize())
        float total = 0; //add after filtering
        for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }
        Transaction newTransaction = new Transaction(publicKey, _recipient,value,inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input: inputs){
            UTXOs.remove(input.transactionOutputID);
        }
        return newTransaction;
    }


    public static void printWalletDetails(){ //add output stream as a param
        int _wallet;
        ArrayList<String> arr = new ArrayList<>();
        for(int i=1; i<wallets.size()-1;i++){
            arr.add("Wallet"+i);
        }
        Scanner wallet = new Scanner(System.in);
        System.out.println("Which wallet you want to see?"+
                " Available wallets:\n "+ arr +"\n Choose index from [1-"+ wallets.size()+"]" //never,log instead
                );
        _wallet = wallet.nextInt();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String walletToJson = gson.toJson(wallets.get(_wallet-1));
        System.out.println(walletToJson);
    }

}
