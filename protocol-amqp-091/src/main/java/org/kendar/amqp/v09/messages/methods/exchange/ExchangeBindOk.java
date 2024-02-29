package org.kendar.amqp.v09.messages.methods.exchange;

import org.kendar.amqp.v09.fsm.events.AmqpFrame;
import org.kendar.amqp.v09.messages.methods.Exchange;
import org.kendar.buffers.BBuffer;
import org.kendar.protocol.messages.ProtoStep;

import java.util.Iterator;

public class ExchangeBindOk extends Exchange {


    public ExchangeBindOk() {
        super();
    }

    public ExchangeBindOk(Class<?>... events) {
        super(events);
    }

    @Override
    protected void setMethod() {
        setMethodId((short) 31);
    }


    @Override
    protected Iterator<ProtoStep> executeMethod(short channel, short classId, short methodId, BBuffer rb, AmqpFrame event) {
        setChannel(channel);
        return iteratorOfList(this);
    }
}
