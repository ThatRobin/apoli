package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.access.StatusEffectSpriteManagerAccess;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpriteAtlasHolder.class)
public abstract class StatusEffectSpriteManagerMixin implements StatusEffectSpriteManagerAccess {
    @Shadow protected abstract Sprite getSprite(Identifier objectId);

    @Override
    public Sprite apoli$getSprite(Identifier objectId) {
        return this.getSprite(objectId);
    }
}
