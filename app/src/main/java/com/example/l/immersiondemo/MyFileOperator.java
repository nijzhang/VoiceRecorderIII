package com.example.l.immersiondemo;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class MyFileOperator {

    Context context;

    File file = new File(Environment.getExternalStorageDirectory()+"/TestMethod");

    public MyFileOperator(Context context)
    {
        this.context = context;
    }

    public void changeFileName(String oriFileName,String destFileName)
    {
        File oriFile = new File(file.getAbsoluteFile()+"/"+oriFileName);

       // Toast.makeText(context,""+oriFile.getAbsolutePath(),Toast.LENGTH_SHORT).show();

        if(oriFile.exists()) {

            File destFile = new File(file.getAbsoluteFile() + "/" + destFileName);

           if( !oriFile.renameTo(destFile))
           {

               Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
           }
        }
    }

    public String[] retureFileList()
    {
        return file.list();
    }
}
