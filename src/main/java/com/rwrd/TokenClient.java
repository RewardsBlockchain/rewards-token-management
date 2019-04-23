package com.rwrd;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import com.rwrd.utils.Environment;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * ERC20 Based Token Client
 */
public class TokenClient {

	private static Web3j web3j;

	private static Admin admin;

	private static String fromAddress = Environment.OWNER_ADDRESS;

	private static String contractAddress = Environment.CONTRACT_ADDRESS;

	private static String emptyAddress = Environment.EMPTY_ADDRESS;

	public static void main(String[] args) {
		web3j = Web3j.build(new HttpService(Environment.RPC_URL));
		admin = Admin.build(new HttpService(Environment.RPC_URL));
		System.out.println("Token Balance: " + getTokenBalance(web3j, fromAddress, contractAddress));
		System.out.println("Token Name: " + getTokenName(web3j, contractAddress));
		System.out.println("Token Decimals: " + getTokenDecimals(web3j, contractAddress));
		System.out.println("Token Symbol: " + getTokenSymbol(web3j, contractAddress));
		System.out.println("Total Supply: " + getTokenTotalSupply(web3j, contractAddress));
		System.out.println("Transaction: " + sendTokenTransaction(
				fromAddress, "",
				"0x5De9e19FD52450bFd28985167CF2cDdEfbDdbC8a", contractAddress,
				BigInteger.valueOf(100000)));
	}

	/**
	 * Get Token Balance
	 * 
	 * @param web3j
	 * @param fromAddress
	 * @param contractAddress
	 * @return
	 */
	public static BigInteger getTokenBalance(Web3j web3j, String fromAddress, String contractAddress) {

		String methodName = "balanceOf";
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();
		Address address = new Address(fromAddress);
		inputParameters.add(address);

		TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {};
		outputParameters.add(typeReference);
		Function function = new Function(methodName, inputParameters, outputParameters);
		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);

		EthCall ethCall;
		BigInteger balanceValue = BigInteger.ZERO;
		try {
			ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			balanceValue = (BigInteger) results.get(0).getValue();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return balanceValue;
	}

	/**
	 * Get Token Name
	 *
	 * @param web3j
	 * @param contractAddress
	 * @return
	 */
	public static String getTokenName(Web3j web3j, String contractAddress) {
		String methodName = "name";
		String name = null;
		String fromAddr = emptyAddress;
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();

		TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {};
		outputParameters.add(typeReference);

		Function function = new Function(methodName, inputParameters, outputParameters);

		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

		EthCall ethCall;
		try {
			ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			name = results.get(0).getValue().toString();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * Get Token Symbol
	 *
	 * @param web3j
	 * @param contractAddress
	 * @return
	 */
	public static String getTokenSymbol(Web3j web3j, String contractAddress) {
		String methodName = "symbol";
		String symbol = null;
		String fromAddr = emptyAddress;
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();

		TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
		};
		outputParameters.add(typeReference);

		Function function = new Function(methodName, inputParameters, outputParameters);

		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

		EthCall ethCall;
		try {
			ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			symbol = results.get(0).getValue().toString();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return symbol;
	}

	/**
	 * Get Token Decimals
	 *
	 * @param web3j
	 * @param contractAddress
	 * @return
	 */
	public static int getTokenDecimals(Web3j web3j, String contractAddress) {
		String methodName = "decimals";
		String fromAddr = emptyAddress;
		int decimal = 0;
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();

		TypeReference<Uint8> typeReference = new TypeReference<Uint8>() {
		};
		outputParameters.add(typeReference);

		Function function = new Function(methodName, inputParameters, outputParameters);

		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

		EthCall ethCall;
		try {
			ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			decimal = Integer.parseInt(results.get(0).getValue().toString());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return decimal;
	}

	/**
	 * Get Token TotalSupply
	 *
	 * @param web3j
	 * @param contractAddress
	 * @return
	 */
	public static BigInteger getTokenTotalSupply(Web3j web3j, String contractAddress) {
		String methodName = "totalSupply";
		String fromAddr = emptyAddress;
		BigInteger totalSupply = BigInteger.ZERO;
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();

		TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
		};
		outputParameters.add(typeReference);

		Function function = new Function(methodName, inputParameters, outputParameters);

		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

		EthCall ethCall;
		try {
			ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			totalSupply = (BigInteger) results.get(0).getValue();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return totalSupply;
	}

	/**
	 * Send Token Transaction
	 */
	public static String sendTokenTransaction(String fromAddress, String password, String toAddress, String contractAddress, BigInteger amount) {
		String txHash = null;

		try {
			PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(
					fromAddress, password, BigInteger.valueOf(10)).send();
			if (personalUnlockAccount.accountUnlocked()) {
				String methodName = "transfer";
				List<Type> inputParameters = new ArrayList<>();
				List<TypeReference<?>> outputParameters = new ArrayList<>();

				Address tAddress = new Address(toAddress);

				Uint256 value = new Uint256(amount);
				inputParameters.add(tAddress);
				inputParameters.add(value);

				TypeReference<Bool> typeReference = new TypeReference<Bool>() {
				};
				outputParameters.add(typeReference);

				Function function = new Function(methodName, inputParameters, outputParameters);

				String data = FunctionEncoder.encode(function);

				EthGetTransactionCount ethGetTransactionCount = web3j
						.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.PENDING).sendAsync().get();
				BigInteger nonce = ethGetTransactionCount.getTransactionCount();
				BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(5), Convert.Unit.GWEI).toBigInteger();

				Transaction transaction = Transaction.createFunctionCallTransaction(fromAddress, nonce, gasPrice,
						BigInteger.valueOf(60000), contractAddress, data);

				EthSendTransaction ethSendTransaction = web3j.ethSendTransaction(transaction).sendAsync().get();
				txHash = ethSendTransaction.getTransactionHash();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txHash;
	}
}
