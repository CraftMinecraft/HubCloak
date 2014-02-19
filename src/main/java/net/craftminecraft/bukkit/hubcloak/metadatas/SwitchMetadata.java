/*
 */
package net.craftminecraft.bukkit.hubcloak.metadatas;

import org.bukkit.metadata.MetadataValueAdapter;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Robin
 */
public class SwitchMetadata extends MetadataValueAdapter {
    boolean bSwitch;

    public SwitchMetadata(Plugin p, boolean bSwitch) {
        super(p);
        this.bSwitch = bSwitch;
    }

    @Override
    public Object value() {
        return bSwitch;
    }

    @Override
    public void invalidate() {
        bSwitch = !bSwitch;
    }
}

