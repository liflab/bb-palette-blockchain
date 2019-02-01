package ca.uqac.lif.cep.bc.eth;

import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;

public class EthereumNodeRPC extends EthereumNode
{
    /**
     * The URL of the node
     */
    public static String DEFAULT_NODE_URL = "http://127.0.0.1:8545";

    private String m_url;

    public EthereumNodeRPC()
    {
        this(DEFAULT_NODE_URL, DEFAULT_NODE_DIR);
    }

    public EthereumNodeRPC(String eth_node_url, String eth_node_dir)
    {
        super(eth_node_dir);
        m_url = eth_node_url;
    }


    @Override
    public Web3jService buildWeb3jService()
    {
        return new HttpService(m_url);
    }

    @Override
    public String[] buildStartCommand()
    {
        return new String[]{
                "geth", "--dev",
                "--datadir", getNodeDir(),
                "--rpc"
        };
    }
}
