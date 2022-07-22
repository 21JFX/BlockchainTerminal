//////  12.07.2022 Cracow  /////////
//  Author: Jakub Adamczyk        ///
//  mail: quba.adamczyk@icloud.com ///
//  Blockchain Project              ///
//  Transaction Input                 ///
//////////////////////////////////////////

/*An unspent transaction output (UTXO)
is the technical term for the amount of digital currency
that remains after a cryptocurrency transaction.
 */
public class TransactionInput {
        public String transactionOutputID;
        public TransactionOutput UTXO;

        public TransactionInput(String transactionOutputID){
            this.transactionOutputID = transactionOutputID;
        }
}
