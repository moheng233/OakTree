package io.github.redstoneparadox.oaktree.test;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class TestBlockEntities {
	static class TestBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
		public TestBlockEntity(BlockEntityType<TestBlockEntity> type, BlockPos pos, BlockState state) {
			super(type, pos, state);
		}

		@Override
		public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
			buf.writeBlockPos(pos);
		}

		@Override
		public Text getDisplayName() {
			return LiteralText.EMPTY;
		}

		@Override
		public @Nullable ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
			return new TestScreens.TestScreenHandler(i, playerEntity);
		}
	}
}