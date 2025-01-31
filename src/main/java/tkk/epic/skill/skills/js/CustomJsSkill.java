package tkk.epic.skill.skills.js;

import net.minecraftforge.event.entity.living.LivingDeathEvent;
import tkk.epic.event.AttackResultEvent;
import tkk.epic.event.SpellKeybindingPressEvent;
import tkk.epic.event.SpellKeybindingUpEvent;
import tkk.epic.event.TryHurtEvent;
import tkk.epic.js.JsContainer;
import tkk.epic.skill.Skill;
import tkk.epic.skill.SkillContainer;
import tkk.epic.skill.SkillManager;

public class CustomJsSkill extends Skill {
    public final String SkillId;

    public final JsContainer jsContainer;

    public CustomJsSkill(JsContainer js){
        jsContainer=js;
        SkillId=(String) js.run("getSkillId");
        this.addDefaultListener();
        js.run("init",this);
    }


    /*
     * 需要满足
     * 根据参数SkillContainer
     * 更新渲染状态网络包
     * 获取需要的事件
     * 对应事件执行的方法体
     * */
    //获取id
    public String getSkillId(){
        return SkillId;
    };



    //事件 初始化（装配技能）
    //自身装配
    public void loadSelf(SkillContainer container){jsContainer.run("loadSelf",container);}
    //其他技能装配后
    public void loadOther(SkillContainer container,SkillContainer target){jsContainer.run("loadOther",container,target);}
    //事件 卸下技能
    //自身卸下
    public void unloadSelf(SkillContainer container){jsContainer.run("unloadSelf",container);}
    //其他技能装配后
    public void unloadOther(SkillContainer container,SkillContainer target){jsContainer.run("unloadOther",container,target);}


    //事件 按下按键
    //按下自身
    public void pressKeySelf(SkillContainer container, SpellKeybindingPressEvent event){jsContainer.run("pressKeySelf",container,event);}
    //其他技能被按下后
    public void pressKeyOther(SkillContainer container,SkillContainer target, SpellKeybindingPressEvent event){jsContainer.run("pressKeyOther",container,target,event);}
    //事件 释放按键
    //释放自身
    public void upKeySelf(SkillContainer container, SpellKeybindingUpEvent event){jsContainer.run("upKeySelf",container,event);}
    //其他技能被释放后
    public void upKeyOther(SkillContainer container,SkillContainer target, SpellKeybindingUpEvent event){jsContainer.run("upKeyOther",container,target,event);}
    //事件 tick
    public void tick(SkillContainer container){jsContainer.run("tick",container);}
    //事件 被攻击
    public void beAttack(SkillContainer container, SkillManager.AttackEvent event){jsContainer.run("beAttack",container,event);}
    //事件 攻击其他实体
    public void onAttack(SkillContainer container, SkillManager.AttackEvent event){jsContainer.run("onAttack",container,event);}
    //事件 受到伤害
    public void beHurt(SkillContainer container, SkillManager.HurtEvent event){jsContainer.run("beHurt",container,event);}
    //对其他实体造成伤害
    public void onHurt(SkillContainer container, SkillManager.HurtEvent event){jsContainer.run("onHurt",container,event);}
    //事件 史诗战斗技能执行包
    public void onExecuteSkill(SkillContainer container, SkillManager.EpicSkillEvent event){jsContainer.run("onExecuteSkill",container,event);}
    //事件 格挡事件
    //自身死亡
    public void beDeath(SkillContainer container, LivingDeathEvent event){jsContainer.run("beDeath",container,event);}
    //造成死亡
    public void onDeath(SkillContainer container, LivingDeathEvent event){jsContainer.run("onDeath",container,event);}
    public void onAttackResult(SkillContainer container, AttackResultEvent event){jsContainer.run("onAttackResult",container,event);}
    public void beAttackResult(SkillContainer container, AttackResultEvent event){jsContainer.run("beAttackResult",container,event);}
    public void beTryHurt(SkillContainer container, TryHurtEvent event){jsContainer.run("beTryHurt",container,event);}
    public void onTryHurt(SkillContainer container, TryHurtEvent event){jsContainer.run("onTryHurt",container,event);}
    /*
    public void preGuard(SkillContainer container, SkillManager.GuardEvent event){jsContainer.run("preGuard",container,event);}
    public void onGuard(SkillContainer container, SkillManager.GuardEvent event){jsContainer.run("onGuard",container,event);}
    //事件 攻击目标
    public void attackPreGuard(SkillContainer container, SkillManager.GuardEvent event){jsContainer.run("attackPreGuard",container,event);}
    public void attackOnGuard(SkillContainer container, SkillManager.GuardEvent event){jsContainer.run("attackOnGuard",container,event);}


    //事件 击晕时间
    public void onSelfHitStunAndKnockback(SkillContainer container, SkillManager.StunAndKnockbackEvent event){jsContainer.run("onSelfHitStunAndKnockback",container,event);}
    //事件 击晕时间
    public void onTargetHitStunAndKnockback(SkillContainer container, SkillManager.StunAndKnockbackEvent event){jsContainer.run("onTargetHitStunAndKnockback",container,event);}
    //事件 攻击动作结束
    public void AttackAnimationEnd(SkillContainer container, AttackAnimationEndEvent event){jsContainer.run("AttackAnimationEnd",container,event);}


    //切换手中物品
    public void updateHeldItem(SkillContainer container, UpdateHeldItemEvent event){jsContainer.run("updateHeldItem",container,event);}

     */



}
