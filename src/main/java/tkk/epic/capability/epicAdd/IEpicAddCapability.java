package tkk.epic.capability.epicAdd;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import tkk.epic.capability.epicAdd.animationCoord.CustomCoord;
import tkk.epic.capability.epicAdd.animationTemplate.AnimationTemplateManager;
import tkk.epic.capability.epicAdd.attackSpeed.AttackSpeed;
import tkk.epic.capability.epicAdd.customMotionAnimation.CustomMotionAnimation;
import tkk.epic.capability.epicAdd.customStateSpectrum.CustomStateSpectrum;
import tkk.epic.capability.epicAdd.damageSource.CustomDamageSource;
import tkk.epic.capability.epicAdd.shouldBlockMoving.ShouldBlockMoving;
import tkk.epic.capability.epicAdd.trail.TkkCustomTrail;
import tkk.epic.skill.SkillContainer;

public interface IEpicAddCapability {

    void addTrail(Vec3 start,Vec3 end,String joint,float startTime,float endTime,float fadeTime,float r,float g,float b,int interpolateCount,int trailLifetime,String texturePath,boolean hand,boolean scalePos);
    void addParticleTrail(Vec3 start, Vec3 end, String joint, float startTime, float endTime, float fadeTime, int interpolateCount, int trailLifetime, boolean hand, boolean scalePos, String particle, String args, float spaceBetween, float speed, double dist, int count);

    void setDoVanillaTrail(boolean enable);

    void clearTrail();

    /**
     * playAnimation pre
     * */
    void updateTrail();

    TkkCustomTrail getTkkCustomTrail();

    /**如果有设置的攻速,或者动作攻速则true,否则false*/
    boolean isCustomAttackSpeed();
    /**null为清除,该设置不会随动作结束而清空*/
    void setAttackSpeed(Float speed);

    float getAttackSpeed(InteractionHand hand);

    /**level */
    void setModifiersAttackSpeed(int level,Float set,Float add,Float scale);

    /**
     * playAnimation pre|post
     * */
    void updateAttackSpeed();

    void clearModifiersAttackSpeed();
    AttackSpeed getAttackSpeedObject();

    CustomDamageSource getCDS();

    CustomCoord getCustomCoord();

    /**
     default:0
     0:RAW_COORD
     1:TRACE_LOC_TARGET
     */
    void setCoordMoveType(int type);

    void addCoordMove(float time,float x,float y,float z);

    void clearCoord();

    /**
     * playAnimation pre
     * */
    void updateCustomCoord();

    CustomStateSpectrum getCustomStateSpectrum();

    void addSimpleState(float start,float end,String stateFactor,Object value);

    /**
     * playAnimation pre|post
     * */
    void updataCustomStateSpectrum();

    AnimationTemplateManager getAnimationTemplateManager();

    CustomMotionAnimation getCustomMotionAnimation();

    ShouldBlockMoving getShouldBlockMoving();
}
