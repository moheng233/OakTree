package io.github.redstoneparadox.oaktree.mixin.client.gui.screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Screen.class)
public interface ScreenAccessor {

    @Accessor("textRenderer")
    abstract TextRenderer getTextRenderer();
}
