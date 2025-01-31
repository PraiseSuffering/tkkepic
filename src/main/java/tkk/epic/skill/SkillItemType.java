package tkk.epic.skill;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SkillItemType {
    private static Map<String, SkillItemType> SKILL_ITEM_TYPE=new HashMap<>();
    public static SkillItemType getSkillItemType(String id){
        return SKILL_ITEM_TYPE.get(id);
    }
    public static void regSkillItemType(String id,SkillItemType skillItemType){
        SKILL_ITEM_TYPE.put(id,skillItemType);
    }
    public static void reg(){
        SkillItemType.regSkillItemType("MainHand",new SkillItemType(player -> new ItemStack[]{player.getItemBySlot(EquipmentSlot.MAINHAND)}));
        SkillItemType.regSkillItemType("Curios",new SkillItemType(player -> new ItemStack[0]));
        SkillItemType.regSkillItemType("OffHand",new SkillItemType(player -> new ItemStack[]{player.getItemBySlot(EquipmentSlot.OFFHAND)}));
        SkillItemType.regSkillItemType("Head",new SkillItemType(player -> new ItemStack[]{player.getItemBySlot(EquipmentSlot.HEAD)}));
        SkillItemType.regSkillItemType("Chest",new SkillItemType(player -> new ItemStack[]{player.getItemBySlot(EquipmentSlot.CHEST)}));
        SkillItemType.regSkillItemType("Legs",new SkillItemType(player -> new ItemStack[]{player.getItemBySlot(EquipmentSlot.LEGS)}));
        SkillItemType.regSkillItemType("Feet",new SkillItemType(player -> new ItemStack[]{player.getItemBySlot(EquipmentSlot.FEET)}));



    }



    private final Function<ServerPlayer, ItemStack[]> getItem;
    public SkillItemType(Function<ServerPlayer,ItemStack[]> getItem){
        this.getItem=getItem;
    }
    public ItemStack[] getItem(ServerPlayer player){
        return getItem.apply(player);
    }
}
