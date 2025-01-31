package tkk.epic.skill;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import tkk.epic.network.SPTkkSpellUpdata;
import tkk.epic.network.TkkEpicNetworkManager;
import tkk.epic.skill.skills.EmptySkill;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class SkillContainer {
    //这个container所属玩家
    public ServerPlayer player;
    //这个container的id(0-SKILL_SIZE)
    public int id;

    //当前储存技能，null为无技能
    @Nonnull
    public Skill skill;
    //是否需要更新渲染
    public boolean needUpdate=true;

    //以下为渲染部分，同步时发送
    //为false时禁止渲染且关闭发包
    public boolean doRender=false;

    //是否禁用，如果为true则渲染禁用,且关闭发包功能
    public boolean isDisable=false;
    //是否激活，如果为true则渲染激活
    public boolean isEnable=false;
    //冷却时间，为0时不渲染，1时渲染cd完毕(白色覆盖),>1时渲染黑色覆盖cd且,>1时关闭发包功能
    public int maxCooldow=0;
    public int cooldown=0;
    //魔力，不会自行恢复的cd，也没有cd完毕的特效，>1时关闭发包功能
    public int maxMana=0;
    public int mana=0;
    //渲染额外的字
    public String text="";
    //技能图标,如果未注册或null则错误图标
    public String spell_textures="";

    public CompoundTag nbt;
    public HashMap tempdata;

    public SkillContainer(ServerPlayer p,int id){
        skill=Skills.EMPTY_SKILL;
        this.id=id;
        player=p;
        nbt=new CompoundTag();
        tempdata=new HashMap();
    }


    //获取渲染逻辑
    public SPTkkSpellUpdata getNetworkPacker(){
        return new SPTkkSpellUpdata(this.id,this.doRender,this.isDisable, this.isEnable, this.maxCooldow,this.cooldown,this.maxMana,this.mana,this.text,this.spell_textures);
    };
    //更新发包
    public void update(){
        TkkEpicNetworkManager.sendToPlayer(getNetworkPacker(),this.player);
    }


    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        if(skill == null){skill=Skills.getSkill(EmptySkill.skillId);}
        compound.putBoolean("doRender",doRender);
        compound.putBoolean("isDisable",isDisable);
        compound.putBoolean("isEnable",isEnable);
        compound.putString("spell_textures",spell_textures);
        compound.putInt("maxCooldow",maxCooldow);
        compound.putInt("cooldown",cooldown);
        compound.putInt("maxMana",maxMana);
        compound.putInt("mana",mana);
        compound.putString("text",text);
        compound.putString("skill",skill.getSkillId());
        compound.put("nbt",nbt);
        return compound;
    }

    public void deserializeNBT(CompoundTag nbt) {
        doRender=nbt.getBoolean("doRender");
        isDisable=nbt.getBoolean("isDisable");
        isEnable=nbt.getBoolean("isEnable");
        spell_textures=nbt.getString("spell_textures");
        maxCooldow=nbt.getInt("maxCooldow");
        cooldown=nbt.getInt("cooldown");
        maxMana=nbt.getInt("maxMana");
        mana=nbt.getInt("mana");
        text=nbt.getString("text");
        skill= Skills.getSkill(nbt.getString("skill"));
        if(skill == null){skill=Skills.getSkill(EmptySkill.skillId);}
        this.nbt= nbt.getCompound("nbt");
    }

}
