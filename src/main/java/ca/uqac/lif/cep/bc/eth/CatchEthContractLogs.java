package ca.uqac.lif.cep.bc.eth;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.http.HttpService;

/**
 * Connects to an Ethereum node via RPC and wait for EVM log events
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
     * @param eth_node_url
     *          The URL of the RPC-enabled ETH node.
     *
     * @param contract_address
     *          The address of the contract in the ETH blockchain (must start with "0x").
     *
     * @param from_first_block
     *          Specifies if all the events of the contract from the beginning of the blockchain
     *          or only new ones should be caught
     */
    public CatchEthContractLogs(String eth_node_url, String contract_address, boolean from_first_block)
    {
        super(0,1);

        System.out.println("Initiating connection to eth node at " + eth_node_url);
        m_web3j = Web3j.build(new HttpService(eth_node_url));

        DefaultBlockParameterName startingBlock = from_first_block ?
                DefaultBlockParameterName.EARLIEST : DefaultBlockParameterName.LATEST;

        m_ethFilter = new EthFilter(
                startingBlock,
                DefaultBlockParameterName.LATEST,
                contract_address);
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
