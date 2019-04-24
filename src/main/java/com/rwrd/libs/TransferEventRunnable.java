package com.rwrd.libs;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;

import com.rwrd.utils.Environment;

import java.util.Arrays;
import java.util.List;

/**
 * Event log
 * Monitor contract event
 */
public class TransferEventRunnable implements Runnable {
	private static String contractAddress;
	private static Web3j web3j;
	private boolean doStop = false;
	
	public TransferEventRunnable(String rpcURL, String _contractAddress) {
		web3j = Web3j.build(new HttpService(rpcURL));
		contractAddress = _contractAddress;
	}
	
	public synchronized void doStop() {
        this.doStop = true;
    }
	
	private synchronized boolean keepRunning() {
        return this.doStop == false;
    }
	
    @Override
	public void run(){
    	if(keepRunning()) {
    		/**
    		 * Listening to ERC20 token transactions
    		 */
    		EthFilter filter = new EthFilter(
    				DefaultBlockParameterName.EARLIEST,
    				DefaultBlockParameterName.LATEST,
    				contractAddress);
    		Event event = new Event("Transfer",
    				Arrays.<TypeReference<?>>asList(
    						new TypeReference<Address>(true) {},
    						new TypeReference<Address>(true) {}, 
    						new TypeReference<Uint256>(false) {}
    				)
    		);

    		String topicData = EventEncoder.encode(event);
    		filter.addSingleTopic(topicData);

    		web3j.ethLogObservable(filter).subscribe(log -> {
    			System.out.println("====================================");
    			System.out.println("Block Number: " + log.getBlockNumber());
    			System.out.println("Transaction Hash: " + log.getTransactionHash());
    			List<String> topics = log.getTopics();
    			
    			List<Type> results = FunctionReturnDecoder.decode(
    	                log.getData(), event.getNonIndexedParameters());
    		 
    			if(topics.size() == 3) {
    				// topics.get(0) Transfer Event Hash
    				String fromAddress = "Ox" + topics.get(1).substring(26);
    				String toAddress = "Ox" + topics.get(2).substring(26);
    				System.out.println("fromAddress: " + fromAddress);
    				System.out.println("toAddress: " + toAddress);
    				System.out.println("amount: " + results.get(0).getValue());
    				
    				String userAddress = "";
    				
    				if(Environment.OWNER_ADDRESS.equalsIgnoreCase(fromAddress)) {
    					System.out.println("Withdraw .... ");
    					userAddress = toAddress;
    				} 
    				if(Environment.OWNER_ADDRESS.equalsIgnoreCase(toAddress)) {
    					System.out.println("Deposit .... ");
    					userAddress = fromAddress;
    				}		
    				// check if transactionHash already exist in Logs
    			}
        	});
    		
    	}
	}
}
