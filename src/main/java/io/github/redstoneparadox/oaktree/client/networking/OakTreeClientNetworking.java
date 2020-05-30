package io.github.redstoneparadox.oaktree.client.networking;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class OakTreeClientNetworking {

    private static final Identifier SYNC_SLOT = new Identifier("oaktree", "sync_slot");
    public static final Identifier SYNC_STACK = new Identifier("oaktree", "sync_stack");

    public static void syncSlot(int x, int y, int index, int syncID) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeIntArray(new int[]{x, y, index, syncID});
        ClientSidePacketRegistry.INSTANCE.sendToServer(SYNC_SLOT, buf);
    }

    public static void syncStack(int slot, int inventoryID, int syncID, ItemStack stack) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeIntArray(new int[]{slot, inventoryID, syncID});
        buffer.writeItemStack(stack);
        ClientSidePacketRegistry.INSTANCE.sendToServer(SYNC_STACK, buffer);
    }
}
