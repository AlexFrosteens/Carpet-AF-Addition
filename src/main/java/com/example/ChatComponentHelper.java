package com.example;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.*;

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

        // 2. 获取 args，第二个参数是数据组件
        Object[] args = translatable.getArgs();
        if (args.length < 2 || !(args[1] instanceof Component dataComponent)) {
            return component;
        }

        // 3. 获取数据组件的所有兄弟节点（它们按顺序构成数据字符串）
        List<Component> siblings = dataComponent.getSiblings();
        if (siblings.isEmpty()) {
            return component;
        }
/*
        Set<String> targetNames = Set.of("Motion", "Pos", "fuse", "Passenger");
        List<List<Component>> collectedFields = new ArrayList<>();

        int i = 0;
        while (i < siblings.size()) {
            Component current = siblings.get(i);
            String text = getLiteralText(current);

            if (text != null && targetNames.stream().anyMatch(t -> t.equalsIgnoreCase(text))) {
                int start = i;
                int depth = 0;
                int end = i;

                while (i < siblings.size()) {
                    Component c = siblings.get(i);
                    String t = getLiteralText(c);
                    if (t != null) {
                        if (t.equals("[")) depth++;
                        else if (t.equals("]")) depth--;
                        else if (depth == 0 && (t.equals(",") || t.equals("}"))) {
                            break;
                        }
                    }
                    end = i;
                    i++;
                }

                List<Component> fieldParts = new ArrayList<>();
                if (text.equals("Passenger")){
                    List<Component> passengerSiblings = new ArrayList<>();
                    for (int j = start; j <= end; j++) {
                        passengerSiblings.add(siblings.get(j));
                    }
                    fieldParts.addAll(addPassengerPart(passengerSiblings, 0, 2));
                }else {
                    for (int j = start; j <= end; j++) {
                        fieldParts.add(siblings.get(j));
                    }
                }
                collectedFields.add(fieldParts);
            } else {
                i++;
            }
        }

        // 4. 构建带格式化换行的新数据组件
        MutableComponent newData = MutableComponent.create(PlainTextContents.EMPTY);
        newData.append(Component.literal("{\n  "));

        for (int idx = 0; idx < collectedFields.size(); idx++) {
            if (idx > 0) {
                newData.append(Component.literal(",\n  "));
            }
            for (Component part : collectedFields.get(idx)) {
                newData.append(part);
            }
        }

        newData.append(Component.literal("\n}"));
        */

        // 5. 构造新的 TranslatableContents
        Object[] newArgs = new Object[args.length];
        newArgs[0] = args[0];
        MutableComponent newComponent = MutableComponent.create(PlainTextContents.EMPTY);
        List<Component> partList = addPassengerPart(siblings, 0, 0);
        for (Component part : partList){
            newComponent.append(part);
        }
        newArgs[1] = newComponent;

        TranslatableContents newContents = new TranslatableContents(
                translatable.getKey(),
                translatable.getFallback(),
                newArgs
        );

        MutableComponent newRoot = MutableComponent.create(newContents);
//        newRoot.setStyle(component.getStyle());
//        for (Component sibling : component.getSiblings()) {
//            newRoot.append(sibling);
//        }

        return newRoot;
    }

    private static String getLiteralText(Component comp) {
        if (comp.getContents() instanceof PlainTextContents.LiteralContents literal) {
            return literal.text();
        }
        return null;
    }

    public static Component enhanceDataCommand(Component component) {
        return component;
    }

    private static List<Component> addPassengerPart(List<Component> siblings, int i, int tab){
        Set<String> targetNames = Set.of("Motion", "Pos", "fuse", "Passenger");
        List<List<Component>> collectedFields = new ArrayList<>();

//        int i = 0;
        while (i < siblings.size()) {
            Component current = siblings.get(i);
            String text = getLiteralText(current);

            if (text != null && targetNames.stream().anyMatch(t -> t.equalsIgnoreCase(text))) {
                int start = i;
                int depth = 0;
                int end = i;

                while (i < siblings.size()) {
                    Component c = siblings.get(i);
                    String t = getLiteralText(c);
                    if (t != null) {
                        if (t.equals("[")) depth++;
                        else if (t.equals("]")) depth--;
                        else if (depth == 0 && (t.equals(",") || t.equals("}"))) {
                            break;
                        }
                    }
                    end = i;
                    i++;
                }

                List<Component> fieldParts = new ArrayList<>();
                if (text.equals("Passenger")){
                    List<Component> passengerSiblings = new ArrayList<>();
                    for (int j = start; j <= end; j++) {
                        passengerSiblings.add(siblings.get(j));
                    }
                    fieldParts.addAll(addPassengerPart(passengerSiblings, 0, tab + 1));
                }else {
                    for (int j = start; j <= end; j++) {
                        fieldParts.add(siblings.get(j));
                    }
                }
                collectedFields.add(fieldParts);
            } else {
                i++;
            }
        }

        // 4. 返回扁平化组件列表
        List<Component> newData = new ArrayList<>();
        addTab(tab - 1, newData);
        newData.add(Component.literal("{\n"));
        addTab(tab, newData);

        for (int idx = 0; idx < collectedFields.size(); idx++) {
            if (idx > 0) {
                newData.add(Component.literal(",\n"));
                addTab(tab, newData);
            }
            newData.addAll(collectedFields.get(idx));
        }

        addTab(tab - 1, newData);
        newData.add(Component.literal("\n}"));
        return newData;
    }

    private static void addTab(int tab, MutableComponent component){
        if (tab <= 0)   return;
        for (int j = 0; j < tab; j++){
            component.append(Component.literal("  "));
        }
    }

    private static void addTab(int tab, List<Component> componentList){
        if (tab <= 0)   return;
        componentList.add(Component.literal("  ".repeat(tab)));
    }
}