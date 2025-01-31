package tkk.epic.skill;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import tkk.epic.event.AttackResultEvent;
import tkk.epic.event.SpellKeybindingPressEvent;
import tkk.epic.event.SpellKeybindingUpEvent;
import tkk.epic.event.TryHurtEvent;

import java.util.HashMap;
import java.util.function.Function;

public abstract class Skill {
    /*
    * 需要满足
    * 根据参数SkillContainer
    * 更新渲染状态网络包
    * 获取需要的事件
    * 对应事件执行的方法体
    * */
    //获取id
    public abstract String getSkillId();

    //准备更新，统一更新避免频繁发包
    public final void readyUpdate(SkillContainer container){
        container.needUpdate=true;
    }

    //注册监听
    public final HashMap<String,ListMultimap<Integer, Function<Object[],Object>>> Listener = new HashMap<>();
    public final void addListener(SkillManager.EventType event,int priority,Function<Object[],Object> function){
        addListener(event.getFn(),priority,function);
    }
    //添加监听，参数 事件类型 优先级(最低的开始执行，最高的最后执行) 方法体
    public final void addListener(String event,int priority,Function<Object[],Object> function){
        if(!Listener.containsKey(event)){
            Listener.put(event,ArrayListMultimap.create());
        }
        Listener.get(event).put(priority,function);
    }
    public void addDefaultListener(){
        addListener(SkillManager.EventType.loadSelf,5,(args)->{this.loadSelf((SkillContainer) args[0]);return null;});
        addListener(SkillManager.EventType.loadOther,5,(args)->{this.loadOther((SkillContainer) args[0],(SkillContainer) args[1]);return null;});
        addListener(SkillManager.EventType.unloadSelf,5,(args)->{this.unloadSelf((SkillContainer) args[0]);return null;});
        addListener(SkillManager.EventType.unloadOther,5,(args)->{this.unloadOther((SkillContainer) args[0],(SkillContainer) args[1]);return null;});
        addListener(SkillManager.EventType.pressKeySelf,5,(args)->{this.pressKeySelf((SkillContainer) args[0],(SpellKeybindingPressEvent) args[1]);return null;});
        addListener(SkillManager.EventType.pressKeyOther,5,(args)->{this.pressKeyOther((SkillContainer) args[0],(SkillContainer) args[1],(SpellKeybindingPressEvent) args[2]);return null;});
        addListener(SkillManager.EventType.upKeySelf,5,(args)->{this.upKeySelf((SkillContainer) args[0],(SpellKeybindingUpEvent) args[1]);return null;});
        addListener(SkillManager.EventType.upKeyOther,5,(args)->{this.upKeyOther((SkillContainer) args[0],(SkillContainer) args[1],(SpellKeybindingUpEvent) args[2]);return null;});
        addListener(SkillManager.EventType.tick,5,(args)->{this.tick((SkillContainer) args[0]);return null;});
        addListener(SkillManager.EventType.beAttack,5,(args)->{this.beAttack((SkillContainer) args[0],(SkillManager.AttackEvent) args[1]);return null;});
        addListener(SkillManager.EventType.onAttack,5,(args)->{this.onAttack((SkillContainer) args[0],(SkillManager.AttackEvent) args[1]);return null;});
        addListener(SkillManager.EventType.beHurt,5,(args)->{this.beHurt((SkillContainer) args[0],(SkillManager.HurtEvent) args[1]);return null;});
        addListener(SkillManager.EventType.onHurt,5,(args)->{this.onHurt((SkillContainer) args[0],(SkillManager.HurtEvent) args[1]);return null;});
        addListener(SkillManager.EventType.onExecuteSkill,5,(args)->{this.onExecuteSkill((SkillContainer) args[0],(SkillManager.EpicSkillEvent) args[1]);return null;});
        addListener(SkillManager.EventType.beDeath,5,(args)->{this.beDeath((SkillContainer) args[0],(LivingDeathEvent) args[1]);return null;});
        addListener(SkillManager.EventType.onDeath,5,(args)->{this.onDeath((SkillContainer) args[0],(LivingDeathEvent) args[1]);return null;});
        addListener("onAttackResult",5,(args)->{this.onAttackResult((SkillContainer) args[0],(AttackResultEvent) args[1]);return null;});
        addListener("beAttackResult",5,(args)->{this.beAttackResult((SkillContainer) args[0],(AttackResultEvent) args[1]);return null;});
        addListener("beTryHurt",5,(args)->{this.beTryHurt((SkillContainer) args[0],(TryHurtEvent) args[1]);return null;});
        addListener("onTryHurt",5,(args)->{this.onTryHurt((SkillContainer) args[0],(TryHurtEvent) args[1]);return null;});


        /*
        addListener(SkillManager.EventType.preGuard,5,(args)->{this.preGuard((SkillContainer) args[0],(SkillManager.GuardEvent) args[1]);return null;});
        addListener(SkillManager.EventType.onGuard,5,(args)->{this.onGuard((SkillContainer) args[0],(SkillManager.GuardEvent) args[1]);return null;});
        addListener(SkillManager.EventType.attackPreGuard,5,(args)->{this.attackPreGuard((SkillContainer) args[0],(SkillManager.GuardEvent) args[1]);return null;});
        addListener(SkillManager.EventType.attackOnGuard,5,(args)->{this.attackOnGuard((SkillContainer) args[0],(SkillManager.GuardEvent) args[1]);return null;});
        addListener(SkillManager.EventType.onSelfHitStunAndKnockback,5,(args)->{this.onSelfHitStunAndKnockback((SkillContainer) args[0],(SkillManager.StunAndKnockbackEvent) args[1]);return null;});
        addListener(SkillManager.EventType.onTargetHitStunAndKnockback,5,(args)->{this.onTargetHitStunAndKnockback((SkillContainer) args[0],(SkillManager.StunAndKnockbackEvent) args[1]);return null;});
        addListener(SkillManager.EventType.AttackAnimationEnd,5,(args)->{this.AttackAnimationEnd((SkillContainer) args[0],(AttackAnimationEndEvent) args[1]);return null;});
        addListener(SkillManager.EventType.updateHeldItem,5,(args)->{this.updateHeldItem((SkillContainer) args[0],(UpdateHeldItemEvent) args[1]);return null;});

         */
    }

