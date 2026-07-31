package com.example.mixin;

import com.example.ChatComponentHelper;
import com.example.extensions.Settings;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @ModifyVariable(
            method = "addMessage",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private Component modifyMessageBeforeSplit(
            Component contents,
            Component unknown,
            MessageSignature signature,
            GuiMessageSource source,
            GuiMessageTag tag
    ) {
        // 判断是否为系统服务器消息，且包含 /data 相关文本
        if (source == GuiMessageSource.SYSTEM_SERVER) {
            String plainText = contents.toString();
            if (plainText.contains("key='commands.data")) {
                Component res = contents.copy();
                if(Settings.dataCommandSimplify){
                    res = ChatComponentHelper.simplifyDataCommand(res);
                }

                if(Settings.dataCommandEnhance){
                    res = ChatComponentHelper.enhanceDataCommand(res);
                }

                return res;
            }
        }
        // 不作处理
        return contents;
    }
}