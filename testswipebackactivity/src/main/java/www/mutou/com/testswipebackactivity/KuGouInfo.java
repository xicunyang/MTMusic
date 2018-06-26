package www.mutou.com.testswipebackactivity;

/**
 * Created by 木头 on 2018/6/26.
 */

public class KuGouInfo {
    private data[] data;

    public KuGouInfo.data[] getData() {
        return data;
    }

    public void setData(KuGouInfo.data[] data) {
        this.data = data;
    }

    public class data{
        private lists[] lists;
        private String play_url;

        public String getPlay_url() {
            return play_url;
        }

        public void setPlay_url(String play_url) {
            this.play_url = play_url;
        }

        public lists[] getLists() {
            return lists;
        }

        public void setLists(lists[] lists) {
            this.lists = lists;
        }

        public class lists{
            private String AlbumName;
            private String FileSize;
            private String FileHash;
            private String FileName;
            private String SingerName;
            private String SongName;

            public String getAlbumName() {
                return AlbumName;
            }

            public void setAlbumName(String albumName) {
                AlbumName = albumName;
            }

            public String getFileSize() {
                return FileSize;
            }

            public void setFileSize(String fileSize) {
                FileSize = fileSize;
            }

            public String getFileHash() {
                return FileHash;
            }

            public void setFileHash(String fileHash) {
                FileHash = fileHash;
            }

            public String getFileName() {
                return FileName;
            }

            public void setFileName(String fileName) {
                FileName = fileName;
            }

            public String getSingerName() {
                return SingerName;
            }

            public void setSingerName(String singerName) {
                SingerName = singerName;
            }

            public String getSongName() {
                return SongName;
            }

            public void setSongName(String songName) {
                SongName = songName;
            }
        }
    }

}
