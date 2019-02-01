package ca.uqac.lif.cep.bc.eth;

import org.junit.Assert;
import org.junit.Test;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigInteger;
import java.util.Arrays;

public class GetEthEventParametersTest {

    @Test
    public void testNoEvent()
    {
        GetEthEventParameters getter = new GetEthEventParameters(Coursetro.INSTRUCTOR_EVENT);
        Log emptyLog = new Log();
        emptyLog.setData("");

        Assert.assertArrayEquals(new Object[0], getter.getValue(emptyLog));
    }

    @Test
    public void testNoCorrespondingEvent()
    {
        GetEthEventParameters getter = new GetEthEventParameters(Coursetro.INSTRUCTOR_EVENT);
        Log incorrectLog = new Log();
        incorrectLog.setTopics(Arrays.asList("0x893f4a2978971884a0fbc323a391e4fea1dd7d1108c750838417466f17f15f7a"));
        incorrectLog.setData("0x000000000000000000000000000000000000000000000000000000000000000a");

        Assert.assertArrayEquals(new Object[0], getter.getValue(incorrectLog));
    }


    @Test
    public void testCorrespondingEvent()
    {
        GetEthEventParameters getter = new GetEthEventParameters(new Event(
                "Dummy",
                Arrays.asList(new TypeReference<Uint256>() {}))
        );
        Log dummyLog = new Log();
        dummyLog.setTopics(Arrays.asList("0x893f4a2978971884a0fbc323a391e4fea1dd7d1108c750838417466f17f15f7a"));
        dummyLog.setData("0x000000000000000000000000000000000000000000000000000000000000000a");

        Object[] expected = new Object[]{BigInteger.valueOf(10)};
        Assert.assertArrayEquals(expected, getter.getValue(dummyLog));
    }

//    @Test
    //TODO: this one fails, must convert an SC containing an event with indexed string param to see how its done
    public void testEventWithIndexedParams()
    {
        Event eventWithIndexedParams = new Event(
            "Dummy",
            Arrays.asList(
                    new TypeReference<Uint256>(true) {},
                    new TypeReference<Uint256>(){},
                    new TypeReference<Utf8String>(true){}));

        GetEthEventParameters getter = new GetEthEventParameters(eventWithIndexedParams);

        Log dummyLog = new Log();
        dummyLog.setTopics(Arrays.asList(
                "0x7960639502c536d54bffd0e17f51006a0fce5b231ae311d0173ebfc7963dc46c",
                "0x000000000000000000000000000000000000000000000000000000000000009c",
                "0x1c8aff950685c2ed4bc3174f3472287b56d9517b9c948127319a09a7a36deac8")
        );

        dummyLog.setData("0x000000000000000000000000000000000000000000000000000000000000000f");

        Object[] expected = new Object[]{BigInteger.valueOf(156), "hello", BigInteger.valueOf(15)};
        Assert.assertArrayEquals(expected, getter.getValue(dummyLog));
    }

}
// [{"address":"0x08970fed061e7747cd9a38d680a601510cb659fb","data":"0x000000000000000000000000000000000000000000000000000000000000000f","topics":["0x7960639502c536d54bffd0e17f51006a0fce5b231ae311d0173ebfc7963dc46c","0x000000000000000000000000000000000000000000000000000000000000009c","0x1c8aff950685c2ed4bc3174f3472287b56d9517b9c948127319a09a7a36deac8"],"rawVMResponse":[{"type":"Buffer","data":[8,151,15,237,6,30,119,71,205,154,56,214,128,166,1,81,12,182,89,251]},[{"type":"Buffer","data":[121,96,99,149,2,197,54,213,75,255,208,225,127,81,0,106,15,206,91,35,26,227,17,208,23,62,191,199,150,61,196,108]},{"type":"Buffer","data":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,156]},{"type":"Buffer","data":[28,138,255,149,6,133,194,237,75,195,23,79,52,114,40,123,86,217,81,123,156,148,129,39,49,154,9,167,163,109,234,200]}],{"type":"Buffer","data":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,15]}]}]
