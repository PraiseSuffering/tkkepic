package tkk.epic.skill;

import com.google.common.collect.ListMultimap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.capability.tkk.ITkkCapability;
import tkk.epic.capability.tkk.TkkCapabilityProvider;
import tkk.epic.event.*;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import static tkk.epic.gui.hud.hotbar.HotBar.SKILL_SIZE;
import static tkk.epic.skill.SkillManager.identifier.containerSelf;

public class SkillManager {
    public static ITkkCapability getSkillData(ServerPlayer p){
        return p.getCapability(TkkCapabilityProvider.TKK_CAPABILITY).orElse(null);
    }
    //设置Skill到SkillContainer
    public static void setSkillToSkillContainer(ServerPlayer player,int id,Skill skill){
        SkillContainer container;
        ITkkCapability cap=getSkillData(player);
        SkillContainer target=cap.getSkillContainer(id);
        //target.skill.unloadSelf(target);
        runEvent(target, EventType.unloadSelf.getFn(),target);
        runEventExclude(player,EventType.unloadOther.getFn(),id,identifier.containerSelf,target);
        target.skill=skill;
        //target.skill.loadSelf(target);
        runEvent(target, EventType.loadSelf.getFn(),target);
        runEventExclude(player,EventType.loadOther.getFn(),id,identifier.containerSelf,target);
        updateDate(cap);
    }
    public static void setSkillToSkillContainer(ServerPlayer player,int id,String skill){
        setSkillToSkillContainer(player,id,Skills.getSkill(skill));
    }
    public static enum identifier {
        containerSelf
    }
    public static void runEventExclude(ServerPlayer player,String eventType,int id,Object... args){
        ITkkCapability cap=getSkillData(player);
        if(cap==null){return;}
        TreeSet<Integer> treeSet=new TreeSet<Integer>((o1,o2)->{return o1.compareTo(o2);});
        ArrayList<SkillContainer> containers=new ArrayList<>();
        for(int i=0;i<SKILL_SIZE;i++){
            if (i==id){continue;}
            SkillContainer container=cap.getSkillContainer(i);
            ListMultimap<Integer, Function<Object[],Object>> map = container.skill.Listener.get(eventType);
            if(map==null || map.isEmpty()){continue;}
            treeSet.addAll(map.keySet());
            containers.add(container);
        }
        for (Integer priority:treeSet){
            for (SkillContainer sc:containers){
                ListMultimap<Integer, Function<Object[],Object>> map = sc.skill.Listener.get(eventType);
                if(!map.containsKey(priority)){return;}
                for(Function<Object[],Object> fn:map.get(priority)){
                    Object[] copy= args.clone();

                    for(int x=0;x< copy.length;x++){
                        if(copy[x]== containerSelf){
                            copy[x]=sc;
                        }
                    }
                    fn.apply(copy);
                }
            }
        }
    }
    public static void runEvent(ServerPlayer player,String eventType,Object... args){
        ITkkCapability cap=getSkillData(player);
        if(cap==null){return;}
        TreeSet<Integer> treeSet=new TreeSet<Integer>((o1,o2)->{return o1.compareTo(o2);});
        ArrayList<SkillContainer> containers=new ArrayList<>();
        for(int i=0;i<SKILL_SIZE;i++){
            SkillContainer container=cap.getSkillContainer(i);
            ListMultimap<Integer, Function<Object[],Object>> map = container.skill.Listener.get(eventType);
            if(map==null || map.isEmpty()){continue;}
            treeSet.addAll(map.keySet());
            containers.add(container);
        }
        for (Integer priority:treeSet){
            for (SkillContainer sc:containers){
                ListMultimap<Integer, Function<Object[],Object>> map = sc.skill.Listener.get(eventType);
                if(!map.containsKey(priority)){continue;}
                for(Function<Object[],Object> fn:map.get(priority)){
                    Object[] copy= args.clone();

                    for(int x=0;x< copy.length;x++){
                        if(copy[x]== containerSelf){
                            copy[x]=sc;
                        }
                    }
                    fn.apply(copy);
                }
            }
        }
    }
    public static void runEvent(SkillContainer skillContainer,String eventType,Object... args){
        ListMultimap<Integer, Function<Object[],Object>> map = skillContainer.skill.Listener.get(eventType);
        if(map==null || map.isEmpty()){return;}
        Set<Integer> prioritySet=map.keySet();
        TreeSet<Integer> treeSet=new TreeSet<Integer>((o1,o2)->{return o1.compareTo(o2);});
        treeSet.addAll(prioritySet);
        for (Integer priority:treeSet){
            for(Function<Object[],Object> fn:map.get(priority)){
                fn.apply(args);
            }
        }
    }

