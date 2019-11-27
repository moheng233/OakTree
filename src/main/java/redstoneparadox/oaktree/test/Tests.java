package redstoneparadox.oaktree.test;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import redstoneparadox.oaktree.client.gui.OakTreeScreen;
import redstoneparadox.oaktree.client.gui.ScreenBuilder;
import redstoneparadox.oaktree.client.gui.control.*;
import redstoneparadox.oaktree.client.gui.style.ColorStyleBox;
import redstoneparadox.oaktree.client.gui.style.ItemStyleBox;
import redstoneparadox.oaktree.client.gui.util.ControlAnchor;
import redstoneparadox.oaktree.client.gui.util.ControlDirection;
import redstoneparadox.oaktree.client.gui.util.RGBAColor;
import redstoneparadox.oaktree.networking.OakTreeNetworking;

public class Tests {

    private final Block TEST_ONE_BLOCK = new TestOneBlock(testSettings());
    private final Block TEST_TWO_BLOCK = new TestTwoBlock(testSettings());
    private final Block TEST_THREE_BLOCK = new TestThreeBlock(testSettings());
    private final Block TEST_FOUR_BLOCK = new TestFourBlock(testSettings());
    private final Block TEST_FIVE_BLOCK = new TestFiveBlock(testSettings());
    private final Block TEST_SIX_BLOCK = new TestSixBlock(testSettings());
    private final Block TEST_SEVEN_BLOCK = new TestSevenBlock(testSettings());

    static Identifier TEST_SIX = new Identifier("oak_tree", "test_six");

    public void init() {
        register(TEST_ONE_BLOCK, "one");
        register(TEST_TWO_BLOCK, "two");
        register(TEST_THREE_BLOCK, "three");
        register(TEST_FOUR_BLOCK, "four");
        register(TEST_FIVE_BLOCK, "five");
        register(TEST_SIX_BLOCK, "six");
        register(TEST_SEVEN_BLOCK, "seven");

        ScreenProviderRegistry.INSTANCE.registerFactory(TEST_SIX, (int syncId, Identifier identifier, PlayerEntity player, PacketByteBuf buf) -> {
            BlockPos pos = buf.readBlockPos();
            return new ScreenBuilder(
                    new GridControl()
                            .setCellSize(16.0f, 16.0f)
                            .setCellSpacing(10.0f, 10.0f)
                            .setRows(3)
                            .setColumns(3)
                            .setExpand(true)
                            .setVisible(false)
                            .cells((row, column, index) -> new ItemSlotControl(index))
                    )
                    .container(new TestSixContainer(syncId, pos, player))
                    .playerInventory(player.inventory)
                    .text(new LiteralText("Test Six GUI"))
                    .buildContainerScreen();
        });

        ContainerProviderRegistry.INSTANCE.registerFactory(TEST_SIX, ((int syncId, Identifier identifier, PlayerEntity player, PacketByteBuf buff) -> {
            BlockPos pos = buff.readBlockPos();
            Container container = new TestSixContainer(syncId, pos, player);
            OakTreeNetworking.addContainerForSyncing(container);
            return container;
        }));
    }

    private Block.Settings testSettings() {
        return FabricBlockSettings.of(Material.METAL).build();
    }

    private void register(Block block, String testNum) {
        Registry.register(Registry.BLOCK, new Identifier("oak_tree", "test_" + testNum + "_block"), block);
    }

    private static class TestOneBlock extends Block {

        TestOneBlock(Settings block$Settings_1) {
            super(block$Settings_1);
        }

        @Override
        public ActionResult onUse(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1, Hand hand_1, BlockHitResult blockHitResult_1) {
            if (world_1.isClient()) {
                BoxControl root = new BoxControl()
                        .setChild(new Control()
                            .setDefaultStyle(new ColorStyleBox(RGBAColor.red()))
                            .setSize(75.0f, 50.0f)
                            .setAnchor(ControlAnchor.CENTER))
                        .setExpand(true)
                        .setMargin(15.0f);


                MinecraftClient.getInstance().openScreen(new OakTreeScreen(root, false, null));
            }

            return ActionResult.SUCCESS;
        }
    }

    private static class TestTwoBlock extends Block {

        TestTwoBlock(Settings block$Settings_1) {
            super(block$Settings_1);
        }

        @Override
        public ActionResult onUse(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1, Hand hand_1, BlockHitResult blockHitResult_1) {
            if (world_1.isClient()) {
                SplitBoxControl root = new SplitBoxControl()
                        .setExpand(true)
                        .setSplitPercent(50.0f)
                        .setFirstMargin(10.0f)
                        .setSecondMargin(10.0f)
                        .setFirstChild(new Control()
                                        .setExpand(true)
                                        .setDefaultStyle(new ColorStyleBox(RGBAColor.red())))
                        .setSecondChild(new Control()
                                        .setExpand(true)
                                        .setDefaultStyle(new ColorStyleBox(RGBAColor.blue())));

                MinecraftClient.getInstance().openScreen(new OakTreeScreen(root, false, null));
            }

            return ActionResult.SUCCESS;
        }
    }

    private static class TestThreeBlock extends Block {

        TestThreeBlock(Settings block$Settings_1) {
            super(block$Settings_1);
        }

