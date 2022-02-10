package io.github.redstoneparadox.oaktree.painter;

import net.minecraft.client.util.math.MatrixStack;

public class NinePatchPainter extends TexturePainter {
	int firstWidth = 1;
	int secondWidth = 1;
	int thirdWidth = 1;

	int firstHeight = 1;
	int secondHeight = 1;
	int thirdHeight = 1;

	public NinePatchPainter(String path) {
		super(path);
	}

	public NinePatchPainter heights(int firstHeight, int secondHeight, int thirdHeight) {
		this.firstHeight = firstHeight;
		this.secondHeight = secondHeight;
		this.thirdHeight = thirdHeight;
		return this;
	}

	public NinePatchPainter widths(int firstWidth, int secondWidth, int thirdWidth) {
		this.firstWidth = firstWidth;
		this.secondWidth = secondWidth;
		this.thirdWidth = thirdWidth;
		return this;
	}

	@Override
	public NinePatchPainter copy() {
		NinePatchPainter copy = (NinePatchPainter) super.copy();

		copy.heights(firstHeight, secondHeight, thirdHeight);
		copy.widths(firstWidth, secondWidth, thirdWidth);

		return copy;
	}

	@Override
	public void draw(MatrixStack matrices, int x, int y, int width, int height) {
		int fullSecondWidth = width - firstWidth - thirdWidth;
		int fullSecondHeight = height - firstHeight - thirdHeight;

		float secondX = x + firstWidth;
		float secondY = y + firstHeight;
		int secondLeft = drawLeft + firstWidth;
		int secondTop = drawTop + firstHeight;

		float thirdX = x + firstWidth + fullSecondWidth;
		float thirdY = y + firstHeight + fullSecondHeight;
		int thirdLeft = drawLeft + firstWidth + secondHeight;
		int thirdTop = drawTop + firstHeight + secondHeight;

		// Top left
		drawTexture(x, y, drawLeft, drawTop, firstWidth, firstHeight);
		// Top Middle
		drawTiled(secondX, y, secondLeft, drawTop, secondWidth, firstHeight, fullSecondWidth, firstHeight);
		// Top Right
		drawTexture(thirdX, y, thirdLeft, drawTop, thirdWidth, firstHeight);
		// Center Left
		drawTiled(x, secondY, drawLeft, secondTop, firstWidth, secondHeight, firstWidth, fullSecondHeight);
		// Center
		drawTiled(secondX, secondY, secondLeft, secondTop, secondWidth, secondHeight, fullSecondWidth, fullSecondHeight);
		// Center Right
		drawTiled(thirdX, secondY, thirdLeft, secondTop, thirdWidth, secondHeight, thirdWidth, fullSecondHeight);
		// Bottom left
		drawTexture(x, thirdY, drawLeft, thirdTop, firstWidth, thirdHeight);
		// Bottom Middle
		drawTiled(secondX, thirdY, secondLeft, thirdTop, secondWidth, thirdHeight, fullSecondWidth, thirdHeight);
		// Bottom Right
		drawTexture(thirdX, thirdY, thirdLeft, thirdTop, thirdWidth, thirdHeight);
	}
}