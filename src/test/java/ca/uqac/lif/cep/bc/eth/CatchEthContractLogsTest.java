package ca.uqac.lif.cep.bc.eth;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.Sink;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Queue;
import java.util.Scanner;

/**
 * Tests the {@link CatchEthContractLogs} processor and {@link GetEventParameters}
 * function. Note that you should have an ETH node running (locally or remotely)
 * and that RPC should be enable.
 */
public class CatchEthContractLogsTest
{
    // Should match your node URL
    private static String ETH_NODE_URL = "http://localhost:8545";

    // Should be changed depending on deployContract() output
    private static String CONTRACT_ADDRESS = "0x6702413C52c8Cf0fc5f061C89960a262f40C850c";

    /**
     * Deploy a {@link Coursetro} contract towards the blockchain.
     * You need a wallet with sufficient funds on your account.
     * @throws Exception
     */
//    @Test
    public void deployContract() throws Exception
    {
        Web3j web3j = Web3j.build(new HttpService(ETH_NODE_URL));
        Credentials credentials = WalletUtils.loadCredentials(
                "", // replace with your wallet password
                "wallet.json" // replace with your wallet file location
        );

        TransactionManager tm = new RawTransactionManager(web3j, credentials, 500, 500);

        Coursetro coursetro = Coursetro.deploy(web3j, tm, BigInteger.ZERO, BigInteger.valueOf(5000000)).send();
        System.out.println(coursetro.getContractAddress());
    }

    /**
     * Sends TXs that should produce EVM log events on a previously deployed contract.
     * You need a wallet with sufficient funds on your account.
     * @throws Exception
     */
//    @Test
    public void provokeEvents() throws Exception
    {
        Web3j web3j = Web3j.build(new HttpService(ETH_NODE_URL));
        Credentials credentials = WalletUtils.loadCredentials(
                "", // replace with your wallet password
                "wallet.json" // replace with your wallet file location
        );
        TransactionManager tm = new RawTransactionManager(web3j, credentials, 500, 500);

        Coursetro coursetro = Coursetro.load(CONTRACT_ADDRESS, web3j, tm, BigInteger.ZERO, BigInteger.valueOf(5000000));
        System.out.println(coursetro.getContractAddress());

        for (int i = 0; i < 10; i++)
        {
            coursetro.setInstructor("Bernard" + i, BigInteger.valueOf(i)).send();
            coursetro.setInstructor("Louis" + i, BigInteger.valueOf(5+i)).send();
            Thread.sleep(1000);
        }
    }

//    @Test
    public void testLogProcessor() throws InterruptedException
    {
        CatchEthContractLogs listener =
                new CatchEthContractLogs(ETH_NODE_URL, CONTRACT_ADDRESS, true);

        ApplyFunction getEventParameters =
                new ApplyFunction(new GetEventParameters(Coursetro.INSTRUCTOR_EVENT));

        Sink printer = new EventPrinter();

        Connector.connect(listener, getEventParameters);
        Connector.connect(getEventParameters, printer);

        listener.start();
        Thread.sleep(60000);
        listener.stop();
    }

    /**
     * Prints the values of the parameters of the caught events
     */
    public class EventPrinter extends Sink
    {
        public EventPrinter()
        {
            super(1);
        }

        @Override
        protected boolean compute(Object[] objects, Queue<Object[]> queue)
        {
            Object[] params = (Object[]) objects[0];

            for(Object param : params) {
                System.out.print(param.toString() + ",");
            }
            System.out.println();
            return false;
        }

        @Override
        public Processor duplicate(boolean b)
        {
            return new EventPrinter();
        }
    }

    @Test
    public void testGeth () throws InterruptedException
    {
        try
        {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("geth version");
            OutputStream out = pr.getOutputStream();
            InputStream err = pr.getInputStream();

            int result = pr.waitFor();

            System.out.println(out);
            Scanner sErr = new Scanner(err);
            while (sErr.hasNext()) {
                System.out.println(sErr.nextLine());
            }

            if(result == 0) {
                System.out.println("Worked");
            } else {
                System.out.println("Damn");
            }

            Assert.assertEquals(0, result);
        }
        catch (IOException e)
        {
            System.out.println("Geth is not installed");
        }
    }
}