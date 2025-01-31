package tkk.epic.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tkk.epic.block.TkkEpicBlocks;
import tkk.epic.gui.container.SkillWorkbenchContainer;
import tkk.epic.skill.EquipSkillHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

public class SkillWorkbenchBlockEntity extends BlockEntity implements MenuProvider {
    public static final String SKILL_ITEM_TAG="TKK_SKILL_ITEM";
    public static final String SKILL_BOOK_TAG="TKK_SKILL";
    /**Object[3]{skillSlot skillItem targetItem}*/
    public static final Map<String, Consumer<CanBeInfusionEvent>> SKILL_PREDICATE=new HashMap<>();
    public static final Map<String, Consumer<InlayEvent>> SKILL_INLAY=new HashMap<>();
    public static final Map<String, Consumer<SeparateEvent>> SKILL_SEPARATE=new HashMap<>();


    private final ItemStackHandler itemHandler = new ItemStackHandler(17) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    protected final ContainerData data;
    public SkillWorkbenchBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(TkkEpicBlockEntitys.SKILL_WORKBENCH.get(), p_155229_, p_155230_);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return 0;
            }

            @Override
            public void set(int pIndex, int pValue) {
            }

            @Override
            public int getCount() {
                return 0;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
    }
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.tkkepic.skill_workbench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return  new SkillWorkbenchContainer(p_39954_, p_39955_, this, this.data);
    }


    public boolean shiftUse(Player player){
        ItemStack target=itemHandler.getStackInSlot(0);
        if (target.isEmpty()){
            player.sendSystemMessage(Component.translatable("block.tkkepic.skill_workbench.target_item_empty"));
            return false;
        }
        CompoundTag skillTag=target.getTag().getCompound(EquipSkillHandle.TAG_KEY);
        CompoundTag skillItemTag=target.getTag().getCompound(SkillWorkbenchBlockEntity.SKILL_ITEM_TAG);
        if (target.getTag().contains(SkillWorkbenchBlockEntity.SKILL_ITEM_TAG)){
            //提取
            ArrayList<String> removeKeys=new ArrayList<>();
            for (String k :skillItemTag.getAllKeys()){
                int slot=Integer.parseInt(k);
                if (slot+1>=itemHandler.getSlots()){
                    continue;
                }
                if(!itemHandler.getStackInSlot(slot+1).isEmpty()){
                    continue;
                }
                ItemStack skillItem=ItemStack.of(skillItemTag.getCompound(k));
                removeKeys.add(k);
                skillTag.remove(k);

                SeparateEvent separateEvent=new SeparateEvent(slot,target,skillItem);
                MinecraftForge.EVENT_BUS.post(separateEvent);
                target=separateEvent.target;
                itemHandler.setStackInSlot(slot+1,separateEvent.skillItem);
            }
            for (String k:removeKeys){
                skillItemTag.remove(k);
            }
            if (skillTag.isEmpty()){
                target.getTag().remove(SkillWorkbenchBlockEntity.SKILL_ITEM_TAG);
            }else{
                target.getTag().put(SkillWorkbenchBlockEntity.SKILL_ITEM_TAG,skillItemTag);
            }
            if (skillTag.isEmpty()){
                target.getTag().remove(EquipSkillHandle.TAG_KEY);
            }else{
                target.getTag().put(EquipSkillHandle.TAG_KEY,skillTag);
            }
        }else{
            //存入
            skillItemTag=new CompoundTag();
            for (int i=1;i<itemHandler.getSlots();i++){
                String k=Integer.toString(i-1);
                ItemStack item=itemHandler.getStackInSlot(i);
                if (item.isEmpty()){
                    continue;
                }
                boolean canBeInfusion=true;
                if(skillTag.contains(k)){
                    canBeInfusion=false;
                }
                if(!item.hasTag() || !item.getTag().contains(SkillWorkbenchBlockEntity.SKILL_BOOK_TAG)){
                    canBeInfusion=false;
                }
                CanBeInfusionEvent canBeInfusionEvent=new CanBeInfusionEvent(i-1,target,item,canBeInfusion);
                MinecraftForge.EVENT_BUS.post(canBeInfusionEvent);
                canBeInfusion=canBeInfusionEvent.canBeInfusion;
                if (!canBeInfusion){continue;}
                InlayEvent inlayEvent=new InlayEvent(i-1,target,item,ItemStack.EMPTY);
                MinecraftForge.EVENT_BUS.post(inlayEvent);
                target=inlayEvent.target;
                skillItemTag.put(k,inlayEvent.skillItem.serializeNBT());
                skillTag.putString(k,item.getTag().getString(SkillWorkbenchBlockEntity.SKILL_BOOK_TAG));

                itemHandler.setStackInSlot(i,inlayEvent.returnItem);
            }
            if (skillTag.isEmpty()){
                target.getTag().remove(SkillWorkbenchBlockEntity.SKILL_ITEM_TAG);
            }else{
                target.getTag().put(SkillWorkbenchBlockEntity.SKILL_ITEM_TAG,skillItemTag);
            }
            if (skillTag.isEmpty()){
                target.getTag().remove(EquipSkillHandle.TAG_KEY);
            }else{
                target.getTag().put(EquipSkillHandle.TAG_KEY,skillTag);
            }
        }

        return true;
    }


    @SubscribeEvent
    public static void onCanBeInfusionEvent(CanBeInfusionEvent event){
        String skillId=event.skillItem.getTag().getString(SkillWorkbenchBlockEntity.SKILL_BOOK_TAG);
        if(SKILL_PREDICATE.containsKey(skillId)){
            SKILL_PREDICATE.get(skillId).accept(event);
        }
    }
    @SubscribeEvent
    public static void onInlayEvent(InlayEvent event){
        String skillId=event.skillItem.getTag().getString(SkillWorkbenchBlockEntity.SKILL_BOOK_TAG);
        if(SKILL_INLAY.containsKey(skillId)){
            SKILL_INLAY.get(skillId).accept(event);
        }
    }
    @SubscribeEvent
    public static void onSeparateEvent(SeparateEvent event){
        String skillId=event.skillItem.getTag().getString(SkillWorkbenchBlockEntity.SKILL_BOOK_TAG);
        if(SKILL_SEPARATE.containsKey(skillId)){
            SKILL_SEPARATE.get(skillId).accept(event);
        }
    }
    public static class CanBeInfusionEvent extends Event {
        public final int slot;
        public ItemStack target;
        public ItemStack skillItem;
        public boolean canBeInfusion;
        public CanBeInfusionEvent(int slot,ItemStack target,ItemStack skillItem,boolean canBeInfusion){
            this.slot=slot;
            this.target=target;
            this.skillItem=skillItem;
            this.canBeInfusion=canBeInfusion;
        }
    }
    public static class InlayEvent extends Event {
        public final int slot;
        public ItemStack target;
        public ItemStack skillItem;
        public ItemStack returnItem;
        public InlayEvent(int slot,ItemStack target,ItemStack skillItem,ItemStack returnItem){
            this.slot=slot;
            this.target=target;
            this.skillItem=skillItem;
            this.returnItem=returnItem;
        }
    }
    public static class SeparateEvent extends Event{
        public final int slot;
        public ItemStack target;
        public ItemStack skillItem;
        public SeparateEvent(int slot,ItemStack target,ItemStack skillItem){
            this.slot=slot;
            this.target=target;
            this.skillItem=skillItem;
        }
    }


}
