package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.model.Product;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;


public class Web3Engine {
    static String ipcPath = "/tmp/geth.ipc";

    public static Web3j web3j = Web3j.build(new UnixIpcService(ipcPath) ); // Create a Web3j instance with the IPC provider

    static String keystoreFilePath = "/home/keithhughes/Documents/African Bank Hackathon/keystore/UTC--2023-11-11T16-56-21.020661058Z--5ab6db908cc141e46fcf40e936b57d26c79e8023";
    static String password = "";
    static String keyAlias = "yourKeyAlias";

    // Load credentials from the keystore file
    public static Credentials credentials;

    static {
        try {
            credentials = WalletUtils.loadCredentials(password, keystoreFilePath);
        } catch (IOException | CipherException e) {
            System.out.println("Unable to load Credentials: "+e.getMessage());;
        }
    }


    //45d117e4174f10c16de97c2c050550a766fc7dc8f85cab6d0d7c00aedcc65834
//    public static Credentials credentials = Credentials.create("8019921b39ddcd77fde5131ed07fc98dd29b5582cf1dec36e5f4818495b2464c"); // Replace with your Ethereum account private key
    public static String contractAddress = "0x430dF29c3A6f159eB98E6db0eA7998B4F911BCb3"; // Replace with your deployed contract address


// Check if the account was successfully unlocked


    public static String addProduct(Web3j web3j, Credentials credentials, String contractAddress, String manufacturer, String manufactureDateTime, String expiryDate, String productDescription, String locationManufactured, String batchNumber  ) throws Exception {
        BigInteger nonce = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.PENDING).send().getTransactionCount();

        // Gas details
        BigInteger gasLimit = BigInteger.valueOf(300000); // Change as needed
        ;

        // Chain ID for EIP-155
        long chainId = 1337; // Replace with your chain ID

        // Contract function data
        String encodedFunction = Product.load(contractAddress, web3j, credentials, new DefaultGasProvider()).addProduct(manufacturer, manufactureDateTime, expiryDate, productDescription, locationManufactured, batchNumber).encodeFunctionCall();

        // Create a raw transaction
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasLimit,
                BigInteger.valueOf(300000), // Gas limit
                contractAddress,
                BigInteger.ZERO, // Value
                encodedFunction
        );

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        System.out.println(signedMessage);

        String hexValue = Numeric.toHexString(signedMessage);
        String transactionHash = web3j.ethSendRawTransaction(hexValue).send().getTransactionHash();

        System.out.println("Transaction Hash: " + transactionHash);
        return transactionHash;
    }

    /**
     * The confirmTransactionHash function is used to confirm that a transaction has been successfully mined.
     *
     *
     * @param hashCode Get the transaction receipt
     *
     * @return A boolean value
     *
     */
    public static boolean confirmTransactionHash(String hashCode, List<JsonNode> data) {
        // Get the transaction receipt

         for (JsonNode i : data) {
             if (i.get("hash").asText().equalsIgnoreCase(hashCode)) {
                 return true;
             }

         }
//       Optional<TransactionReceipt> transactionReceipt;
//       try {
//           transactionReceipt = web3j.ethGetTransactionReceipt(hashCode).send().getTransactionReceipt();
//       } catch (IOException e) {
//           throw new RuntimeException(e);
//       }


       // Check if the transaction receipt is not null and the status is successful
       return false;
        }

}
