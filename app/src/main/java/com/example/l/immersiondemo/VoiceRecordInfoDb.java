package com.example.l.immersiondemo;

import org.litepal.crud.DataSupport;

public class VoiceRecordInfoDb extends DataSupport {
    private String recordingTime;
    private String dateInfo;
    private String fileName;

    public VoiceRecordInfoDb()
    {

    }

    public VoiceRecordInfoDb(String fileName,String recordingTime,String dateInfo)
    {
        this.recordingTime = recordingTime;
        this.dateInfo = dateInfo;
        this.fileName = fileName;
    }

    public String getDateInfo() {
        return dateInfo;
    }

    public String getFileName() {
        return fileName;
    }

    public String getRecordingTime() {
        return recordingTime;
    }

    public void setDateInfo(String dateInfo) {
        this.dateInfo = dateInfo;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setRecordingTime(String recordingTime) {
        this.recordingTime = recordingTime;
    }


}
