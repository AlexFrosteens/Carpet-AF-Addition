package com.example;

import carpet.CarpetServer;
import com.example.extensions.ALExtension;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarpetALAddition implements ModInitializer {
	public static final String MOD_ID = "carpetaladdition";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		CarpetServer.manageExtension(new ALExtension());
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
