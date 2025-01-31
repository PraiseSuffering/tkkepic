package tkk.epic.gui;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tkk.epic.TkkEpic;
import tkk.epic.gui.container.SkillWorkbenchContainer;

public class TkkMenuType {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, TkkEpic.MODID);

    public static final RegistryObject<MenuType<SkillWorkbenchContainer>> SKILL_WORKBENCH_MENU =MENUS.register("skill_workbench_menu", () -> {return IForgeMenuType.create(SkillWorkbenchContainer::new);});

}
