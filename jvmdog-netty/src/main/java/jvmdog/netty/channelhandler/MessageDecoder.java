package jvmdog.netty.channelhandler;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import jvmdog.protocol.api.model.DogMessage;

public class MessageDecoder  extends ReplayingDecoder<Void>{

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int version = byteBuf.readInt();
        int type = byteBuf.readInt();
        int headerLength = byteBuf.readInt();
        int dataLength = byteBuf.readInt();

        byte[] header = new byte[headerLength];
        if(headerLength>0){            
            byteBuf.readBytes(header);
        }
        byte[] data = new byte[dataLength];
        if(dataLength>0){            
            byteBuf.readBytes(data);
        }

        DogMessage message = new DogMessage();
        message.setVersion(version);
        message.setType(type);
        message.setHeader(header);
        message.setData(data);

        list.add(message);
    }

}
