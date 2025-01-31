package tkk.epic.utils;

import net.minecraft.server.level.ServerPlayer;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class StupidOnlyClientTool {
    public static StaticAnimation getAnimation(String resourceLocation) {
        return AnimationManager.getInstance().byKeyOrThrow(resourceLocation);
    }
    public static int getWeaponSkillStack(ServerPlayer player){
        return ((ServerPlayerPatch)player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null)).getSkill(SkillSlots.WEAPON_INNATE).getStack();
    }
    public static float getWeaponSkillResource(ServerPlayer player){
        return ((ServerPlayerPatch)player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null)).getSkill(SkillSlots.WEAPON_INNATE).getResource();
    }
    public static float getWeaponSkillMaxResource(ServerPlayer player){
        return ((ServerPlayerPatch)player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null)).getSkill(SkillSlots.WEAPON_INNATE).getMaxResource();
    }

    public static void setWeaponSkillStack(ServerPlayer player,int stack){
        ServerPlayerPatch playerPatch = ((ServerPlayerPatch)player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null));
        SkillContainer skillContainer = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
        skillContainer.getSkill().setStackSynchronize(playerPatch,stack);
    }
    public static void setWeaponSkillResource(ServerPlayer player,float resource){
        ServerPlayerPatch playerPatch = ((ServerPlayerPatch)player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null));
        SkillContainer skillContainer = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
        skillContainer.getSkill().setConsumptionSynchronize(playerPatch,resource);

    }

}
