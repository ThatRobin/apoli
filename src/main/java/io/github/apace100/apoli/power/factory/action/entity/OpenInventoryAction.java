package io.github.apace100.apoli.power.factory.action.entity;

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

public class OpenInventoryAction {

    public static void action(SerializableData.Instance data, Entity entity) {
        if (!data.isPresent("power") || !(entity instanceof LivingEntity livingEntity)) return;

        PowerType<?> targetPowerType = data.get("power");
        Power targetPower = PowerHolderComponent.KEY.get(livingEntity).getPower(targetPowerType);

        if (!(targetPower instanceof InventoryPower inventoryPower)) return;

        inventoryPower.onUse();
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apoli.identifier("open_inventory"),
            new SerializableData()
                .add("power", ApoliDataTypes.POWER_TYPE, null),
            OpenInventoryAction::action
        );
    }

}
