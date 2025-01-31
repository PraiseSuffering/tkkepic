package tkk.epic.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tkk.epic.TkkEpic;
import tkk.epic.item.SkillItem;

public class TkkEpicBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TkkEpic.MODID);


    public static final RegistryObject<Block> SKILL_WORKBENCH = BLOCKS.register("skill_workbench", () -> {
        return new SkillWorkbenchBlock();
    });
}
