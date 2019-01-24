package ca.uqac.lif.cep.bc.eth;

import ca.uqac.lif.cep.functions.UnaryFunction;
import org.web3j.abi.EventValues;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tx.Contract;

import java.util.List;

/**
 * Retrieves the parameters of a specified {@link Event} in a {@link Log}, add them
 * to an {@link Object[]} and outputs it.
 * Please note that indexed parameters, if any, WILL PRECEDE the non-indexed
 * ones in the output array, regardless of their original order.
 *
 * It is important that the specified {@link Event} is instantiated with the exact
 * same name and types of parameters as in the Solidity contract.
 *
 * For example, the following Solidity event
 *
 * <pre>{@code
 * event Instructor(
 *        string name,
 *        uint age
 *     );
 * }</pre>
 *
 * should be instantiated as follow:
 *
 * <pre>{@code
 * Event INSTRUCTOR_EVENT = new Event(
 *      "Instructor",
 *      Arrays.asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {})
 * );
 * }</pre>
 *
 * This {@link Event} can then be used to instantiate the {@link GetEventParameters} function.
 *
 */
public class GetEventParameters extends UnaryFunction<Log, Object[]>
{
    /**
     * The {@link Event} whose parameters shall be retrieved
     */
    private Event m_event;

    /**
     * Initializes a {@link GetEventParameters} function.
     *
     * @param event
     *          The {@link Event} whose parameters shall be retrieved
     */
    public GetEventParameters(Event event)
    {
        super(Log.class, Object[].class);
        m_event = event;
    }

    /**
     * Retrieves the parameters of the {@link Event} if any in the {@link Log}.
     *
     * @param log
     *          The {@link Log} to retrieve the {@link Event} parameters from
     *
     * @return The array containing the values of the parameters in their declaration
     *          order in the Solidity contract. Please note that indexed parameters,
     *          if any, WILL PRECEDE the non-indexed ones in the output array, regardless
     *          of their original order.
     */
    @Override
    public Object[] getValue(Log log)
    {
        EventValues eventValues = Contract.staticExtractEventParameters(m_event, log);
        if(eventValues == null)
        {
            return new Object[0];
        }

        List<Type> indexedParameters = eventValues.getIndexedValues();
        List<Type> nonIndexedParameters = eventValues.getNonIndexedValues();

        int paramNb = indexedParameters.size() + nonIndexedParameters.size();
        Object[] paramValues = new Object[paramNb];

        // Filling output array with parameter values
        for(int i = 0; i < indexedParameters.size(); i++)
        {
            paramValues[i] = indexedParameters.get(i).getValue();
        }
        for(int i = indexedParameters.size(); i < paramNb; i++)
        {
            paramValues[i] = nonIndexedParameters.get(i).getValue();
        }

        return paramValues;
    }
}
