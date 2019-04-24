package com.rwrd.libs;

import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalListAccounts;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Account management related
 */
public class Account {
	private static Admin admin;

	public Account(String rpcURL) {
		admin = Admin.build(new HttpService(rpcURL));
	}

	/**
	 * Create an account
	 * 
	 * @param Password
	 * @return account
	 */
	public String createNewAccount(String password) {
		String account = null;
		try {
			NewAccountIdentifier newAccountIdentifier = admin.personalNewAccount(password).send();
			account = newAccountIdentifier.getAccountId();
		} catch (IOException e) {
			e.printStackTrace();
					}
		return account;
	}

	/**
	 * Get a list of accounts
	 * 
	 * @return accountList
	 */
	public List<String> getAccountList() {
		List<String> accountList = null;
		try {
			PersonalListAccounts personalListAccounts = admin.personalListAccounts().send();
			accountList =  personalListAccounts.getAccountIds();
		} catch (IOException e) {
			e.printStackTrace();
					}
		return accountList;
	}

	/**
	 * Account unlock
	 * 
	 * @param Wallet Address
	 * @param Password
	 * @return unlock
	 */
	public Boolean unlockAccount(String address, String password) {
		//Account unlock duration unit seconds Default 300 seconds
		Boolean unlock = false;
		BigInteger unlockDuration = BigInteger.valueOf(60L);
		try {
			PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(address, password, unlockDuration).send();
			unlock = personalUnlockAccount.accountUnlocked();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return unlock;
	}
}
