package tkk.epic.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import tkk.epic.skill.Skill;
import tkk.epic.skill.Skills;

public class RegisterTkkSkillEvent extends Event implements IModBusEvent {
    public void regSkill(Skill obj){
        Skills.SKILL_HASH_MAP.put(obj.getSkillId(),obj);
    }
}
