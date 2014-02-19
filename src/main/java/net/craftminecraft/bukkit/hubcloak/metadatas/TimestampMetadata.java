/*
 */
package net.craftminecraft.bukkit.hubcloak.metadatas;

import org.bukkit.metadata.MetadataValueAdapter;
import org.bukkit.plugin.Plugin;

/**
 * Use asBoolean to check if the 
 * @author Robin
 */
public class TimestampMetadata extends MetadataValueAdapter {
    private long timeout;
    private long current;

    public TimestampMetadata(Plugin p, long timeout, boolean startNow) {
        super(p);
        this.timeout = timeout;
        this.current = startNow ? timeout + System.currentTimeMillis() : 0;
    }

    @Override
    public Object value() {
        return current;
    }

    // Returns true if time's up, false otherwise.
    @Override
    public boolean asBoolean() {
        return System.currentTimeMillis() - current > 0;
    }
    
    // Reset.
    @Override
    public void invalidate() {
        this.current = timeout + System.currentTimeMillis();
    }
}
