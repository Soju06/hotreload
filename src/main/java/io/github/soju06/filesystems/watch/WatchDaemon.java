package io.github.soju06.filesystems.watch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public abstract class WatchDaemon implements Closeable {
    private WatchService service;
    private final File file;
    private boolean error;
    private boolean is_init;

    public WatchDaemon(File file) {
        this.file = file;
    }

    public WatchService getService() {
        return service;
    }

    public File getFile() {
        return file;
    }

    public String getPath() {
        return file.getPath();
    }

    public boolean getError() {
        return error;
    }

    public boolean isInit() {
        return is_init;
    }

    public void onModify(File file) {

    }

    public void onCreate(File file) {

    }

    public void onDelete(File file) {

    }

    public boolean watch() throws IOException {
        if (service == null) {
            is_init = false;
            service = FileSystems.getDefault().newWatchService();

            var path = "";
            if (file.isDirectory()) path = file.getPath();
            else if (file.isFile()) path = file.getParent();

            Paths.get(path).register(service,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
            error = false;
            is_init = true;
        }

        try {
            var key = service.poll();
            if (key != null) {
                List<WatchEvent<?>> events = key.pollEvents();
                for (WatchEvent<?> event : events) {
                    var kind = event.kind();
                    var path = (Path)event.context();
                    var file = new File(this.file.getParent(), path.toString());
                    if(kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                        onCreate(file);
                    } else if(kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                        onDelete(file);
                    } else if(kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                        onModify(file);
                    } else if(kind.equals(StandardWatchEventKinds.OVERFLOW)) {
                        error = true;
                    }
                }

                if (!key.reset()) {
                    error = true;
                }
                return true;
            }
        } catch (ClosedWatchServiceException ex) {
            service = null;
            return watch();
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        if (service != null) service.close();
    }
}
