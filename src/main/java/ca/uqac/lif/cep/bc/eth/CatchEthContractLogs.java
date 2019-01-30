package ca.uqac.lif.cep.bc.eth;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import org.apache.commons.lang3.SystemUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.ipc.IpcService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.protocol.ipc.WindowsIpcService;
import org.web3j.utils.Async;

/**
 * Connects to an Ethereum node via RPC or IPC and wait for EVM log events
 * on a specific smart contract. When a corresponding event occur,
 * its log is pushed to the output.

 * THIS PROCESSOR FORCES PUSH MODE.
 *
 * This processor outputs {@link Log} events (see
 * <a href="https://github.com/web3j/web3j/blob/master/core/src/main/java/org/web3j/protocol/core/methods/response/Log.java">web3j code source</a>
 * for more info).
 *
 * Basically, each ETH transaction (TX) produces an EVM log holding
 * general information about the TX (its block hash, block number, etc.).
 * If the TX is supposed to provoke a Smart Contract event, this log will
 * contain information about the event, such as its parameters.
 *
 * @author Quentin Betti
 */
public class CatchEthContractLogs extends Processor implements Runnable, Consumer<Log>
{
    /**
     * The interval (in milliseconds) at which the ETH node will be polled
     */
    public static final long POLLING_INTERVAL = 500;

    /**
     * Semaphore used to stop the listener
     */
    private volatile boolean m_run;

    /**
     * The {@link Web3j} object acting as interface to the ETH node
     */
    private Web3j m_web3j;

    /**
     * The filter to specify what kind of log should be caught
     */
    private EthFilter m_ethFilter;

    /**
     * The subscription to the event.
     * Used to stop listening to events.
     */
    private Disposable m_subscription;

    /**
     * Initializes the catcher so it can communicate with a running and
     * available ETH node via RPC, and filter events for a specific contract
     * already deployed to the blockchain.
     *
     * THS PROCESSOR MUST BE INSTANTIATED VIA {@link #buildWithIPC(String, String, boolean)}
     * or {@link #buildWithRPC(String, String, boolean)}
     *
     * THIS PROCESSOR FORCES PUSH MODE
     *
     * @param web3j_service
     *          The service used for the connection (either IPC or RPC)
     *
     * @param contract_address
     *          The address of the contract in the ETH blockchain (must start with "0x")
     *
     * @param from_first_block
     *          Specifies if all the events of the contract from the beginning of the blockchain
     *          (true) or if only the latest ones should be caught (false). The second option means
     *          that if events have already been triggered in the blockchain, only those contained
     *          in the last block and in the new ones will be retrieved.
     */
    private CatchEthContractLogs(Web3jService web3j_service, String contract_address, boolean from_first_block)
    {
        super(0,1);
        System.out.println("Initiating connection to eth node at " + web3j_service.toString());
        m_web3j = Web3j.build(web3j_service, POLLING_INTERVAL, Async.defaultExecutorService());

        DefaultBlockParameterName startingBlock = from_first_block ?
                DefaultBlockParameterName.EARLIEST : DefaultBlockParameterName.LATEST;

        m_ethFilter = new EthFilter(
                startingBlock,
                DefaultBlockParameterName.LATEST,
                contract_address);
    }

    /**
     * Constructs a {@link CatchEthContractLogs} which will listen to events via RPC
     * (RPC should be enabled on the node).
     * A {@link CatchEthContractLogs} PROCESSOR FORCES PUSH MODE.
     *
     * @param eth_node_url
     *          The node RPC url
     *
     * @param contract_address
     *          The address of the contract in the ETH blockchain (must start with "0x")
     *
     * @param from_first_block
     *          Specifies if all the events of the contract from the beginning of the blockchain
     *          (true) or if only the latest ones should be caught (false). The second option means
     *          that if events have already been triggered in the blockchain, only those contained
     *          in the last block and in the new ones will be retrieved.
     *
     * @return The initiated RPC-enabled {@link CatchEthContractLogs} processor
     */
    public static CatchEthContractLogs buildWithRPC(String eth_node_url, String contract_address, boolean from_first_block)
    {
        return new CatchEthContractLogs(new HttpService(eth_node_url), contract_address, from_first_block);
    }

    /**
     * Constructs a {@link CatchEthContractLogs} which will listen to events via IPC
     * (IPC should be enabled on the node).
     * A {@link CatchEthContractLogs} PROCESSOR FORCES PUSH MODE.
     *
     * @param eth_node_ipc
     *          The node IPC socket path
     *
     * @param contract_address
     *          The address of the contract in the ETH blockchain (must start with "0x")
     *
     * @param from_first_block
     *          Specifies if all the events of the contract from the beginning of the blockchain
     *          (true) or if only the latest ones should be caught (false). The second option means
     *          that if events have already been triggered in the blockchain, only those contained
     *          in the last block and in the new ones will be retrieved.
     *
     * @return The initiated RPC-enabled {@link CatchEthContractLogs} processor
     */
    public static CatchEthContractLogs buildWithIPC(String eth_node_ipc, String contract_address, boolean from_first_block)
    {
        IpcService ipcService;

        if(SystemUtils.IS_OS_WINDOWS)
        {
            ipcService = new WindowsIpcService(eth_node_ipc);
        }
        else if(SystemUtils.IS_OS_UNIX)
        {
            ipcService = new UnixIpcService(eth_node_ipc);
        }
        else
        {
            throw new UnsupportedOperationException("Operation system is neither UNIX nor Windows");
        }
        return new CatchEthContractLogs(ipcService, contract_address, from_first_block);
    }

    @Override
    public void run()
    {
        m_run = true;
        System.out.println("Listening for events...");
        m_subscription = m_web3j.ethLogFlowable(m_ethFilter).subscribe(this);
    }

    /**
     * Pushes to the output each log it catches.
     *
     * @param log
     *          The caught log
     */
    @Override
    public void accept(Log log)
    {
        Pushable pushable = getPushableOutput(0);
        pushable.push(log);
    }

    @Override
    public void start()
    {
        if (!m_run)
        {
            Thread t = new Thread(this);
            t.start();
        }
    }

    @Override
    public synchronized void stop()
    {
        m_run = false;
        m_subscription.dispose();
    }

    /**
     * Indicates if the {@link CatchEthContractLogs} is currently listening
     * to events from the blockchain.
     *
     * @return true if the {@link CatchEthContractLogs} is currently listening to
     *          events from the blockchain, false otherwise.
     */
    public boolean isCatching()
    {
        return m_subscription != null && !m_subscription.isDisposed();
    }


    @Override
    public Pushable getPushableInput(int i)
    {
        throw new UnsupportedOperationException();
        // there is no input for this processor
    }

    @Override
    public synchronized Pullable getPullableInput(int i)
    {
        throw new UnsupportedOperationException();
        // there is no input for this processor
    }

    @Override
    public Pullable getPullableOutput(int i)
    {
        throw new UnsupportedOperationException();
        // this processor forces push mode!
    }

    @Override
    public Processor duplicate(boolean b)
    {
        throw new UnsupportedOperationException();
        // makes no sense to duplicate this processor
    }
}
