package io.github.redstoneparadox.oaktree.networking;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

@Deprecated
public interface InventoryScreenHandlerAccess {
   @NotNull Inventory getInventory(int inventoryID);

   @NotNull PlayerEntity getPlayer();

   int getSyncID();
}
