//////  12.07.2022 Cracow  /////////
//  Author: Jakub Adamczyk        ///
//  mail: quba.adamczyk@icloud.com ///
//  Blockchain Project              ///
//  Algo Utils                        ///
//////////////////////////////////////////

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import org.bouncycastle.crypto.signers.ECDSASigner;

public class algoUtils {
        //Applies Sha256 to a string and returns the result.
        public static String applySha256(String input){
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                //Applies sha256 to our input,
                byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder(); // This will contain hash as hexidecimal
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            }
            catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
        //methods for applying key pairs to a transaction, tbh just a copy paste, i cant make sth better
        public static byte[] applyECDSASignature(PrivateKey privateKey, String input){
            Signature dsa;
            byte [] output = new byte[0];
            try{
                dsa = Signature.getInstance("SHA256withECDSA"); //getInstance always need expection,
                dsa.initSign(privateKey);
                byte [] strByte = input.getBytes();
                dsa.update(strByte);
                byte[] realSign = dsa.sign();
                output = realSign;
            }catch(Exception ex){
                throw new RuntimeException(ex);
            }
            return output; //return array of encoded bytes
        }
        //verify a string signature
        public static boolean verifyECDSASignature(PublicKey publicKey,String data, byte [] signature){
            try{
                Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
                ecdsaVerify.initVerify(publicKey);
                ecdsaVerify.update(data.getBytes());
                return ecdsaVerify.verify(signature);
            }catch(Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    public static String getStringFromKey(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    public static String getMerkleRoot(ArrayList<Transaction> transactions){
            int counter = transactions.size();
            ArrayList<String> previousTreeLayer = new ArrayList<String>();
            for(Transaction transaction: transactions){
                previousTreeLayer.add(transaction.transactionID);
            }
            ArrayList<String> treeLayer = previousTreeLayer;
            while(counter > 1){
                treeLayer = new ArrayList<String>();
                for(int i =1;i<previousTreeLayer.size()-1;i++){
                    treeLayer.add(applySha256(previousTreeLayer.get(i-1)+previousTreeLayer.get(i+1)));//applied for neigh of i
                }
                counter = treeLayer.size();
                previousTreeLayer = treeLayer;
            }
            String merkleRoot =(treeLayer.size()==1)?treeLayer.get(0) :"";
            return merkleRoot;
    }

    public static String getDificultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }
}
