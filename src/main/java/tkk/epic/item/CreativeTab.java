package tkk.epic.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import tkk.epic.TkkEpic;
import tkk.epic.block.TkkEpicBlocks;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.item.EpicFightItems;

public class CreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TkkEpic.MODID);

    public static final RegistryObject<CreativeModeTab> SKILLS = TABS.register("skills", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tkkepic.items"))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> new ItemStack(TkkEpicItems.SKILL_ITEM.get()))
            .displayItems((params, output) -> {
                TkkEpicItems.ITEMS.getEntries().forEach(item -> {
                    output.accept(item.get());
                });
            })
            .build());
}
