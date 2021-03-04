package xyz.ashleyz.zeppelin.util.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import xyz.ashleyz.zeppelin.util.ZepReference;

public class ZeppelinInfoPacket implements IMessage {
    private float velocity;
    private double x;
    private double y;
    private double z;
    
    public ZeppelinInfoPacket() { }
    public ZeppelinInfoPacket(float velocity, double x, double y, double z) {
        this.velocity = velocity;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.velocity = buf.readFloat();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(velocity);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }
    public static class Handler implements IMessageHandler<ZeppelinInfoPacket, IMessage> {
        @Override
        public IMessage onMessage(ZeppelinInfoPacket message, MessageContext ctx) {
            ZepReference.eatMessage(message.velocity, message.x, message.y, message.z);
            return null;
        }
    }
}