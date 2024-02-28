package io.github.apace100.apoli.power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.apoli.util.TextureUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class PowerStatusEffectPower extends CooldownPower {

    private final Map<Identifier, Identifier> textureMap;
    private final ActionFactory<?>.Instance actionOnApplied;
    private final ActionFactory<?>.Instance actionOnRemoved;
    private final ActionFactory<?>.Instance actionOverTime;

    public PowerStatusEffectPower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, Map<Identifier, Identifier> textureMap, ActionFactory<?>.Instance actionOnApplied, ActionFactory<?>.Instance actionOnRemoved, ActionFactory<?>.Instance actionOverTime) {
        super(type, entity, cooldownDuration, hudRender);
        this.textureMap = textureMap;
        this.actionOnApplied = actionOnApplied;
        this.actionOnRemoved = actionOnRemoved;
        this.actionOverTime = actionOverTime;
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(Apoli.identifier("power_status_effect"),
                new SerializableData()
                        .add("action_on_applied", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("action_on_removed", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("action_over_time", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("interval", SerializableDataTypes.INT, 20)
                        .add("cooldown", SerializableDataTypes.INT, 0)
                        .add("texture_map", ApoliDataTypes.IDENTIFIER_MAP, new HashMap<>())
                        .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER),
                data ->
                        (type, player) -> {
                            ActionFactory<?>.Instance actionOnApplied = data.get("action_on_applied");
                            ActionFactory<?>.Instance actionOnRemoved = data.get("action_on_applied");
                            ActionFactory<?>.Instance actionOverTime = data.get("action_on_applied");
                            Map<Identifier, Identifier> textureMap = data.get("texture_map");
                            return new PowerStatusEffectPower(type, player, data.getInt("cooldown"), data.get("hud_render"), textureMap, actionOnApplied, actionOnRemoved, actionOverTime);
                        })
                .allowCondition();
    }

    public Identifier getSprite(Identifier fallbackTexture) {
        Identifier id = fallbackTexture;
        if(this.textureMap.containsKey(fallbackTexture)) {
            Identifier textureID = this.textureMap.get(fallbackTexture);
            id = TextureUtil.tryLoadingSprite(textureID, TextureUtil.GUI_ATLAS_TEXTURE).result().orElseGet(() -> {
                Apoli.LOGGER.warn("Attempt to use texture \"" + textureID + "\" failed, using fallback \"" + fallbackTexture);
                return fallbackTexture;
            });
        }
        return id;
    }


}
