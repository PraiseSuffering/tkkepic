package tkk.epic.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Quaternionf;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.capability.epicAdd.animationCoord.CustomCoord;
import tkk.epic.capability.epicAdd.customStateSpectrum.CustomStateSpectrum;
import tkk.epic.capability.epicAdd.customStateSpectrum.StatesTemplateManager;
import tkk.epic.capability.epicAdd.customStateSpectrum.template.ICustomStateSpectrum;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.utils.math.Vec3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SPEpicCustomStatesSpectrumUpdata {
    public int entityId;
    public Set<ICustomStateSpectrum> timePairs;

    public SPEpicCustomStatesSpectrumUpdata(){}
    public SPEpicCustomStatesSpectrumUpdata(int entityId, CustomStateSpectrum customCoord){
        this.entityId=entityId;
        this.timePairs=customCoord.timePairs;
    }
    public void onArrive() {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.player.level().getEntity(this.entityId);

        if (entity == null) {
            return;
        }

        IEpicAddCapability epicAddCap = entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if(epicAddCap==null){return;}
        CustomStateSpectrum customStateSpectrum=epicAddCap.getCustomStateSpectrum();
        customStateSpectrum.timePairs.clear();
        customStateSpectrum.timePairs.addAll(this.timePairs);
        customStateSpectrum.updateTick=entity.tickCount;
    }
    public static SPEpicCustomStatesSpectrumUpdata fromBytes(FriendlyByteBuf buf) {
        SPEpicCustomStatesSpectrumUpdata msg = new SPEpicCustomStatesSpectrumUpdata();
        msg.entityId=buf.readInt();
        msg.timePairs=new HashSet<>();
        int size=buf.readInt();
        for(int i=0;i<size;i++){
            msg.timePairs.add(StatesTemplateManager.readStateSpectrum(buf));
        }
        return msg;
    }
    public static void toBytes(SPEpicCustomStatesSpectrumUpdata msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeInt(msg.timePairs.size());
        for(ICustomStateSpectrum stateSpectrum:msg.timePairs){
            StatesTemplateManager.writeStateSpectrum(buf,stateSpectrum);
        }
    }
    public static void handle(SPEpicCustomStatesSpectrumUpdata msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            msg.onArrive();
        });

        ctx.get().setPacketHandled(true);
    }
}
