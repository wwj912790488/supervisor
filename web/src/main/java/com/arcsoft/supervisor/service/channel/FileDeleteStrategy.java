package com.arcsoft.supervisor.service.channel;

import java.nio.file.Path;

public interface FileDeleteStrategy {
    public boolean shouldDelete(Path path);
}
