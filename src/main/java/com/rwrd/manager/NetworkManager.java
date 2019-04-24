package com.rwrd.manager;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;

import com.rwrd.utils.Environment;

import java.io.IOException;
import java.math.BigInteger;

public class NetworkManager {
	private static Web3j web3j;

	public static void main(String[] args) {
		web3j = Web3j.build(new HttpService(Environment.RPC_URL));
		getEthInfo();
	}

	/**
	 * Request Blockchain Information
	 */
	private static void getEthInfo() {

		Web3ClientVersion web3ClientVersion = null;
		try {
			//Web3 Client Version
			web3ClientVersion = web3j.web3ClientVersion().send();
			String clientVersion = web3ClientVersion.getWeb3ClientVersion();
			System.out.println("clientVersion: " + clientVersion);
			//End BlockNumber
			EthBlockNumber ethBlockNumber = web3j.ethBlockNumber().send();
			BigInteger blockNumber = ethBlockNumber.getBlockNumber();
			System.out.println("Block Number:" + blockNumber);
			//ETH CoinBase
			EthCoinbase ethCoinbase = web3j.ethCoinbase().send();
			String coinbaseAddress = ethCoinbase.getAddress();
			System.out.println("CoinBase: " + coinbaseAddress);
			//Whether in the sync block
			EthSyncing ethSyncing = web3j.ethSyncing().send();
			boolean isSyncing = ethSyncing.isSyncing();
			System.out.println("Is Syncing: " + isSyncing);
			//Whether Mining
			EthMining ethMining = web3j.ethMining().send();
			boolean isMining = ethMining.isMining();
			System.out.println("Is Mining: " + isMining);
			//Gas Price
			EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
			BigInteger gasPrice = ethGasPrice.getGasPrice();
			System.out.println("Gas Price:" + gasPrice);
			//Hash Rate
			EthHashrate ethHashrate = web3j.ethHashrate().send();
			BigInteger hashRate = ethHashrate.getHashrate();
			System.out.println("Has Rate: " + hashRate);
			//Protocol Version
			EthProtocolVersion ethProtocolVersion = web3j.ethProtocolVersion().send();
			String protocolVersion = ethProtocolVersion.getProtocolVersion();
			System.out.println("Protocol Version: " + protocolVersion);
			// Number of nodes connected
			// NetPeerCount netPeerCount = web3j.netPeerCount().send();
			// BigInteger peerCount = netPeerCount.getQuantity();
			// System.out.println("Peer Count:" + peerCount);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
