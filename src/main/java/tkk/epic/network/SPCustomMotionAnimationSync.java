package tkk.epic.network;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Quaternionf;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.capability.epicAdd.animationCoord.CustomCoord;
import tkk.epic.capability.epicAdd.customMotionAnimation.CustomMotionAnimation;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.math.Vec3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SPCustomMotionAnimationSync {
    public int entityId;
    private List<LivingMotion> motionList = Lists.newArrayList();
    private List<StaticAnimation> animationList = Lists.newArrayList();
    private int count;
    private List<LivingMotion> defaultMotionList = Lists.newArrayList();
    private List<StaticAnimation> defaultAnimationList = Lists.newArrayList();
    private int defaultCount;

    public SPCustomMotionAnimationSync(){}
    public SPCustomMotionAnimationSync(int entityId, CustomMotionAnimation customMotionAnimation){
        this.entityId=entityId;
        Iterator<LivingMotion> iterator=customMotionAnimation.livingAnimations.keySet().iterator();
        count=customMotionAnimation.livingAnimations.size();
        while (iterator.hasNext()){
            LivingMotion temp=iterator.next();
            motionList.add(temp);
            animationList.add(customMotionAnimation.livingAnimations.get(temp));
        }
        iterator=customMotionAnimation.defaultLivingAnimations.keySet().iterator();
        defaultCount=customMotionAnimation.defaultLivingAnimations.size();
        while (iterator.hasNext()){
            LivingMotion temp=iterator.next();
            defaultMotionList.add(temp);
            defaultAnimationList.add(customMotionAnimation.defaultLivingAnimations.get(temp));
        }
    }
    public void onArrive() {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.player.level().getEntity(this.entityId);

        if (entity == null) {
            return;
        }

        IEpicAddCapability epicAddCap = entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if(epicAddCap==null){return;}
        epicAddCap.getCustomMotionAnimation().clear();
        for(int i=0;i<count;i++){
            epicAddCap.getCustomMotionAnimation().addLivingAnimation(motionList.get(i),animationList.get(i));
        }
        for(int i=0;i<defaultCount;i++){
            epicAddCap.getCustomMotionAnimation().addDefaultLivingAnimation(defaultMotionList.get(i),defaultAnimationList.get(i));
        }
    }
    public static SPCustomMotionAnimationSync fromBytes(FriendlyByteBuf buf) {
        SPCustomMotionAnimationSync msg = new SPCustomMotionAnimationSync();
        msg.entityId=buf.readInt();
        msg.count=buf.readInt();
        msg.defaultCount=buf.readInt();

        for (int i = 0; i < msg.count; i++) {
            msg.motionList.add(LivingMotion.ENUM_MANAGER.getOrThrow(buf.readInt()));
        }

        for (int i = 0; i < msg.count; i++) {
            msg.animationList.add(AnimationManager.getInstance().byId(buf.readInt()));
        }

        for (int i = 0; i < msg.defaultCount; i++) {
            msg.defaultMotionList.add(LivingMotion.ENUM_MANAGER.getOrThrow(buf.readInt()));
        }

        for (int i = 0; i < msg.defaultCount; i++) {
            msg.defaultAnimationList.add(AnimationManager.getInstance().byId(buf.readInt()));
        }
        return msg;
    }
    public static void toBytes(SPCustomMotionAnimationSync msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeInt(msg.count);
        buf.writeInt(msg.defaultCount);

        for (LivingMotion motion : msg.motionList) {
            buf.writeInt(motion.universalOrdinal());
        }

        for (StaticAnimation anim : msg.animationList) {
            buf.writeInt(anim.getId());
        }

        for (LivingMotion motion : msg.defaultMotionList) {
            buf.writeInt(motion.universalOrdinal());
        }

        for (StaticAnimation anim : msg.defaultAnimationList) {
            buf.writeInt(anim.getId());
        }
    }
    public static void handle(SPCustomMotionAnimationSync msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            msg.onArrive();
        });

        ctx.get().setPacketHandled(true);
    }
}
