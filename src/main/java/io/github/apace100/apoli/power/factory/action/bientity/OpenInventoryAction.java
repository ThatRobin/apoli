package io.github.apace100.apoli.power.factory.action.bientity;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.InventoryPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;

public class OpenInventoryAction {

    public static void action(SerializableData.Instance data, Pair<Entity, Entity> actorAndTarget) {
        if (!data.isPresent("power") || !(actorAndTarget.getLeft() instanceof PlayerEntity actorEntity && actorAndTarget.getRight() instanceof LivingEntity targetEntity)) return;

        PowerType<?> targetPowerType = data.get("power");
        Power targetPower = PowerHolderComponent.KEY.get(targetEntity).getPower(targetPowerType);

        if (!(targetPower instanceof InventoryPower inventoryPower)) return;

        inventoryPower.openInventory(actorEntity);
    }

    public static ActionFactory<Pair<Entity, Entity>> getFactory() {
        return new ActionFactory<>(Apoli.identifier("open_inventory"),
            new SerializableData()
                .add("power", ApoliDataTypes.POWER_TYPE, null),
            OpenInventoryAction::action
        );
    }

}
