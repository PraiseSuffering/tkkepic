package tkk.epic.gameasset;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import tkk.epic.event.RegisterAiModuleEvent;
import tkk.epic.gameasset.aiModules.ClearGoalSelectorModule;
import tkk.epic.gameasset.aiModules.MoveToTargetModule;

public class AiModules {
    public static void register(RegisterAiModuleEvent event) {
        event.regModule(ClearGoalSelectorModule.ID,new ClearGoalSelectorModule());
        event.regModule(MoveToTargetModule.ID,new MoveToTargetModule());
    }
}
