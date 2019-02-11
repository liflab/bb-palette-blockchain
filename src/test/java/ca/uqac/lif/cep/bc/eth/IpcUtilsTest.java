package ca.uqac.lif.cep.bc.eth;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.protocol.ipc.WindowsIpcService;

public class IpcUtilsTest
{
    @Test
    public void testNewIPCServiceWindows()
    {
        if (SystemUtils.IS_OS_WINDOWS)
        {
            Assert.assertTrue(IpcUtils.newIpcService(EthereumNodeIPC.DEFAULT_IPC_PATH_WINDOWS) instanceof WindowsIpcService);
        }
    }

    @Test
    public void testNewIPCServiceLinux()
    {
        if(SystemUtils.IS_OS_LINUX)
        {
            Assert.assertTrue(IpcUtils.newIpcService(EthereumNodeIPC.DEFAULT_IPC_PATH_UNIX) instanceof UnixIpcService);
        }
    }

    @Test
    public void testNewIPCServiceMacOS()
    {
        if(SystemUtils.IS_OS_MAC_OSX)
        {
            Assert.assertTrue(IpcUtils.newIpcService(EthereumNodeIPC.DEFAULT_IPC_PATH_UNIX) instanceof UnixIpcService);
        }
    }

    @Test
    public void testNewIPCServiceException()
    {
        if(!SystemUtils.IS_OS_UNIX && !SystemUtils.IS_OS_WINDOWS)
        {
            try
            {
                IpcUtils.newIpcService("");
                Assert.fail("IPC is available only on Unix and Windows systems");
            } catch (UnsupportedOperationException e)
            {
                Assert.assertEquals("Operation system is neither UNIX nor Windows", e.getMessage());
            }
        }
    }
}
