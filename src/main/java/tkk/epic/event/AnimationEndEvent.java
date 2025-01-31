package tkk.epic.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class AnimationEndEvent extends LivingEvent {
    public final LivingEntityPatch patch;
    public final DynamicAnimation nextAnimation;
    public final StaticAnimation animation;
    public final boolean isEnd;
    public AnimationEndEvent(StaticAnimation animation,LivingEntity entity, LivingEntityPatch patch,DynamicAnimation nextAnimation,boolean isEnd){
        super(entity);
        this.animation=animation;
        this.patch=patch;
        this.nextAnimation=nextAnimation;
        this.isEnd=isEnd;
    }


}
