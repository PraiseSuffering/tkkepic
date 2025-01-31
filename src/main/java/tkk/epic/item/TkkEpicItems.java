package tkk.epic.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tkk.epic.TkkEpic;
import tkk.epic.block.TkkEpicBlocks;
import yesman.epicfight.main.EpicFightMod;

public class TkkEpicItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TkkEpic.MODID);

    public static final RegistryObject<Item> SKILL_ITEM = ITEMS.register("skill_item", () -> {
        return new SkillItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
    });
    public static final RegistryObject<Item> RANDOM_SKILL_ITEM = ITEMS.register("random_skill_item", () -> {
        return new RandomSkillItem(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.RARE));
    });
    public static final RegistryObject<Item> SKILL_WORKBENCH_Item = ITEMS.register("skill_workbench", () -> {
        return new BlockItem(TkkEpicBlocks.SKILL_WORKBENCH.get(), new Item.Properties());
    });

}
