package xyz.ashleyz.zeppelin.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import xyz.ashleyz.zeppelin.items.CompassItem;
import xyz.ashleyz.zeppelin.items.RadioItem;

public class ModItems {
	public static final List<Item> ITEMS = new ArrayList<Item>();
	
	public static final Item radio = new RadioItem("radio_item");
	public static final Item compass = new CompassItem("compass_item");
}
