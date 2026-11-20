
package com.healpot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class HealPotClient implements ClientModInitializer {

    private KeyBinding healKey;
    private int restoreTicks = -1;

    @Override
    public void onInitializeClient() {

        healKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.healpot.use",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.healpot"
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (client.player == null || client.world == null) return;

            if (restoreTicks >= 0) {
                if (restoreTicks == 0) {
                    client.player.inventory.selectedSlot = 0;
                    restoreTicks = -1;
                } else {
                    restoreTicks--;
                }
            }

            if (client.currentScreen != null || restoreTicks >= 0) return;

            if (healKey.wasPressed()) {
                usePotion(client);
            }
        });
    }

    private void usePotion(MinecraftClient client) {

        for (int slot = 8; slot >= 3; slot--) {

            ItemStack stack = client.player.inventory.getStack(slot);

            if (stack.getItem() == Items.SPLASH_POTION) {

                if (PotionUtil.getPotion(stack) == Potions.HEALING
                    || PotionUtil.getPotion(stack) == Potions.STRONG_HEALING) {

                    client.player.inventory.selectedSlot = slot;

                    client.interactionManager.interactItem(
                        client.player,
                        client.world,
                        Hand.MAIN_HAND
                    );

                    restoreTicks = 1;
                    return;
                }
            }
        }
    }
}
