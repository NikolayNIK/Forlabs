package ru.kollad.forlabs.util;

import android.content.Context;
import android.net.Uri;

import java.io.File;

import androidx.documentfile.provider.DocumentFile;

/**
 * Created by NikolayNIK on 27.11.2018.
 */
public class DocumentFileUtil {

	public static DocumentFile fromUri(Context context, Uri uri) {
		return "file".equals(uri.getScheme()) ?
				DocumentFile.fromFile(new File(uri.getPath())) :
				DocumentFile.fromSingleUri(context, uri);
	}
}
