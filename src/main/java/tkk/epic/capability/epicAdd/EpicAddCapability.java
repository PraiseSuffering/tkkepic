package tkk.epic.capability.epicAdd;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import tkk.epic.capability.epicAdd.animationCoord.CustomCoord;
import tkk.epic.capability.epicAdd.animationTemplate.AnimationTemplateManager;
import tkk.epic.capability.epicAdd.attackSpeed.AttackSpeed;
import tkk.epic.capability.epicAdd.customMotionAnimation.CustomMotionAnimation;
import tkk.epic.capability.epicAdd.customStateSpectrum.CustomStateSpectrum;
import tkk.epic.capability.epicAdd.damageSource.CustomDamageSource;
import tkk.epic.capability.epicAdd.shouldBlockMoving.ShouldBlockMoving;
import tkk.epic.capability.epicAdd.trail.TkkCustomTrail;
import tkk.epic.network.SPEpicAddTrailUpdata;
import tkk.epic.network.TkkEpicNetworkManager;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class EpicAddCapability implements IEpicAddCapability  {
    public LivingEntity entity;
    public final TkkCustomTrail tkkCustomTrail;
    public final AttackSpeed attackSpeed;
    public final CustomDamageSource customDamageSource;
    public final CustomCoord customCoord;
    public final CustomStateSpectrum customStateSpectrum;
    public final AnimationTemplateManager animationTemplateManager;
    public final CustomMotionAnimation customMotionAnimation;
    public final ShouldBlockMoving shouldBlockMoving;
    public EpicAddCapability(LivingEntity entity){
        this.entity=entity;
        tkkCustomTrail=new TkkCustomTrail();
        attackSpeed=new AttackSpeed(entity);
        customDamageSource=new CustomDamageSource(entity);
        customCoord=new CustomCoord(entity);
        customStateSpectrum=new CustomStateSpectrum(entity);
        animationTemplateManager=new AnimationTemplateManager(entity);
        customMotionAnimation=new CustomMotionAnimation(entity);
        shouldBlockMoving=new ShouldBlockMoving(entity);
    }


    @Override
    public void addTrail(Vec3 start, Vec3 end, String joint, float startTime, float endTime, float fadeTime, float r, float g, float b, int interpolateCount, int trailLifetime, String texturePath, boolean hand, boolean scalePos) {
        tkkCustomTrail.addTrail(start,end,joint,startTime,endTime,fadeTime,r,g,b,interpolateCount,trailLifetime,texturePath,hand,scalePos);
    }
    @Override
    public void addParticleTrail(Vec3 start, Vec3 end, String joint, float startTime, float endTime, float fadeTime, int interpolateCount, int trailLifetime, boolean hand, boolean scalePos, String particle, String args, float spaceBetween, float speed, double dist, int count){
        tkkCustomTrail.addParticleTrail(start,end,joint,startTime,endTime,fadeTime,interpolateCount,trailLifetime,hand,scalePos,new ResourceLocation(particle),args,spaceBetween,speed,dist,count);
    }

    @Override
    public void setDoVanillaTrail(boolean enable) {
        tkkCustomTrail.setDoVanillaTrail(enable);
    }

    @Override
    public void clearTrail() {
        tkkCustomTrail.clear();
    }

    @Override
    public void updateTrail() {
        SPEpicAddTrailUpdata msg=new SPEpicAddTrailUpdata(entity.getId(),tkkCustomTrail);
        TkkEpicNetworkManager.sendToAllPlayerTrackingThisEntity(msg,entity);
        if(entity instanceof ServerPlayer){
            TkkEpicNetworkManager.sendToPlayer(msg, (ServerPlayer) entity);
        }
    }

    @Override
    public TkkCustomTrail getTkkCustomTrail() {
        return tkkCustomTrail;
    }

    @Override
    public boolean isCustomAttackSpeed() {
        return this.attackSpeed.isCustomAttackSpeed();
    }

    @Override
    public void setAttackSpeed(Float speed) {
        this.attackSpeed.setAttackSpeed(speed);
    }

    @Override
    public float getAttackSpeed(InteractionHand hand) {
        return this.attackSpeed.getAttackSpeed(hand);
    }

    @Override
    public void setModifiersAttackSpeed(int level, Float set, Float add, Float scale) {
        this.attackSpeed.setModifiersAttackSpeed(level,set,add,scale);
    }

    @Override
    public void updateAttackSpeed() {
        this.attackSpeed.updateAttackSpeed();
    }

    @Override
    public void clearModifiersAttackSpeed(){
        this.attackSpeed.clearModifiersAttackSpeed();
    }

    @Override
    public AttackSpeed getAttackSpeedObject(){
        return this.attackSpeed;
    }

    @Override
    public CustomDamageSource getCDS(){
        return this.customDamageSource;
    }

    @Override
    public CustomCoord getCustomCoord() {
        return this.customCoord;
    }

    @Override
    public void setCoordMoveType(int type){
        customCoord.moveType=type;
    }

    @Override
    public void addCoordMove(float time, float x, float y, float z) {
        customCoord.addKeyframe(time,x,y,z);
    }

    @Override
    public void clearCoord() {
        customCoord.clear();
    }

    @Override
    public void updateCustomCoord() {
        customCoord.update();
    }

    @Override
    public CustomStateSpectrum getCustomStateSpectrum() {
        return customStateSpectrum;
    }

    @Override
    public void addSimpleState(float start, float end, String stateFactor, Object value) {
        customStateSpectrum.addSimpleState(start,end,stateFactor,value);
    }

    @Override
    public void updataCustomStateSpectrum() {
        customStateSpectrum.updataCustomStateSpectrum();
    }

    @Override
    public AnimationTemplateManager getAnimationTemplateManager(){
        return animationTemplateManager;
    }

    @Override
    public CustomMotionAnimation getCustomMotionAnimation(){
        return customMotionAnimation;
    }

    @Override
    public ShouldBlockMoving getShouldBlockMoving(){return shouldBlockMoving;}
}
