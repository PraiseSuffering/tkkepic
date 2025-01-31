package tkk.epic.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tkk.epic.TkkEpic;
import tkk.epic.block.TkkEpicBlocks;

public class TkkEpicBlockEntitys {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TkkEpic.MODID);


    public static final RegistryObject<BlockEntityType<SkillWorkbenchBlockEntity>> SKILL_WORKBENCH =
            BLOCK_ENTITIES.register("skill_workbench", () ->
                    BlockEntityType.Builder.of(SkillWorkbenchBlockEntity::new,
                            TkkEpicBlocks.SKILL_WORKBENCH.get()).build(null));
}
