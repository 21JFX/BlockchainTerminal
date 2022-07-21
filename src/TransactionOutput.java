//////  12.07.2022 Cracow  /////////
//  Author: Jakub Adamczyk        ///
//  mail: quba.adamczyk@icloud.com ///
//  Blockchain Project              ///
//  Class for a Data Structure        ///
//////////////////////////////////////////


import java.security.PublicKey;


public class TransactionOutput {
    public String id;
    public PublicKey recipient;
    public float value;
    public String parentTransactionID;

    public TransactionOutput(PublicKey recipient,float value, String parentTransactionID){
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionID = parentTransactionID;
        this.id = algoUtils.applySha256(algoUtils.getStringFromKey(recipient)+ value +parentTransactionID);
    }
    public boolean isOwned(PublicKey publicKey){
        return (publicKey == recipient);
    }
}


