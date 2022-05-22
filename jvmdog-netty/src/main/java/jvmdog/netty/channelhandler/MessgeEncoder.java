package jvmdog.netty.channelhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import jvmdog.protocol.api.model.DogMessage;

public class MessgeEncoder extends MessageToByteEncoder<DogMessage>{

    @Override
    protected void encode(ChannelHandlerContext ctx, DogMessage msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getVersion());
        out.writeInt(msg.getType());
        out.writeInt(msg.getHeaderLength());
        out.writeInt(msg.getDataLength());
        if(msg.getHeaderLength()>0){            
            out.writeBytes(msg.getHeader());
        }
        if(msg.getDataLength()>0){            
            out.writeBytes(msg.getData());
        }
    }

}
