package com.example.mixin;

import carpet.script.language.Sys;
import com.example.extensions.Settings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.level.portal.TeleportTransition;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(EndGatewayBlock.class)
public class EndGatewayBlockMixin {
	@Inject(method = "getPortalDestination", at = @At("RETURN"), cancellable = true)
	public void onGetPortalDestination(ServerLevel currentLevel, Entity entity, BlockPos portalEntryPos,
									   CallbackInfoReturnable<TeleportTransition> cir) {
		TeleportTransition original = cir.getReturnValue();
		if (original == null) return;

		String mode = Settings.endGatewayLoadChunk;
		boolean shouldSkip = switch (mode) {
			case "never" -> true;
			case "item_only" -> !(entity instanceof ItemEntity);
			case "non_player_only" -> !(entity instanceof ServerPlayer);
			default -> false;
		};
		System.out.println(shouldSkip);
		if (shouldSkip) {
			cir.setReturnValue(new TeleportTransition(
					original.newLevel(),
					original.position(),
					original.deltaMovement(),
					original.yRot(),
					original.xRot(),
					original.missingRespawnBlock(),
					original.asPassenger(),
					original.relatives(),
					TeleportTransition.DO_NOTHING
			));
		}
	}
}