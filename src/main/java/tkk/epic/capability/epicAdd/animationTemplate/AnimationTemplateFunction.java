package tkk.epic.capability.epicAdd.animationTemplate;

import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.registries.ForgeRegistries;
import tkk.epic.TkkEpic;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.capability.epicAdd.customStateSpectrum.CustomStateSpectrum;
import tkk.epic.capability.epicAdd.damageSource.CustomDamageSource;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

public class AnimationTemplateFunction {

    public static Consumer<Object[]> customTrail_FN_prePlay=(args)->{
        AnimationTemplate animationTemplate= (AnimationTemplate) args[0];
        LivingEntity entity= (LivingEntity) args[1];
        IEpicAddCapability epicAddCap=entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);

        epicAddCap.clearTrail();
        boolean doVanillaTrail= (boolean) animationTemplate.data.get("customTrail_doVanilla");
        epicAddCap.setDoVanillaTrail(doVanillaTrail);

        ArrayList<Object[]> trails= (ArrayList<Object[]>) animationTemplate.data.get("customTrail_trail");
        for (Object[] trailArgs:trails){
            epicAddCap.addTrail((Vec3) trailArgs[0],(Vec3) trailArgs[1],(String) trailArgs[2],(float) trailArgs[3],(float) trailArgs[4],(float) trailArgs[5],(float) trailArgs[6],(float) trailArgs[7],(float) trailArgs[8],(int) trailArgs[9],(int) trailArgs[10],(String) trailArgs[11],(boolean) trailArgs[12],(boolean) trailArgs[13]);
        }

        ArrayList<Object[]> particleTrails= (ArrayList<Object[]>) animationTemplate.data.get("customTrail_particleTrail");
        for (Object[] trailArgs:particleTrails){
            epicAddCap.addParticleTrail((Vec3) trailArgs[0],(Vec3) trailArgs[1],(String) trailArgs[2],(float) trailArgs[3],(float) trailArgs[4],(float) trailArgs[5],(int) trailArgs[6],(int) trailArgs[7],(boolean) trailArgs[8],(boolean) trailArgs[9],(String) trailArgs[10],(String) trailArgs[11],(float) trailArgs[12],(float) trailArgs[13],(double) trailArgs[14],(int) trailArgs[15]);
        }



