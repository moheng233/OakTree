package io.github.redstoneparadox.oaktree.client.gui.style;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.redstoneparadox.oaktree.client.gui.OakTreeGUI;
import io.github.redstoneparadox.oaktree.mixin.client.gui.DrawableHelperAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;

public class NinePatchStyleBox extends TextureStyleBox {
    int firstWidth = 1;
    int secondWidth = 1;
    int thirdWidth = 1;

    int firstHeight = 1;
    int secondHeight = 1;
    int thirdHeight = 1;

    public NinePatchStyleBox(String path) {
        super(path);
    }

    public NinePatchStyleBox heights(int firstHeight, int secondHeight, int thirdHeight) {
        this.firstHeight = firstHeight;
        this.secondHeight = secondHeight;
        this.thirdHeight = thirdHeight;
        return this;
    }

    public NinePatchStyleBox widths(int firstWidth, int secondWidth, int thirdWidth) {
        this.firstWidth = firstWidth;
        this.secondWidth = secondWidth;
        this.thirdWidth = thirdWidth;
        return this;
    }

    @Override
    public void draw(float x, float y, float width, float height, OakTreeGUI gui, boolean mirroredHorizontal, boolean mirroredVertical) {
        GlStateManager.color4f(1.0f,1.0f, 1.0f, 1.0f);
        MinecraftClient.getInstance().getTextureManager().bindTexture(textureID);
        if (gui instanceof DrawableHelper) {

            int xPatches = (int) ((width - firstWidth - thirdWidth)/secondWidth) + 2;
            int yPatches = (int) ((height - firstHeight - thirdHeight)/secondHeight) + 2;

            int currentX = (int) x;
            int currentY = (int) y;

            for (int yPatch = 1; yPatch <= yPatches; yPatch += 1) {
                for (int xPatch = 1; xPatch <= xPatches; xPatch += 1) {
                    // Top Left Corner
                    if (xPatch == 1 && yPatch == 1) {
                        ((DrawableHelperAccessor) gui).invokeBlit(currentX, currentY, drawLeft, drawTop, firstWidth, firstHeight);
                        currentX += firstWidth;
                    }
                    // Top Middle
                    else if (xPatch > 1 && xPatch < xPatches && yPatch == 1) {
                        int left = drawLeft + firstWidth;
                        ((DrawableHelperAccessor) gui).invokeBlit(currentX, currentY, left, drawTop, secondWidth, firstHeight);
                        currentX += secondWidth;
                    }
                    // Top Right Corner
                    else if (xPatch == xPatches && yPatch == 1) {
                        int left = drawLeft + firstWidth + secondWidth;
                        ((DrawableHelperAccessor) gui).invokeBlit(currentX, currentY, left, drawTop, thirdWidth, firstHeight);
                        currentX = (int) x;
                        currentY += firstHeight;
                    }
                    // Middle Left
                    else if (xPatch == 1 && yPatch > 1 && yPatch < yPatches) {
                        int top = drawTop + firstHeight;
                        ((DrawableHelperAccessor) gui).invokeBlit(currentX, currentY, drawLeft, top, firstWidth, secondHeight);
                        currentX += firstWidth;
                    }
                    // Center
                    else if (xPatch > 1 && xPatch < xPatches && yPatch > 1 && yPatch < yPatches){
                        int left = drawLeft + firstWidth;
                        int top = drawTop + firstHeight;
                        ((DrawableHelperAccessor) gui).invokeBlit(currentX, currentY, left, top, secondWidth, secondHeight);
                        currentX += secondWidth;
                    }
                    // Middle Right
                    else if (xPatch == xPatches && yPatch > 1 && yPatch < yPatches) {
                        int top = drawTop + firstHeight;
                        int left = drawLeft + firstWidth + secondWidth;
                        ((DrawableHelperAccessor) gui).invokeBlit(currentX, currentY, left, top, thirdWidth, secondHeight);
                        currentX = (int) x;
                        currentY += secondHeight;
                    }
                    // Bottom Left Corner
                    else if (xPatch == 1 && yPatch == yPatches) {
                        int top = drawTop + firstHeight + secondHeight;
                        ((DrawableHelperAccessor) gui).invokeBlit(currentX, currentY, drawLeft, top, firstWidth, thirdHeight);
                        currentX += firstWidth;
                    }
                    // Bottom Middle
                    else if (xPatch > 1 && xPatch < xPatches && yPatch == yPatches) {
                        int top = drawTop + firstHeight + secondHeight;
                        int left = drawLeft + firstWidth;
                        ((DrawableHelperAccessor) gui).invokeBlit(currentX, currentY, left, top, secondWidth, thirdHeight);
                        currentX += secondWidth;
                    }
                    // Bottom Right Corner
                    else {
                        int left = drawLeft + firstWidth + secondWidth;
                        int top = drawTop + firstHeight + secondHeight;
                        ((DrawableHelperAccessor) gui).invokeBlit(currentX, currentY, left, top, thirdWidth, thirdHeight);
                    }
                }
            }
        }
    }
}