package io.github.redstoneparadox.oaktree.control;

import io.github.redstoneparadox.oaktree.listeners.ClientListeners;
import io.github.redstoneparadox.oaktree.listeners.MouseButtonListener;
import io.github.redstoneparadox.oaktree.networking.InventoryScreenHandlerAccess;
import io.github.redstoneparadox.oaktree.painter.Theme;
import io.github.redstoneparadox.oaktree.util.Color;
import io.github.redstoneparadox.oaktree.util.RenderHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Predicate;

/**
 * <p>A {@link Control} that can be used for interacting
 * with items in an inventory. Rather than delegating to
 * a vanilla {@link net.minecraft.screen.slot.Slot}, it
 * is implemented from scratch and requires your
 * {@link net.minecraft.screen.ScreenHandler} to
 * implement {@link InventoryScreenHandlerAccess} in order to
 * work. Syncing inventory contents is also handled
 * through the same interface so avoid implementing that
 * yourself.</p>
 */
public class SlotControl extends Control implements MouseButtonListener {
	protected @NotNull Color highlightColor = Color.rgba(0.75f, 0.75f, 0.75f, 0.5f);
	protected int slotBorder = 1;
	protected @NotNull Predicate<ItemStack> canInsert = (stack) -> true;
	protected @NotNull Predicate<ItemStack> canTake = (stack) -> true;
	protected boolean locked = false;

	private final int slot;
	private final PlayerEntity player;
	private final Inventory inventory;
	private boolean leftClicked = false;
	private boolean rightClicked = false;
	private boolean highlighted = false;

	/**
	 * @param slot The index of the "slot" in your
	 *             {@link Inventory} implementation.
	 * @param player The player interacting with this control.
	 * @param inventory The inventory to access.
	 */
	public SlotControl(int slot, PlayerEntity player, Inventory inventory) {
		this.slot = slot;
		this.player = player;
		this.inventory = inventory;
		this.id = "item_slot";

		LabelControl tooltip = new LabelControl();
		tooltip.setId("tooltip");
		tooltip.setText("");
		tooltip.setShadow(true);
		tooltip.setFitText(true);
		tooltip.visible = false;

		this.tooltip = tooltip;
		this.setSize(18, 18);

		ClientListeners.MOUSE_BUTTON_LISTENERS.add(this);
	}

	/**
	 * Sets what color should be used to highlight this {@link SlotControl}
	 * when hovering over it in the GUI. Default value is
	 * {@code Color.rgba(0.75f, 0.75f, 0.75f, 0.5f)}.
	 *
	 * @param highlightColor The color to be used.
	 */
	public void setHighlightColor(@NotNull Color highlightColor) {
		this.highlightColor = highlightColor;
	}

	public @NotNull Color getHighlightColor() {
		return highlightColor;
	}

	public void setSlotBorder(int slotBorder) {
		this.slotBorder = slotBorder;
	}

	public int getSlotBorder() {
		return slotBorder;
	}

	public void canInsert(@NotNull Predicate<ItemStack> canInsert) {
		this.canInsert = canInsert;
	}

	/**
	 * Helper method for setting a list of {@link Item }
	 * to match against when inserting into slot.
	 *
	 * @param allow Whether matching an item in the list
	 *              should allow or deny inserting a stack.
	 * @param items The items to allow/deny.
	 * @return The {@link SlotControl} for further modification.
	 */
	public SlotControl filter(boolean allow, Item... items) {
		this.canInsert = ((stack) -> {
			for (Item item: items) {
				if (stack.getItem() == item) return allow;
			}
			return !allow;
		});
		return this;
	}

	/**
	 * Helper method for setting a list of {@link Tag<Item>}
	 * to match against when inserting into slot.
	 *
	 * @param allow Whether matching an item tag in the list
	 *              should allow or deny inserting a stack.
	 * @param tags The item tags to match against.
	 * @return The {@link SlotControl} for further modification.
	 */
	public SlotControl filter(boolean allow, Tag<Item>... tags) {
		this.canInsert = ((stack) -> {
			for (Tag<Item> tag: tags) {
				if (tag.contains(stack.getItem())) return allow;
			}
			return !allow;
		});
		return this;
	}