    public static void updateDate(ITkkCapability cap){
        SkillContainer container;
        for(int i=0;i<SKILL_SIZE;i++){
            container=cap.getSkillContainer(i);
            if(container.needUpdate){
                container.update();
                container.needUpdate=false;
            }
        }
    }


    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void attackResult(AttackResultEvent event){
        if(event.target instanceof ServerPlayer) {
            runEvent((ServerPlayer) event.target,"beAttackResult",identifier.containerSelf,event);
        }
        if(event.patch.getOriginal() instanceof ServerPlayer) {
            runEvent((ServerPlayer) event.patch.getOriginal(),"onAttackResult",identifier.containerSelf,event);
        }

    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void SpellKeyPress(SpellKeybindingPressEvent event){
        ITkkCapability cap=getSkillData(event.player);
        SkillContainer target=cap.getSkillContainer(event.spellId);
        runEvent(target, EventType.pressKeySelf.getFn(),target,event);
        runEventExclude(event.player,EventType.pressKeyOther.getFn(), event.spellId,identifier.containerSelf,target,event);
        /*
        SkillContainer container;
        for(int i=0;i<SKILL_SIZE;i++){
            if(i== event.spellId){continue;}
            container=cap.getSkillContainer(i);
            runEvent(container, EventType.pressKeyOther,container,target,event);
        }

         */
        updateDate(cap);
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void SpellKeyUp(SpellKeybindingUpEvent event){
        ITkkCapability cap=getSkillData(event.player);
        SkillContainer target=cap.getSkillContainer(event.spellId);
        runEvent(target, EventType.upKeySelf.getFn(),target,event);
        runEventExclude(event.player,EventType.upKeyOther.getFn(), event.spellId,identifier.containerSelf,target,event);
        /*
        SkillContainer container;
        for(int i=0;i<SKILL_SIZE;i++){
            if(i== event.spellId){continue;}
            container=cap.getSkillContainer(i);
            runEvent(container, EventType.upKeyOther,container,target,event);
        }

         */
        updateDate(cap);
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ITkkCapability cap=event.getEntity().getCapability(TkkCapabilityProvider.TKK_CAPABILITY,null).orElse(null);
        if(cap!=null){
            for(int i=0;i<SKILL_SIZE;i++){
                cap.getSkillContainer(i).update();
            }

        }
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(!(event.player instanceof ServerPlayer)){return;}
        if(event.phase == TickEvent.Phase.END){return;}
        ITkkCapability cap=getSkillData((ServerPlayer) event.player);
        if(cap==null){return;}

        SkillContainer container;
        for(int i=0;i<SKILL_SIZE;i++){
            container=cap.getSkillContainer(i);
            if(container.cooldown!=0){container.cooldown-=1;}
        }
        runEvent((ServerPlayer) event.player,EventType.tick.getFn(), containerSelf);
        /*
        SkillContainer container;
        for(int i=0;i<SKILL_SIZE;i++){
            container=cap.getSkillContainer(i);
            if(container.cooldown!=0){container.cooldown-=1;}
            runEvent(container, EventType.tick,container);
        }

         */
        updateDate(cap);
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onAttack(LivingAttackEvent event){
        if(event.isCanceled()){return;}
        ITkkCapability cap;
        SkillContainer container;
        AttackEvent e=new AttackEvent(event);
        if(event.getEntity() instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.getEntity());
            runEvent((ServerPlayer) event.getEntity(), EventType.beAttack.getFn(), containerSelf,e);
            updateDate(cap);
            if(e.isCanceled()){
                event.setCanceled(true);
                return;
            }
        }
        if(event.getSource().getEntity() instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.getSource().getEntity());
            runEvent((ServerPlayer) event.getSource().getEntity(), EventType.onAttack.getFn(), containerSelf,e);
            updateDate(cap);
            if(e.isCanceled()){
                event.setCanceled(true);
                return;
            }
        }
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onHurt(LivingHurtEvent event){
        if(event.isCanceled()){return;}
        ITkkCapability cap;
        SkillContainer container;
        HurtEvent e=new HurtEvent(event);
        if(event.getEntity() instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.getEntity());
            runEvent((ServerPlayer) event.getEntity(), EventType.beHurt.getFn(), containerSelf,e);
            updateDate(cap);
            if(e.isCanceled()){
                event.setCanceled(true);
                return;
            }
        }
        if(event.getSource().getEntity() instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.getSource().getEntity());
            runEvent((ServerPlayer) event.getSource().getEntity(), EventType.onHurt.getFn(), containerSelf,e);
            updateDate(cap);
            if(e.isCanceled()){
                event.setCanceled(true);
                return;
            }
        }
        event.setAmount((e.damageAdd*e.damageAddScale) + (event.getAmount()*e.damageScale));
        if(event.getSource() instanceof EpicFightDamageSource){
            EpicFightDamageSource source=(EpicFightDamageSource) event.getSource();
            source.setImpact(source.getImpact()*e.impactScale + e.impactAdd*e.impactAddScale);
            source.setArmorNegation(source.getArmorNegation()*e.ArmorNegationScale + e.ArmorNegationAdd*e.ArmorNegationAddScale);
        }
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void ExecuteEpicSkill(ExecuteSkillEvent event){
        if(event.isCanceled()){return;}
        ITkkCapability cap=getSkillData(event.serverPlayer);
        if(cap==null){return;}
        EpicSkillEvent e=new EpicSkillEvent(event);
        runEvent(event.serverPlayer, EventType.onExecuteSkill.getFn(), containerSelf,e);
        updateDate(cap);
        if(e.isCanceled()){
            event.setCanceled(true);
        }
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void tryHurt(TryHurtEvent event){
        if(event.isCanceled()){return;}
        ITkkCapability cap;
        if(event.patch.getOriginal() instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.patch.getOriginal());
            runEvent((ServerPlayer) event.patch.getOriginal(),"beTryHurt", containerSelf,event);
            updateDate(cap);
        }
        if(event.getAttacker()!=null && event.getAttacker() instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.getAttacker());
            runEvent((ServerPlayer) event.getAttacker(), "onTryHurt", containerSelf,event);
            updateDate(cap);
        }
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void livingDeathEvent(LivingDeathEvent event){
        if(event.isCanceled()){return;}
        ITkkCapability cap;
        if(event.getEntity() instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.getEntity());
            runEvent((ServerPlayer) event.getEntity(), EventType.beDeath.getFn(),containerSelf,event);
            updateDate(cap);
        }
        if(event.isCanceled()){return;}
        if(event.getSource()!=null && event.getSource().getEntity() instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.getSource().getEntity());
            runEvent((ServerPlayer) event.getSource().getEntity(), EventType.onDeath.getFn(),containerSelf,event);
            updateDate(cap);
        }
    }


    /*
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void HitStunAndKnockback(HitStunAndKnockbackEvent event){
        if(event.isCanceled()){return;}
        ITkkCapability cap;
        SkillContainer container;
        StunAndKnockbackEvent e=new StunAndKnockbackEvent(event);
        if(event.sourceEvent.getSource().getEntity() instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.sourceEvent.getSource().getEntity());
            runEvent((ServerPlayer) event.sourceEvent.getSource().getEntity(), EventType.onTargetHitStunAndKnockback, containerSelf,e);
            updateDate(cap);
            if(e.isCanceled()){
                event.setCanceled(true);
                return;
            }
        }
        if(event.hitEntity instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.hitEntity);
            runEvent((ServerPlayer) event.hitEntity, EventType.onSelfHitStunAndKnockback, containerSelf,e);
            updateDate(cap);
            if(e.isCanceled()){
                event.setCanceled(true);
                return;
            }
        }
        if(e.resetStunAnimation){event.hitAnimation=event.hitEntity.hasEffect(EpicFightMobEffects.STUN_IMMUNITY.get()) ? null : event.hitentitypatch.getHitAnimation(ExtendedDamageSource.StunType.LONG);}
        event.extendStunTime=((e.extendStunTimeAdd*e.extendStunTimeAddScale) + (event.extendStunTime*e.extendStunTimeScale));
        event.knockBackAmount=((e.knockBackAmountAdd*e.knockBackAmountAddScale) + (event.knockBackAmount*e.knockBackAmountScale));
    }

    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void PreGuardIsBlockable(GuardIsBlockableEvent event){
        if(event.isCanceled()){return;}
        ITkkCapability attackCap=null;
        ITkkCapability hurtCap=null;
        if(event.entity instanceof ServerPlayer){
            hurtCap=getSkillData((ServerPlayer) event.entity);
        }
        if(event.damageSource.getEntity() instanceof ServerPlayer){
            attackCap=getSkillData((ServerPlayer) event.damageSource.getEntity());
        }

        GuardEvent e=new GuardEvent(event);

        if(attackCap!=null){
            runEvent((ServerPlayer) event.damageSource.getEntity(), EventType.attackPreGuard, containerSelf,e);
        }
        if(hurtCap!=null) {
            runEvent((ServerPlayer) event.entity, EventType.preGuard, containerSelf,e);
        }
        if(!e.canBlock){
            if(attackCap!=null){updateDate(attackCap);};
            if(hurtCap!=null){updateDate(hurtCap);}
            return;
        }


    }
    @SubscribeEvent(priority= EventPriority.LOW)
    public static void GuardIsBlockable(GuardIsBlockableEvent event){
        if(!event.canBlock){return;}
        ITkkCapability attackCap=null;
        ITkkCapability hurtCap=null;
        if(event.entity instanceof ServerPlayer){
            hurtCap=getSkillData((ServerPlayer) event.entity);
        }
        if(event.damageSource.getEntity() instanceof ServerPlayer){
            attackCap=getSkillData((ServerPlayer) event.damageSource.getEntity());
        }

        GuardEvent e=new GuardEvent(event);


        if(attackCap!=null){
            runEvent((ServerPlayer) event.damageSource.getEntity(), EventType.attackOnGuard, containerSelf,e);
        }
        if(hurtCap!=null) {
            runEvent((ServerPlayer) event.entity, EventType.onGuard, containerSelf,e);
        }
        if(!e.guardSucceed){
            if(attackCap!=null){updateDate(attackCap);};
            if(hurtCap!=null){updateDate(hurtCap);}
            return;
        }
        if(attackCap!=null){updateDate(attackCap);};
        if(hurtCap!=null){updateDate(hurtCap);}

        event.impact=e.getImpact(event.impact);
        event.knockback=e.getKnockback(event.knockback);
        event.consumeAdd=e.consumeAdd;
        event.consumeScale=e.consumeScale;
        event.consumeAddScale=e.consumeAddScale;
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onAttackAnimationEnd(AttackAnimationEndEvent event){
        if(!(event.getEntity() instanceof ServerPlayer)){return;}
        ITkkCapability cap=getSkillData((ServerPlayer) event.getEntity());
        runEvent((ServerPlayer) event.getEntity(), EventType.AttackAnimationEnd, containerSelf,event);
        updateDate(cap);
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onUpdateHeldItem(UpdateHeldItemEvent event){
        ITkkCapability cap=getSkillData((ServerPlayer) event.player);
        runEvent((ServerPlayer) event.player, EventType.updateHeldItem, containerSelf,event);
        updateDate(cap);
    }


     */

    /*
    *
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void SpellKeyPress(SpellKeybindingPressEvent event){
        ITkkCapability cap=getSkillData(event.player);
        SkillContainer target=cap.getSkillContainer(event.spellId);
        target.skill.pressKeySelf(target,event);
        SkillContainer container;
        for(int i=0;i<SKILL_SIZE;i++){
            if(i== event.spellId){continue;}
            container=cap.getSkillContainer(i);
            container.skill.pressKeyOther(container,target,event);
        }
        updateDate(cap);
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void SpellKeyUp(SpellKeybindingUpEvent event){
        ITkkCapability cap=getSkillData(event.player);
        SkillContainer target=cap.getSkillContainer(event.spellId);
        target.skill.upKeySelf(target,event);
        SkillContainer container;
        for(int i=0;i<SKILL_SIZE;i++){
            if(i== event.spellId){continue;}
            container=cap.getSkillContainer(i);
            container.skill.upKeyOther(container,target,event);
        }
        updateDate(cap);
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ITkkCapability cap=event.getPlayer().getCapability(TkkCapabilityProvider.TKK_CAPABILITY,null).orElse(null);
        if(cap!=null){
            for(int i=0;i<SKILL_SIZE;i++){
                cap.getSkillContainer(i).update();
            }

        }
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(!(event.player instanceof ServerPlayer)){return;}
        if(event.phase == TickEvent.Phase.END){return;}
        ITkkCapability cap=getSkillData((ServerPlayer) event.player);
        SkillContainer container;
        for(int i=0;i<SKILL_SIZE;i++){
            container=cap.getSkillContainer(i);
            if(container.cooldown!=0){container.cooldown-=1;}
            container.skill.tick(container);
        }
        updateDate(cap);
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onAttack(LivingAttackEvent event){
        if(event.isCanceled()){return;}
        ITkkCapability cap;
        SkillContainer container;
        AttackEvent e=new AttackEvent(event);
        if(event.getEntityLiving() instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.getEntityLiving());
            for(int i=0;i<SKILL_SIZE;i++){
                container=cap.getSkillContainer(i);
                container.skill.beAttack(container,e);
            }
            updateDate(cap);
            if(e.isCanceled()){
                event.setCanceled(true);
                return;
            }
        }
        if(event.getSource().getEntity() instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.getSource().getEntity());
            for(int i=0;i<SKILL_SIZE;i++){
                container=cap.getSkillContainer(i);
                container.skill.onAttack(container,e);
            }
            updateDate(cap);
            if(e.isCanceled()){
                event.setCanceled(true);
                return;
            }
        }
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onHurt(LivingHurtEvent event){
        if(event.isCanceled()){return;}
        ITkkCapability cap;
        SkillContainer container;
        HurtEvent e=new HurtEvent(event);
        if(event.getEntityLiving() instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.getEntityLiving());
            for(int i=0;i<SKILL_SIZE;i++){
                container=cap.getSkillContainer(i);
                container.skill.beHurt(container,e);
            }
            updateDate(cap);
            if(e.isCanceled()){
                event.setCanceled(true);
                return;
            }
        }
        if(event.getSource().getEntity() instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.getSource().getEntity());
            for(int i=0;i<SKILL_SIZE;i++){
                container=cap.getSkillContainer(i);
                container.skill.onHurt(container,e);
            }
            updateDate(cap);
            if(e.isCanceled()){
                event.setCanceled(true);
                return;
            }
        }
        event.setAmount((e.damageAdd*e.damageAddScale) + (event.getAmount()*e.damageScale));
        if(event.getSource() instanceof ExtendedDamageSource){
            ExtendedDamageSource source=(ExtendedDamageSource) event.getSource();
            source.setImpact(source.getImpact()*e.impactScale + e.impactAdd*e.impactAddScale);
            source.setArmorNegation(source.getArmorNegation()*e.ArmorNegationScale + e.ArmorNegationAdd*e.ArmorNegationAddScale);
        }
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void ExecuteEpicSkill(ExecuteSkillEvent event){
        if(event.isCanceled()){return;}
        ITkkCapability cap;
        SkillContainer container;
        EpicSkillEvent e=new EpicSkillEvent(event);
        cap=getSkillData(event.serverPlayer);
        for(int i=0;i<SKILL_SIZE;i++){
            container=cap.getSkillContainer(i);
            container.skill.onExecuteSkill(container,e);
        }
        updateDate(cap);
        if(e.isCanceled()){
            event.setCanceled(true);
        }
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void HitStunAndKnockback(HitStunAndKnockbackEvent event){
        if(event.isCanceled()){return;}
        ITkkCapability cap;
        SkillContainer container;
        StunAndKnockbackEvent e=new StunAndKnockbackEvent(event);
        if(event.sourceEvent.getSource().getEntity() instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.sourceEvent.getSource().getEntity());
            for(int i=0;i<SKILL_SIZE;i++){
                container=cap.getSkillContainer(i);
                container.skill.onTargetHitStunAndKnockback(container,e);
            }
            updateDate(cap);
            if(e.isCanceled()){
                event.setCanceled(true);
                return;
            }
        }
        if(event.hitEntity instanceof ServerPlayer){
            cap=getSkillData((ServerPlayer) event.hitEntity);
            for(int i=0;i<SKILL_SIZE;i++){
                container=cap.getSkillContainer(i);
                container.skill.onSelfHitStunAndKnockback(container,e);
            }
            updateDate(cap);
            if(e.isCanceled()){
                event.setCanceled(true);
                return;
            }
        }
        if(e.resetStunAnimation){event.hitAnimation=event.hitEntity.hasEffect(EpicFightMobEffects.STUN_IMMUNITY.get()) ? null : event.hitentitypatch.getHitAnimation(ExtendedDamageSource.StunType.LONG);}
        event.extendStunTime=((e.extendStunTimeAdd*e.extendStunTimeAddScale) + (event.extendStunTime*e.extendStunTimeScale));
        event.knockBackAmount=((e.knockBackAmountAdd*e.knockBackAmountAddScale) + (event.knockBackAmount*e.knockBackAmountScale));
    }

    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void PreGuardIsBlockable(GuardIsBlockableEvent event){
        if(event.isCanceled()){return;}
        ITkkCapability attackCap=null;
        ITkkCapability hurtCap=null;
        if(event.entity instanceof ServerPlayer){
            hurtCap=getSkillData((ServerPlayer) event.entity);
        }
        if(event.damageSource.getEntity() instanceof ServerPlayer){
            attackCap=getSkillData((ServerPlayer) event.damageSource.getEntity());
        }

        GuardEvent e=new GuardEvent(event);

        SkillContainer container;
        if(attackCap!=null){
            for(int i=0;i<SKILL_SIZE;i++){
                container=attackCap.getSkillContainer(i);
                container.skill.attackPreGuard(container,e);
            }
        }
        if(hurtCap!=null) {
            for (int i = 0; i < SKILL_SIZE; i++) {
                container = hurtCap.getSkillContainer(i);
                container.skill.preGuard(container, e);
            }
        }
        if(!e.canBlock){
            if(attackCap!=null){updateDate(attackCap);};
            if(hurtCap!=null){updateDate(hurtCap);}
            return;
        }


    }
    @SubscribeEvent(priority= EventPriority.LOW)
    public static void GuardIsBlockable(GuardIsBlockableEvent event){
        if(!event.canBlock){return;}
        ITkkCapability attackCap=null;
        ITkkCapability hurtCap=null;
        if(event.entity instanceof ServerPlayer){
            hurtCap=getSkillData((ServerPlayer) event.entity);
        }
        if(event.damageSource.getEntity() instanceof ServerPlayer){
            attackCap=getSkillData((ServerPlayer) event.damageSource.getEntity());
        }

        GuardEvent e=new GuardEvent(event);

        SkillContainer container;
        if(attackCap!=null){
            for(int i=0;i<SKILL_SIZE;i++){
                container=attackCap.getSkillContainer(i);
                container.skill.attackOnGuard(container,e);
            }
        }
        if(hurtCap!=null) {
            for (int i = 0; i < SKILL_SIZE; i++) {
                container = hurtCap.getSkillContainer(i);
                container.skill.onGuard(container, e);
            }
        }
        if(!e.guardSucceed){
            if(attackCap!=null){updateDate(attackCap);};
            if(hurtCap!=null){updateDate(hurtCap);}
            return;
        }
        if(attackCap!=null){updateDate(attackCap);};
        if(hurtCap!=null){updateDate(hurtCap);}

        event.impact=e.getImpact(event.impact);
        event.knockback=e.getKnockback(event.knockback);
        event.consumeAdd=e.consumeAdd;
        event.consumeScale=e.consumeScale;
        event.consumeAddScale=e.consumeAddScale;
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onAttackAnimationEnd(AttackAnimationEndEvent event){
        if(!(event.getEntity() instanceof ServerPlayer)){return;}
        ITkkCapability cap=getSkillData((ServerPlayer) event.getEntity());
        SkillContainer container;
        for(int i=0;i<SKILL_SIZE;i++){
            container=cap.getSkillContainer(i);
            container.skill.AttackAnimationEnd(container,event);
        }
        updateDate(cap);
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void livingDeathEvent(LivingDeathEvent event){
        if(event.getEntity() instanceof ServerPlayer){
            ITkkCapability cap=getSkillData((ServerPlayer) event.getEntity());
            SkillContainer container;
            for(int i=0;i<SKILL_SIZE;i++){
                container=cap.getSkillContainer(i);
                container.skill.beDeath(container,event);
            }
            updateDate(cap);
        }
        if(event.isCanceled()){return;}
        if(event.getSource()!=null && event.getSource().getEntity() instanceof ServerPlayer){
            ITkkCapability cap=getSkillData((ServerPlayer) event.getSource().getEntity());
            SkillContainer container;
            for(int i=0;i<SKILL_SIZE;i++){
                container=cap.getSkillContainer(i);
                container.skill.onDeath(container,event);
            }
            updateDate(cap);
        }
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onUpdateHeldItem(UpdateHeldItemEvent event){
        ITkkCapability cap=getSkillData((ServerPlayer) event.player);
        SkillContainer container;
        for(int i=0;i<SKILL_SIZE;i++){
            container=cap.getSkillContainer(i);
            container.skill.updateHeldItem(container,event);
        }
        updateDate(cap);
    }

*/

    public enum EventType{
        loadSelf,
        loadOther,
        unloadSelf,
        unloadOther,
        pressKeySelf,
        pressKeyOther,
        upKeySelf,
        upKeyOther,
        tick,
        beAttack,
        onAttack,
        beHurt,
        onHurt,
        onExecuteSkill,
        preGuard,
        onGuard,
        attackPreGuard,
        attackOnGuard,
        onSelfHitStunAndKnockback,
        onTargetHitStunAndKnockback,
        AttackAnimationEnd,
        beDeath,
        onDeath,
        updateHeldItem;

        private final String fn;
        EventType(){
            this.fn=null;
        }
        EventType(String fn){
            this.fn=fn;
        }
        public String getFn(){
            if(this.fn==null){return this.toString();}
            return this.fn;
        }
    }


    public static class AttackEvent{
        public LivingAttackEvent event;
        public boolean canceled=false;
        public AttackEvent(LivingAttackEvent e){
            event=e;
        }
        public void setCanceled(boolean b){
            canceled=b;
        }
        public boolean isCanceled(){
            return canceled;
        }
    }
    public static class HurtEvent{
        public LivingHurtEvent event;
        public boolean canceled=false;
        public float damageAdd=0.0f;
        public float damageScale=1.0f;
        public float damageAddScale=1.0f;

        public float impactAdd=0.0f;
        public float impactScale=1.0f;
        public float impactAddScale=1.0f;

        public float ArmorNegationAdd=0.0f;
        public float ArmorNegationScale=1.0f;
        public float ArmorNegationAddScale=1.0f;

        public HurtEvent(LivingHurtEvent e){
            event=e;
        }
        public void setCanceled(boolean b){
            canceled=b;
        }
        public boolean isCanceled(){
            return canceled;
        }


        public float getDamageAdd() {
            return damageAdd;
        }

        public void setDamageAdd(float damageAdd) {
            this.damageAdd = damageAdd;
        }

        public float getDamageScale() {
            return damageScale;
        }

        public void setDamageScale(float damageScale) {
            this.damageScale = damageScale;
        }

        public float getDamageAddScale() {
            return damageAddScale;
        }

        public void setDamageAddScale(float damageAddScale) {
            this.damageAddScale = damageAddScale;
        }
    }
    public static class EpicSkillEvent{
        public ExecuteSkillEvent event;
        public boolean canceled=false;
        public EpicSkillEvent(ExecuteSkillEvent e){
            event=e;
        }
        public void setCanceled(boolean b){
            canceled=b;
        }
        public boolean isCanceled(){
            return canceled;
        }
    }

    /*
    public static class StunAndKnockbackEvent{
        public HitStunAndKnockbackEvent event;
        public boolean canceled=false;
        private boolean resetStunAnimation=false;
        private ExtendedDamageSource.StunType stunType;

        public float extendStunTimeAdd=0.0f;
        public float extendStunTimeScale=1.0f;
        public float extendStunTimeAddScale=1.0f;
        public float knockBackAmountAdd=0.0f;
        public float knockBackAmountScale=1.0f;
        public float knockBackAmountAddScale=1.0f;
        public StunAndKnockbackEvent(HitStunAndKnockbackEvent e){
            event=e;
            stunType=e.stunType;
        }
        public void setCanceled(boolean b){
            canceled=b;
            event.hitAnimation=null;
            event.extendStunTime=0;
        }
        public boolean isCanceled(){
            return canceled;
        }


        public ExtendedDamageSource.StunType getStunType() {
            return stunType;
        }

        public void setStunType(ExtendedDamageSource.StunType stunType) {
            this.stunType = stunType;
            this.resetStunAnimation=true;
        }
    }
    public static class GuardEvent{
        public final GuardIsBlockableEvent sourceEvent;
        private boolean canBlock;
        public boolean guardSucceed;


        public float consumeAdd=0.0f;
        public float consumeScale=1.0f;
        public float consumeAddScale=1.0f;

        public float knockbackAdd=0.0f;
        public float knockbackScale=1.0f;
        public float knockbackAddScale=1.0f;

        public float impactAdd=0.0f;
        public float impactScale=1.0f;
        public float impactAddScale=1.0f;

        public GuardEvent(GuardIsBlockableEvent event){
            sourceEvent=event;
            canBlock=event.canBlock;
            guardSucceed=event.canBlock;
        }


        public boolean isCanBlock() {
            return canBlock;
        }

        public void setCanBlock(boolean canBlock) {
            this.canBlock = canBlock;
        }

        public void canceledDefaultGuard(){
            sourceEvent.canBlock=false;
        }

        public float getConsume(float source){
            return source*consumeScale + consumeAdd*consumeAddScale;
        }
        public float getKnockback(float source){
            return source*knockbackScale + knockbackAdd*knockbackAddScale;
        }
        public float getImpact(float source){
            return source*impactScale + impactAdd*impactAddScale;
        }


    }

     */




}

