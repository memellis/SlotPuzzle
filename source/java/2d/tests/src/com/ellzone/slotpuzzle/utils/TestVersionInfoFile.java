/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle.utils;

import com.ellzone.slotpuzzle2d.utils.TimeStamp;
import com.ellzone.slotpuzzle2d.utils.Version;
import com.ellzone.slotpuzzle2d.utils.VersionInfo;
import com.ellzone.slotpuzzle2d.utils.VersionInfoFile;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.tomgrill.gdxtesting.GdxTestRunnerGetAllTestClasses;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(GdxTestRunnerGetAllTestClasses.class)
public class TestVersionInfoFile {

    @Test
    public void testVersionInfoFileSaveAndLoad() {
        VersionInfo versionInfo = getVersionInfo();
        VersionInfoFile versionInfoFile = new VersionInfoFile(versionInfo);
        versionInfoFile.saveVersionInfo("myVersionInfo");
        VersionInfo versionInfoLoaded = versionInfoFile.loadVersionInfo("myVersionInfo");
        assertVersionInfoAreEqual(versionInfo, versionInfoLoaded);
    }

    @Test
    public void testVersionInfoFileSavedPrettyAndLoad() {
        VersionInfo versionInfo = getVersionInfo();
        VersionInfoFile versionInfoFile = new VersionInfoFile(versionInfo);
        versionInfoFile.saveVersionInfoPretty("myVersionInfo");
        VersionInfo versionInfoLoaded =
                versionInfoFile.loadVersionInfo("myVersionInfo");
        assertVersionInfoAreEqual(versionInfo, versionInfoLoaded);
    }

    @Test
    public void testVersionInfoFileInternalLoad() {
        VersionInfoFile versionInfoFile = new VersionInfoFile();
        VersionInfo versionInfoLoaded =
                versionInfoFile.loadVersionInfoInternal("version_info/SlotPuzzleVersionInfo");
        assertThat(versionInfoLoaded.getAuthor(), is(equalTo("Mark Ellis")));
    }

    @Test
    public void testGetYearFromVersionInfo() {
        VersionInfo versionInfo = getVersionInfo();
        String year = versionInfo.getTimestampSerializer().getYear();
        assertThat(year, is(equalTo("2020")));
    }

    private VersionInfo getVersionInfo() {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setAuthor("Mark Ellis");
        versionInfo.setTimestamp(TimeStamp.getTimeStamp());
        versionInfo.setVersion(Version.VERSION);
        return versionInfo;
    }

    private void assertVersionInfoAreEqual(VersionInfo versionInfo, VersionInfo versionInfoLoaded) {
        assertThat(versionInfo.getAuthor(),
                is(equalTo(versionInfoLoaded.getAuthor())));
        assertThat(versionInfo.getTimetamp(),
                is(equalTo(versionInfoLoaded.getTimetamp())));
        assertThat(versionInfo.getVersion(),
                is(equalTo(versionInfoLoaded.getVersion())));
    }
}
