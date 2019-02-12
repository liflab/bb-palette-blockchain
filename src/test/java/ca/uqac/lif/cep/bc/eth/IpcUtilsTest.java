package ca.uqac.lif.cep.bc.eth;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.protocol.ipc.WindowsIpcService;

import java.awt.*;

public class IpcUtilsTest
{
    @Test
    public void testNewIPCServiceWindows()
    {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        Assert.assertTrue(IpcUtils.newIpcService(EthereumNodeIPC.DEFAULT_IPC_PATH_WINDOWS) instanceof WindowsIpcService);
    }

    @Test
    public void testNewIPCServiceLinux()
    {
        Assume.assumeTrue(SystemUtils.IS_OS_LINUX);
        Assert.assertTrue(IpcUtils.newIpcService(EthereumNodeIPC.DEFAULT_IPC_PATH_UNIX) instanceof UnixIpcService);
    }

    @Test
    public void testNewIPCServiceMacOS()
    {
        Assume.assumeTrue(SystemUtils.IS_OS_MAC_OSX);
        Assert.assertTrue(IpcUtils.newIpcService(EthereumNodeIPC.DEFAULT_IPC_PATH_UNIX) instanceof UnixIpcService);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNewIPCServiceException()
    {
        Assume.assumeFalse(SystemUtils.IS_OS_UNIX || SystemUtils.IS_OS_WINDOWS);
        IpcUtils.newIpcService("");
    }
}
