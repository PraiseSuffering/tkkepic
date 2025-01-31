package tkk.epic.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import tkk.epic.event.SpellKeybindingUpEvent;

import java.util.function.Supplier;

public class CPTkkSpellUp {
    public int spellId;
    public boolean isEnable;
    public boolean forward;
    public boolean backward;
    public boolean left;
    public boolean right;
    public CPTkkSpellUp(){}
    public CPTkkSpellUp(int id, boolean isEnable,boolean forward,boolean backward,boolean left,boolean right){
        this.spellId=id;
        this.isEnable=isEnable;
        this.forward=forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
    }

    public static CPTkkSpellUp fromBytes(FriendlyByteBuf buf) {
        CPTkkSpellUp msg = new CPTkkSpellUp(buf.readInt(),buf.readBoolean(),buf.readBoolean(),buf.readBoolean(),buf.readBoolean(),buf.readBoolean());
        return msg;
    }

    public static void toBytes(CPTkkSpellUp msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.spellId);
        buf.writeBoolean(msg.isEnable);
        buf.writeBoolean(msg.forward);
        buf.writeBoolean(msg.backward);
        buf.writeBoolean(msg.left);
        buf.writeBoolean(msg.right);
    }

    public static void handle(CPTkkSpellUp msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            SpellKeybindingUpEvent event=new SpellKeybindingUpEvent(ctx.get().getSender(), msg.spellId, msg.isEnable, msg.forward, msg.backward,msg.left,msg.right);
            MinecraftForge.EVENT_BUS.post(event);
        });

        ctx.get().setPacketHandled(true);
    }


}
