package com.ellzone.slotpuzzle2d.utils;

import org.junit.Test;

import java.util.Date;

public class TestVersionInfoFile {

    @Test
    public void testVersionInfoFileSaveAndLoad() {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setAuthor("Mark Ellis");
        versionInfo.setTimestamp(TimeStamp.getTimeStamp());
//        versionInfo.setVersion(Version.VERSION);
        VersionInfoFile versionInfoFile = new VersionInfoFile(versionInfo);
        versionInfoFile.saveVersionInfo("myVersionInfo");
    }
}
