package requests;

import java.util.List;

public class ListResponse {
    private final List<FileInfo> files;

    public ListResponse(List<FileInfo> files) {
        this.files = files;
    }

    public int getCount() {
        return files.size();
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public static class FileInfo {
        private int id;
        private String name;
        private int size;

        public FileInfo(int id, String name, int size) {
            this.id = id;
            this.name = name;
            this.size = size;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }
    }
}