	public void canTake(@NotNull Predicate<ItemStack> canTake) {
		this.canTake = canTake;
	}

	/**
	 * Used to disable or enable interactions with
	 * this {@link SlotControl}.
	 *
	 * @param locked Whether the slot should be locked.
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isLocked() {
		return locked;
	}

	@Override
	protected boolean interact(int mouseX, int mouseY, float deltaTime, boolean captured) {
		captured = super.interact(mouseX, mouseY, deltaTime, captured);

		if (captured) {
			highlighted = true;
			boolean stackChanged = false;
			ItemStack stackInSlot = inventory.getStack(slot);
			ScreenHandler handler = player.currentScreenHandler;

			if (handler.getCursorStack().isEmpty()) {
				if (!locked && canTake.test(stackInSlot)) {
					if (leftClicked) {
						handler.setCursorStack(inventory.removeStack(slot));
						stackChanged = true;
					}
					else if (rightClicked) {
						handler.setCursorStack(inventory.removeStack(slot, Math.max(stackInSlot.getCount()/2, 1)));
						stackChanged = true;
					}
				}
			}
			else {
				ItemStack cursorStack = handler.getCursorStack();

				if (!locked && canInsert.test(cursorStack)) {
					if (leftClicked) {
						if (stackInSlot.isEmpty()) {
							inventory.setStack(slot, handler.getCursorStack());
							handler.setCursorStack(ItemStack.EMPTY);
							stackChanged = true;
						}
						else {
							combineStacks(cursorStack, stackInSlot, cursorStack.getCount());
							stackChanged = true;
						}
					}
					else if (rightClicked) {
						if (stackInSlot.isEmpty()) {
							inventory.setStack(slot, handler.getCursorStack().split(1));
							stackChanged = true;
						}
						else {
							combineStacks(cursorStack, stackInSlot, 1);
							stackChanged = true;
						}
					}
				}

			}

			//TODO: Find out why I actually need to do this and fix the problem at the source.
			leftClicked = false;
			rightClicked = false;

			stackInSlot = inventory.getStack(slot);

			if (stackChanged) {
				inventory.markDirty();
			}
			if (tooltip instanceof LabelControl) {
				if (!stackInSlot.isEmpty()) {
					List<Text> texts = stackInSlot.getTooltip(player, TooltipContext.Default.NORMAL);
					((LabelControl) tooltip).setText(texts);
					tooltip.setVisible(true);
				}
				else {
					((LabelControl) tooltip).clearText();
					tooltip.setVisible(false);
				}
			}

			if (stackInSlot.isEmpty() && tooltip != null) tooltip.visible = false;
		} else {
			tooltip.setVisible(false);
			highlighted = false;
		}

		return captured;
	}

	@Override
	protected void draw(MatrixStack matrices, Theme theme) {
		super.draw(matrices, theme);

		int x = trueArea.getX();
		int y = trueArea.getY();

		ItemStack stack = inventory.getStack(slot);
		RenderHelper.drawItemStackCentered(x, y, trueArea.getWidth(), trueArea.getHeight(), stack);

		if (highlighted) {
			RenderHelper.setzOffset(200.0);
			RenderHelper.drawRectangle(matrices, x + slotBorder, y + slotBorder, trueArea.getWidth() - (2 * slotBorder), trueArea.getHeight() - (2 * slotBorder), highlightColor);
			RenderHelper.setzOffset(0.0);
		}
	}

	private void combineStacks(ItemStack from, ItemStack to, int amount) {
		if (from.getCount() < amount) {
			combineStacks(from, to, from.getCount());
		}
		else if (to.getMaxCount() - to.getCount() < amount) {
			combineStacks(from, to, to.getMaxCount() - to.getCount());
		}
		else if (ItemStack.areItemsEqual(from, to)) {
			from.decrement(amount);
			to.increment(amount);
		}
	}

	@Override
	public void onMouseButton(int button, boolean justPressed, boolean released) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			leftClicked = justPressed;
		}
		else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			rightClicked = justPressed;
		}
	}
}
