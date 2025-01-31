package tkk.epic.capability.epicAdd.damageSource;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomDamageSource {
    public final LivingEntity entity;
    public boolean enable=false;
    public boolean newDamageSource=false;
    public Float adder = null;
    public Float multiplier = null;
    public Float setter = null;
    public ItemStack hurtItem = null;
    public Float impact = null;
    public Float armorNegation = null;
    public StunType stunType = null;
    public Set<ExtraDamageInstance> extraDamages = new HashSet<>();

    public HashSet<TagKey<DamageType>> runtimeTags = new HashSet<>();
    public HashSet<ResourceKey<DamageType>> runtimeTypes = new HashSet<>();

    /**If it is the same as the current tick, it is not cleared at the end of the action*/
    public int updateTick=-999;
    public CustomDamageSource(LivingEntity entity){
        this.entity=entity;
    }
    public void enable(){
        this.enable=true;
        this.updateTick=this.entity.tickCount;
    }
    public void setDamageModifier(Float set,float add,float scale){
        if (set==null){
            setter=Float.NaN;
        }else{
            setter=set;
        }
        adder=add;
        multiplier=scale;
    }
    public void setImpact(float set){this.impact=set;}
    public void setArmorNegation(float set){this.armorNegation=set;}
    public void setStunType(StunType type){this.stunType=type;}
    public void addExtraDamage(ExtraDamageInstance extraDamageInstance){
        this.extraDamages.add(extraDamageInstance);
    }

    public void clear(){
        enable=false;
        newDamageSource=false;
        adder = null;
        multiplier = null;
        setter = null;
        hurtItem = null;
        impact = null;
        armorNegation = null;
        stunType = null;
        updateTick=-999;
        extraDamages.clear();
        runtimeTags.clear();
        runtimeTypes.clear();

    }

    public EpicFightDamageSource getDamageSource(EpicFightDamageSource source){
        if (!enable){return source;}
        EpicFightDamageSource damageSource=source;
        if (newDamageSource){
            damageSource= EpicFightDamageSources.copy(source).setAnimation(source.getAnimation());
            damageSource.setInitialPosition(source.getInitialPosition());
        }
        if(adder!=null && multiplier!=null && setter!=null) {
            damageSource.setDamageModifier(
                    new ValueModifier(adder == null ? source.getDamageModifier().getAdder() : adder,
                    multiplier == null ? source.getDamageModifier().getMultiplier() : multiplier,
                    setter == null ? source.getDamageModifier().getSetter() : setter
            ));
        }
        if (hurtItem!=null){damageSource.setHurtItem(hurtItem);}
        if (impact!=null){damageSource.setImpact(impact);}
        if (armorNegation!=null){damageSource.setArmorNegation(armorNegation);}
        if (stunType!=null){damageSource.setStunType(stunType);}
        for (ExtraDamageInstance extraDamageInstance:extraDamages){
            damageSource.addExtraDamage(extraDamageInstance);
        }
        for (TagKey<DamageType> tag:runtimeTags){
            damageSource.addRuntimeTag(tag);
        }
        for (ResourceKey<DamageType> resourceKey:runtimeTypes){
            damageSource.addRuntimeTag(resourceKey);
        }
        return source;
    }

    /**
     * Function<Object[],float> extraDamageFN float [LivingEntity attacker, ItemStack itemStack, LivingEntity target, float baseDamage, float[] var5]
     * Consumer<Object[]> tooltipFN void [ItemStack var1, MutableComponent var2, double var3, float[] var5]
     * */
    public static ExtraDamageInstance.ExtraDamage createExtraDamage(final Function<Object[],Float> extraDamageFN,final Consumer<Object[]> tooltipFN){
        ExtraDamageInstance.ExtraDamageFunction extraDamage=new ExtraDamageInstance.ExtraDamageFunction() {
            @Override
            public float getBonusDamage(LivingEntity livingEntity, ItemStack itemStack, LivingEntity livingEntity1, float v, float[] floats) {
                return extraDamageFN.apply(new Object[]{livingEntity,itemStack,livingEntity1,v,floats});
            }
        };
        ExtraDamageInstance.ExtraDamageTooltipFunction tooltip=new ExtraDamageInstance.ExtraDamageTooltipFunction() {
            @Override
            public void setTooltip(ItemStack itemStack, MutableComponent mutableComponent, double v, float[] floats) {
                if (tooltipFN==null){return;}
                tooltipFN.accept(new Object[]{itemStack,mutableComponent,v,floats});
            }
        };
        return new ExtraDamageInstance.ExtraDamage(extraDamage,tooltip);
    }


}
