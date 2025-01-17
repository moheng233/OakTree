package io.github.redstoneparadox.oaktree.test;

import io.github.redstoneparadox.oaktree.control.Anchor;
import io.github.redstoneparadox.oaktree.control.ButtonControl;
import io.github.redstoneparadox.oaktree.control.GridPanelControl;
import io.github.redstoneparadox.oaktree.control.LabelControl;
import io.github.redstoneparadox.oaktree.control.RootPanelControl;
import io.github.redstoneparadox.oaktree.control.SliderControl;
import io.github.redstoneparadox.oaktree.control.SlotControl;
import io.github.redstoneparadox.oaktree.math.Rectangle;
import io.github.redstoneparadox.oaktree.util.Color;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestScreens {
	public static void init() {
		ScreenRegistry.register(
				TestScreenHandlers.TEST_INVENTORY_SCREEN_HANDLER,
				// TODO: Find out why the compiler doesn't like not having the cast here
				(ScreenRegistry.Factory<TestScreenHandlers.TestScreenHandler, HandledTestScreen>) (handler, inventory, text) -> new HandledTestScreen(handler, inventory, text, testInventory(inventory))
		);
	}

	public static RootPanelControl testDraw() {
		RootPanelControl root = new RootPanelControl();

		root.setAnchor(Anchor.CENTER);
		root.setSize(160, 160);
		root.setId("base");

		return root;
	}

	public static RootPanelControl testInteractables() {
		RootPanelControl root = new RootPanelControl();
		ButtonControl button = new ButtonControl();
		LabelControl label1 = new LabelControl();
		SliderControl slider = new SliderControl();
		LabelControl label2 = new LabelControl();

		root.setAnchor(Anchor.CENTER);
		root.setSize(160, 180);
		root.setId("base");
		root.addChild(button);
		root.addChild(label1);
		root.addChild(slider);
		root.addChild(label2);

		button.setAnchor(Anchor.TOP_CENTER);
		button.setSize(120, 20);
		button.setOffset(0, 20);
		button.onClick(() -> label1.setText("Button Pressed."));
		button.onRelease(() -> label1.setText("Button Released."));

		label1.setAnchor(Anchor.TOP_CENTER);
		label1.setSize(120, 20);
		label1.setOffset(0, 60);
		label1.setText("Button Not Pressed");
		label1.setFontColor(Color.BLACK);

		slider.setAnchor(Anchor.TOP_CENTER);
		slider.setSize(120, 20);
		slider.setBarLength(10);
		slider.setHorizontal(true);
		slider.setOffset(0, 100);
		slider.onSlide(() -> label2.setText("Scroll Percent = " + (int) slider.getScrollPercent() + "."));

		label2.setAnchor(Anchor.TOP_CENTER);
		label2.setSize(120, 20);
		label2.setOffset(0, 140);
		label2.setText("Bar Not Scrolled.");
		label2.setFontColor(Color.BLACK);

		return root;
	}

	public static RootPanelControl testInventory(PlayerInventory inventory) {
		RootPanelControl root = new RootPanelControl();
		GridPanelControl grid = new GridPanelControl();
		PlayerEntity player = inventory.player;

		root.setAnchor(Anchor.CENTER);
		root.setSize(178, 88);
		root.setId("base");

		grid.setSize(162, 72);
		grid.setAnchor(Anchor.CENTER);
		grid.setRows(4);
		grid.setColumns(9);
		grid.addChildren(36, false, integer -> {
			int index = integer < 27 ? integer + 9 : integer - 27;
			return new SlotControl(index, player, inventory);
		});
		root.addChild(grid);

		return root;
	}

	private static List<Text> createText(int lines) {
		List<Text> texts = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < lines; i += 1) {
			LiteralText text = new LiteralText("Text.");
			int j = random.nextInt(3);
			Text formatted = switch (j) {
				case 0 -> text.formatted(Formatting.BOLD);
				case 1 -> text.formatted(Formatting.ITALIC);
				case 2 -> text.formatted(Formatting.UNDERLINE);
				default -> throw new IllegalStateException("Unexpected value: " + j);
			};

			texts.add(formatted);
		}

		return texts;
	}

	static class TestScreen extends Screen {
		private final RootPanelControl root;

		protected TestScreen(Text title, RootPanelControl root) {
			super(title);
			this.root = root;
		}

		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			super.render(matrices, mouseX, mouseY, delta);
			root.render(matrices, mouseX, mouseY, delta);
		}

		@Override
		public boolean isPauseScreen() {
			return false;
		}
	}

	static class HandledTestScreen extends HandledScreen<TestScreenHandlers.TestScreenHandler> implements ScreenHandlerProvider<TestScreenHandlers.TestScreenHandler> {
		private final RootPanelControl root;

		public HandledTestScreen(TestScreenHandlers.TestScreenHandler handler, PlayerInventory playerInventory, Text title, RootPanelControl root) {
			super(handler, playerInventory, title);
			this.root = root;

			Rectangle area = root.getArea();

			this.backgroundWidth = area.getWidth();
			this.backgroundHeight = area.getHeight();
		}

		@Override
		protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
			return false;
		}

		@Override
		protected boolean isPointWithinBounds(int i, int j, int k, int l, double d, double e) {
			return true;
		}

		@Override
		protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {

		}

		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			super.render(matrices, mouseX, mouseY, delta);
			root.render(matrices, mouseX, mouseY, delta);
		}

		@Override
		public boolean isPauseScreen() {
			return super.isPauseScreen();
		}

		@Override
		public void onClose() {
			super.onClose();
		}
	}
}
