package tkk.epic.capability.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class TkkAiTickEventGoal extends Goal {
    public final ITkkAiCapability cap;
    public TkkAiTickEventGoal(ITkkAiCapability cap){
        this.cap=cap;
    }
    @Override
    public boolean canUse() {
        if (cap.getEntity().level().isClientSide()){return false;}
        return cap.isEnable();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }
    @Override
    public void tick() {
        if (cap.getEntity().level().isClientSide()){return;}
        this.cap.aiTick();
    }
}
