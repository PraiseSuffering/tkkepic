package tkk.epic.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.capability.epicAdd.customStateSpectrum.CustomStateSpectrum;
import tkk.epic.capability.epicAdd.customStateSpectrum.StatesTemplateManager;
import tkk.epic.capability.epicAdd.customStateSpectrum.template.ICustomStateSpectrum;
import tkk.epic.capability.epicAdd.shouldBlockMoving.ShouldBlockMoving;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SPShouldBlockMoving {
    public int entityId;
    public Boolean block;

    public SPShouldBlockMoving(){}
    public SPShouldBlockMoving(int entityId, ShouldBlockMoving shouldBlockMoving){
        this.entityId=entityId;
        this.block=shouldBlockMoving.shouldBlockMoving;
    }
    public void onArrive() {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.player.level().getEntity(this.entityId);

        if (entity == null) {
            return;
        }

        IEpicAddCapability epicAddCap = entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if(epicAddCap==null){return;}
        epicAddCap.getShouldBlockMoving().shouldBlockMoving=this.block;
    }
    public static SPShouldBlockMoving fromBytes(FriendlyByteBuf buf) {
        SPShouldBlockMoving msg = new SPShouldBlockMoving();
        msg.entityId=buf.readInt();
        int i=buf.readInt();
        switch (i){
            case 0:
                msg.block=null;
                break;
            case 1:
                msg.block=true;
                break;
            case 2:
                msg.block=false;
                break;
        }
        return msg;
    }
    public static void toBytes(SPShouldBlockMoving msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        if (msg.block==null){
            buf.writeInt(0);
        }else{
            buf.writeInt(msg.block?1:2);
        }
    }
    public static void handle(SPShouldBlockMoving msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            msg.onArrive();
        });

        ctx.get().setPacketHandled(true);
    }
}
