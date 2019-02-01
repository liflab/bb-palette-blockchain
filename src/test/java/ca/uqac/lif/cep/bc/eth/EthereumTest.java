package ca.uqac.lif.cep.bc.eth;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.Tank;
import org.junit.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests the {@link CatchEthContractLogs} processor and {@link GetEthEventParameters}
 * function. Note that you should have an Geth installed and reference in the PATH
 * environment variable.s
 */
public class EthereumTest
{
    /**
     * The Ethereum node that will be started
     */
    private EthereumNode m_node;

    /**
     * Check that geth is installed before launching tests
     */
    @BeforeClass
    public static void checkGethInstallation()
    {
        try
        {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("geth version");
            if(pr.waitFor() != 0)
            {
                Assert.fail("In command \"geth version\", version option was not recognized");
            }
        }
        catch (IOException | InterruptedException e)
        {
            Assert.fail("Geth not installed or not in PATH env variable");
        }
    }

    /**
     * Starts an Ethereum node and wait until it is ready for transactions
     *
     * @param rpcEnabled
     *          Specifies if the Ethereum node should accept RPC connections or not
     * @throws IOException
     */
    public void startNode(boolean rpcEnabled) throws IOException
    {
        m_node = rpcEnabled ? new EthereumNodeRPC(): new EthereumNodeIPC();
        m_node.start();
        m_node.waitUntilReady();
    }

    /**
     * Stops the Ethereum node previously started
     * @throws InterruptedException
     */
    public void stopNode() throws InterruptedException
    {
        m_node.stop();
        m_node.eraseData();
    }

    @Test
    public void testReachNode() throws IOException, InterruptedException {
        startNode(false);
        String version = m_node.getGethVersion();
        Assert.assertNotNull(version);
        stopNode();
    }

    @Test
    public void testCatchAllEthContractLogsViaRPC() throws Exception
    {
        startNode(true);
        Coursetro contract = m_node.deployContract();
        List<Object[]> eventsParams = initExpectedEventParamValues(10, "Quentin");
        sendSomeEvents(eventsParams, contract, 0, 5);

        CatchEthContractLogs listener =
                CatchEthContractLogs.buildWithRPC(
                        EthereumNodeRPC.DEFAULT_NODE_URL,
                        contract.getContractAddress(),
                        true);

        ApplyFunction getEventParameters =
                new ApplyFunction(new GetEthEventParameters(Coursetro.INSTRUCTOR_EVENT));

        Tank tank = new Tank();

        Connector.connect(listener, getEventParameters, tank);
        listener.start();
        Pullable pullable = tank.getPullableOutput(0);

        // Adding more events in parallel
        sendSomeEvents(eventsParams, contract, 5, 5);

        // Retrieving events from tank and testing
        int counter = 0;
        while (pullable.hasNextSoft() != Pullable.NextStatus.NO && counter < 10)
        {
            Object o = pullable.pullSoft();
            if(o != null)
            {
                Object[] params = (Object[] ) o;
                Assert.assertArrayEquals(eventsParams.get(counter), params);
                counter++;
            }
        }

        listener.stop();
        stopNode();
    }

    @Test
    public void testCatchOnlyLastAndNewEthContractLogsViaIPC() throws Exception
    {
        startNode(false);
        Coursetro contract = m_node.deployContract();
        List<Object[]> eventsParams = initExpectedEventParamValues(10, "Quentin");
        sendSomeEvents(eventsParams, contract, 0, 5);

        // Piping the event catcher, the event caster and a tank so we can pull them
        CatchEthContractLogs listener =
                CatchEthContractLogs.buildWithIPC(
                        EthereumNodeIPC.getDefaultIPCPath(),
                        contract.getContractAddress(),
                        false);

        ApplyFunction getEventParameters =
                new ApplyFunction(new GetEthEventParameters(Coursetro.INSTRUCTOR_EVENT));

        Tank tank = new Tank();

        Connector.connect(listener, getEventParameters, tank);
        listener.start();

        while (!listener.isCatching())
        {
            Thread.sleep(100);
        }

        // Adding more events in parallel
        sendSomeEvents(eventsParams, contract, 5, 5);

        // Retrieving last and new events from tank and testing
        Pullable pullable = tank.getPullableOutput(0);
        int counter = 4;
        while (pullable.hasNextSoft() != Pullable.NextStatus.NO && counter < 10)
        {
            Object o = pullable.pullSoft();
            if(o != null)
            {
                Object[] params = (Object[] ) o;
                Assert.assertArrayEquals(eventsParams.get(counter), params);
                counter++;
            }
        }

        listener.stop();
        stopNode();
    }

    /**
     * Initiates and fills in a {@List} with a specified amount of {@link Object[]}.
     * Each {@link Object[]} is of size 2 and is composed as follow:
     * <ul>
     *     <li>
     *          Index 0 - {@link String}: the name, starts with a specified prefix followed
     *          by the index of the event (e.g. "Quentin0")
     *     </li>
     *     <li>
     *         Index 1 - {@link BigInteger}: the age, correspond to the index of the event
     *     </li>
     * </ul>
     *
     * @param event_nb
     *          The size of the list to be initiated
     *
     * @param name_prefix
     *          The prefix to start the {@link String} with
     *
     * @return The initiated list of events
     */
    private static List<Object[]> initExpectedEventParamValues(int event_nb, String name_prefix)
    {
        List<Object[]> expected = new ArrayList<>();
        for(int i = 0; i < event_nb; i++)
        {
            expected.add(new Object[]{name_prefix + i, BigInteger.valueOf(i)});
        }
        return expected;
    }

    /**
     * Sends events to a smart contract.
     *
     * @param events_params
     *          The list containing the events to send
     *
     * @param contract
     *          The contract towards where the events must be send
     *
     * @param starting_index
     *          The index of the first events to send in the events_params list
     *
     * @param event_nb
     *          The number of events to send in the events_params list
     */
    private static void sendSomeEvents(List<Object[]> events_params, Coursetro contract, int starting_index, int event_nb)
    {
        for(int i = starting_index; i < starting_index + event_nb; i++)
        {
            Object[] params = events_params.get(i);
            try
            {
                contract.setInstructor((String) params[0], (BigInteger) params[1]).send();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

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