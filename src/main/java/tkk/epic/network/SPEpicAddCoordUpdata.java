package tkk.epic.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Quaternionf;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.capability.epicAdd.animationCoord.CustomCoord;
import tkk.epic.capability.epicAdd.trail.ParticleTrail;
import tkk.epic.capability.epicAdd.trail.TkkCustomTrail;
import tkk.epic.capability.epicAdd.trail.TkkTrail;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.utils.math.Vec3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SPEpicAddCoordUpdata {
    public int entityId;
    public int moveType;
    public boolean canStopMove;
    public ArrayList<Keyframe> keyframes=new ArrayList<>();

    public SPEpicAddCoordUpdata(){}
    public SPEpicAddCoordUpdata(int entityId, CustomCoord customCoord){
        this.entityId=entityId;
        this.moveType=customCoord.moveType;
        this.keyframes=customCoord.keyframes;
        this.canStopMove=customCoord.canStopMove;
    }
    public void onArrive() {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.player.level().getEntity(this.entityId);

        if (entity == null) {
            return;
        }

        IEpicAddCapability epicAddCap = entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if(epicAddCap==null){return;}
        CustomCoord customCoord=epicAddCap.getCustomCoord();
        customCoord.keyframes=this.keyframes;
        customCoord.moveType=this.moveType;
        customCoord.canStopMove=this.canStopMove;
    }
    public static SPEpicAddCoordUpdata fromBytes(FriendlyByteBuf buf) {
        SPEpicAddCoordUpdata msg = new SPEpicAddCoordUpdata();
        msg.entityId=buf.readInt();
        msg.moveType=buf.readInt();
        msg.canStopMove=buf.readBoolean();
        msg.keyframes=new ArrayList<>();
        int size=buf.readInt();
        for (int i=0;i<size;i++){
            Keyframe temp=new Keyframe(buf.readFloat(),new JointTransform(new Vec3f(buf.readFloat(),buf.readFloat(),buf.readFloat()),new Quaternionf(buf.readFloat(),buf.readFloat(),buf.readFloat(),buf.readFloat()),new Vec3f(buf.readFloat(),buf.readFloat(),buf.readFloat())));
            msg.keyframes.add(temp);
        }
        return msg;
    }
    public static void toBytes(SPEpicAddCoordUpdata msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeInt(msg.moveType);
        buf.writeBoolean(msg.canStopMove);
        buf.writeInt(msg.keyframes.size());
        for (int i=0;i<msg.keyframes.size();i++){
            Keyframe temp=msg.keyframes.get(i);
            buf.writeFloat(temp.time());
            buf.writeFloat(temp.transform().translation().x);
            buf.writeFloat(temp.transform().translation().y);
            buf.writeFloat(temp.transform().translation().z);
            buf.writeFloat(temp.transform().rotation().x);
            buf.writeFloat(temp.transform().rotation().y);
            buf.writeFloat(temp.transform().rotation().z);
            buf.writeFloat(temp.transform().rotation().w);
            buf.writeFloat(temp.transform().scale().x);
            buf.writeFloat(temp.transform().scale().y);
            buf.writeFloat(temp.transform().scale().z);
        }
    }
    public static void handle(SPEpicAddCoordUpdata msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            msg.onArrive();
        });

        ctx.get().setPacketHandled(true);
    }
}
