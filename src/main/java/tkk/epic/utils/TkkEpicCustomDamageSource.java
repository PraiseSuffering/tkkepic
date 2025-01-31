package tkk.epic.utils;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

import java.util.HashMap;

public class TkkEpicCustomDamageSource extends EpicFightDamageSource {
    public float impact;
    public float armorNegation;
    public boolean finisher;
    public boolean canDodge;
    public boolean canBlock;
    public StunType stunType;
    public boolean isBasicAttack;
    public int animationId;
    public Vec3 initialPosition;
    public HashMap tempdata;
    public TkkEpicCustomDamageSource(Holder<DamageType> damageTypeIn, Entity directEntity, Entity damageSourceEntityIn, float impact, float armorNegation, boolean finisher, StunType stunType, boolean isBasicAttack, boolean canDodge, boolean canBlock, int animationId, Vec3 initialPosition, HashMap hashMap){
        super(damageTypeIn,directEntity,damageSourceEntityIn,initialPosition);
        this.impact=impact;
        this.armorNegation=armorNegation;
        this.finisher=finisher;
        this.stunType=stunType;
        this.isBasicAttack=isBasicAttack;
        this.animationId=animationId;
        this.initialPosition=initialPosition;
        this.tempdata=hashMap;
        this.canDodge=canDodge;
        this.canBlock=canBlock;
    }

    public void setCanDodge(boolean b){canDodge=b;}

    public boolean isCanDodge(){return canDodge;}

    public void setCanBlock(boolean b){canBlock=b;}

    public boolean isCanBlock(){return canBlock;}

    @Override
    public TkkEpicCustomDamageSource setImpact(float amount) {
        this.impact = amount;
        return this;
    }

    @Override
    public TkkEpicCustomDamageSource setArmorNegation(float amount) {
        this.armorNegation = amount;
        return this;
    }

    @Override
    public TkkEpicCustomDamageSource setStunType(StunType stunType) {
        this.stunType = stunType;
        return this;
    }


    @Override
    public TkkEpicCustomDamageSource setInitialPosition(Vec3 initialPosition) {
        this.initialPosition = initialPosition;
        return this;
    }

    @Override
    public float getImpact() {
        return this.impact;
    }

    @Override
    public float getArmorNegation() {
        return this.armorNegation;
    }

    @Override
    public StunType getStunType() {
        return this.stunType;
    }

    @Override
    public Entity getEntity() {
        return super.getEntity();
    }

    @Override
    public String getMsgId() {
        return super.getMsgId();
    }

    @Override
    public boolean isBasicAttack() {
        return isBasicAttack;
    }


    @Override
    public Vec3 getSourcePosition() {
        return this.initialPosition != null ? this.initialPosition : super.getSourcePosition();
    }
}
