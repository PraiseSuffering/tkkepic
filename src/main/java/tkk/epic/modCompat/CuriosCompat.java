package tkk.epic.modCompat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.IItemHandlerModifiable;
import tkk.epic.TkkEpic;
import tkk.epic.skill.EquipSkillHandle;
import tkk.epic.skill.SkillItemType;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Map;

public class CuriosCompat implements ICompat{
    public CuriosCompat(){}

    @Override
    public void init() {
        SkillItemType.regSkillItemType("Curios",new SkillItemType(this::getCuriosItems));
        MinecraftForge.EVENT_BUS.register(this);
    }
    public ItemStack[] getCuriosItems(ServerPlayer player){
        Map<String, ISlotType> slotTypeMap= CuriosApi.getPlayerSlots(player);
        ICuriosItemHandler iCuriosItemHandler=CuriosApi.getCuriosInventory(player).orElse(null);
        if (iCuriosItemHandler==null){return new ItemStack[0];}
        IItemHandlerModifiable a= iCuriosItemHandler.getEquippedCurios();
        ItemStack[] items=new ItemStack[a.getSlots()];
        for (int i=0;i<a.getSlots();i++){
            items[i]=a.getStackInSlot(i);
        };
        return items;
    }
    @SubscribeEvent
    public void onCurioChange(CurioChangeEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)){return;}
        EquipSkillHandle.updatePlayerSkill((ServerPlayer) event.getEntity());
    }

}
