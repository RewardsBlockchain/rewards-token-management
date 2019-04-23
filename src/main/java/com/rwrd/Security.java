package com.rwrd;

import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import com.rwrd.utils.Environment;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;

public class Security {
	private static Web3j web3j;

	public static void main(String[] args) {
		web3j = Web3j.build(new HttpService(Environment.RPC_URL));

		exportPrivateKey("...", "yzw");

		importPrivateKey(new BigInteger("", 16),
				"yzw",
				WalletUtils.getTestnetKeyDirectory());

		exportBip39Wallet(WalletUtils.getTestnetKeyDirectory(),
				"yzw");
	}

	/**
	 * Export private key
	 *
	 * @param The KeytorePath of the keystorePath account
	 * @param password password
	 */
	private static void exportPrivateKey(String keystorePath, String password) {
		try {
			Credentials credentials = WalletUtils.loadCredentials(
					password,
					keystorePath);
			BigInteger privateKey = credentials.getEcKeyPair().getPrivateKey();
			System.out.println(privateKey.toString(16));
		} catch (IOException | CipherException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Import private key
	 *
	 * @param privateKey privateKey
	 * @param password   password
	 * @param directory  Storage path default test network WalletUtils.getTestnetKeyDirectory() Default primary network WalletUtils.getMainnetKeyDirectory()
	 */
	private static void importPrivateKey(BigInteger privateKey, String password, String directory) {
		ECKeyPair ecKeyPair = ECKeyPair.create(privateKey);
		try {
			String keystoreName = WalletUtils.generateWalletFile(password,
					ecKeyPair,
					new File(directory),
					true);
			System.out.println("keystore name " + keystoreName);
		} catch (CipherException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate an account with mnemonic
	 *
	 * @param keystorePath
	 * @param password
	 */
	private static void exportBip39Wallet(String keystorePath, String password) {
		try {
			// TODO: 2018/3/14 Will throw an exception has been issued to the official to be replied
			Bip39Wallet bip39Wallet = WalletUtils.generateBip39Wallet(password, new File(keystorePath));
			System.out.println(bip39Wallet);
		} catch (CipherException | IOException e) {
			e.printStackTrace();
		}
	}

}
