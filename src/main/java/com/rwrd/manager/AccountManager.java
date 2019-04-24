package com.rwrd.manager;

import com.rwrd.utils.Environment;
import com.rwrd.libs.Account;

import java.util.List;


/**
 * Account management related
 */
public class AccountManager {
	private static Account account;

	public static void main(String[] args) {
		account = new Account(Environment.RPC_URL);
		
		System.out.println("New Address: " + createNewAccount(Environment.MNEMONIC_PASSWORD));
		//getAccountList();
		//unlockAccount("0xabe7ca679b400d5fdb4357c0683fa6126832b0da", "123456789");
	}

	/**
	 * Create an account
	 * 
	 * @param Password
	 */
	public static String createNewAccount(String password) {
		return account.createNewAccount(password);
	}

	/**
	 * Get a list of accounts
	 */
	public static List<String> getAccountList() {
		return account.getAccountList();
	}

	/**
	 * Account unlock
	 * 
	 * @param Wallet Address
	 * @param Password
	 */
	public static Boolean unlockAccount(String address, String password) {
		return account.unlockAccount(address, password);
	}
}
