package com.example;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import java.util.List;

public class ComponentTreePrinter {

    public static void printTree(Component component) {
        printNode(component, 0);
    }

    private static void printNode(Object node, int depth) {
        String indent = "-".repeat(Math.max(0, depth));

        if (node instanceof Component comp) {
            // 打印组件节点：类名 + 是否有样式
            System.out.print(indent + comp.getClass().getSimpleName());
            if (!comp.getStyle().isEmpty()) {
                System.out.print(" [hasStyle]");
            }
            System.out.println();

            // 递归打印内容（contents）
            printNode(comp.getContents(), depth + 1);

            // 递归打印所有兄弟节点（siblings）
            for (Component sibling : comp.getSiblings()) {
                printNode(sibling, depth + 1);
            }

        } else if (node instanceof ComponentContents contents) {
            // 打印内容节点
            System.out.print(indent + contents.getClass().getSimpleName()
                    + (contents instanceof PlainTextContents.LiteralContents cont? cont.toString(): ""));

            if (contents instanceof TranslatableContents trans) {
                // 特殊处理翻译内容：打印 key，并将 args 作为子节点
                System.out.print(" key=\"" + trans.getKey() + "\"");
                Object[] args = trans.getArgs();
                System.out.println(); // 换行，准备打印子节点

                for (Object arg : args) {
                    if (arg instanceof Component) {
                        printNode((Component) arg, depth + 1);
                    } else {
                        // 非 Component 参数（兜底处理，实际中通常不会出现）
                        System.out.println("-".repeat(depth + 1) + arg.getClass().getSimpleName() + ": " + arg);
                    }
                }
            } else {
                // 其他内容类型仅打印类名
                System.out.println();
            }

        } else {
            // 意外类型（理论上不会发生）
            System.out.println(indent + node.getClass().getSimpleName() + ": " + node);
        }
    }
}