package tkk.epic.capability.epicAdd.animationCoord;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import tkk.epic.TkkEpic;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.network.SPEpicAddAttackSpeedUpdata;
import tkk.epic.network.SPEpicAddCoordUpdata;
import tkk.epic.network.TkkEpicNetworkManager;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.Vec3f;

import java.util.ArrayList;

public class CustomCoord {
    public static final MoveCoordFunctions.MoveCoordSetter TKK_RAW_COORD = (self, entitypatch, transformSheet) -> {
        IEpicAddCapability cap = entitypatch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        transformSheet.readFrom(cap.getCustomCoord().getTransformSheet().copyAll());
    };
    public static final MoveCoordFunctions.MoveCoordSetter TKK_TRACE_LOC_TARGET = (self, entitypatch, transformSheet) -> {
        IEpicAddCapability cap = entitypatch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        LivingEntity attackTarget = entitypatch.getTarget();

        if (attackTarget != null) {
            TransformSheet transform = (self.isLinkAnimation())?self.getCoord().copyAll():cap.getCustomCoord().getTransformSheet().copyAll();
            Keyframe[] keyframes = transform.getKeyframes();
            int startFrame = 0;
            int endFrame = keyframes.length - 1;
            Vec3f keyLast = keyframes[endFrame].transform().translation();
            Vec3 pos = entitypatch.getOriginal().position();
            Vec3 targetpos = attackTarget.position();
            Vec3 toTarget = targetpos.subtract(pos);
            Vec3 viewVec = entitypatch.getOriginal().getViewVector(1.0F);
            float horizontalDistance = Math.max((float)toTarget.horizontalDistance() - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
            Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
            float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);

            if (scale > 1.0F) {
                float dot = (float)toTarget.normalize().dot(viewVec.normalize());
                scale = Math.max(scale * dot, 1.0F);
            }

            for (int i = startFrame; i <= endFrame; i++) {
                Vec3f translation = keyframes[i].transform().translation();

                if (translation.z < 0.0F) {
                    translation.z *= scale;
                }
            }

            transformSheet.readFrom(transform);
        } else {
            transformSheet.readFrom((self.isLinkAnimation())?self.getCoord().copyAll():cap.getCustomCoord().getTransformSheet().copyAll());
        }
    };
    public static final MoveCoordFunctions.MoveCoordSetter TKK_TRACE_LOC_TARGET_NEARER = (self, entitypatch, transformSheet) -> {
        IEpicAddCapability cap = entitypatch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        LivingEntity attackTarget = entitypatch.getTarget();

        if (attackTarget != null) {
            TransformSheet transform = (self.isLinkAnimation())?self.getCoord().copyAll():cap.getCustomCoord().getTransformSheet().copyAll();
            Keyframe[] keyframes = transform.getKeyframes();
            int startFrame = 0;
            int endFrame = keyframes.length - 1;
            Vec3f keyLast = keyframes[endFrame].transform().translation();
            Vec3 pos = entitypatch.getOriginal().position();
            Vec3 targetpos = attackTarget.position();
            Vec3 toTarget = targetpos.subtract(pos);
            Vec3 viewVec = entitypatch.getOriginal().getViewVector(1.0F);
            float horizontalDistance = Math.max((float)toTarget.horizontalDistance() - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
            Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
            float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);

            if (scale > 1.0F) {
                float dot = (float)toTarget.normalize().dot(viewVec.normalize());
                scale = Math.max(scale * dot, 1.0F);
            }

            for (int i = startFrame; i <= endFrame; i++) {
                Vec3f translation = keyframes[i].transform().translation();

                if (translation.z < 0.0F) {
                    translation.z *= scale;
                }
            }

            transformSheet.readFrom(transform);
        } else {
            transformSheet.readFrom((self.isLinkAnimation())?self.getCoord().copyAll():cap.getCustomCoord().getTransformSheet().copyAll());
        }
    };

