package tkk.epic.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.capability.epicAdd.attackSpeed.AttackSpeed;
import tkk.epic.capability.epicAdd.trail.ParticleTrail;
import tkk.epic.capability.epicAdd.trail.TkkCustomTrail;
import tkk.epic.capability.epicAdd.trail.TkkTrail;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SPEpicAddAttackSpeedUpdata {
    public int entityId;
    public Float customAttackSpeed;
    public boolean attackSpeedModifiers;
    public Float preDelay_attackSpeedModifiers_set=null;
    public Float preDelay_attackSpeedModifiers_add=null;
    public Float preDelay_attackSpeedModifiers_scale=null;
    public Float contact_attackSpeedModifiers_set=null;
    public Float contact_attackSpeedModifiers_add=null;
    public Float contact_attackSpeedModifiers_scale=null;
    public Float recovery_attackSpeedModifiers_set=null;
    public Float recovery_attackSpeedModifiers_add=null;
    public Float recovery_attackSpeedModifiers_scale=null;
    public SPEpicAddAttackSpeedUpdata(){}
    public SPEpicAddAttackSpeedUpdata(LivingEntity entity, AttackSpeed attackSpeed){
        this.entityId=entity.getId();
        this.customAttackSpeed=attackSpeed.customAttackSpeed;
        this.attackSpeedModifiers=attackSpeed.attackSpeedModifiers;
        this.preDelay_attackSpeedModifiers_set=attackSpeed.preDelay_attackSpeedModifiers_set;
        this.preDelay_attackSpeedModifiers_add=attackSpeed.preDelay_attackSpeedModifiers_add;
        this.preDelay_attackSpeedModifiers_scale=attackSpeed.preDelay_attackSpeedModifiers_scale;
        this.contact_attackSpeedModifiers_set=attackSpeed.contact_attackSpeedModifiers_set;
        this.contact_attackSpeedModifiers_add=attackSpeed.contact_attackSpeedModifiers_add;
        this.contact_attackSpeedModifiers_scale=attackSpeed.contact_attackSpeedModifiers_scale;
        this.recovery_attackSpeedModifiers_set=attackSpeed.recovery_attackSpeedModifiers_set;
        this.recovery_attackSpeedModifiers_add=attackSpeed.recovery_attackSpeedModifiers_add;
        this.recovery_attackSpeedModifiers_scale=attackSpeed.recovery_attackSpeedModifiers_scale;
    }
    public void onArrive() {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.player.level().getEntity(this.entityId);

        if (entity == null) {
            return;
        }

        IEpicAddCapability epicAddCap = entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if(epicAddCap==null){return;}
        epicAddCap.setAttackSpeed(customAttackSpeed);
        epicAddCap.setModifiersAttackSpeed(1,preDelay_attackSpeedModifiers_set,preDelay_attackSpeedModifiers_add,preDelay_attackSpeedModifiers_scale);
        epicAddCap.setModifiersAttackSpeed(2,contact_attackSpeedModifiers_set,contact_attackSpeedModifiers_add,contact_attackSpeedModifiers_scale);
        epicAddCap.setModifiersAttackSpeed(3,recovery_attackSpeedModifiers_set,recovery_attackSpeedModifiers_add,recovery_attackSpeedModifiers_scale);

        epicAddCap.getAttackSpeedObject().updateTick=entity.tickCount;
    }
    public static SPEpicAddAttackSpeedUpdata fromBytes(FriendlyByteBuf buf) {
        SPEpicAddAttackSpeedUpdata msg = new SPEpicAddAttackSpeedUpdata();
        msg.entityId=buf.readInt();
        if(buf.readBoolean()){
            msg.customAttackSpeed=null;
        }else{
            msg.customAttackSpeed=buf.readFloat();
        }
        msg.attackSpeedModifiers=buf.readBoolean();
        int type=buf.readInt();
        while (type!=0){
            float num=buf.readFloat();
            int level=type/10%10;
            int modifier=type%10;
            switch (level){
                case 1:
                    switch (modifier){
                        case 1:
                            msg.preDelay_attackSpeedModifiers_set=num;
                            break;
                        case 2:
                            msg.preDelay_attackSpeedModifiers_add=num;
                            break;
                        case 3:
                            msg.preDelay_attackSpeedModifiers_scale=num;
                            break;
                    }
                    break;
                case 2:
                    switch (modifier){
                        case 1:
                            msg.contact_attackSpeedModifiers_set=num;
                            break;
                        case 2:
                            msg.contact_attackSpeedModifiers_add=num;
                            break;
                        case 3:
                            msg.contact_attackSpeedModifiers_scale=num;
                            break;
                    }
                    break;
                case 3:
                    switch (modifier){
                        case 1:
                            msg.recovery_attackSpeedModifiers_set=num;
                            break;
                        case 2:
                            msg.recovery_attackSpeedModifiers_add=num;
                            break;
                        case 3:
                            msg.recovery_attackSpeedModifiers_scale=num;
                            break;
                    }
                    break;
            }
            type=buf.readInt();
        }
        return msg;
    }
    public static void toBytes(SPEpicAddAttackSpeedUpdata msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeBoolean(msg.customAttackSpeed==null);
        if(msg.customAttackSpeed!=null){
            buf.writeFloat(msg.customAttackSpeed);
        }
        buf.writeBoolean(msg.attackSpeedModifiers);

        if (msg.preDelay_attackSpeedModifiers_set!=null){
            buf.writeInt(11);
            buf.writeFloat(msg.preDelay_attackSpeedModifiers_set);
        }
        if (msg.preDelay_attackSpeedModifiers_add!=null){
            buf.writeInt(12);
            buf.writeFloat(msg.preDelay_attackSpeedModifiers_add);
        }
        if (msg.preDelay_attackSpeedModifiers_scale!=null){
            buf.writeInt(13);
            buf.writeFloat(msg.preDelay_attackSpeedModifiers_scale);
        }

        if (msg.contact_attackSpeedModifiers_set!=null){
            buf.writeInt(21);
            buf.writeFloat(msg.contact_attackSpeedModifiers_set);
        }
        if (msg.contact_attackSpeedModifiers_add!=null){
            buf.writeInt(22);
            buf.writeFloat(msg.contact_attackSpeedModifiers_add);
        }
        if (msg.contact_attackSpeedModifiers_scale!=null){
            buf.writeInt(23);
            buf.writeFloat(msg.contact_attackSpeedModifiers_scale);
        }

        if (msg.recovery_attackSpeedModifiers_set!=null){
            buf.writeInt(31);
            buf.writeFloat(msg.recovery_attackSpeedModifiers_set);
        }
        if (msg.recovery_attackSpeedModifiers_add!=null){
            buf.writeInt(32);
            buf.writeFloat(msg.recovery_attackSpeedModifiers_add);
        }
        if (msg.recovery_attackSpeedModifiers_scale!=null){
            buf.writeInt(33);
            buf.writeFloat(msg.recovery_attackSpeedModifiers_scale);
        }
        buf.writeInt(0);

    }
    public static void handle(SPEpicAddAttackSpeedUpdata msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            msg.onArrive();
        });

        ctx.get().setPacketHandled(true);
    }
}
