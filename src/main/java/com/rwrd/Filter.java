package com.rwrd;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.http.HttpService;

import com.rwrd.utils.Environment;

import rx.Subscription;

import java.math.BigInteger;

/**
 * Filter related
 * Listening block, transaction
 * All listeners are in Web3jRx
 */
public class Filter {
	private static Web3j web3j;

	public static void main(String[] args) {
		web3j = Web3j.build(new HttpService(Environment.RPC_URL));
		/**
		 * New block monitoring
		 */
		newBlockFilter(web3j);
		/**
		 * New transaction monitoring
		 */
		newTransactionFilter(web3j);
		/**
		 * Traversing old blocks, trading
		 */
		replayFilter(web3j);
		/**
		 * From a block to the latest block, transaction
		 */
		catchUpFilter(web3j);

		/**
		 * Cancel listening
		 */
		//subscription.unsubscribe();
	}

	private static void newBlockFilter(Web3j web3j) {
		Subscription subscription = web3j.
				blockObservable(false).
				subscribe(block -> {
					System.out.println("new block come in");
					System.out.println("block number" + block.getBlock().getNumber());
				});
	}

	private static void newTransactionFilter(Web3j web3j) {
		Subscription subscription = web3j.
				transactionObservable().
				subscribe(transaction -> {
					System.out.println("transaction come in");
					System.out.println("transaction txHash " + transaction.getHash());
				});
	}

	private static void replayFilter(Web3j web3j) {
		BigInteger startBlock = BigInteger.valueOf(2000000);
		BigInteger endBlock = BigInteger.valueOf(2010000);
		/**
		 * Traversing old transactions
		 */
		Subscription subscription = web3j.
				replayBlocksObservable(
						DefaultBlockParameter.valueOf(startBlock),
						DefaultBlockParameter.valueOf(endBlock),
						false).
				subscribe(ethBlock -> {
					System.out.println("replay block");
					System.out.println(ethBlock.getBlock().getNumber());
				});

		/**
		 * Traversing old transactions
		 */
		Subscription subscription1 = web3j.
				replayTransactionsObservable(
						DefaultBlockParameter.valueOf(startBlock),
						DefaultBlockParameter.valueOf(endBlock)).
				subscribe(transaction -> {
					System.out.println("replay transaction");
					System.out.println("txHash " + transaction.getHash());
				});
	}

	private static void catchUpFilter(Web3j web3j) {
		BigInteger startBlock = BigInteger.valueOf(2000000);

		/**
		 * Traverse the old block and listen to the new block
		 */
		Subscription subscription = web3j.catchUpToLatestAndSubscribeToNewBlocksObservable(
				DefaultBlockParameter.valueOf(startBlock), false)
				.subscribe(block -> {
					System.out.println("block");
					System.out.println(block.getBlock().getNumber());
				});

		/**
		 * Traversing old deals and listening for new deals
		 */
		Subscription subscription2 = web3j.catchUpToLatestAndSubscribeToNewTransactionsObservable(
				DefaultBlockParameter.valueOf(startBlock))
				.subscribe(tx -> {
					System.out.println("transaction");
					System.out.println(tx.getHash());
				});
	}
}
