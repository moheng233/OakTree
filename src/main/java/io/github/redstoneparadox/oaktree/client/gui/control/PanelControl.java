package io.github.redstoneparadox.oaktree.client.gui.control;

import io.github.redstoneparadox.oaktree.client.gui.OakTreeGUI;
import io.github.redstoneparadox.oaktree.client.gui.util.ScreenVec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A {@link Control} containing any number of children
 * {@link Control} instances. Children are drawn
 * without any arrangement applied to them; subclasses
 * offer various arrangement options.
 *
 * @param <C> The {@link PanelControl} type.
 */
public class PanelControl<C extends PanelControl> extends PaddingControl<C> {
    public final List<Control> children = new ArrayList<>();

    public PanelControl() {
        this.id = "panel";
    }

    /**
     * Adds a child to this PanelControl.
     *
     * @param child The child control.
     * @return The panel control itself.
     */
    public C child(Control child) {
        children.add(child);
        return (C) this;
    }

    /**
     * Adds the specified number of children to this PanelControl;
     * children are supplied by the function which is passed the
     * index for that child.
     *
     * @param count The amount of children to add.
     * @param function The function to supply children.
     * @return The PanelControl itself.
     */
    public C children(int count, Function<Integer, Control<?>> function) {
        children.clear();
        for (int i  = 0; i < count; i++) {
            children.add(function.apply(i));
        }
        return (C) this;
    }

    @Override
    public void setup(MinecraftClient client, OakTreeGUI gui) {
        super.setup(client, gui);
        for (Control child: children) {
            child.setup(client, gui);
        }
    }

    @Override
    public void preDraw(int mouseX, int mouseY, float deltaTime, OakTreeGUI gui, float offsetX, float offsetY, float containerWidth, float containerHeight) {
        if (!visible) return;
        super.preDraw(mouseX, mouseY, deltaTime, gui, offsetX, offsetY, containerWidth, containerHeight);
        arrangeChildren(mouseX, mouseY, deltaTime, gui);
    }

    void arrangeChildren(int mouseX, int mouseY, float deltaTime, OakTreeGUI gui) {
        ScreenVec innerPosition = innerPosition(trueX, trueY);
        ScreenVec innerDimensions = innerDimensions(width, height);

        for (Control child: children) {
            if (child != null) child.preDraw(mouseX, mouseY, deltaTime, gui, innerPosition.x, innerPosition.y, innerDimensions.x, innerDimensions.y);
        }
    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float deltaTime, OakTreeGUI gui) {
        if (!visible) return;
        super.draw(matrices, mouseX, mouseY, deltaTime, gui);
        for (Control child: children) {
            if (child != null && shouldDraw(child)) child.draw(matrices, mouseX, mouseY, deltaTime, gui);
        }
    }

    boolean shouldDraw(Control child) {
        return true;
    }
}
