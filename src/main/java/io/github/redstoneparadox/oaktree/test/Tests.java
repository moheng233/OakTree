package io.github.redstoneparadox.oaktree.test;

import io.github.redstoneparadox.oaktree.client.gui.ControlGui;
import io.github.redstoneparadox.oaktree.client.gui.control.*;
import io.github.redstoneparadox.oaktree.client.gui.style.Theme;
import io.github.redstoneparadox.oaktree.client.gui.util.ControlAnchor;
import io.github.redstoneparadox.oaktree.client.geometry.Direction2D;
import io.github.redstoneparadox.oaktree.networking.OakTreeNetworking;
import io.github.redstoneparadox.oaktree.util.InventoryScreenHandler;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Tests {

    public void init() {

        Identifier testThree = new Identifier("oaktree:test_three");

        register(new TestBlock(true, this::testOne), "one");
        register(new TestBlock(true, this::testTwo), "two");
        register(new ContainerTestBlock(true, this::testThree, testThree), "three");
        register(new TestBlock(true, this::testFour), "four");


        ScreenProviderRegistry.INSTANCE.registerFactory(testThree, (screenHandler -> {
            return new HandledTestScreen((TestScreenHandler) screenHandler, new LiteralText(""), true, testThree());
        }));
        ContainerProviderRegistry.INSTANCE.registerFactory(testThree, (syncId, identifier, player, buf) -> new TestScreenHandler(syncId, player));

        /*
        ScreenProviderRegistry.INSTANCE.registerFactory(testFourID, (syncId, identifier, player, buf) -> {
            BlockPos pos = buf.readBlockPos();
            return new ScreenBuilder
                    (
                            testFour()
                    )
                    .container(new TestScreenHandler(syncId, player))
                    .playerInventory(player.inventory)
                    .text(new LiteralText("Test 4"))
                    .buildContainerScreen();
        });

        ContainerProviderRegistry.INSTANCE.registerFactory(testFourID, (syncId, identifier, player, buf) -> {
            ScreenHandler screenHandler = new TestScreenHandler(syncId, player);
            OakTreeNetworking.addContainerForSyncing(screenHandler);
            return screenHandler;
        });
         */


    }

    private static Block.Settings testSettings() {
        return FabricBlockSettings.of(Material.METAL);
    }

    private void register(Block block, String suffix) {
        Registry.register(Registry.BLOCK, new Identifier("oaktree", "test_" + suffix), block);
    }

    class TestBlock extends Block {
        private final Supplier<Control<?>> supplier;
        private final boolean vanilla;

        TestBlock(boolean vanilla, Supplier<Control<?>> supplier) {
            super(testSettings());
            this.vanilla = vanilla;
            this.supplier = supplier;
        }

        @Override
        public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
            if (world.isClient) {
                MinecraftClient.getInstance().openScreen(new TestScreen(new LiteralText("test screen"), vanilla, supplier.get()));
            }
            return ActionResult.SUCCESS;
        }
    }

    class ContainerTestBlock extends TestBlock {
        private final Identifier containerID;

        ContainerTestBlock(boolean vanilla, Supplier<Control<?>> supplier, Identifier containerID) {
            super(vanilla, supplier);
            this.containerID = containerID;
        }

        @Override
        public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
            if (!world.isClient()) {
                ContainerProviderRegistry.INSTANCE.openContainer(containerID, player, (buf -> buf.writeBlockPos(pos)));
            }
            return ActionResult.SUCCESS;
        }
    }

    static class TestScreen extends Screen {
        private final ControlGui gui;

        protected TestScreen(Text title, boolean vanilla, Control<?> control) {
            super(title);
            this.gui = new ControlGui(this, control);
            if (vanilla) this.gui.applyTheme(Theme.vanilla());
        }

        @Override
        protected void init() {
            super.init();
            gui.init();
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            super.render(matrices, mouseX, mouseY, delta);
            gui.draw(matrices, mouseX, mouseY, delta);
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }
    }

    static class HandledTestScreen extends HandledScreen<TestScreenHandler> {
        private final ControlGui gui;

        public HandledTestScreen(TestScreenHandler handler, Text title, boolean vanilla, Control<?> control) {
            super(handler, handler.player.inventory, title);
            this.gui = new ControlGui(this, control);
            if (vanilla) this.gui.applyTheme(Theme.vanilla());
        }

        @Override
        protected void init() {
            super.init();
            gui.init();
        }

        @Override
        protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {

        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            super.render(matrices, mouseX, mouseY, delta);
            gui.draw(matrices, mouseX, mouseY, delta);
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }

        @Override
        public void onClose() {
            super.onClose();
            handler.close(handler.player);
        }

        @Override
        public void removed() {
            super.removed();
        }
    }

    static class TestScreenHandler extends ScreenHandler implements InventoryScreenHandler {
        private final PlayerEntity player;
        private final List<Inventory> inventories = new ArrayList<>();

        protected TestScreenHandler(int syncId, PlayerEntity player) {
            super(null, syncId);
            this.player = player;
            inventories.add(player.inventory);

            if (!player.world.isClient) OakTreeNetworking.listenForStackSync(this);
            inventories.add(new SimpleInventory(ItemStack.EMPTY, ItemStack.EMPTY));
        }

        @Override
        public boolean canUse(PlayerEntity player) {
            return true;
        }

        @Override
        public @Nullable Inventory getInventory(int inventoryID) {
            return inventories.get(inventoryID);
        }

        @Override
        public @NotNull PlayerEntity getPlayer() {
            return player;
        }

        @Override
        public int getSyncID() {
            return syncId;
        }

        @Override
        public void close(PlayerEntity player) {
            super.close(player);
            OakTreeNetworking.stopListening(this);
        }
    }

    private Control<?> testOne() {
        DropdownControl leftDropdown = new DropdownControl(
                new ListPanelControl()
                        .id("base")
                        .size(80, 80)
                        .children(4, this::itemLabel)
                        .displayCount(4)
        )
                .size(40, 20)
                .id("button")
                .dropdownDirection(Direction2D.LEFT)
                .anchor(ControlAnchor.CENTER);

        DropdownControl rightDropdown = new DropdownControl(
                new ListPanelControl()
                        .id("base")
                        .size(80, 80)
                        .children(4, this::itemLabel)
                        .displayCount(4)
        )
                .size(40, 20)
                .id("button")
                .dropdownDirection(Direction2D.RIGHT)
                .anchor(ControlAnchor.CENTER);


        return new PanelControl<>()
                .child(new DropdownControl(
                        new ListPanelControl()
                                .id("base")
                                .size(60, 60)
                                .child(leftDropdown)
                                .child(rightDropdown)
                                .displayCount(2)
                        )
                        .size(60, 20)
                        .id("button")
                        .anchor(ControlAnchor.CENTER)
                )
                .size(90, 50)
                .anchor(ControlAnchor.CENTER)
                .id("base");
    }

    private Control<?> testTwo() {
        ListPanelControl listPanel = new ListPanelControl()
                .size(100, 100)
                .children(20, this::itemLabel)
                .displayCount(5)
                .anchor(ControlAnchor.CENTER);

        SliderControl scrollBar = new SliderControl()
                .size(20, 100)
                .onSlide((gui, control) -> {
                    listPanel.startIndex((int) Math.floor(((listPanel.children.size() - listPanel.displayCount) * (control.scrollPercent)/100)));
                })
                .barLength(10)
                .anchor(ControlAnchor.CENTER);

        return new SplitPanelControl()
                .everyOther()
                .id("base")
                .size(140, 120)
                .splitSize(30)
                .child(scrollBar)
                .child(listPanel)
                .anchor(ControlAnchor.CENTER);
    }

    private Control<?> testThree() {
        GridPanelControl playerInvGrid = new GridPanelControl()
                .size(162, 72)
                .anchor(ControlAnchor.CENTER)
                .rows(4)
                .columns(9)
                .children(36, integer -> {
                    int index = integer;
                    if (integer < 27) {
                        index += 9;
                    }
                    else {
                        index -= 27;
                    }

                    return new SlotControl(index, 0);
                });

        SlotControl slot1 = new SlotControl(0, 1)
                .filter(Items.ANDESITE);

        SlotControl slot2 = new SlotControl(1, 1)
                .canTake((slotControl, stack) -> false);

        return new SplitPanelControl()
                .id("base")
                .size(180, 120)
                .splitSize(30)
                .anchor(ControlAnchor.CENTER)
                .child(
                        new GridPanelControl()
                                .size(36, 18)
                                .rows(1).columns(2)
                                .child(slot1).child(slot2)
                )
                .child(playerInvGrid);
    }

    private Control<?> itemLabel(int number) {
        return new LabelControl()
                .size(60, 20)
                .text("Item No. " + (number + 1))
                .anchor(ControlAnchor.CENTER)
                .shadow(true);
    }

    private Control<?> testFour() {
        return new ButtonControl()
                .id("button")
                .size(200, 20)
                .anchor(ControlAnchor.CENTER)
                .tooltip(
                        new LabelControl()
                                .text("Hi!")
                                .size(40, 20)
                );
    }
}
