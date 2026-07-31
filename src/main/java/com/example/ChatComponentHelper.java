package com.example;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatComponentHelper {
    public static Component printTree(Component component) {
        ComponentTreePrinter.printTree(component);
        return component;
    }

    public static Component simplifyDataCommand(Component component) {
        // 1. 确保顶层是 TranslatableContents
        if (!(component.getContents() instanceof TranslatableContents translatable)) {
            return component;
        }

        // 2. 获取 args，其中 args[1] 是数据组件
        Object[] args = translatable.getArgs();
        if (args.length < 2 || !(args[1] instanceof Component dataComponent)) {
            return component;
        }

        // 3. 获取完整的数据字符串，如 "{Motion:[0.0d,0.0d,0.0d],Facing:1b,...}"
        String fullData = dataComponent.getString();

        // 4. 用正则提取 Motion, Pos, Fuse 的值（按原顺序）
        //    列表值：字段名:[...]  普通值：字段名:非逗号非大括号的字符序列
        Pattern fieldPattern = Pattern.compile("(?<=\\{|, )?(Motion|Pos|Fuse):(?:\\[[^\\]]*\\]|[^,}]+)");
        Matcher matcher = fieldPattern.matcher(fullData);

        // 使用 LinkedHashMap 保持顺序并去重（理论上每个字段只出现一次）
        Map<String, String> foundFields = new LinkedHashMap<>();
        while (matcher.find()) {
            String fieldText = matcher.group(); // 例如 "Motion:[0.0d,0.0d,0.0d]"
            String fieldName = matcher.group(1);
            foundFields.putIfAbsent(fieldName, fieldText);
        }

        // 5. 构建简化后的数据字符串
        StringBuilder sb = new StringBuilder("{");
        List<String> fieldEntries = new ArrayList<>(foundFields.values());
        for (int i = 0; i < fieldEntries.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(fieldEntries.get(i));
        }
        sb.append("}");

        // 6. 创建新的数据组件（纯文字）
        Component newDataComponent = Component.literal(sb.toString());

        // 7. 构造新的 TranslatableContents，保持原来的 key 和 fallback
        Object[] newArgs = new Object[args.length];
        System.arraycopy(args, 0, newArgs, 0, args.length);
        newArgs[1] = newDataComponent; // 替换第二个参数

        TranslatableContents newContents = new TranslatableContents(
                translatable.getKey(),
                translatable.getFallback(),
                newArgs
        );

        // 8. 创建新的根组件并复制样式与可能的兄弟节点
        MutableComponent newRoot = MutableComponent.create(newContents);
        newRoot.setStyle(component.getStyle());
        for (Component sibling : component.getSiblings()) {
            newRoot.append(sibling);
        }

        return newRoot;
    }

    // 另一个方法暂时保留原样
    public static Component enhanceDataCommand(Component component) {
        return component;
    }
}