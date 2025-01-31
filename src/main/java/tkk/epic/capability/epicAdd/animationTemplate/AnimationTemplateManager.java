package tkk.epic.capability.epicAdd.animationTemplate;

import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;

public class AnimationTemplateManager {
    public final LivingEntity entity;
    public AnimationTemplate nowAnimationTemplate=null;
    public boolean playTick=false;
    public HashMap data=new HashMap();

    public AnimationTemplateManager(LivingEntity entity){
        this.entity=entity;
    }

    public AnimationTemplate getNow(){
        return nowAnimationTemplate;
    };
    public void setNow(AnimationTemplate iAnimationTemplate){
        this.nowAnimationTemplate=iAnimationTemplate;
    };
}
