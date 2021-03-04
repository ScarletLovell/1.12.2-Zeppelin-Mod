package xyz.ashleyz.zeppelin.util.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import xyz.ashleyz.zeppelin.util.ZepReference;

public class KeybindPacket implements IMessage {
    private int fwdBck;
    private int lftRgt;
    private int upDown;
    private long who;
    private boolean forceStop;
    
    public KeybindPacket() { }
    public KeybindPacket(int fwdBck, int lftRght, int upDown, long who, boolean forceStop) {
        this.fwdBck = fwdBck;
        this.lftRgt = lftRght;
        this.upDown = upDown;
        this.who = who;
        this.forceStop = forceStop;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        fwdBck = buf.readInt();
        lftRgt = buf.readInt();
        upDown = buf.readInt();
        who = buf.readLong();
        forceStop = buf.readBoolean();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(fwdBck);
        buf.writeInt(lftRgt);
        buf.writeInt(upDown);
        buf.writeLong(who);
        buf.writeBoolean(forceStop);
    }
    public static class Handler implements IMessageHandler<KeybindPacket, IMessage> {
        @Override
        public IMessage onMessage(KeybindPacket message, MessageContext ctx) {
            ZepReference.push(message.who, message.fwdBck, message.lftRgt, message.upDown,message.forceStop);
            return null;
        }
    }
}
