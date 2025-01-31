package tkk.epic.capability.epicAdd.customMotionAnimation;

import com.google.common.collect.Maps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import tkk.epic.network.SPCustomMotionAnimationSync;
import tkk.epic.network.SPEpicAddAttackSpeedUpdata;
import tkk.epic.network.TkkEpicNetworkManager;
import tkk.epic.utils.StupidOnlyClientTool;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeLivingMotion;

import java.util.Map;

public class CustomMotionAnimation {
    public final LivingEntity entity;
    public final Map<LivingMotion, StaticAnimation> defaultLivingAnimations;
    public final Map<LivingMotion, StaticAnimation> livingAnimations;
    public CustomMotionAnimation(LivingEntity entity){
        this.entity=entity;
        this.defaultLivingAnimations = Maps.newHashMap();
        this.livingAnimations = Maps.newHashMap();
    }


    public void clear(){
        defaultLivingAnimations.clear();
        livingAnimations.clear();
    }
    public void addLivingAnimation(LivingMotion motion,StaticAnimation animation){
        livingAnimations.put(motion,animation);
    }
    public void addDefaultLivingAnimation(LivingMotion motion,StaticAnimation animation){
        defaultLivingAnimations.put(motion,animation);
    }

    public void addLivingAnimation(String motion,StaticAnimation animation){
        addLivingAnimation(LivingMotion.ENUM_MANAGER.getOrThrow(motion),animation);
    }
    public void addDefaultLivingAnimation(String motion,StaticAnimation animation){
        addDefaultLivingAnimation(LivingMotion.ENUM_MANAGER.getOrThrow(motion),animation);
    }
    public void addLivingAnimation(String motion,String animation){
        addLivingAnimation(motion, StupidOnlyClientTool.getAnimation(animation));
    }
    public void addDefaultLivingAnimation(String motion,String animation){
        addDefaultLivingAnimation(motion,StupidOnlyClientTool.getAnimation(animation));
    }


    public void update(){
        SPCustomMotionAnimationSync packet=new SPCustomMotionAnimationSync(this.entity.getId(),this);
        TkkEpicNetworkManager.sendToAllPlayerTrackingThisEntity(packet,this.entity);
        if(entity instanceof ServerPlayer){
            TkkEpicNetworkManager.sendToPlayer(packet, (ServerPlayer) entity);
        }
    }
    public void updateAndChange(){
        update();
        SPChangeLivingMotion packet=new SPChangeLivingMotion(entity.getId());
        EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(packet,this.entity);
        if(entity instanceof ServerPlayer){
            EpicFightNetworkManager.sendToPlayer(packet, (ServerPlayer) entity);
        }

    }


}
