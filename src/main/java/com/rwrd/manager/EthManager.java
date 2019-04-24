package com.rwrd.manager;

import com.rwrd.libs.EthClient;
import com.rwrd.utils.Environment;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class EthManager {
	private static EthClient ethClient;

	public static void main(String[] args) {
		ethClient = new EthClient(Environment.RPC_URL);
		getEthBalance(Environment.OWNER_ADDRESS);
	}

	public static BigInteger getEthBalance(String address) {
		return ethClient.getBalance(address);
	}
	
	public static String sendEth(String fromAddress, String password, String toAddress, double amount) {
		return ethClient.sendTransaction(fromAddress, password, toAddress, amount);
	}
	
	public static String sendEthfromColdWallet(String fromAddress, String privateKey, String toAddress, double amount) {
		return ethClient.sendSelfSignedTransaction(fromAddress, privateKey, toAddress, amount);
	}
}
