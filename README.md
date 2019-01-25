# A BeepBeep palette to monitor Blockchain events

This project contains a palette for [BeepBeep3](https://liflab.github.io/beepbeep-3/ "BeepBeep3's Homepage"), 
a Complexe Event Processing and monitoring tool developed at Laboratoire d'Informatique Formelle in Université du Québec à Chicoutimi.

## Ethereum Smart Contracts

For now, the project only deals with events procuded by Ethereum's smart contracts. 
We strongly advise you to take a look at the [Web3j library](https://github.com/web3j/web3j/ "Web3j's GitHub")
for a better understanding.
Let's say that the following event is emitted at some point by a contract deployed on the blockchain: 

```javascript
event Instructor(
  string name,
  uint age
);
```

Then, it is possible to catch and deal with these events with the following BeepBeep3 piping:

```java
// Initiating the smart contract's log catcher
// Please note that such processor forces `push` mode
CatchEthContractLogs catcher = new CatchEthContractLogs(
  "http://localhost:8545",    // should be replaced with the actual URL 
                              // of the RPC-enabled Ethereum node
                              
  "0x6702413C52c8Cf0fc5f061C89960a262f40C850c",   // should be replaced with the actual address
                                                  // of the concerned smart contract
                                                  
  true    // will catch every events from the earliest block, put it to false if you only want new events
);

// Now, we have to specify what kind of event we want to retrieve
Event myEvent = new Event(
  "Instructor",   // should match the event name
  Arrays.asList(  
    new TypeReference<Utf8String>() {}, // should match the parameter types of your event
    new TypeReference<Uint256>() {}
  )
);

// This processor will then retrieve these events and output their parameters' values
ApplyFunction getEventParameters = new ApplyFunction(
    new GetEventParameters(myEvent)
);

// Connect you processors
Connector.connect(listener, getEventParameters);
Connector.connect(getEventParameters, nextProcessorInMyComputation);

// ...
// The output[0] of getEventParameters will be an array of Objects that you can reuse
// for further computation or monitoring steps
// ...

// Once you've connected your processors, you can start the catcher
catcher.start();
Thread.sleep(30000); // will actually stop after 30s
catcher.stop();
```

See the Javadoc inside the source code for more details (no web Javadoc for now).
