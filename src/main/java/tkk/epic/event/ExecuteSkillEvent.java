package tkk.epic.event;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@Cancelable
public class ExecuteSkillEvent extends Event {
    public ServerPlayer serverPlayer;
    public ServerPlayerPatch playerPatch;
    public int skillSlot;
    public CPExecuteSkill.WorkType workType;
    public FriendlyByteBuf buffer;



    public ExecuteSkillEvent(ServerPlayerPatch serverPlayerPatch,ServerPlayer serverPlayerEntity,int skillSlot,CPExecuteSkill.WorkType workType,FriendlyByteBuf buffer){
        this.serverPlayer=serverPlayerEntity;
        this.playerPatch=serverPlayerPatch;
        this.skillSlot=skillSlot;
        this.workType=workType;
        this.buffer=buffer;
    }

}
