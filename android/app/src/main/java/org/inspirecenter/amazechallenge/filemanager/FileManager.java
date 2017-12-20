package org.inspirecenter.amazechallenge.filemanager;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.google.blockly.model.Block;

import org.inspirecenter.amazechallenge.ui.BlocklyActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class FileManager {

    private static final String DEFAULT_CODE_FILENAME_PREFIX = "defaultcode_";
    private static final String PLAYER_CODE_FILENAME_PREFIX = "playercode_";
    private static final String XML_FILE_EXTENSION = ".xml";
    private static final String DEFAULT_CODE_MARKER = "(Sample code)";
    private static final String SIMPLE_DATE_FORMAT = "dd-MM-yyyy HH:mm";

    /**
     * Sets the file name for the saved code.
     * @param saveFilename
     */
    public static void setSaveFilename(String saveFilename) {
        BlocklyActivity.SAVE_FILENAME = saveFilename;
    }

    /**
     * Searches for files within the internal storage (/data/user/0/[app-domain]/files/)
     * Populates the codeNames and codeFiles lists firstly with demo codes and then with user codes.
     * codeFilesList stores the raw filename e.g playercode_mySavedCode.xml
     * codeNamesList stores the filtered filename for display in the list e.g mySavedCode
     */
    public static void getCodes(BlocklyActivity a) {
        //Clear the arrays first:
        BlocklyActivity.codeNamesList.clear();
        BlocklyActivity.codeFilesList.clear();
        BlocklyActivity.codeFilesLastModifiedList.clear();

        //Get all files from the internal files directory:
        File mydir = a.getFilesDir();
        File listFile[] = mydir.listFiles();
        if (listFile != null && listFile.length > 0) {

            //Default codes:
            for (File aListFile : listFile) {
                if (aListFile.isFile() && aListFile.getName().startsWith(DEFAULT_CODE_FILENAME_PREFIX)) {
                    BlocklyActivity.codeFilesList.add(aListFile.getName());
                    String filteredName = aListFile.getName().replace(DEFAULT_CODE_FILENAME_PREFIX, "");
                    filteredName = filteredName.replace(XML_FILE_EXTENSION, "");
                    BlocklyActivity.codeNamesList.add(filteredName);
                    BlocklyActivity.codeFilesLastModifiedList.add(DEFAULT_CODE_MARKER);
                }//end if
            }//end for (default codes)

            //Player codes:
            ArrayList<File> unsortedFiles = new ArrayList<>();
            for (File aListFile : listFile) {
                if (aListFile.isFile() && aListFile.getName().startsWith(PLAYER_CODE_FILENAME_PREFIX))
                    unsortedFiles.add(aListFile);
            }//end for (player codes)

            //Sort the player's code files from latest to newest:
            Collections.sort(unsortedFiles, new Comparator<File>() {
                @Override
                public int compare(File file1, File file2) {
                    long difference = file1.lastModified() - file2.lastModified();
                    if(difference < 0) return 1;
                    else if (difference == 0) return 0;
                    else return -1;
                }//end compare()
            });

            //Put the sorted items into the lists:
            for (File aListFile : unsortedFiles) {
                if (aListFile.isFile() && aListFile.getName().startsWith(PLAYER_CODE_FILENAME_PREFIX)) {
                    BlocklyActivity.codeFilesList.add(aListFile.getName());
                    String filteredName = aListFile.getName().replace(PLAYER_CODE_FILENAME_PREFIX, "");
                    filteredName = filteredName.replace(XML_FILE_EXTENSION, "");
                    BlocklyActivity.codeNamesList.add(filteredName);
                    Date lastModifiedDate = new Date(aListFile.lastModified());
                    DateFormat dateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
                    BlocklyActivity.codeFilesLastModifiedList.add("(Modified: " + dateFormat.format(lastModifiedDate) + ")");
                }//end if
            }//end for (player codes)

        }//end if
    }//end getCodes()

    /**
     * Matches asset code files to internal storage code files.
     * If an internal file is missing, the asset code file is copied into the internal folder.
     * This is necessary as Blockly only loads XML files from internal app storage.
     */
    public static void updateInternalStorageCodes(BlocklyActivity a) {

        //Get the asset files:
        ArrayList<String> assetFiles = new ArrayList<>();
        try { assetFiles = listAssetCodes(a); }
        catch (IOException e) { e.printStackTrace(); }

        //Get the internal files:
        ArrayList<String> internalFilesList = new ArrayList<>();
        File mydir = a.getFilesDir();
        File listFile[] = mydir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (File aListFile : listFile)
                if (aListFile.isFile() && aListFile.getName().startsWith(DEFAULT_CODE_FILENAME_PREFIX)) internalFilesList.add(aListFile.getName());
        }//end if

        //Compare files in assets with internal files:
        for (String filename : assetFiles) {
            boolean found = false;
            for (String internalFilename : internalFilesList) {
                if (internalFilename.equals(filename)) {
                    found = true;
                    break;
                }//end if
            }//end foreach internal filename

            //If file in assets does not exist in internals, read its content
            // and create a new file in internals with the same name:
            if (!found) {
                //Read the contents of the file in assets:
                String content = "";
                try { content = readFromAssets(a, BlocklyActivity.ASSETS_CODES_DIR + "/" + filename); }
                catch (IOException e) { e.printStackTrace(); }
                //Create a new file in internals and copy content and name of the assets file:
                if (!content.isEmpty()) {
                    File file = new File(a.getFilesDir(), filename);
                    FileOutputStream outputStream;
                    try {
                        outputStream = a.openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(content.getBytes());
                        outputStream.close();
                    } catch (Exception e) { e.printStackTrace(); }
                }//end if content non empty
            }//end if file was not found
        }//end foreach file in assets
    }//end updateInternalStorageCodes()

    /**
     * Finds and returns a list of all the asset (default) codes.
     * @return Returns an ArrayList of Strings of the asset code files.
     * @throws IOException Throws exception if a file cannot be accessed.
     */
    public static ArrayList<String> listAssetCodes(BlocklyActivity a) throws IOException {
        ArrayList<String> assetsFilenames = new ArrayList<>();
        Resources res = a.getResources();
        AssetManager am = res.getAssets();
        String fileList[] = am.list(BlocklyActivity.ASSETS_CODES_DIR);
        if (fileList != null) {
            for (String file : fileList)
                if (file.startsWith(DEFAULT_CODE_FILENAME_PREFIX)) assetsFilenames.add(file);
        }//end if
        return assetsFilenames;
    }//end listAssets()

    /**
     * Reads a text file from assets.
     * @param context Current Context
     * @param filename The filename to read.
     * @return Returns a string of the file's contents.
     * @throws IOException Throws exception if the file cannot be accessed.
     */
    public static String readFromAssets(Context context, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            sb.append(mLine); // process line
            mLine = reader.readLine();
        }//end while
        reader.close();
        return sb.toString();
    }//end readFromAssets()

    /**
     * Delete a given internal file.
     * @param filename Internal file to delete.
     */
    public static void deleteInternalFile(BlocklyActivity a, String filename) {
        File dir = a.getFilesDir();
        File file = new File(dir, filename);
        file.delete();
        updateInternalStorageCodes(a);
        getCodes(a);
    }//end deleteInternalFile()

    /**
     * Checks if an internal file with the given name exists.
     * @param filename The name of the file.
     * @return Returns true if file exists, false if not.
     */
    public static boolean internalFileExists(BlocklyActivity a, String filename) {
        filename = PLAYER_CODE_FILENAME_PREFIX + filename + XML_FILE_EXTENSION;
        File file = a.getBaseContext().getFileStreamPath(filename);
        return file.exists();
    }//end internalFileExists()

}//end class FileManager