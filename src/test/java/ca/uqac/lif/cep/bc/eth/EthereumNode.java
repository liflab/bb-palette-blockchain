package ca.uqac.lif.cep.bc.eth;

import org.apache.commons.io.FileUtils;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * A wrapper class around the geth command line program and its
 * related features used for testing.
 */
public abstract class EthereumNode
{
    /**
     * The directory that will containing the node's data
     */
    public static final String DEFAULT_NODE_DIR = "./node";

    /**
     * The process running the node
     */
    private Process m_process;

    /**
     * The thread wrapping the process of the node
     */
    private Thread m_thread;

    private String m_nodeDir;


    /**
     * Initialize a node
     */
    public EthereumNode()
    {
        this(DEFAULT_NODE_DIR);
    }

    public EthereumNode(String node_dir)
    {
        m_nodeDir = node_dir;
    }

    /**
     * Starts the Ethereum node in a thread
     *
     * @throws IOException
     */
    public void start() throws IOException
    {
        Runtime rt = Runtime.getRuntime();
        m_process = rt.exec(buildStartCommand());

        m_thread = new Thread(() ->
        {
            try
            {
                m_process.waitFor();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        });
        m_thread.start();
    }

    /**
     * Stops the process and the thread running the node
     *
     * @throws InterruptedException
     */
    public void stop() throws InterruptedException
    {
        m_process.destroy();
        m_thread.join();
    }

    /**
     * Blocks until the node is ready for new transactions
     */
    public void waitUntilReady()
    {
        Scanner sErr = new Scanner(m_process.getErrorStream());
        while (sErr.hasNext())
        {
            String line = sErr.nextLine();
            if (line.contains("Sealing paused, waiting for transactions"))
            {
                break;
            }
        }
    }

    /**
     * Retrieves the relative path to the wallet of the developer account
     *
     * @return the relative path to the wallet of the developer account
     */
    public String getWalletFilePath()
    {
        File walletDir = new File(getNodeDir() + "/keystore");
        File wallet = walletDir.listFiles()[0];
        return wallet.getPath();
    }

    /**
     * Deploys a {@link Coursetro} contract to the blockchain
     *
     * @return the deployed contract
     * @throws Exception
     */
    public Coursetro deployContract() throws Exception
    {
        Web3j web3j = Web3j.build(buildWeb3jService());
        Credentials credentials = WalletUtils.loadCredentials("", getWalletFilePath());
        TransactionManager tm = new RawTransactionManager(web3j, credentials, 500, 200);

        return Coursetro.deploy(web3j, tm, BigInteger.ONE, BigInteger.valueOf(5000000)).send();
    }


    public String getGethVersion()
    {
        Web3j web3j = Web3j.build(buildWeb3jService());
        try
        {
            Web3ClientVersion version = web3j.web3ClientVersion().send();
            return version.getWeb3ClientVersion();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Erases all created data related to the Ethereum node
     */
    public void eraseData()
    {
        File nodeDir = new File(getNodeDir());
        try
        {
            FileUtils.deleteDirectory(nodeDir);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public abstract Web3jService buildWeb3jService();

    public abstract String[] buildStartCommand();

    public String getNodeDir()
    {
        return m_nodeDir;
    }
}
