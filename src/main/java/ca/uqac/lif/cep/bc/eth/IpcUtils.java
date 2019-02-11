package ca.uqac.lif.cep.bc.eth;

import org.apache.commons.lang3.SystemUtils;
import org.web3j.protocol.ipc.IpcService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.protocol.ipc.WindowsIpcService;

public class IpcUtils
{
    /**
     * Constructs an {@link IpcService} towards an IPC path based on the OS of the machine running the program
     *
     * @param ipc_path
     *          The IPC path of the Ethereum node
     *
     * @return the correspondent new {@link IpcService}
     */
    public static IpcService newIpcService(String ipc_path) throws UnsupportedOperationException
    {
        IpcService ipcService;

        if(SystemUtils.IS_OS_WINDOWS)
        {
            ipcService = new WindowsIpcService(ipc_path);
        }
        else if(SystemUtils.IS_OS_UNIX)
        {
            ipcService = new UnixIpcService(ipc_path);
        }
        else
        {
            throw new UnsupportedOperationException("Operation system is neither UNIX nor Windows");
        }

        return ipcService;
    }
}
