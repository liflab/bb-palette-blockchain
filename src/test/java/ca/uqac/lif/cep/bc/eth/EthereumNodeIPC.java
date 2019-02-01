package ca.uqac.lif.cep.bc.eth;

import org.apache.commons.lang3.SystemUtils;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.protocol.ipc.WindowsIpcService;

import java.io.File;

public class EthereumNodeIPC extends EthereumNode
{
    public static final String DEFAULT_IPC_PATH_WINDOWS = "\\\\.\\pipe\\geth.ipc";
    public static final String DEFAULT_IPC_PATH_UNIX = DEFAULT_NODE_DIR + "/geth.ipc";

    private String m_ipcPath;


    public EthereumNodeIPC()
    {
        this(getDefaultIPCPath(), DEFAULT_NODE_DIR);
    }


    public EthereumNodeIPC(String eth_node_ipc_path, String eth_node_dir)
    {
        super(eth_node_dir);
        m_ipcPath = eth_node_ipc_path;
    }

    @Override
    public Web3jService buildWeb3jService()
    {
        if(SystemUtils.IS_OS_WINDOWS)
        {
            return new WindowsIpcService(m_ipcPath);
        }
        else if(SystemUtils.IS_OS_UNIX)
        {
            return new UnixIpcService(m_ipcPath);
        }
        else
        {
            throw new UnsupportedOperationException("Operation system is neither UNIX nor Windows");
        }
    }

    @Override
    public String[] buildStartCommand()
    {
        return new String[]{
                "geth", "--dev",
                "--datadir", getNodeDir(),
                "--ipcpath", m_ipcPath
        };
    }

    public static String getDefaultIPCPath()
    {
        if(SystemUtils.IS_OS_WINDOWS)
        {
            return DEFAULT_IPC_PATH_WINDOWS;
        }
        else if(SystemUtils.IS_OS_UNIX)
        {
            return new File(DEFAULT_IPC_PATH_UNIX).getAbsolutePath();
        }
        else
        {
            throw new UnsupportedOperationException("Operation system is neither UNIX nor Windows");
        }
    }
}
