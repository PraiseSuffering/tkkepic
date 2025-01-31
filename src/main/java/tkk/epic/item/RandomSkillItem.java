package tkk.epic.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tkk.epic.block.entity.SkillWorkbenchBlockEntity;
import tkk.epic.skill.Skills;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RandomSkillItem extends Item {
    public final static Map<String,Integer> SKILL_RANDOM_RATE=new HashMap<>();
    public final static Map<String, Supplier<ItemStack>> SKILL_RANDOM_CREATE_ITEM_STACK=new HashMap<>();
    public static ItemStack createRandomSkillItem(String id){
        if(SKILL_RANDOM_CREATE_ITEM_STACK.containsKey(id)){
            return SKILL_RANDOM_CREATE_ITEM_STACK.get(id).get();
        }else{
            return ItemStack.EMPTY;
        }

    }
    public static String getRandomSkill(){
        int allRate=0;
        ArrayList<String> skills=new ArrayList<>();
        ArrayList<Integer> rates=new ArrayList<>();

        for (String skill:SKILL_RANDOM_RATE.keySet()){
            if (SKILL_RANDOM_RATE.get(skill)<=0){continue;}
            skills.add(skill);
            rates.add(SKILL_RANDOM_RATE.get(skill));
            allRate+=SKILL_RANDOM_RATE.get(skill);
        }
        if (skills.size()==0){
            return Skills.EMPTY_SKILL.getSkillId();
        }
        int randomRate= (int) (Math.floor(Math.random()*allRate)+1);
        int temp=0;
        for (int i=0;i<skills.size();i++){
            temp+=rates.get(i);
            if(temp>=randomRate){
                return skills.get(i);
            }
        }
        return Skills.EMPTY_SKILL.getSkillId();
    }
    public static void regRandomSkill(String skillId,int rate){
        SKILL_RANDOM_RATE.put(skillId,rate);
        regRandomSkill(skillId,rate,()->{
            ItemStack give=new ItemStack(TkkEpicItems.SKILL_ITEM.get(),1);
            give.getOrCreateTag().putString(SkillWorkbenchBlockEntity.SKILL_BOOK_TAG,skillId);
            give.setHoverName(Component.translatable(skillId));
            return give;
        });
    }
    public static void regRandomSkill(String skillId,int rate, Supplier<ItemStack> create){
        SKILL_RANDOM_RATE.put(skillId,rate);
        SKILL_RANDOM_CREATE_ITEM_STACK.put(skillId,create);
    }

    public RandomSkillItem(Properties p_41383_) {
        super(p_41383_);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
        ItemStack itemstack = playerIn.getItemInHand(hand);


        playerIn.awardStat(Stats.ITEM_USED.get(this));
        if (!worldIn.isClientSide) {
            String skill=RandomSkillItem.getRandomSkill();
            playerIn.addItem(createRandomSkillItem(skill));
        }
        if (!playerIn.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.success(itemstack);
    }

}
