package net.redstoneparadox.oaktree.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.redstoneparadox.oaktree.client.gui.nodes.Node;

public class OakTreeScreen extends Screen implements OakTreeGUI {

    Node root;

    boolean shouldPause;

    public OakTreeScreen(Node treeRoot, boolean pause) {
        super(new LiteralText("gui"));
        root = treeRoot;
    }

    @Override
    public boolean isPauseScreen() {
        return shouldPause;
    }

    @Override
    public void init(MinecraftClient minecraftClient_1, int int_1, int int_2) {
        super.init(minecraftClient_1, int_1, int_2);
        root.setup(minecraftClient_1, int_1, int_2);
    }

    @Override
    public void render(int int_1, int int_2, float float_1) {
        root.preDraw(this, minecraft.window, 0, 0, width, height);
        root.draw(int_1, int_2, float_1, this,0, 0, width, height);

    }

    @Override
    public void drawString(String string, float xPos, float yPos, boolean withShadow) {

        if (withShadow) {
            font.drawWithShadow(string, xPos, yPos, 0);
        }
        else {
            font.draw(string, xPos, yPos, 0);
        }
    }

    @Override
    public void drawTexture(int posX, int posY, int left, int top, int width, int height) {
        blit(posX, posY, left, top, width, height);
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }
}
