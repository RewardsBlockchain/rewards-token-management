package com.rwrd.manager;

import com.rwrd.libs.TokenClient;
import com.rwrd.libs.TransferEventRunnable;
import com.rwrd.utils.Environment;
import java.math.BigInteger;

public class TokenManager {
	private static TokenClient tokenClient;
	private static Thread transferEventThread;
	private static TransferEventRunnable transferEventRunnable;
	
	public static void main(String[] args) {
		
		tokenClient = new TokenClient(Environment.RPC_URL);
		transferEventRunnable = new TransferEventRunnable(Environment.RPC_URL, Environment.CONTRACT_ADDRESS);
		transferEventThread = new Thread(transferEventRunnable);
		
		System.out.println("Balance of:" + getTokenBalance(Environment.OWNER_ADDRESS, Environment.CONTRACT_ADDRESS));
		
		System.out.println("Token Name:" + getTokenName(Environment.CONTRACT_ADDRESS));
		
		/** Withdraw from node account **/
		System.out.println("Tx Hash:" + withDraw(
				Environment.OWNER_ADDRESS, 
				Environment.MNEMONIC_PASSWORD, 
				"0x3abE2b74Ff82b6e1000d259B7a4f47966B02e4Cd", 
				Environment.CONTRACT_ADDRESS, 
				1, 
				18
				));

		/** Withdraw from cold-wallet account **/
		System.out.println("Self Signed Tx Hash:" + withDrawFromColdWallet(
				Environment.OWNER_ADDRESS, 
				"adf2685633ce3aa65c64f2e08bcafb9a37fc0cc03ec934da4ef346ea8c2c2d67",
				Environment.CONTRACT_ADDRESS,
				"0x3abE2b74Ff82b6e1000d259B7a4f47966B02e4Cd",
				2,
				18
				));
		// thread start
		// startWatchingEvent();
		// thread stop
		// stopWatchingEvent();
	}
	
	public static String withDraw(String fromAddress, String password, String toAddress, String contractAddress, double amount, int decimals) {
		return tokenClient.sendTokenTransaction(fromAddress, password, toAddress, contractAddress, amount, decimals);
	}
	
	public static String withDrawFromColdWallet(String fromAddress, String privateKey, String contractAddress, String toAddress, double amount, int decimals) {
		return tokenClient.sendSelfSignedTokenTransaction(fromAddress, privateKey, contractAddress, toAddress, amount, decimals);
	}
	
    public static BigInteger getTokenBalance(String fromAddress, String contractAddress) {
    	return tokenClient.getTokenBalance(fromAddress, contractAddress);
    }
    
    public static  String getTokenName(String contractAddress) {
    	return tokenClient.getTokenName(contractAddress);
    }
    
    public static  String getTokenSymbol(String contractAddress) {
    	return tokenClient.getTokenSymbol(contractAddress);
    }
    
    public static BigInteger getTokenTotalSupply(String contractAddress) {
    	return tokenClient.getTokenTotalSupply(contractAddress);
    }
    
    public static int getTokenDecimals(String contractAddress) {
    	return tokenClient.getTokenDecimals(contractAddress);
    }
    
    public static void startWatchingEvent() {
    	transferEventThread.start();
    }
    
    public static void stopWatchingEvent() {
    	transferEventRunnable.doStop();
    }
}