    //事件 初始化（装配技能）
    //自身装配
    public void loadSelf(SkillContainer container){}
    //其他技能装配后
    public void loadOther(SkillContainer container,SkillContainer target){}
    //事件 卸下技能
    //自身卸下
    public void unloadSelf(SkillContainer container){}
    //其他技能装配后
    public void unloadOther(SkillContainer container,SkillContainer target){}


    //事件 按下按键
    //按下自身
    public void pressKeySelf(SkillContainer container, SpellKeybindingPressEvent event){}
    //其他技能被按下后
    public void pressKeyOther(SkillContainer container, SkillContainer target, SpellKeybindingPressEvent event){}
    //事件 释放按键
    //释放自身
    public void upKeySelf(SkillContainer container, SpellKeybindingUpEvent event){}
    //其他技能被释放后
    public void upKeyOther(SkillContainer container,SkillContainer target, SpellKeybindingUpEvent event){}
    //事件 tick
    public void tick(SkillContainer container){}
    //事件 被攻击
    public void beAttack(SkillContainer container, SkillManager.AttackEvent event){}
    //事件 攻击其他实体
    public void onAttack(SkillContainer container, SkillManager.AttackEvent event){}
    //事件 受到伤害
    public void beHurt(SkillContainer container, SkillManager.HurtEvent event){}
    //对其他实体造成伤害
    public void onHurt(SkillContainer container, SkillManager.HurtEvent event){}
    //事件 史诗战斗技能执行包
    public void onExecuteSkill(SkillContainer container, SkillManager.EpicSkillEvent event){}
    //自身死亡
    public void beDeath(SkillContainer container, LivingDeathEvent event){}
    //造成死亡
    public void onDeath(SkillContainer container, LivingDeathEvent event){}

    public void onAttackResult(SkillContainer container, AttackResultEvent event){}
    public void beAttackResult(SkillContainer container, AttackResultEvent event){}
    public void beTryHurt(SkillContainer container, TryHurtEvent event){}
    public void onTryHurt(SkillContainer container, TryHurtEvent event){}
    /*
    //事件 格挡事件
    public void preGuard(SkillContainer container, SkillManager.GuardEvent event){}
    public void onGuard(SkillContainer container, SkillManager.GuardEvent event){}

    //事件 攻击目标
    public void attackPreGuard(SkillContainer container, SkillManager.GuardEvent event){}
    public void attackOnGuard(SkillContainer container, SkillManager.GuardEvent event){}



    //事件 击晕时间
    public void onSelfHitStunAndKnockback(SkillContainer container, SkillManager.StunAndKnockbackEvent event){}
    //事件 击晕时间
    public void onTargetHitStunAndKnockback(SkillContainer container, SkillManager.StunAndKnockbackEvent event){}
    //事件 攻击动作结束
    //public void AttackAnimationEnd(SkillContainer container, AttackAnimationEndEvent event){}

    //切换手中物品
    //public void updateHeldItem(SkillContainer container, UpdateHeldItemEvent event){}

     */

}
