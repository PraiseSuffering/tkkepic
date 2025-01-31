package tkk.epic.skill;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;
import tkk.epic.TkkEpic;
import tkk.epic.capability.tkk.ITkkCapability;
import tkk.epic.config.ConfigManager;
import tkk.epic.event.UpdatePlayerSkillEvent;
import tkk.epic.gui.hud.hotbar.HotBar;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EquipSkillHandle {
    public static final String TAG_KEY="tkk_item_skill";
    public static final Map<ResourceLocation,String[]> itemDefaultSkill=new HashMap<>();
    private static boolean hasCuriosMod;
    private static BiFunction<ServerPlayer,String,ItemStack[]> getCuriosFN=(player,s)->{return new ItemStack[]{ItemStack.EMPTY};};
    public static void regEvent(){
        hasCuriosMod=ModList.get().getModContainerById("curios____").orElse(null)!=null;

        MinecraftForge.EVENT_BUS.register(EquipSkillHandle.class);
        if(hasCuriosMod){
            MinecraftForge.EVENT_BUS.register(new Object() { // from class: com.momosoftworks.coldsweat.util.compat.CompatManager.1
                @SubscribeEvent
                public void onCurioChange(CurioChangeEvent event) {
                    if (!(event.getEntity() instanceof ServerPlayer)){return;}
                    updatePlayerSkill((ServerPlayer) event.getEntity());
                }
            });
            new Object(){
                public void a(){
                    getCuriosFN=(player,s)->{
                        ICuriosItemHandler cap=player.getCapability(CuriosCapability.INVENTORY).orElse(null);
                        if (cap==null){return new ItemStack[]{ItemStack.EMPTY};}
                        ItemStack[] arr=new ItemStack[cap.getCurios().get(s).getStacks().getSlots()];
                        for (int i=0;i<arr.length;i++){
                            arr[i]=cap.getCurios().get(s).getStacks().getStackInSlot(i);
                        }
                        return arr;
                    };
                }
            }.a();
        }
    }


    @SubscribeEvent
    public static void livingEquipmentChangeEvent(LivingEquipmentChangeEvent event){
        if(!(event.getEntity() instanceof ServerPlayer)){return;}
        updatePlayerSkill((ServerPlayer) event.getEntity());
    }


    public static void updatePlayerSkill(ServerPlayer player){
        ITkkCapability cap = SkillManager.getSkillData(player);
        if(cap==null){return;}
        String[] skills=new String[HotBar.SKILL_SIZE];
        for (int i=0;i<HotBar.SKILL_SIZE;i++){
            skills[i]="EmptySkill";
        }
        for(String skillItemSlotEnum: ConfigManager.ITEM_SKILL_PRIORITY.get()){
            SkillItemType itemType=SkillItemType.getSkillItemType(skillItemSlotEnum);
            if (itemType==null){continue;}
            ItemStack[] itemStacks=itemType.getItem(player);
            for (ItemStack itemStack:itemStacks){
                skills=getItemSkill(skills,itemStack);
            }
        }
        MinecraftForge.EVENT_BUS.post(new UpdatePlayerSkillEvent(player,skills));
        for (int i=0;i<HotBar.SKILL_SIZE;i++){
            if(Objects.equals(cap.getSkillContainer(i).skill.getSkillId(), skills[i])){continue;}
            SkillManager.setSkillToSkillContainer(player,i,skills[i]);
        }

    }



    public static String[] getItemSkill(String[] skills, ItemStack itemStack){
        if(itemDefaultSkill.containsKey(ForgeRegistries.ITEMS.getKey(itemStack.getItem()))){
            String[] defaultSkill=itemDefaultSkill.get(ForgeRegistries.ITEMS.getKey(itemStack.getItem()));
            for (int i=0;i< HotBar.SKILL_SIZE;i++){
                if(defaultSkill[i]==null){continue;}
                skills[i]=defaultSkill[i];
            }
        }
        if (itemStack.hasTag() && itemStack.getTag().contains(TAG_KEY) && itemStack.getTag().get(TAG_KEY) instanceof CompoundTag){
            CompoundTag tag= (CompoundTag) itemStack.getTag().get(TAG_KEY);
            for (int i=0;i< HotBar.SKILL_SIZE;i++){
                String skillId=tag.getString(Integer.toString(i));
                if(skillId==null || skillId.equals("")){continue;}
                skills[i]=skillId;
            }
        }
        return skills;
    }
}
