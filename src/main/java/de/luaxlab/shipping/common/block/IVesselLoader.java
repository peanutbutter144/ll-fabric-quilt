package de.luaxlab.shipping.common.block;

import de.luaxlab.shipping.common.util.LinkableEntity;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public interface IVesselLoader {
    enum Mode {
        EXPORT,
        IMPORT
    }

    static <T extends Component> T getEntityCapability(BlockPos pos, ComponentKey<T> capability, Level level){
        List<Entity> fluidEntities = level.getEntities((Entity) null,
                getSearchBox(pos),
                (e -> entityPredicate(e, pos, capability))
        );

        if(fluidEntities.isEmpty()){
            return null;
        } else {
            Entity entity = fluidEntities.get(0);
            return entity.getComponent(capability);
        }
    }

    static boolean entityPredicate(Entity entity, BlockPos pos, ComponentKey<?> capability) {
        var cap = entity.getComponent(capability);
        if (cap != null) {
            if (entity instanceof LinkableEntity l) {
                return l.allowDockInterface() && (l.getBlockPos().getX() == pos.getX() && l.getBlockPos().getZ() == pos.getZ());
            } else {
                return true;
            }
        }
        return false;
    }

    static AABB getSearchBox(BlockPos pos) {
        return new AABB(
                pos.getX() ,
                pos.getY(),
                pos.getZ(),
                pos.getX() + 1D,
                pos.getY() + 1D,
                pos.getZ() + 1D);
    }

    <T extends Entity & LinkableEntity<T>> boolean hold(T vehicle, Mode mode);

}
