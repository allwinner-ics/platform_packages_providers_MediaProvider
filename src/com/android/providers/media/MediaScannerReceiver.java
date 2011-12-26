/* //device/content/providers/media/src/com/android/providers/media/MediaScannerReceiver.java
**
** Copyright 2007, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/

package com.android.providers.media;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.provider.Settings;

import java.io.File;


public class MediaScannerReceiver extends BroadcastReceiver
{
    private final static String TAG = "MediaScannerReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Uri uri = intent.getData();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            // scan internal storage
            /* modified by Gary. start {{----------------------------------- */
            Log.v(TAG, "receive ACTION_BOOT_COMPLETED intent.");
            scan(context, MediaProvider.INTERNAL_VOLUME, null);
            /* modified by Gary. end   -----------------------------------}} */
        } else {
            if (uri.getScheme().equals("file")) {
                // handle intents related to external storage
                String path = uri.getPath();
                String externalStoragePath = Environment.getExternalStorageDirectory().getPath();

                /* modified by Gary. start {{----------------------------------- */
                Log.d(TAG, "action: " + action + " path: " + path);
                if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
//                    if(path.equals(Environment.getExternalStorageDirectory().toString())
//                       || ( path.equals(Environment.getFlashStroageDirectory().toString())
//                            && Settings.System.getInt(context.getContentResolver(),Settings.System.IS_SCAN_TF_CARD,0)==1)){
//                        scan(context, MediaProvider.EXTERNAL_VOLUME, path);
//                    }
                    scan(context, MediaProvider.EXTERNAL_VOLUME, path);
                } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE) &&
                        path != null ) {
                    /* modified by Gary. start {{----------------------------------- */
                    /* 2011-9-28 13:19:51 */
                    /* support scaning a directory */
                    File f = new File(path);
                    if(f.isDirectory())
                        scan(context, MediaProvider.EXTERNAL_VOLUME, path);
                    else
                        scanFile(context, path);
                    /* modified by Gary. end   -----------------------------------}} */
                }
                /* modified by Gary. end   -----------------------------------}} */
            }
        }
    }

        /* modified by Gary. start {{----------------------------------- */
    private void scan(Context context, String volume, String path) {
        Bundle args = new Bundle();
        args.putString("volume", volume);
        args.putString("dirpath", path);
        /* modified by Gary. end   -----------------------------------}} */
        context.startService(
                new Intent(context, MediaScannerService.class).putExtras(args));
    }    

    private void scanFile(Context context, String path) {
        Bundle args = new Bundle();
        args.putString("filepath", path);
        context.startService(
                new Intent(context, MediaScannerService.class).putExtras(args));
    }    
}