    public static final MoveCoordFunctions.MoveCoordSetter OLD_COMMON_COORD_SETTER = (self, entitypatch, transformSheet) -> {
        IEpicAddCapability cap = entitypatch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        LivingEntity attackTarget = entitypatch.getTarget();

        if (attackTarget != null) {
            TransformSheet transform = (self.isLinkAnimation())?self.getCoord().copyAll():cap.getCustomCoord().getTransformSheet().copyAll();
            Keyframe[] keyframes = transform.getKeyframes();
            int startFrame = 0;
            int endFrame = transform.getKeyframes().length - 1;
            Vec3f keyLast = keyframes[endFrame].transform().translation();
            Vec3 pos = entitypatch.getOriginal().getEyePosition(1.0F);
            Vec3 targetpos = attackTarget.position();
            //float horizontalDistance = Math.max((float) targetpos.subtract(pos).horizontalDistance() - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
            float horizontalDistance = Math.max((float) (targetpos.subtract(pos).horizontalDistance() - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F), 0.0F);
            Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);

            float scale = Math.min(worldPosition.length() / keyLast.length(), 1.0F);

            float nowTime=entitypatch.getAnimator().getPlayerFor(null).getElapsedTime();
            for (int i = startFrame; i <= endFrame; i++) {
                Vec3f translation = keyframes[i].transform().translation();
                float timeScale=Math.min(keyframes[i].time()/nowTime,1.0f);
                if (translation.z < 0.0F) {
                    translation.z *= Math.min(scale+scale*timeScale,1.0f);
                }
            }
            transformSheet.readFrom(transform);
        } else {
            transformSheet.readFrom((self.isLinkAnimation())?self.getCoord().copyAll():cap.getCustomCoord().getTransformSheet().copyAll());
        }
    };
    public static final MoveCoordFunctions.MoveCoordSetter OLD_NEARER_COMMON_COORD_SETTER = (self, entitypatch, transformSheet) -> {
        IEpicAddCapability cap = entitypatch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        LivingEntity attackTarget = entitypatch.getTarget();

        if (attackTarget != null) {
            TransformSheet transform = (self.isLinkAnimation())?self.getCoord().copyAll():cap.getCustomCoord().getTransformSheet().copyAll();
            Keyframe[] keyframes = transform.getKeyframes();
            int startFrame = 0;
            int endFrame = transform.getKeyframes().length - 1;
            Vec3f keyLast = keyframes[endFrame].transform().translation();
            Vec3 pos = entitypatch.getOriginal().getEyePosition(1.0F);
            Vec3 targetpos = attackTarget.position();
            //float horizontalDistance = Math.max((float) targetpos.subtract(pos).horizontalDistance() - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
            float horizontalDistance = Math.max((float) (targetpos.subtract(pos).horizontalDistance() - (attackTarget.getBbWidth()) * 0.5F), 0.0F);
            Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);

            float scale = Math.min(worldPosition.length() / keyLast.length(), 1.0F);

            float nowTime=entitypatch.getAnimator().getPlayerFor(null).getElapsedTime();
            for (int i = startFrame; i <= endFrame; i++) {
                Vec3f translation = keyframes[i].transform().translation();
                float timeScale=Math.min(keyframes[i].time()/nowTime,1.0f);
                if (translation.z < 0.0F) {
                    translation.z *= Math.min(scale+scale*timeScale,1.0f);
                }
            }
            transformSheet.readFrom(transform);
        } else {
            transformSheet.readFrom((self.isLinkAnimation())?self.getCoord().copyAll():cap.getCustomCoord().getTransformSheet().copyAll());
        }
    };


    public static MoveCoordFunctions.MoveCoordSetter getTkkMoveCoordSetter(int id){
        switch (id){
            default :
            case 0:
                return TKK_RAW_COORD;
            case 1:
                return OLD_COMMON_COORD_SETTER;
            case 2:
                return OLD_NEARER_COMMON_COORD_SETTER;
            case 3:
                return TKK_TRACE_LOC_TARGET;
            case 4:
                return TKK_TRACE_LOC_TARGET_NEARER;
        }
    }

    public final LivingEntity entity;

    public int moveType=0;

    public ArrayList<Keyframe> keyframes=new ArrayList<>();

    public ArrayList<Keyframe> readyKeyframes=null;

    public TransformSheet transformSheet=null;

    public boolean canStopMove=true;

    public CustomCoord(LivingEntity entity){
        this.entity=entity;
    }

    public void addKeyframe(float time,float moveX,float moveY,float moveZ) {
        addKeyframe(time,moveX,moveY,moveZ,0,0,0,1.0f);
    }
    public void addKeyframe(float time,float moveX,float moveY,float moveZ,float rotationX,float rotationY,float rotationZ,float rotationW) {
        addKeyframe(time,moveX,moveY,moveZ,rotationX,rotationY,rotationZ,rotationW,1.0f,1.0f,1.0f);
    }
    public void addKeyframe(float time,float moveX,float moveY,float moveZ,float rotationX,float rotationY,float rotationZ,float rotationW,float scaleX,float scaleY,float scaleZ){
        keyframes.add(new Keyframe(time,new JointTransform(new Vec3f(moveX,moveY,moveZ),new Quaternionf(rotationX,rotationY,rotationZ,rotationW),new Vec3f(scaleX,scaleY,scaleZ))));
    }
    public void addKeyframe(Keyframe keyframe){
        keyframes.add(keyframe);
    }



    public TransformSheet getTransformSheet() {
        if (readyKeyframes==null){return null;}
        if (transformSheet==null){
            transformSheet=new TransformSheet(readyKeyframes);
        }
        return transformSheet;
    }
    public void animationBegin(){
        transformSheet=null;
        if (keyframes.isEmpty()){
            readyKeyframes=null;
            moveType=0;
            canStopMove=true;
            return;
        }
        readyKeyframes=keyframes;
        keyframes=new ArrayList<>();
    }
    public void clear(){
        keyframes.clear();
    }

    public void update(){
        if(!(entity instanceof ServerPlayer)){return;}
        SPEpicAddCoordUpdata packet=new SPEpicAddCoordUpdata(this.entity.getId(),this);
        //TkkEpicNetworkManager.sendToAllPlayerTrackingThisEntity(packet,this.entity);
        TkkEpicNetworkManager.sendToPlayer(packet, (ServerPlayer) entity);

    };

}