        epicAddCap.updateTrail();
    };
    public static Consumer<Object[]> customDamage_FN_prePlay=(args)->{
        AnimationTemplate animationTemplate= (AnimationTemplate) args[0];
        LivingEntity entity= (LivingEntity) args[1];
        IEpicAddCapability epicAddCap=entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);

        CustomDamageSource cds=epicAddCap.getCDS();

        cds.enable();

        if(animationTemplate.data.containsKey("customDamage_set")){cds.setDamageModifier((Float) animationTemplate.data.get("customDamage_set"),(float) animationTemplate.data.get("customDamage_add"),(float) animationTemplate.data.get("customDamage_scale"));};
        if(animationTemplate.data.containsKey("customDamage_armorNegation")){cds.setArmorNegation((Float) animationTemplate.data.get("customDamage_armorNegation"));};
        if(animationTemplate.data.containsKey("customDamage_impact")){cds.setImpact((Float) animationTemplate.data.get("customDamage_impact"));};
        if(animationTemplate.data.containsKey("customDamage_stunType")){cds.setStunType((StunType) animationTemplate.data.get("customDamage_stunType"));};
        if(animationTemplate.data.containsKey("customDamage_extraDamageInstances")) {
            ArrayList<ExtraDamageInstance> extraDamageInstances = (ArrayList<ExtraDamageInstance>) animationTemplate.data.get("customDamage_extraDamageInstances");
            for (ExtraDamageInstance extraDamageInstance : extraDamageInstances) {
                cds.addExtraDamage(extraDamageInstance);
            }
        }
        if(animationTemplate.data.containsKey("customDamage_tagKeyDamageType")) {
            ArrayList<TagKey<DamageType>> tagKeys = (ArrayList<TagKey<DamageType>>) animationTemplate.data.get("customDamage_tagKeyDamageType");
            for (TagKey<DamageType> tagKey : tagKeys) {
                cds.runtimeTags.add(tagKey);
            }
        }
        if(animationTemplate.data.containsKey("customDamage_resourceKeyDamageType")) {
            ArrayList<ResourceKey<DamageType>> resourceKeys = (ArrayList<ResourceKey<DamageType>>) animationTemplate.data.get("customDamage_resourceKeyDamageType");
            for (ResourceKey<DamageType> resourceKey : resourceKeys) {
                cds.runtimeTypes.add(resourceKey);
            }
        }

    };
    public static Consumer<Object[]> customStateSpectrum_FN_prePlay=(args)->{
        AnimationTemplate animationTemplate= (AnimationTemplate) args[0];
        LivingEntity entity= (LivingEntity) args[1];
        IEpicAddCapability epicAddCap=entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        epicAddCap.getCustomStateSpectrum().clearTimePairs();
        if(animationTemplate.data.containsKey("customStateSpectrum_stateSpectrum")) {
            ArrayList<Object[]> stateSpectrumArgs = (ArrayList<Object[]>) animationTemplate.data.get("customStateSpectrum_stateSpectrum");
            for (Object[] arg : stateSpectrumArgs) {
                epicAddCap.addSimpleState((float)arg[0],(float)arg[1], (String) arg[2],arg[3]);
            }
        }


        epicAddCap.updataCustomStateSpectrum();
    };
    public static Consumer<Object[]> customAttackSpeed_FN_postPlay=(args)->{
        AnimationTemplate animationTemplate= (AnimationTemplate) args[0];
        LivingEntity entity= (LivingEntity) args[1];
        IEpicAddCapability epicAddCap=entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);

        if(animationTemplate.data.containsKey("customAttackSpeed_1")) {
            Float[] arg= (Float[]) animationTemplate.data.get("customAttackSpeed_1");
            epicAddCap.setModifiersAttackSpeed(1,arg[0],arg[1],arg[2]);
        }
        if(animationTemplate.data.containsKey("customAttackSpeed_2")) {
            Float[] arg= (Float[]) animationTemplate.data.get("customAttackSpeed_2");
            epicAddCap.setModifiersAttackSpeed(2,arg[0],arg[1],arg[2]);
        }
        if(animationTemplate.data.containsKey("customAttackSpeed_3")) {
            Float[] arg= (Float[]) animationTemplate.data.get("customAttackSpeed_3");
            epicAddCap.setModifiersAttackSpeed(3,arg[0],arg[1],arg[2]);
        }
        epicAddCap.updateAttackSpeed();
    };
    public static Consumer<Object[]> customCoord_FN_prePlay=(args)->{
        AnimationTemplate animationTemplate= (AnimationTemplate) args[0];
        LivingEntity entity= (LivingEntity) args[1];
        IEpicAddCapability epicAddCap=entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if(animationTemplate.data.containsKey("customCoord_moveType")) {
            epicAddCap.setCoordMoveType((Integer) animationTemplate.data.get("customCoord_moveType"));
        }
        if(animationTemplate.data.containsKey("customCoord_args")) {
            ArrayList<float[]> stateSpectrumArgs = (ArrayList<float[]>) animationTemplate.data.get("customCoord_args");
            for (float[] arg : stateSpectrumArgs) {
                epicAddCap.addCoordMove(arg[0],arg[1],arg[2],arg[3]);
            }
        }
        epicAddCap.getCustomCoord().canStopMove=(Boolean)animationTemplate.data.get("customCoord_canStopMove");
        epicAddCap.updateCustomCoord();
    };
    public static Consumer<Object[]> customPotion_FN_prePlay=(args)->{
        AnimationTemplate animationTemplate= (AnimationTemplate) args[0];
        LivingEntity entity= (LivingEntity) args[1];
        IEpicAddCapability epicAddCap=entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if(animationTemplate.data.containsKey("customPotion_start")) {
            ArrayList<Object[]> startArgs = (ArrayList<Object[]>) animationTemplate.data.get("customPotion_start");
            for (Object[] arg : startArgs) {
                addEffectForLivingEntity(entity,(String) arg[0],(int)arg[1],(int)arg[2],(boolean) arg[3],entity);
            }
        }
    };
    public static Consumer<Object[]> customPotion_FN_onHurt=(args)->{
        AnimationTemplate animationTemplate= (AnimationTemplate) args[0];
        LivingEntity entity= (LivingEntity) args[1];
        LivingHurtEvent event= (LivingHurtEvent) args[2];
        IEpicAddCapability epicAddCap=entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        Entity target=event.getEntity();
        if (target!=null && target instanceof LivingEntity && animationTemplate.data.containsKey("customPotion_hitTarget")){
            ArrayList<Object[]> hitTargetEffects = (ArrayList<Object[]>) animationTemplate.data.get("customPotion_hitTarget");
            for (Object[] arg : hitTargetEffects) {
                addEffectForLivingEntity((LivingEntity) target,(String) arg[0],(int)arg[1],(int)arg[2],(boolean) arg[3],entity);
            }
        }
        if(animationTemplate.data.containsKey("customPotion_hitSelf")) {
            ArrayList<Object[]> hitSelfEffects = (ArrayList<Object[]>) animationTemplate.data.get("customPotion_hitSelf");
            for (Object[] arg : hitSelfEffects) {
                addEffectForLivingEntity(entity,(String) arg[0],(int)arg[1],(int)arg[2],(boolean) arg[3],entity);
            }
        }
    };
    public static Consumer<Object[]> customPotion_FN_beHurt=(args)->{
        AnimationTemplate animationTemplate= (AnimationTemplate) args[0];
        LivingEntity entity= (LivingEntity) args[1];
        LivingHurtEvent event= (LivingHurtEvent) args[2];
        IEpicAddCapability epicAddCap=entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        Entity target=event.getSource().getEntity();
        if (target!=null && target instanceof LivingEntity && animationTemplate.data.containsKey("customPotion_hurtTarget")){
            ArrayList<Object[]> hitTargetEffects = (ArrayList<Object[]>) animationTemplate.data.get("customPotion_hurtTarget");
            for (Object[] arg : hitTargetEffects) {
                addEffectForLivingEntity((LivingEntity) target,(String) arg[0],(int)arg[1],(int)arg[2],(boolean) arg[3],entity);
            }
        }
        if(animationTemplate.data.containsKey("customPotion_hurtSelf")) {
            ArrayList<Object[]> hitSelfEffects = (ArrayList<Object[]>) animationTemplate.data.get("customPotion_hurtSelf");
            for (Object[] arg : hitSelfEffects) {
                addEffectForLivingEntity(entity,(String) arg[0],(int)arg[1],(int)arg[2],(boolean) arg[3],entity);
            }
        }
    };
    public static Consumer<Object[]> customPotion_FN_preEnd=(args)->{
        AnimationTemplate animationTemplate= (AnimationTemplate) args[0];
        LivingEntity entity= (LivingEntity) args[1];
        boolean isBreak= (boolean) args[2];
        IEpicAddCapability epicAddCap=entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if(isBreak){
            if(animationTemplate.data.containsKey("customPotion_break")) {
                ArrayList<Object[]> effects = (ArrayList<Object[]>) animationTemplate.data.get("customPotion_break");
                for (Object[] arg : effects) {
                    addEffectForLivingEntity(entity,(String) arg[0],(int)arg[1],(int)arg[2],(boolean) arg[3],entity);
                }
            }
        }else{
            if(animationTemplate.data.containsKey("customPotion_end")) {
                ArrayList<Object[]> effects = (ArrayList<Object[]>) animationTemplate.data.get("customPotion_end");
                for (Object[] arg : effects) {
                    addEffectForLivingEntity(entity,(String) arg[0],(int)arg[1],(int)arg[2],(boolean) arg[3],entity);
                }
            }
        }
    };
    public static Consumer<Object[]> addWeaponSkillConsumption_FN_onHurt=(args)->{
        AnimationTemplate animationTemplate= (AnimationTemplate) args[0];
        LivingEntity entity= (LivingEntity) args[1];
        LivingHurtEvent event= (LivingHurtEvent) args[2];
        IEpicAddCapability epicAddCap=entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        Entity target=event.getEntity();
        if (entity instanceof ServerPlayer) {
            if (animationTemplate.data.containsKey("addWeaponSkillConsumption")) {
                Float[] consumption = (Float[]) animationTemplate.data.get("addWeaponSkillConsumption");
                ServerPlayerPatch playerPatch=(ServerPlayerPatch) entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
                SkillContainer skillContainer = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                skillContainer.getSkill().setConsumptionSynchronize(playerPatch,skillContainer.getResource()+consumption[0]+(consumption[1]*event.getAmount()));
            }
        }
    };


    public static void addEffectForLivingEntity(LivingEntity entity, String effect, int tick, int amplifier, boolean visible, @Nullable Entity source){
        MobEffect mobEffect=ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effect));
        if (mobEffect==null){return;}
        entity.addEffect(new MobEffectInstance(mobEffect,tick,amplifier,false,visible),source);

    }


    public static void customTrail_addTrail(AnimationTemplate template,Vec3 start,Vec3 end,String joint,float startTime,float endTime,float fadeTime,float r,float g,float b,int interpolateCount,int trailLifetime,String texturePath,boolean hand,boolean scalePos){
        if (!template.data.containsKey("customTrail_trail")){
            template.data.put("customTrail_trail",new ArrayList<>());
            template.data.put("customTrail_particleTrail",new ArrayList<>());
            template.data.put("customTrail_doVanilla",false);
            template.prePlayFN.normal.add(customTrail_FN_prePlay);
        }
        ArrayList<Object[]> trails= (ArrayList<Object[]>) template.data.get("customTrail_trail");
        trails.add(new Object[]{start,end,joint,startTime,endTime,fadeTime,r,g,b,interpolateCount,trailLifetime,texturePath,hand,scalePos});
    }
    public static void customTrail_addTrail(AnimationTemplate template,double start_x,double start_y,double start_z,double end_x,double end_y,double end_z,String joint,float startTime,float endTime,float fadeTime,float r,float g,float b,int interpolateCount,int trailLifetime,String texturePath,boolean hand,boolean scalePos){
        AnimationTemplateFunction.customTrail_addTrail(template,new Vec3(start_x,start_y,start_z),new Vec3(end_x,end_y,end_z),joint,startTime,endTime,fadeTime,r,g,b,interpolateCount,trailLifetime,texturePath,hand,scalePos);
    }
    public static void customTrail_addTrail(AnimationTemplate template,double minZ,double maxZ,float startTime,float endTime,float fadeTime,float r,float g,float b,int interpolateCount,int trailLifetime,String texturePath,boolean hand){
        String joint=(hand)?"Tool_R":"Tool_L";
        AnimationTemplateFunction.customTrail_addTrail(template,new Vec3(1,1,minZ),new Vec3(1,1,maxZ),joint,startTime,endTime,fadeTime,r,g,b,interpolateCount,trailLifetime,texturePath,hand,true);
    }
    public static void customTrail_addParticleTrail(AnimationTemplate template,Vec3 start, Vec3 end, String joint, float startTime, float endTime, float fadeTime, int interpolateCount, int trailLifetime, boolean hand, boolean scalePos, String particle, String args, float spaceBetween, float speed, double dist, int count){
        if (!template.data.containsKey("customTrail_trail")){
            template.data.put("customTrail_trail",new ArrayList<>());
            template.data.put("customTrail_particleTrail",new ArrayList<>());
            template.data.put("customTrail_doVanilla",false);
            template.prePlayFN.normal.add(customTrail_FN_prePlay);
        }
        ArrayList<Object[]> trails= (ArrayList<Object[]>) template.data.get("customTrail_particleTrail");
        trails.add(new Object[]{start,end,joint,startTime,endTime,fadeTime,interpolateCount,trailLifetime,hand,scalePos,particle,args,spaceBetween,speed,dist,count});
    }
    public static void customTrail_addParticleTrail(AnimationTemplate template,double start_x,double start_y,double start_z,double end_x,double end_y,double end_z, String joint, float startTime, float endTime, float fadeTime, int interpolateCount, int trailLifetime, boolean hand, boolean scalePos, String particle, String args, float spaceBetween, float speed, double dist, int count) {
        AnimationTemplateFunction.customTrail_addParticleTrail(template,new Vec3(start_x,start_y,start_z),new Vec3(end_x,end_y,end_z),joint,startTime,endTime,fadeTime,interpolateCount,trailLifetime,hand,scalePos,particle,args,spaceBetween,speed,dist,count);
    }
    public static void customTrail_addParticleTrail(AnimationTemplate template,double minZ,double maxZ, float startTime, float endTime, float fadeTime, int interpolateCount, int trailLifetime, boolean hand, String particle, String args, float spaceBetween, float speed, double dist, int count) {
        String joint=(hand)?"Tool_R":"Tool_L";
        AnimationTemplateFunction.customTrail_addParticleTrail(template,new Vec3(1,1,minZ),new Vec3(1,1,maxZ),joint,startTime,endTime,fadeTime,interpolateCount,trailLifetime,hand,true,particle,args,spaceBetween,speed,dist,count);
    }
    public static void customTrail_setDoVanillaTrail(AnimationTemplate template,boolean doTrail){
        if (!template.data.containsKey("customTrail_trail")){
            template.data.put("customTrail_trail",new ArrayList<>());
            template.data.put("customTrail_particleTrail",new ArrayList<>());
            template.prePlayFN.normal.add(customTrail_FN_prePlay);
        }
        template.data.put("customTrail_doVanilla",doTrail);
    }

    public static void customDamage_addFN(AnimationTemplate template){
        if (!template.data.containsKey("customDamage_FN")){
            template.data.put("customDamage_FN",true);
            template.prePlayFN.normal.add(customDamage_FN_prePlay);
        }
    }
    /**scale does not take effect on ExtraDamage*/
    public static void customDamage_damage(AnimationTemplate template,Float set,float add,float scale){
        customDamage_addFN(template);
        template.data.put("customDamage_set",set);
        template.data.put("customDamage_add",add);
        template.data.put("customDamage_scale",scale);
    }
    public static void customDamage_armorNegation(AnimationTemplate template,Float set){
        customDamage_addFN(template);
        template.data.put("customDamage_armorNegation",set);
    }
    public static void customDamage_impact(AnimationTemplate template,Float set){
        customDamage_addFN(template);
        template.data.put("customDamage_impact",set);
    }
    public static void customDamage_stunType(AnimationTemplate template,String type){
        customDamage_addFN(template);
        template.data.put("customDamage_stunType",StunType.valueOf(type));
    }
    public static void customDamage_addExtraDamage(AnimationTemplate template,final Function<Object[],Float> extraDamageFN, final Consumer<Object[]> tooltipFN){
        customDamage_addFN(template);
        if (!template.data.containsKey("customDamage_extraDamageInstances")){
            template.data.put("customDamage_extraDamageInstances",new ArrayList<>());
        }
        ArrayList<ExtraDamageInstance> extraDamageInstances = (ArrayList<ExtraDamageInstance>) template.data.get("customDamage_extraDamageInstances");
        extraDamageInstances.add(CustomDamageSource.createExtraDamage(extraDamageFN,tooltipFN).create());
    }
    public static void customDamage_addCurrentLifeDamage(AnimationTemplate template,float value){
        customDamage_addExtraDamage(template,(args)->{
            LivingEntity attacker= (LivingEntity) args[0];
            ItemStack itemStack= (ItemStack) args[1];
            LivingEntity target= (LivingEntity) args[2];
            float baseDamage= (float) args[3];
            float[] floats= (float[]) args[4];
            return target.getHealth()*value;
        },null);
    }
    public static void customDamage_addMaximumLifeDamage(AnimationTemplate template,float value){
        customDamage_addExtraDamage(template,(args)->{
            LivingEntity attacker= (LivingEntity) args[0];
            ItemStack itemStack= (ItemStack) args[1];
            LivingEntity target= (LivingEntity) args[2];
            float baseDamage= (float) args[3];
            float[] floats= (float[]) args[4];
            return target.getMaxHealth()*value;
        },null);
    }
    public static void customDamage_adLostLifeDamage(AnimationTemplate template,float value){
        customDamage_addExtraDamage(template,(args)->{
            LivingEntity attacker= (LivingEntity) args[0];
            ItemStack itemStack= (ItemStack) args[1];
            LivingEntity target= (LivingEntity) args[2];
            float baseDamage= (float) args[3];
            float[] floats= (float[]) args[4];
            return (target.getMaxHealth()-target.getHealth())*value;
        },null);
    }
    public static void customDamage_addTagKeyDamageType(AnimationTemplate template,TagKey<DamageType> tagKey){
        customDamage_addFN(template);
        if (!template.data.containsKey("customDamage_tagKeyDamageType")){
            template.data.put("customDamage_tagKeyDamageType",new ArrayList<>());
        }
        ArrayList<TagKey<DamageType>> tagKeys = (ArrayList<TagKey<DamageType>>) template.data.get("customDamage_tagKeyDamageType");
        tagKeys.add(tagKey);
    }
    public static void customDamage_addTagKeyDamageType(AnimationTemplate template,String tagKey){
        customDamage_addTagKeyDamageType(template,TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(tagKey)));
    }
    public static void customDamage_addResourceKeyDamageType(AnimationTemplate template,ResourceKey<DamageType> resourceKey){
        customDamage_addFN(template);
        if (!template.data.containsKey("customDamage_resourceKeyDamageType")){
            template.data.put("customDamage_resourceKeyDamageType",new ArrayList<>());
        }
        ArrayList<ResourceKey<DamageType>> resourceKeys = (ArrayList<ResourceKey<DamageType>>) template.data.get("customDamage_resourceKeyDamageType");
        resourceKeys.add(resourceKey);
    }
    public static void customDamage_addResourceKeyDamageType(AnimationTemplate template,String tagKey){
        customDamage_addResourceKeyDamageType(template,ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(tagKey)));
    }

    public static void customStateSpectrum_addFN(AnimationTemplate template){
        if (!template.data.containsKey("customStateSpectrum_stateSpectrum")){
            template.data.put("customStateSpectrum_stateSpectrum",new ArrayList<>());
            template.prePlayFN.normal.add(customStateSpectrum_FN_prePlay);
        }
    }
    public static void customStateSpectrum_add(AnimationTemplate template,float start,float end,String stateFactor,Object value){
        customStateSpectrum_addFN(template);
        ArrayList<Object[]> stateSpectrumArgs = (ArrayList<Object[]>) template.data.get("customStateSpectrum_stateSpectrum");
        stateSpectrumArgs.add(new Object[]{start,end,stateFactor,value});
    }

    public static void customAttackSpeed_addFN(AnimationTemplate template){
        if (!template.data.containsKey("customAttackSpeed_FN")){
            template.data.put("customAttackSpeed_FN",true);
            template.postPlayFN.normal.add(customAttackSpeed_FN_postPlay);
        }
    }
    public static void customAttackSpeed_anticipation(AnimationTemplate template,Float set,Float add,Float scale){
        customAttackSpeed_addFN(template);
        template.data.put("customAttackSpeed_1",new Float[]{set,add,scale});
    }
    public static void customAttackSpeed_attacking(AnimationTemplate template,Float set,Float add,Float scale){
        customAttackSpeed_addFN(template);
        template.data.put("customAttackSpeed_2",new Float[]{set,add,scale});
    }
    public static void customAttackSpeed_recovery(AnimationTemplate template,Float set,Float add,Float scale){
        customAttackSpeed_addFN(template);
        template.data.put("customAttackSpeed_3",new Float[]{set,add,scale});
    }

    public static void customCoord_addFN(AnimationTemplate template){
        if (!template.data.containsKey("customCoord_args")){
            template.data.put("customCoord_args",new ArrayList<>());
            template.data.put("customCoord_moveType",0);
            template.data.put("customCoord_canStopMove",true);
            template.prePlayFN.normal.add(customCoord_FN_prePlay);
        }
    }
    public static void customCoord_add(AnimationTemplate template,float time,float x,float y,float z){
        customCoord_addFN(template);
        ((ArrayList )template.data.get("customCoord_args")).add(new float[]{time,x,y,z});

    }
    public static void customCoord_setMoveType(AnimationTemplate template,int type){
        customCoord_addFN(template);
        template.data.put("customCoord_moveType",type);
    }
    public static void customCoord_setCanStopMove(AnimationTemplate template,boolean canStopMove){
        customCoord_addFN(template);
        template.data.put("customCoord_canStopMove",canStopMove);
    }


    public static void customPotion_addFN(AnimationTemplate template){
        if (!template.data.containsKey("customPotion_FN")){
            template.data.put("customPotion_FN",true);
            template.prePlayFN.normal.add(customPotion_FN_prePlay);
            template.onHurtFN.normal.add(customPotion_FN_onHurt);
            template.beHurtFN.normal.add(customPotion_FN_beHurt);
            template.preEndFN.normal.add(customPotion_FN_preEnd);
        }
    }
    public static void customPotion_addStart(AnimationTemplate template,String effect, int tick, int amplifier, boolean visible){
        customPotion_addFN(template);
        template.data.putIfAbsent("customPotion_start",new ArrayList<>());
        ((ArrayList )template.data.get("customPotion_start")).add(new Object[]{effect,tick,amplifier,visible});

    }
    public static void customPotion_addHitTarget(AnimationTemplate template,String effect, int tick, int amplifier, boolean visible){
        customPotion_addFN(template);
        template.data.putIfAbsent("customPotion_hitTarget",new ArrayList<>());
        ((ArrayList )template.data.get("customPotion_hitTarget")).add(new Object[]{effect,tick,amplifier,visible});

    }
    public static void customPotion_addHitSelf(AnimationTemplate template,String effect, int tick, int amplifier, boolean visible){
        customPotion_addFN(template);
        template.data.putIfAbsent("customPotion_hitSelf",new ArrayList<>());
        ((ArrayList )template.data.get("customPotion_hitSelf")).add(new Object[]{effect,tick,amplifier,visible});

    }
    public static void customPotion_addHurtTarget(AnimationTemplate template,String effect, int tick, int amplifier, boolean visible){
        customPotion_addFN(template);
        template.data.putIfAbsent("customPotion_hurtTarget",new ArrayList<>());
        ((ArrayList )template.data.get("customPotion_hurtTarget")).add(new Object[]{effect,tick,amplifier,visible});

    }
    public static void customPotion_addHurtSelf(AnimationTemplate template,String effect, int tick, int amplifier, boolean visible){
        customPotion_addFN(template);
        template.data.putIfAbsent("customPotion_hurtSelf",new ArrayList<>());
        ((ArrayList )template.data.get("customPotion_hurtSelf")).add(new Object[]{effect,tick,amplifier,visible});

    }
    public static void customPotion_addEnd(AnimationTemplate template,String effect, int tick, int amplifier, boolean visible){
        customPotion_addFN(template);
        template.data.putIfAbsent("customPotion_end",new ArrayList<>());
        ((ArrayList )template.data.get("customPotion_end")).add(new Object[]{effect,tick,amplifier,visible});

    }
    public static void customPotion_addBreak(AnimationTemplate template,String effect, int tick, int amplifier, boolean visible){
        customPotion_addFN(template);
        template.data.putIfAbsent("customPotion_break",new ArrayList<>());
        ((ArrayList )template.data.get("customPotion_break")).add(new Object[]{effect,tick,amplifier,visible});
    }
    public static void customPotion_addEndAndBreak(AnimationTemplate template,String effect, int tick, int amplifier, boolean visible){
        customPotion_addEnd(template,effect,tick,amplifier,visible);
        customPotion_addBreak(template,effect,tick,amplifier,visible);
    }

    public static void addWeaponSkillConsumption_addFN(AnimationTemplate template){
        if (!template.data.containsKey("addWeaponSkillConsumption")){
            template.data.put("addWeaponSkillConsumption",new Float[]{0.0f,1.0f});
            template.onHurtFN.low.add(addWeaponSkillConsumption_FN_onHurt);
        }
    }
    public static void addWeaponSkillConsumption_add(AnimationTemplate template,Float add,Float damageScale){
        addWeaponSkillConsumption_addFN(template);
        template.data.put("addWeaponSkillConsumption",new Float[]{add,damageScale});
    }

}
