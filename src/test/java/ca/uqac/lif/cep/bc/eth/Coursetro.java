package ca.uqac.lif.cep.bc.eth;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.1.0.
 */
public class Coursetro extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b506103bb806100206000396000f3fe608060405234801561001057600080fd5b5060043610610052577c0100000000000000000000000000000000000000000000000000000000600035046322faf03a81146100575780633c1b81a514610101575b600080fd5b6100ff6004803603604081101561006d57600080fd5b81019060208101813564010000000081111561008857600080fd5b82018360208201111561009a57600080fd5b803590602001918460018302840111640100000000831117156100bc57600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295505091359250610188915050565b005b610109610248565b6040518080602001838152602001828103825284818151815260200191508051906020019080838360005b8381101561014c578181015183820152602001610134565b50505050905090810190601f1680156101795780820380516001836020036101000a031916815260200191505b50935050505060405180910390f35b815161019b9060009060208501906102f4565b50806001819055507f010becc10ca1475887c4ec429def1ccc2e9ea1713fe8b0d4e9a1d009042f6b8e82826040518080602001838152602001828103825284818151815260200191508051906020019080838360005b838110156102095781810151838201526020016101f1565b50505050905090810190601f1680156102365780820380516001836020036101000a031916815260200191505b50935050505060405180910390a15050565b6060600080600154818054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156102e55780601f106102ba576101008083540402835291602001916102e5565b820191906000526020600020905b8154815290600101906020018083116102c857829003601f168201915b50505050509150915091509091565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061033557805160ff1916838001178555610362565b82800160010185558215610362579182015b82811115610362578251825591602001919060010190610347565b5061036e929150610372565b5090565b61038c91905b8082111561036e5760008155600101610378565b9056fea165627a7a72305820c2087a541f07512ccdaa60c652935536392e440466d444ba6d11051ac8c29f290029";

    public static final String FUNC_SETINSTRUCTOR = "setInstructor";

    public static final String FUNC_GETINSTRUCTOR = "getInstructor";

    public static final Event INSTRUCTOR_EVENT = new Event("Instructor", 
            Arrays.asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected Coursetro(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Coursetro(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Coursetro(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Coursetro(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> setInstructor(String _fName, BigInteger _age) {
        final Function function = new Function(
                FUNC_SETINSTRUCTOR, 
                Arrays.<Type>asList(new Utf8String(_fName),
                new Uint256(_age)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple2<String, BigInteger>> getInstructor() {
        final Function function = new Function(FUNC_GETINSTRUCTOR, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple2<String, BigInteger>>(
                new Callable<Tuple2<String, BigInteger>>() {
                    @Override
                    public Tuple2<String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<String, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public List<InstructorEventResponse> getInstructorEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(INSTRUCTOR_EVENT, transactionReceipt);
        ArrayList<InstructorEventResponse> responses = new ArrayList<InstructorEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            InstructorEventResponse typedResponse = new InstructorEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.name = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.age = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<InstructorEventResponse> instructorEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, InstructorEventResponse>() {
            @Override
            public InstructorEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(INSTRUCTOR_EVENT, log);
                InstructorEventResponse typedResponse = new InstructorEventResponse();
                typedResponse.log = log;
                typedResponse.name = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.age = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<InstructorEventResponse> instructorEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INSTRUCTOR_EVENT));
        return instructorEventFlowable(filter);
    }

    @Deprecated
    public static Coursetro load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Coursetro(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Coursetro load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Coursetro(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Coursetro load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Coursetro(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Coursetro load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Coursetro(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Coursetro> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Coursetro.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Coursetro> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Coursetro.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Coursetro> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Coursetro.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Coursetro> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Coursetro.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class InstructorEventResponse {
        public Log log;

        public String name;

        public BigInteger age;
    }
}