        @Override
        public ActionResult onUse(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1, Hand hand_1, BlockHitResult blockHitResult_1) {
            if (world_1.isClient()) {
                SplitBoxControl root = new SplitBoxControl()
                        .setExpand(true)
                        .setSplitPercent(50.0f)
                        .setFirstMargin(10.0f)
                        .setSecondMargin(10.0f)
                        .setFirstChild(new ProgressBarControl()
                                .setDefaultStyle(new ColorStyleBox(RGBAColor.black()))
                                .setBarStyle(new ColorStyleBox(RGBAColor.red()))
                                .setSize(20.0f, 100.0f)
                                .setBarSize(16.0f, 96.0f)
                                .setAnchor(ControlAnchor.CENTER)
                                .setDirection(ControlDirection.DOWN)
                                .setPercent(50.0f))
                        .setSecondChild(new Control()
                                .setExpand(true)
                                .setDefaultStyle(new ColorStyleBox(RGBAColor.blue())));

                MinecraftClient.getInstance().openScreen(new OakTreeScreen(root, false, null));
            }

            return ActionResult.SUCCESS;
        }
    }

    private static class TestFourBlock extends Block {

        TestFourBlock(Settings block$Settings_1) {
            super(block$Settings_1);
        }

        @Override
        public ActionResult onUse(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1, Hand hand_1, BlockHitResult blockHitResult_1) {
            if (world_1.isClient()) {
                ProgressBarControl bar = new ProgressBarControl();

                SplitBoxControl root = new SplitBoxControl()
                        .setExpand(true)
                        .setSplitPercent(50.0f)
                        .setFirstMargin(10.0f)
                        .setSecondMargin(10.0f)
                        .setSecondChild(bar
                                .setDefaultStyle(new ColorStyleBox(RGBAColor.black()))
                                .setBarStyle(new ColorStyleBox(RGBAColor.red()))
                                .setSize(20.0f, 100.0f)
                                .setBarSize(16.0f, 96.0f)
                                .setAnchor(ControlAnchor.CENTER)
                                .setDirection(ControlDirection.DOWN)
                                .setPercent(0.0f))
                        .setFirstChild(new ButtonControl()
                                .setExpand(true)
                                .onClick(((gui, node) -> {
                                    System.out.println("I was clicked!");
                                    node.setVisible(false);
                                    bar.percent += 10.0f;
                                    if (bar.percent > 100.0f) {
                                        bar.percent = 0.0f;
                                    }
                                }))
                                .setAnchor(ControlAnchor.CENTER)
                                .setDefaultStyle(new ColorStyleBox(RGBAColor.blue()))
                                .setHeldStyle(new ColorStyleBox(RGBAColor.red())));

                MinecraftClient.getInstance().openScreen(new OakTreeScreen(root, false, null));
            }

            return ActionResult.SUCCESS;
        }
    }

    private static class TestFiveBlock extends Block {

        TestFiveBlock(Settings block$Settings_1) {
            super(block$Settings_1);
        }

        @Override
        public ActionResult onUse(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1, Hand hand_1, BlockHitResult blockHitResult_1) {
            if (world_1.isClient()) {
                BoxControl root = new BoxControl()
                        .setSize(16, 16)
                        .setAnchor(ControlAnchor.CENTER)
                        .setSize(16, 16)
                        .setDefaultStyle(new ColorStyleBox(RGBAColor.black()))
                        .setChild(new Control()
                                .setDefaultStyle(new ItemStyleBox("stone"))
                                .setAnchor(ControlAnchor.CENTER));

                MinecraftClient.getInstance().openScreen(new OakTreeScreen(root, false, null));
            }

            return ActionResult.SUCCESS;
        }
    }

    //Test 6
    private static class TestSixBlock extends Block {

        TestSixBlock(Settings block$Settings_1) {
            super(block$Settings_1);
        }

        @Override
        public ActionResult onUse(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1, Hand hand_1, BlockHitResult blockHitResult_1) {
            if (!world_1.isClient()) {
                ContainerProviderRegistry.INSTANCE.openContainer(TEST_SIX, playerEntity_1, (packetByteBuf -> packetByteBuf.writeBlockPos(blockPos_1)));
            }

            return ActionResult.SUCCESS;
        }
    }

    private static class TestSixContainer extends Container {
        final PlayerInventory playerInventory;
        BlockPos pos;

        TestSixContainer(int syncID, BlockPos pos, PlayerEntity player) {
            super(null, syncID);
            this.pos = pos;
            playerInventory = player.inventory;

            for (int i = 0; i < 9; i++) {
                this.addSlot(new Slot(playerInventory, i, 0, 0));
            }
        }

        @Override
        public boolean canUse(PlayerEntity var1) {
            return true;
        }
    }

    private static class TestSevenBlock extends Block {

        public TestSevenBlock(Settings block$Settings_1) {
            super(block$Settings_1);
        }

        @Override
        public ActionResult onUse(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1, Hand hand_1, BlockHitResult blockHitResult_1) {
            if (world_1.isClient) {
                Screen screen = new ScreenBuilder(
                        new BoxControl()
                                .setExpand(true)
                                .setDefaultStyle(new ColorStyleBox(RGBAColor.black()))
                                .setChild(new DraggableControl()
                                        .whileHeld((gui, node) -> System.out.println("I'm being dragged!"))
                                        .setSize(16.0f, 16.0f)
                                        .setDefaultStyle(new ColorStyleBox(RGBAColor.white())))
                ).build();

                MinecraftClient.getInstance().openScreen(screen);
            }

            return ActionResult.SUCCESS;
        }
    }
}
