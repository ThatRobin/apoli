package io.github.apace100.apoli.mixin;

import com.google.common.collect.Ordering;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.access.StatusEffectSpriteManagerAccess;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerStatusEffectPower;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mixin(AbstractInventoryScreen.class)
public class AbstractInventoryScreenMixin {

    @Unique
    private static final Identifier EFFECT_BACKGROUND_LARGE_TEXTURE = new Identifier("container/inventory/effect_background_large");
    @Unique
    private static final Identifier EFFECT_BACKGROUND_SMALL_TEXTURE = new Identifier("container/inventory/effect_background_small");

    @Inject(method = "drawStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStatusEffects()Ljava/util/Collection;"))
    private void drawPowerStatusEffectBackgrounds(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        int i = ((HandledScreenAccessor)this).getX() + ((HandledScreenAccessor)this).getBackgroundWidth() + 2;
        int j = ((AbstractInventoryScreen<?>)(Object)this).width - i;
        assert MinecraftClient.getInstance().player != null;
        Collection<StatusEffectInstance> collection = MinecraftClient.getInstance().player.getStatusEffects();
        List<PowerStatusEffectPower> powers = PowerHolderComponent.getPowers(MinecraftClient.getInstance().player, PowerStatusEffectPower.class);
        int k = 33;
        int size = collection.size() + powers.size();
        boolean bl = j >= 120;
        if (size > 5) {
            k = 132 / (size - 1);
        }
        int multiplier = collection.size();
        drawStatusEffectBackgrounds(context, i, k, multiplier, powers, bl);
        drawStatusEffectSprites(context, i, k, multiplier, powers, bl);
        if (bl) {
            drawStatusEffectDescriptions(context, i, k, multiplier, powers);
        } else if (mouseX >= i && mouseX <= i + 33) {
            int l = ((HandledScreenAccessor)this).getY();
            PowerStatusEffectPower statusEffectInstance = null;
            for (PowerStatusEffectPower statusEffectInstance2 : powers) {
                if (mouseY >= l && mouseY <= l + k) {
                    statusEffectInstance = statusEffectInstance2;
                }
                l += k;
            }
            if (statusEffectInstance != null) {
                //List<Text> list = List.of(this.getStatusEffectDescription(statusEffectInstance), StatusEffectUtil.getDurationText(statusEffectInstance, 1.0f));
                List<Text> list = List.of(statusEffectInstance.getType().getName(), Text.literal(StringHelper.formatTicks(statusEffectInstance.getRemainingTicks())));
                context.drawTooltip(MinecraftClient.getInstance().textRenderer, list, Optional.empty(), mouseX, mouseY);
            }
        }
    }

    @Unique
    private void drawStatusEffectSprites(DrawContext context, int x, int height, int multiplier, List<PowerStatusEffectPower> powers, boolean wide) {
        int i  = ((HandledScreenAccessor)this).getY();
        i += height * multiplier;

        for (PowerStatusEffectPower statusEffectPower : powers) {
            SpriteAtlasHolder spriteAtlasHolder = MinecraftClient.getInstance().getStatusEffectSpriteManager();
            Sprite sprite = ((StatusEffectSpriteManagerAccess) spriteAtlasHolder).apoli$getSprite(statusEffectPower.getSprite(Apoli.identifier("power_effect/icon")));
            context.drawSprite(x + (wide ? 6 : 7), i + 7, 0, 18, 18, sprite);
            i += height;
        }
    }

    @Unique
    private void drawStatusEffectBackgrounds(DrawContext context, int x, int height, int multiplier, List<PowerStatusEffectPower> powers, boolean wide) {
        int i = ((HandledScreenAccessor)this).getY();
        i += height * multiplier;

        for (PowerStatusEffectPower statusEffectPower : powers) {
            if (wide) {
                context.drawGuiTexture(statusEffectPower.getSprite(EFFECT_BACKGROUND_LARGE_TEXTURE), x, i, 120, 32);
            } else {
                context.drawGuiTexture(statusEffectPower.getSprite(EFFECT_BACKGROUND_SMALL_TEXTURE), x, i, 32, 32);
            }
            i += height;
        }
    }

    @Unique
    private void drawStatusEffectDescriptions(DrawContext context, int x, int height, int multiplier, List<PowerStatusEffectPower> powers) {
        int i = ((HandledScreenAccessor)this).getY();

        i += height * multiplier;

        for (PowerStatusEffectPower statusEffectPower : powers) {
            Text text = statusEffectPower.getType().getName();
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text, x + 10 + 18, i + 6, 0xFFFFFF);
            int ticks = MathHelper.floor((float)statusEffectPower.getRemainingTicks());
            Text text2 = Text.literal(StringHelper.formatTicks(ticks));
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text2, x + 10 + 18, i + 6 + 10, 0x7F7F7F);
            i += height;
        }
    }

}
