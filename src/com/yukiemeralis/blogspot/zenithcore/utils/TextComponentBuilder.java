package com.yukiemeralis.blogspot.zenithcore.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;

public class TextComponentBuilder 
{
    public static TextComponent clickEvent(ClickEvent.Action action, String host, String data)
    {
        TextComponent message = new TextComponent(host);

        switch (action.toString())
        {
            case "COPY_TO_CLIPBOARD":
                message.setClickEvent(new ClickEvent(action, data));
                return message;
            case "OPEN_FILE":
                message.setClickEvent(new ClickEvent(action, data));
                return message;
            case "OPEN_URL":
                message.setClickEvent(new ClickEvent(action, data));
                return message;
            case "RUN_COMMAND":
                message.setClickEvent(new ClickEvent(action, data));
                return message;
            case "SUGGEST_COMMAND":
                message.setClickEvent(new ClickEvent(action, data));
                return message;
            default:
                PrintUtils.sendMessage("Unknown enum " + action.toString() + " (TextComponentBuilder : 13)");
                return message;
        }
    }

    public static TextComponent hoverEvent(HoverEvent.Action action, String host, Object data)
    {
        TextComponent message = new TextComponent(host);

        switch (action.toString())
        {
            case "SHOW_ENTITY":
                //message.setHoverEvent(new HoverEvent(action, data));
                return message;
            case "SHOW_ITEM":
                return message;
            case "SHOW_TEXT":
                message.setHoverEvent(new HoverEvent(action, (Content) data));
                return message;
            default:
                return message;
        }
    }

    public static TextComponent setClickEvent(TextComponent input, ClickEvent.Action action, String data)
    {
        TextComponent buffer = input;
        buffer.setClickEvent(new ClickEvent(action, data));
        return buffer;
    }

    public static TextComponent setHoverText(TextComponent input, String data)
    {
        TextComponent buffer = input;
        buffer.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(data)));
        return buffer;
    }

    public static TextComponent regularText(String message)
    {
        return new TextComponent(message);
    }
}
