package tkk.epic.item;

import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import tkk.epic.TkkEpic;
import tkk.epic.block.entity.SkillWorkbenchBlockEntity;
import tkk.epic.event.AnimationEndEvent;
import yesman.epicfight.main.EpicFightMod;

public class SkillItem extends Item {
    public SkillItem(Properties p_41383_) {
        super(p_41383_);
    }
    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains(SkillWorkbenchBlockEntity.SKILL_BOOK_TAG);
    }



}
