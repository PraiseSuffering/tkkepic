package tkk.epic.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import tkk.epic.gui.hud.hotbar.HotBarManager;

import java.util.function.Supplier;

public class SPTkkSpellUpdata {
    public int spellId;

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
    public String spell_textures;

    public SPTkkSpellUpdata(){}
    public SPTkkSpellUpdata(int id,boolean doRender,boolean isDisable, boolean isEnable,int maxCooldow,int cooldown,int maxMana,int mana,String text,String texturess){
        this.spellId=id;
        this.doRender=doRender;
        this.isDisable=isDisable;
        this.isEnable=isEnable;
        this.maxCooldow=maxCooldow;
        this.cooldown=cooldown;
        this.maxMana=maxMana;
        this.mana=mana;
        this.text=text;
        this.spell_textures=texturess;
    }

    public static SPTkkSpellUpdata fromBytes(FriendlyByteBuf buf) {
        SPTkkSpellUpdata msg = new SPTkkSpellUpdata(buf.readInt(),buf.readBoolean(),buf.readBoolean(),buf.readBoolean(),buf.readInt(),buf.readInt(),buf.readInt(),buf.readInt(),buf.readUtf(),buf.readUtf());
        return msg;
    }

    public static void toBytes(SPTkkSpellUpdata msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.spellId);
        buf.writeBoolean(msg.doRender);
        buf.writeBoolean(msg.isDisable);
        buf.writeBoolean(msg.isEnable);
        buf.writeInt(msg.maxCooldow);
        buf.writeInt(msg.cooldown);
        buf.writeInt(msg.maxMana);
        buf.writeInt(msg.mana);
        buf.writeUtf(msg.text);
        buf.writeUtf(msg.spell_textures);
    }

    public static void handle(SPTkkSpellUpdata msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            HotBarManager.Spell spell=HotBarManager.spells[msg.spellId];
            if(spell.doRender!=msg.doRender){spell.doRender= msg.doRender;}
            if(spell.isDisable!=msg.isDisable){spell.isDisable= msg.isDisable;}
            if(spell.isEnable!=msg.isEnable){spell.isEnable= msg.isEnable;}
            if(spell.maxCooldow!=msg.maxCooldow){spell.maxCooldow= msg.maxCooldow;}
            if(spell.cooldown!=msg.cooldown){spell.cooldown= msg.cooldown;}
            if(spell.maxMana!=msg.maxMana){spell.maxMana= msg.maxMana;}
            if(spell.mana!=msg.mana){spell.mana= msg.mana;}
            if(!spell.text.equals(msg.text)){spell.text= msg.text;}
            if(!spell.spell_textures.equals(msg.spell_textures)){spell.spell_textures= msg.spell_textures;}
        });

        ctx.get().setPacketHandled(true);
    }


}
