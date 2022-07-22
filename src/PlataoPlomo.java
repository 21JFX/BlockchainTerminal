//////  12.07.2022 Cracow  /////////
//  Author: Jakub Adamczyk        ///
//  mail: quba.adamczyk@icloud.com ///
//  Blockchain Project              ///
//  Main Class                        ///
//////////////////////////////////////////

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;

import java.lang.reflect.Type;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//main class for my coin
public class PlataoPlomo {
    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<>();
    public static Wallet Wallet1;
    public static Wallet Wallet2;
    public static float minTransaction =0.1f;
    public static Transaction genesisTransaction; //1st transaction in a blockchain
    private static String blockchainJson;
    public static int blockNumber;
    static Gson gson= new GsonBuilder().setPrettyPrinting().create();

    public static void main(String [] args) {
        Security.addProvider(new BouncyCastleProvider()); //sec provider for a blockchain
        Wallet1 = new Wallet(); //USER 1
        Wallet2 = new Wallet(); //USER 2 => this solved the requirement to print users balance
        Wallet.wallets.add(Wallet1);
        Wallet.wallets.add(Wallet2);
        //genesis transaction
        genesisTransaction();

        System.out.println("Genesis block created!");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        blockchain.add(genesis);
        blockNumber++;

        System.out.println("Wallet1 balance after genesis transaction: " + Wallet1.getBalance());

        Block block1 = new Block(genesis.getHash());
        block1.addTransaction(Wallet1.sendFunds(Wallet2.publicKey, 10f));
        blockchain.add(block1);
        blockNumber++;

        System.out.println("Wallet2 ballance: " +Wallet2.getBalance());
        System.out.println("Wallet1 ballance: " +Wallet1.getBalance());

        Block block2 = new Block(block1.getHash());
        block2.addTransaction(Wallet1.sendFunds(Wallet2.publicKey, 300f));
        blockchain.add(block2);
        blockNumber++;

        System.out.println("Wallet2 ballance: " +Wallet2.getBalance());
        System.out.println("Wallet1 ballance: " +Wallet1.getBalance());



        //UTILS FUNCTIONS SECTION\\
        //*******************************************************************\\
        //*****************************************************************\\
        Explorer(); //EXPLORER FUNCTIONALITY TO PROVIDE MANY OPTIONS IN BLOCKCHAIN


    }

    //FILE HANDLING METHODS
    public static void writeBlockchainToFile(){
        blockchainJson = gson.toJson(blockchain); //whole blockchain to json
        String writeToFile;
        Scanner wtf = new Scanner(System.in);
        Scanner scn = new Scanner(System.in);
        System.out.println("Do you want to write blockchain details to a file? [Y/N] ");
        writeToFile = wtf.nextLine();
        if(Objects.equals(writeToFile, "Yes") || Objects.equals(writeToFile, "Y") || Objects.equals(writeToFile, "y"))
        {
            System.out.println("\n---------------------------------------------------------");
            System.out.println("Writing blockchain details to a file ");
            System.out.println("Choose a name for a file: ");
            String path = scn.nextLine();

            try (PrintWriter out = new PrintWriter(new FileWriter("json/" + path + ".json"))) {
                out.write(blockchainJson);
                System.out.println("File saved in: "+"json/" + path + ".json");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else{
            System.out.println("File not saved...\n");
        }
    }

    //import without transactions, due to a problems with public.key
    public static void importJsonFile(){
        Scanner _lgc =new Scanner(System.in);
        System.out.println("Do you want to import a file? [Y/N]");
        String lgc = _lgc.nextLine();
        if(Objects.equals(lgc, "Yes") || Objects.equals(lgc, "Y") || Objects.equals(lgc, "y")) {
            Scanner inScn = new Scanner(System.in);
            String filename = inScn.nextLine();
            final Type BlockList = new TypeToken<ArrayList<Block>>() {
            }.getType();
            GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().addDeserializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return (f.getDeclaringClass() == Block.class && f.getName().equals("transactions"));
                }

                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            });
            Gson _gson = gsonBuilder.create();
            try {
                JsonReader reader = new JsonReader(new FileReader("json/" + filename + ".json"));
                ArrayList<Block> data = _gson.fromJson(reader, BlockList);
                //System.out.println(gson.toJson(data));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }else{
            System.out.println("File wasn't imported...Exit");
        }
    }

