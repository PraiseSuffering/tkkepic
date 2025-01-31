package tkk.epic.skill.skills;

import tkk.epic.skill.Skill;
import tkk.epic.skill.SkillContainer;

public class EmptySkill extends Skill {
    public static final String skillId="EmptySkill";

    public EmptySkill(){
        this.addDefaultListener();
    }

    @Override
    public String getSkillId() {
        return skillId;
    }

    //自身装配
    public void loadSelf(SkillContainer container){
        container.doRender=false;
        readyUpdate(container);
    }
}
