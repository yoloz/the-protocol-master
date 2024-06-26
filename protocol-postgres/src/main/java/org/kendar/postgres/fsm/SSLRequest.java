package org.kendar.postgres.fsm;

import org.kendar.buffers.BBufferUtils;
import org.kendar.iterators.ProcessId;
import org.kendar.postgres.messages.NoticeReponse;
import org.kendar.protocol.events.BytesEvent;
import org.kendar.protocol.messages.ProtoStep;
import org.kendar.protocol.states.ProtoState;

import java.util.Iterator;

public class SSLRequest extends ProtoState {
    public static final byte[] SSL_MESSAGE_MARKER = BBufferUtils.toByteArray(0x04, 0xd2, 0x16, 0x2f);

    public SSLRequest(Class<?>... messages) {
        super(messages);
    }

    public boolean canRun(BytesEvent event) {
        var inputBuffer = event.getBuffer();
        return inputBuffer.contains(SSL_MESSAGE_MARKER, 4);
    }


    public Iterator<ProtoStep> execute(BytesEvent event) {
        var inputBuffer = event.getBuffer();
        var protoContext = event.getContext();
        var postgresContext = (PostgresProtoContext) protoContext;
        var pid = (ProcessId) protoContext.getValue("PG_PID");
        if (pid == null) {
            pid = new ProcessId(postgresContext.getPid());
            protoContext.setValue("PG_PID", pid);
        }
        var pidValue = pid.getPid();

        inputBuffer.truncate(8);
        return iteratorOfList(new NoticeReponse());

    }
}
