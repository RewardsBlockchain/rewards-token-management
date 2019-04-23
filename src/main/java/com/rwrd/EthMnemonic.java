package com.rwrd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rwrd.utils.Environment;

import org.bitcoinj.crypto.*;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ethereum mnemonic
 * using org.bitcoinj
 */
public class EthMnemonic {
	/**
	 * Universal Ethereum's mnemonic path based on the bip44 protocol （imtoken jaxx Metamask myetherwallet）
	 */
	private static String ETH_TYPE = "m/44'/60'/0'/0/0";

	private static SecureRandom secureRandom = new SecureRandom();

	public static void main(String[] args) {
		//Generating mnemonic
		generateMnemonic(ETH_TYPE, Environment.MNEMONIC_PASSWORD);

		//Import mnemonic
		//[team, bid, property, oval, hedgehog, observe, badge, cabin, color, cruel, casino, blame]
		List<String> list = new ArrayList<>();
		list.add("team");
		list.add("bid");
		list.add("property");
		list.add("oval");
		list.add("hedgehog");
		list.add("observe");
		list.add("badge");
		list.add("cabin");
		list.add("color");
		list.add("cruel");
		list.add("casino");
		list.add("blame");
//		importMnemonic(ETH_TYPE, list, Environment.MNEMONIC_PASSWORD);
	}

	public static EthHDWallet generateMnemonic(String path, String password) {
		if (!path.startsWith("m") && !path.startsWith("M")) {
			//Invalid Parameter
			return null;
		}
		String[] pathArray = path.split("/");
		if (pathArray.length <= 1) {
			//PathArray Error
			return null;
		}

		if (password.length() < 8) {
			//Password is too short
			return null;
		}

		String passphrase = "";
		long creationTimeSeconds = System.currentTimeMillis() / 1000;
		DeterministicSeed ds = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
		return createEthWallet(ds, pathArray, password);
	}

	private static EthHDWallet importMnemonic(String path, List<String> list, String password) {
		if (!path.startsWith("m") && !path.startsWith("M")) {
			//Invalid Parameter
			return null;
		}
		String[] pathArray = path.split("/");
		if (pathArray.length <= 1) {
			//Path Array Error
			return null;
		}
		if (password.length() < 8) {
			//Password is too short
			return null;
		}
		String passphrase = "";
		long creationTimeSeconds = System.currentTimeMillis() / 1000;
		DeterministicSeed ds = new DeterministicSeed(list, null, passphrase, creationTimeSeconds);

		return createEthWallet(ds, pathArray, password);
	}

	private static EthHDWallet createEthWallet(DeterministicSeed ds, String[] pathArray, String password) {
		//Root private key
		byte[] seedBytes = ds.getSeedBytes();
		System.out.println("Root private key " + Arrays.toString(seedBytes));
		//Mnemonic
		List<String> mnemonic = ds.getMnemonicCode();
		System.out.println("Mnemonic " + Arrays.toString(mnemonic.toArray()));

		try {
			//Mnemonic seed
			byte[] mnemonicSeedBytes = MnemonicCode.INSTANCE.toEntropy(mnemonic);
			System.out.println("Mnemonic seed " + Arrays.toString(mnemonicSeedBytes));
			ECKeyPair mnemonicKeyPair = ECKeyPair.create(mnemonicSeedBytes);
			WalletFile walletFile = Wallet.createLight(password, mnemonicKeyPair);
			ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
			//Save this keystore after use
			String jsonStr = objectMapper.writeValueAsString(walletFile);
			System.out.println("mnemonic keystore " + jsonStr);
			//verification
			WalletFile checkWalletFile = objectMapper.readValue(jsonStr, WalletFile.class);
			ECKeyPair ecKeyPair = Wallet.decrypt(password, checkWalletFile);
			byte[] checkMnemonicSeedBytes = Numeric.hexStringToByteArray(ecKeyPair.getPrivateKey().toString(16));
			System.out.println("Verify mnemonic seed "
					+ Arrays.toString(checkMnemonicSeedBytes));
			List<String> checkMnemonic = MnemonicCode.INSTANCE.toMnemonic(checkMnemonicSeedBytes);
			System.out.println("Verification mnemonic " + Arrays.toString(checkMnemonic.toArray()));

		} catch (MnemonicException.MnemonicLengthException | MnemonicException.MnemonicWordException | MnemonicException.MnemonicChecksumException | CipherException | IOException e) {
			e.printStackTrace();
		}

		if (seedBytes == null)
			return null;
		DeterministicKey dkKey = HDKeyDerivation.createMasterPrivateKey(seedBytes);
		for (int i = 1; i < pathArray.length; i++) {
			ChildNumber childNumber;
			if (pathArray[i].endsWith("'")) {
				int number = Integer.parseInt(pathArray[i].substring(0,
						pathArray[i].length() - 1));
				childNumber = new ChildNumber(number, true);
			} else {
				int number = Integer.parseInt(pathArray[i]);
				childNumber = new ChildNumber(number, false);
			}
			dkKey = HDKeyDerivation.deriveChildKey(dkKey, childNumber);
		}
		System.out.println("path " + dkKey.getPathAsString());

		ECKeyPair keyPair = ECKeyPair.create(dkKey.getPrivKeyBytes());
		System.out.println("eth privateKey " + keyPair.getPrivateKey().toString(16));
		System.out.println("eth publicKey " + keyPair.getPublicKey().toString(16));

		EthHDWallet ethHDWallet = null;
		try {
			WalletFile walletFile = Wallet.createLight(password, keyPair);
			System.out.println("eth address " + "0x" + walletFile.getAddress());
			ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
			//Save
			String jsonStr = objectMapper.writeValueAsString(walletFile);
			System.out.println("eth keystore " + jsonStr);

			ethHDWallet = new EthHDWallet(keyPair.getPrivateKey().toString(16),
					keyPair.getPublicKey().toString(16),
					mnemonic, dkKey.getPathAsString(),
					"0x" + walletFile.getAddress(), jsonStr);
		} catch (CipherException | JsonProcessingException e) {
			e.printStackTrace();
		}

		return ethHDWallet;
	}

	public static class EthHDWallet {
		String privateKey;
		String publicKey;
		List<String> mnemonic;
		String mnemonicPath;
		String Address;
		String keystore;

		public EthHDWallet(String privateKey, String publicKey, List<String> mnemonic, String mnemonicPath, String address, String keystore) {
			this.privateKey = privateKey;
			this.publicKey = publicKey;
			this.mnemonic = mnemonic;
			this.mnemonicPath = mnemonicPath;
			this.Address = address;
			this.keystore = keystore;
		}
	}

}
