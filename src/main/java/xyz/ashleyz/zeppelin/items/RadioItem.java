package xyz.ashleyz.zeppelin.items;

import net.minecraft.creativetab.CreativeTabs;
import xyz.ashleyz.zeppelin.util.IHasModel;

public class RadioItem extends ItemBase implements IHasModel {
    public RadioItem(String name) {
        super(name);
        setCreativeTab(CreativeTabs.TRANSPORTATION);
    }
}
