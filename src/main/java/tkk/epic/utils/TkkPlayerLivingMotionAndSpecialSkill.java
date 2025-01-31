package tkk.epic.utils;

import com.google.common.collect.LinkedHashMultimap;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.Skill;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TkkPlayerLivingMotionAndSpecialSkill {

    public final HashMap<LivingMotion, LinkedHashMultimap<Integer, StaticAnimation>> livingAnimation = new HashMap<>();
    public final LinkedHashMultimap<Integer,Skill> specialAttack = LinkedHashMultimap.create();

    public void clearSkill(){
        specialAttack.clear();
    }
    public void clearLivingMotion(){
        this.livingAnimation.clear();
    }


    public final void addLivingMotion(LivingMotion livingMotion,StaticAnimation staticAnimation,int priority){
        if(!livingAnimation.containsKey(livingMotion)){
            livingAnimation.put(livingMotion, LinkedHashMultimap.create());
        }
        livingAnimation.get(livingMotion).put(priority,staticAnimation);
    }
    public final void removeLivingMotion(LivingMotion livingMotion,StaticAnimation staticAnimation,int priority){
        if(!livingAnimation.containsKey(livingMotion)){
            return;
        }
        livingAnimation.get(livingMotion).remove(priority,staticAnimation);
    }
    public final void replaceLivingMotion(Map<LivingMotion, StaticAnimation> map){
        for (LivingMotion livingMotion:livingAnimation.keySet()){
            LinkedHashMultimap<Integer, StaticAnimation> lhm=livingAnimation.get(livingMotion);
            Iterator<StaticAnimation> staticAnimations=lhm.get(Collections.max(lhm.keySet())).iterator();
            StaticAnimation staticAnimation=null;
            while (staticAnimations.hasNext()){
                staticAnimation=staticAnimations.next();
            }
            if(staticAnimation!=null){
                map.put(livingMotion,staticAnimation);
            }

        }
    }

    public final void addSpecialAttack(int priority,Skill skill){
        specialAttack.put(priority,skill);
    }
    public final void removeSpecialAttack(int priority,Skill skill){
        specialAttack.remove(priority,skill);
    }
    public final Skill getSkill(Skill source){
        if(specialAttack.isEmpty()){return source;}
        Iterator<Skill> staticAnimations=specialAttack.get(Collections.max(specialAttack.keySet())).iterator();
        Skill staticAnimation=null;
        while (staticAnimations.hasNext()){
            staticAnimation=staticAnimations.next();
        }
        return staticAnimation;
    }


}
