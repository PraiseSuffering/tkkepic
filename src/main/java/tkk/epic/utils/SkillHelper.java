package tkk.epic.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class SkillHelper {

    public static LivingEntity getNearestEntityForYP(Entity entity, float checkRange, int checkCount){
        return (LivingEntity) getEntityForYP(entity.level(), entity.position(), entity.getYRot(), entity.getXRot(), checkRange, checkCount, new Predicate<Entity>() {
            public final Entity self=entity;
            @Override
            public boolean test(Entity entity) {
                if (entity==this.self){return false;}
                return entity instanceof LivingEntity;
            }
        });
    }

    public static Entity getEntityForYP(Level world, Vec3 pos, float yaw, float pitch, float checkRange, int checkCount, Predicate<Entity> predicate){
        double yawCos = Math.cos((Math.PI / 180)*(-yaw));
        double yawSin = Math.sin((Math.PI / 180)*(-yaw));
        double pitchCos = Math.cos((Math.PI / 180)*(pitch));
        double pitchSin = Math.sin((Math.PI / 180)*(pitch));
        double y= -1 * pitchSin;
        double z= 1 * pitchCos;
        double x= z * yawSin;
        z= z * yawCos;
        Comparator<Entity> comparator=new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                double aDistance=o1.position().distanceTo(pos);
                double bDistance=o2.position().distanceTo(pos);
                return Double.compare(aDistance, bDistance);
            }
        };
        for (int i=0;i<checkCount;i++){
            float add= i*(checkRange/2.0f);
            AABB aabb=new AABB(0,0,0,1,1,1).move(pos.add(x*add,y*add,z*add)).inflate(checkRange);
            List<Entity> list = world.getEntitiesOfClass(Entity.class,aabb);
            list.sort(comparator);
            for (Entity entity:list){
                if(predicate.test(entity)){
                    return entity;
                }
            }
        }
        return null;
    }
}
