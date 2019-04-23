package com.rwrd;

import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalListAccounts;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.http.HttpService;

import com.rwrd.utils.Environment;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Account management related
 */
public class AccountManager {
	private static Admin admin;

	public static void main(String[] args) {
		admin = Admin.build(new HttpService(Environment.RPC_URL));
		createNewAccount("123456789");
		getAccountList();
		unlockAccount("0xabe7ca679b400d5fdb4357c0683fa6126832b0da", "123456789");

//		admin.personalSendTransaction(); This method is the same as web3j.sendTransaction. No examples are written here.
	}

	/**
	 * Create an account
	 * 
	 * @param Password
	 */
	private static void createNewAccount(String password) {
		try {
			NewAccountIdentifier newAccountIdentifier = admin.personalNewAccount(password).send();
			String address = newAccountIdentifier.getAccountId();
			System.out.println("new account address " + address);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a list of accounts
	 */
	private static void getAccountList() {
		try {
			PersonalListAccounts personalListAccounts = admin.personalListAccounts().send();
			List<String> addressList;
			addressList = personalListAccounts.getAccountIds();
			System.out.println("account size " + addressList.size());
			for (String address : addressList) {
				System.out.println(address);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Account unlock
	 * 
	 * @param Wallet Address
	 * @param Password
	 */
	private static void unlockAccount(String address, String password) {
		//Account unlock duration unit seconds Default 300 seconds
		BigInteger unlockDuration = BigInteger.valueOf(60L);
		try {
			PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(address, password, unlockDuration).send();
			Boolean isUnlocked = personalUnlockAccount.accountUnlocked();
			System.out.println("account unlock " + isUnlocked);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
