package io.github.apace100.apoli.power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class ModifyItemRenderPower extends Power {
    private final Predicate<Pair<World, ItemStack>> itemCondition;
    private final Identifier model;

    public ModifyItemRenderPower(PowerType<?> type, LivingEntity entity, Identifier model, Predicate<Pair<World, ItemStack>> itemCondition) {
        super(type, entity);
        this.itemCondition = itemCondition;
        this.model = model;
    }

    public ModelIdentifier getModelIdentifier() {
        return new ModelIdentifier(this.model, "inventory");
    }

    public boolean doesApply(ItemStack stack) {
        return itemCondition == null || itemCondition.test(new Pair<>(entity.getWorld(), stack));
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(Apoli.identifier("modify_item_render"),
                new SerializableData()
                        .add("model", SerializableDataTypes.IDENTIFIER)
                        .add("item_condition", ApoliDataTypes.ITEM_CONDITION, null),
                data ->
                        (type, player) -> {
                            ModifyItemRenderPower power = new ModifyItemRenderPower(type, player, data.getId("model"), data.get("item_condition"));

                            return power;
                        })
                .allowCondition();
    }
}
