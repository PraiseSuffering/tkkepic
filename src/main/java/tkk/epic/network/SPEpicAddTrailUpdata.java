package tkk.epic.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import tkk.epic.capability.epicAdd.EpicAddCapability;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.capability.epicAdd.trail.ParticleTrail;
import tkk.epic.capability.epicAdd.trail.TkkCustomTrail;
import tkk.epic.capability.epicAdd.trail.TkkTrail;
import tkk.epic.gui.hud.hotbar.HotBarManager;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SPEpicAddTrailUpdata {
    public int entityId;
    public boolean doVanillaTrail;
    public List<TkkTrail> trails;
    public ArrayList<ParticleTrail> particleTrails;
    public SPEpicAddTrailUpdata(){}
    public SPEpicAddTrailUpdata(int entityId,TkkCustomTrail tkkCustomTrail){
        this.entityId=entityId;
        this.doVanillaTrail=tkkCustomTrail.doVanillaTrail;
        this.trails=tkkCustomTrail.trails;
        this.particleTrails=tkkCustomTrail.particleTrails;
    }
    public void onArrive() {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.player.level().getEntity(this.entityId);

        if (entity == null) {
            return;
        }

        IEpicAddCapability epicAddCap = entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if(epicAddCap==null){return;}
        TkkCustomTrail tkkCustomTrail= epicAddCap.getTkkCustomTrail();
        tkkCustomTrail.clear();
        tkkCustomTrail.doVanillaTrail=this.doVanillaTrail;
        for(TkkTrail tkkTrail:this.trails){
            tkkCustomTrail.trails.add(tkkTrail);
        }
        for(ParticleTrail trail:this.particleTrails){
            tkkCustomTrail.particleTrails.add(trail);
        }
    }
    public static SPEpicAddTrailUpdata fromBytes(FriendlyByteBuf buf) {
        SPEpicAddTrailUpdata msg = new SPEpicAddTrailUpdata();
        msg.entityId=buf.readInt();
        msg.doVanillaTrail=buf.readBoolean();
        msg.trails=new ArrayList<>();
        msg.particleTrails=new ArrayList<>();
        int size=buf.readInt();
        int particleSize=buf.readInt();
        for(int i=0;i<size;i++){
            msg.trails.add(new TkkTrail(new Vec3(buf.readDouble(),buf.readDouble(),buf.readDouble()),new Vec3(buf.readDouble(),buf.readDouble(),buf.readDouble()),
                    buf.readUtf(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readUtf(),
                    buf.readBoolean(),
                    buf.readBoolean()
                    ));
        }
        for(int i=0;i<particleSize;i++){
            msg.particleTrails.add(new ParticleTrail(new Vec3(buf.readDouble(),buf.readDouble(),buf.readDouble()),new Vec3(buf.readDouble(),buf.readDouble(),buf.readDouble()),
                    buf.readUtf(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    new ResourceLocation(buf.readUtf()),
                    buf.readUtf(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readDouble(),
                    buf.readInt()
            ));
        }
        return msg;
    }
    public static void toBytes(SPEpicAddTrailUpdata msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeBoolean(msg.doVanillaTrail);
        buf.writeInt(msg.trails.size());
        buf.writeInt(msg.particleTrails.size());
        for (TkkTrail trail:msg.trails){
            buf.writeDouble(trail.start.x);
            buf.writeDouble(trail.start.y);
            buf.writeDouble(trail.start.z);
            buf.writeDouble(trail.end.x);
            buf.writeDouble(trail.end.y);
            buf.writeDouble(trail.end.z);
            buf.writeUtf(trail.joint);
            buf.writeFloat(trail.startTime);
            buf.writeFloat(trail.endTime);
            buf.writeFloat(trail.fadeTime);
            buf.writeFloat(trail.r);
            buf.writeFloat(trail.g);
            buf.writeFloat(trail.b);
            buf.writeInt(trail.interpolateCount);
            buf.writeInt(trail.trailLifetime);
            buf.writeUtf(trail.texturePath);
            buf.writeBoolean(trail.hand);
            buf.writeBoolean(trail.scalePos);
        }
        for (ParticleTrail trail:msg.particleTrails){
            buf.writeDouble(trail.start.x);
            buf.writeDouble(trail.start.y);
            buf.writeDouble(trail.start.z);
            buf.writeDouble(trail.end.x);
            buf.writeDouble(trail.end.y);
            buf.writeDouble(trail.end.z);
            buf.writeUtf(trail.joint);
            buf.writeFloat(trail.startTime);
            buf.writeFloat(trail.endTime);
            buf.writeFloat(trail.fadeTime);
            buf.writeInt(trail.interpolateCount);
            buf.writeInt(trail.trailLifetime);
            buf.writeBoolean(trail.hand);
            buf.writeBoolean(trail.scalePos);

            buf.writeUtf(trail.particle.toString());
            buf.writeUtf(trail.args);
            buf.writeFloat(trail.spaceBetween);
            buf.writeFloat(trail.speed);
            buf.writeDouble(trail.dist);
            buf.writeInt(trail.count);


        }
    }
    public static void handle(SPEpicAddTrailUpdata msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            msg.onArrive();
        });

        ctx.get().setPacketHandled(true);
    }
}
