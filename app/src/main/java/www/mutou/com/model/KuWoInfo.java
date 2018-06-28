package www.mutou.com.model;

/**
 * Created by 在旭 on 2018/3/4.
 */

public class KuWoInfo {
    private abslist[] abslist;

    public KuWoInfo.abslist[] getAbslist() {
        return abslist;
    }

    public void setAbslist(KuWoInfo.abslist[] abslist) {
        this.abslist = abslist;
    }

    public class abslist {
        private String ALBUM;
        private String ARTIST;
        private String MP3RID;
        private String SONGNAME;
        private String mp4sig1 = "";

        public String getMp4sig1() {
            return mp4sig1;
        }

        public void setMp4sig1(String mp4sig1) {
            this.mp4sig1 = mp4sig1;
        }

        public String getSONGNAME() {
            return SONGNAME;
        }

        public String getALBUM() {
            return ALBUM;
        }

        public void setALBUM(String ALBUM) {
            this.ALBUM = ALBUM;
        }

        public String getARTIST() {
            return ARTIST;
        }

        public void setARTIST(String ARTIST) {
            this.ARTIST = ARTIST;
        }

        public String getMP3RID() {
            return MP3RID;
        }

        public void setMP3RID(String MP3RID) {
            this.MP3RID = MP3RID;
        }

        public void setSONGNAME(String SONGNAME) {
            this.SONGNAME = SONGNAME;
        }
    }

}