    //method to explore blockchain features
    public static void Explorer(){
        String blockchainID;
        String transBlock;
        int whichTransBlock;

        String msg = """
                --------------------------------------------------
                Welcome to Simple Blockchain Explorer!
                You can perform few actions(by pressing the number):
                
                [1]Print out blockchain details
                [2]Print out block details
                [3]Print out transactions included in a block
                [4]Print out block by block hash
                [5]Print out block by block height
                [6]Print out last block
                [7]Write blockchain to a file
                [8]Import blockchain from a file
                [9]Show wallet details
                [0]Exit""";
            System.out.println(msg);
            Scanner simpleBE = new Scanner(System.in);
            int sbe = simpleBE.nextInt();
        switch (sbe) {
            case 0 -> System.out.println("Exit...\n-----------------------------");
            case 1 -> //whole blockchain
                    blockchainPrinter();
            case 2 -> {//print out block details
                Scanner whB = new Scanner(System.in);
                System.out.println("Which block do you want to see? [1-" + blockNumber + "]");
                int whichBlock = whB.nextInt();
                blockchainID = gson.toJson(blockchain.get(whichBlock));
                System.out.println("Hash of the block details: \n" + blockchainID);
            }
            case 3 -> { //print out transactions included in a block
                Gson gstr = new GsonBuilder().setPrettyPrinting()
                        .setExclusionStrategies(new ExclusionStrategy() {
                            @Override
                            public boolean shouldSkipField(FieldAttributes f) {
                                return ((f.getDeclaringClass() == Block.class && f.getName().equals("hash"))
                                        || (f.getDeclaringClass() == Block.class && f.getName().equals("previousHash"))
                                        || (f.getDeclaringClass() == Block.class && f.getName().equals("timeStamp"))
                                );
                            }

                            @Override
                            public boolean shouldSkipClass(Class<?> aClass) {
                                return false;
                            }
                        }).create();
                System.out.println("Which block you want to see?\n");
                Scanner sc = new Scanner(System.in);
                whichTransBlock = sc.nextInt();
                transBlock = gstr.toJson(blockchain.get(whichTransBlock));
                System.out.println("Transactions included in a block " + whichTransBlock + "\n" + transBlock);
            }
            case 4 -> { //print out block by hash
                Scanner wh = new Scanner(System.in);
                System.out.println("Which block do you want to see? [1-" + blockNumber + "]");
                int whichHash = wh.nextInt();
                System.out.println("Hash of the block details: \n" + blockchain.get(whichHash - 1).getHash());
            }
            case 5 -> { //print out block by height
                /* cite from Investopedia
                 * The block height of a particular block is
                 * defined as the number of blocks preceding it in the blockchain.
                 * In my case I think I should give 2 options. Printing from the first block and
                 * the last block(reversed list order)
                 */
                System.out.println("Do you want to print the details from the top[1] or bottom[2]?");
                Scanner _input = new Scanner(System.in);
                int input = _input.nextInt();
                if (input == 1) {
                    blockchainPrinter();
                } else {
                    reversedBlockchain();
                }
            }
            case 6 -> { //print out the last block
                blockchainID = gson.toJson(blockchain.get(blockchain.size() - 1));
                System.out.println("The last block details: \n" +
                        blockchainID);
            }
            case 7 ->{ //write blockchain to a file
                writeBlockchainToFile();
            }
            case 8 ->{//import a blockchain from a file
                importJsonFile();
            }
            case 9 ->{//print wallet details you like, transactions
                Wallet.printWalletDetails();
            }
            default -> System.out.println("There isn't such an option");
        }

    }
    public static void blockchainPrinter(){
        blockchainJson = gson.toJson(blockchain);
        ///////Blockchain validation and printing details for a blockchain\\\\\\
        if (Block.isBchValid(blockchain)) {
            System.out.println("\n");
            System.out.println("The blockchain: "+blockchainJson);
        }
        //////////////////////////////
    }
    public static void reversedBlockchain(){
        ArrayList<Block> revBlockchain = IntStream.iterate(blockchain.size() - 1, i -> i >= 0, i -> i - 1).mapToObj(i -> blockchain.get(i)).collect(Collectors.toCollection(ArrayList::new));
        String revBlockchainJSON = gson.toJson(revBlockchain);
        System.out.println(revBlockchainJSON);
    }
    public static void genesisTransaction(){
        Wallet coinbase= new Wallet();
        genesisTransaction = new Transaction(coinbase.publicKey, Wallet1.publicKey,1000f,null);
        genesisTransaction.generateSignature(coinbase.privateKey);
        genesisTransaction.transactionID="0";
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionID));
        UTXOs.put(PlataoPlomo.genesisTransaction.outputs.get(0).id,genesisTransaction.outputs.get(0));
    }
}
