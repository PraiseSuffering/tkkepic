package tkk.epic.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tkk.epic.TkkEpic;
import tkk.epic.event.ExecuteSkillEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.function.Supplier;

@Mixin(value = CPExecuteSkill.class, remap = true)
public abstract class MixinCPExecuteSkill {


    @Shadow @Final private int skillSlot;

    @Accessor("skillSlot")
    public abstract int getSkillSlot();

    @Accessor("workType")
    public abstract CPExecuteSkill.WorkType getWorkType();

    @Accessor("buffer")
    public abstract FriendlyByteBuf getBuffer();
    /**
     * @author
     */
    @Overwrite
    public static void handle(CPExecuteSkill msg, Supplier<NetworkEvent.Context> ctx) {
        (ctx.get()).enqueueWork(() -> {
            ServerPlayer serverPlayer = ((NetworkEvent.Context)ctx.get()).getSender();
            ServerPlayerPatch playerpatch = (ServerPlayerPatch) EpicFightCapabilities.getEntityPatch(serverPlayer, ServerPlayerPatch.class);
            ExecuteSkillEvent event=new ExecuteSkillEvent(playerpatch,serverPlayer,((MixinCPExecuteSkill)(Object)msg).getSkillSlot(),((MixinCPExecuteSkill)(Object)msg).getWorkType(),((MixinCPExecuteSkill)(Object)msg).getBuffer());
            MinecraftForge.EVENT_BUS.post(event);
            if(event.isCanceled()){return;}
            SkillContainer skillContainer = playerpatch.getSkill(event.skillSlot);
            switch(event.workType) {
                case ACTIVATE:
                    skillContainer.requestExecute(playerpatch, event.buffer);
                    break;
                case CANCEL:
                    skillContainer.requestCancel(playerpatch, event.buffer);
                    break;
                case CHARGING_START:
                    skillContainer.requestCharging(playerpatch, event.buffer);
            }

        });
        ((NetworkEvent.Context)ctx.get()).setPacketHandled(true);
    }

}
