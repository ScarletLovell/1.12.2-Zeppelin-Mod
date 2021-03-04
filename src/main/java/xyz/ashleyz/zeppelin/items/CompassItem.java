package xyz.ashleyz.zeppelin.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemCompass;
import xyz.ashleyz.zeppelin.Main;
import xyz.ashleyz.zeppelin.init.ModItems;
import xyz.ashleyz.zeppelin.util.IHasModel;

public class CompassItem extends ItemCompass implements IHasModel {
    public CompassItem(String name) {
        super();
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.TRANSPORTATION);
    
        ModItems.ITEMS.add(this);
    }
    @Override
    public void registerModels()
    {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }
}
