package ca.uqac.lif.cep.bc.eth;

import org.junit.Test;

public class CatchEthContractLogsBasicTests
{

    private static CatchEthContractLogs buildDummy()
    {
        return CatchEthContractLogs.buildWithIPC(
                EthereumNodeIPC.getDefaultIPCPath(),
                "0x0",
                true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDuplicate()
    {
        CatchEthContractLogs p = buildDummy();
        p.duplicate(true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetPushableInput()
    {
        CatchEthContractLogs p = buildDummy();
        p.getPushableInput();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetPullableInput()
    {
        CatchEthContractLogs p = buildDummy();
        p.getPullableInput(0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetPullableOutput()
    {
        CatchEthContractLogs p = buildDummy();
        p.getPullableOutput();
    }
}
