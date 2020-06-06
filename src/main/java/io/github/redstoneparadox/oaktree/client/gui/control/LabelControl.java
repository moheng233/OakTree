package io.github.redstoneparadox.oaktree.client.gui.control;

import io.github.redstoneparadox.oaktree.client.RenderHelper;
import io.github.redstoneparadox.oaktree.client.TextHelper;
import io.github.redstoneparadox.oaktree.client.gui.Color;
import io.github.redstoneparadox.oaktree.client.gui.ControlGui;
import net.minecraft.class_5348;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LabelControl extends Control<LabelControl> {
	protected  @NotNull Text text = LiteralText.EMPTY;
	protected boolean shadow = false;
	public @NotNull Color fontColor = Color.WHITE;
	protected int maxLines = 1;
	protected boolean fitText = false;

	private @Nullable TextRenderer renderer = null;

	public LabelControl() {
		this.id = "label";
	}

	/**
	 * Sets the text for this LabelControl
	 * to display.
	 *
	 * @param text The text to display
	 * @return The control itself.
	 */
	public LabelControl text(String text) {
		return this.text(new LiteralText(text));
	}

	/**
	 * Sets the text for this LabelControl
	 * to display.
	 *
	 * @param text The text to display
	 * @return The control itself.
	 */
	public LabelControl text(Text text) {
		this.text = text;
		return this;
	}

	/**
	 * <p>Sets the text for this {@link LabelControl}
	 * to display. Useful for when you have a
	 * {@link List<Text>} and don't want to add
	 * the newlines yourself.</p>
	 *
	 * @param texts A {@link List<Text>}
	 * @return The {@link Control} for further modification.
	 */
	public LabelControl text(List<Text> texts) {
		if (fitText && renderer != null) {
			this.area.width = 0;

			for (Text text: texts) {
				this.area.width = Math.max(this.area.width, renderer.getWidth(text));
			}

			this.area.width += 8;
			area.height = TextHelper.getFontHeight() * texts.size() + 8;
		}

		this.text = TextHelper.combine(texts, true);
		this.maxLines = texts.size();
		return this;
	}

	/**
	 * Clears the LabelControl
	 *
	 * @return The control itself.
	 */
	public LabelControl clear() {
		this.text = LiteralText.EMPTY;
		return this;
	}

	public @NotNull Text getText() {
		return text;
	}

	/**
	 * Sets whether the text should be drawn with a shadow.
	 *
	 * @param shadow The value.
	 * @return The control itself.
	 */
	public LabelControl shadow(boolean shadow) {
		this.shadow = shadow;
		return this;
	}

	public boolean isShadow() {
		return shadow;
	}

	/**
	 * Sets the color of the font to be drawn. Note that transparency
	 * is ignored here due to Minecraft internals.
	 *
	 * @param fontColor The RGBA Color
	 * @return The control itself.
	 */
	public LabelControl fontColor(@NotNull Color fontColor) {
		this.fontColor = fontColor;
		return this;
	}

	public @NotNull Color getFontColor() {
		return fontColor;
	}

	/**
	 * Sets the maximum number of lines.
	 *
	 * @param maxLines The max number of lines.
	 * @return The control itself.
	 */
	public LabelControl maxLines(int maxLines) {
		if (maxLines > 0) this.maxLines = maxLines;
		return this;
	}

	public int getMaxLines() {
		return maxLines;
	}

	/**
	 * Sets whether or not this {@link LabelControl}
	 * should resize to fit its text. The only way
	 * to get newlines in this mode is to insert
	 * them yourself or pass a list to.
	 * {@link LabelControl#text(List)}
	 *
	 * @param fitText The value itself
	 * @return The {@link Control} for further
	 * 		modification.
	 */
	public LabelControl fitText(boolean fitText) {
		this.fitText = fitText;
		return this;
	}

	public boolean isFitText() {
		return fitText;
	}

	@Override
	public void setup(MinecraftClient client, ControlGui gui) {
		super.setup(client, gui);
		this.renderer = gui.getTextRenderer();
	}

	@Override
	public void draw(MatrixStack matrices, int mouseX, int mouseY, float deltaTime, ControlGui gui) {
		super.draw(matrices, mouseX, mouseY, deltaTime, gui);

		if (renderer != null) {
			List<class_5348> lines = TextHelper.wrapText(text, area.width, 0, maxLines, shadow);
			int yOffset = 0;
			for (class_5348 line : lines) {
				RenderHelper.drawText(matrices, line, trueX, trueY + yOffset, shadow, fontColor);
				yOffset += TextHelper.getFontHeight();
			}
		}
	}
}
