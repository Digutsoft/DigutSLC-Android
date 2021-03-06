package com.digutsoft.slc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SLCMain extends ActionBarActivity {
    static TextView tvFileName;
    static EditText etTextBox;
    static String mLoadedFileName;

    static int mCheckType;
    long mBackKeyPressedTime;

    static boolean isFileLoadMode = false;

    SharedPreferences sharedPreferences;

    TextView tvLength;
    boolean mByteMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        tvLength = (TextView) findViewById(R.id.tvLength);
        tvLength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mByteMode = !mByteMode;
                checkLengthAndDisplay();
            }
        });

        tvFileName = (TextView) findViewById(R.id.tvFileName);

        etTextBox = (EditText) findViewById(R.id.etText);
        etTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                checkLengthAndDisplay();
            }
        });

        sharedPreferences = getSharedPreferences("DigutSLC", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().hasExtra("slcLoadedText")) {
            etTextBox.setText(getIntent().getStringExtra("slcLoadedText"));
        }

        if (isFileLoadMode) {
            openFile(mLoadedFileName);
        }

        mCheckType = sharedPreferences.getInt("checkType", 0);

        File slcDirectory = new File(Environment.getExternalStorageDirectory() + "/DigutSLC/");
        if (!slcDirectory.exists()) {
            slcDirectory.mkdirs();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itOpen:
                mnOpenFile();
                return true;
            case R.id.itSave:
                mnSaveFile(false);
                return true;
            case R.id.itSaveAs:
                mnSaveFile(true);
                return true;
            case R.id.itFileList:
                startActivity(new Intent(SLCMain.this, SLCFileList.class));
                return true;
            case R.id.itType:
                mnCheckType();
                return true;
            case R.id.itShare:
                Intent intShare = new Intent(Intent.ACTION_SEND);
                intShare.setType("text/plain");
                intShare.putExtra(Intent.EXTRA_TEXT, etTextBox.getText().toString());
                startActivity(Intent.createChooser(intShare, getString(R.string.share)));
                return true;
            case R.id.itInfo:
                String appVersion;
                PackageInfo packageInfo;
                try {
                    packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    appVersion = packageInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    appVersion = "Undefined";
                }
                AlertDialog.Builder adbInfo = new AlertDialog.Builder(SLCMain.this);
                adbInfo.setIcon(R.drawable.ic_launcher);
                adbInfo.setTitle(R.string.app_name);
                adbInfo.setMessage(String.format(getString(R.string.info_message), appVersion));
                adbInfo.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                adbInfo.show();
                return true;
            default:
                return onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > mBackKeyPressedTime + 2000) {
            mBackKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(SLCMain.this, R.string.back_again_to_exit, Toast.LENGTH_LONG).show();
        } else {
            isFileLoadMode = false;
            mLoadedFileName = null;
            finish();
        }
    }

    private void checkLengthAndDisplay() {
        if (mByteMode) {
            tvLength.setText(Integer.toString(checkByteLength()) + "bytes");
        } else {
            tvLength.setText(Integer.toString(checkLength()));
        }
    }

    private int checkLength() {
        switch (mCheckType) {
            case 0:
                return etTextBox.getText().toString().length();
            case 1:
                return etTextBox.getText().toString().replace(" ", "").length();
            case 2:
                return etTextBox.getText().toString().replace("\n", "").length();
            case 3:
                return etTextBox.getText().toString().replace(" ", "").replace("\n", "").length();
            case 4:
                return etTextBox.getText().toString().trim().length();
            case 5:
                return etTextBox.getText().toString().replace(" ", "").trim().length();
            case 6:
                return etTextBox.getText().toString().replace("\n", "").trim().length();
            case 7:
                return etTextBox.getText().toString().replace(" ", "").replace("\n", "").trim().length();
            default:
                return -1;
        }
    }

    private int checkByteLength() {
        switch (mCheckType) {
            case 0:
                return etTextBox.getText().toString().getBytes().length;
            case 1:
                return etTextBox.getText().toString().replace(" ", "").getBytes().length;
            case 2:
                return etTextBox.getText().toString().replace("\n", "").getBytes().length;
            case 3:
                return etTextBox.getText().toString().replace(" ", "").replace("\n", "").getBytes().length;
            case 4:
                return etTextBox.getText().toString().trim().getBytes().length;
            case 5:
                return etTextBox.getText().toString().replace(" ", "").trim().getBytes().length;
            case 6:
                return etTextBox.getText().toString().replace("\n", "").trim().getBytes().length;
            case 7:
                return etTextBox.getText().toString().replace(" ", "").replace("\n", "").trim().getBytes().length;
            default:
                return -1;
        }
    }

    private void mnOpenFile() {
        try {
            final ArrayList<String> alFileList = new ArrayList<>();
            File flDirectory = new File(Environment.getExternalStorageDirectory() + "/DigutSLC");
            if (flDirectory.listFiles().length > 0) {
                for (File file : flDirectory.listFiles()) {
                    String name = file.getName();
                    alFileList.add(name);
                }

                AlertDialog.Builder adbFileChooser = new AlertDialog.Builder(SLCMain.this);
                adbFileChooser.setTitle(R.string.open);
                adbFileChooser.setItems(alFileList.toArray(new String[alFileList.size()]), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, final int i) {
                        if (!etTextBox.getText().toString().equals("")) {
                            AlertDialog.Builder adbOpenOverwriteAsk = new AlertDialog.Builder(SLCMain.this);
                            adbOpenOverwriteAsk.setTitle(R.string.open);
                            adbOpenOverwriteAsk.setMessage(R.string.open_overwrite_ask);
                            adbOpenOverwriteAsk.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    openFile(alFileList.get(i));
                                }
                            });
                            adbOpenOverwriteAsk.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                            adbOpenOverwriteAsk.show();
                        } else {
                            openFile(alFileList.get(i));
                        }
                    }
                });
                adbFileChooser.show();
            } else {
                AlertDialog.Builder adbFileNotExist = new AlertDialog.Builder(SLCMain.this);
                adbFileNotExist.setTitle(R.string.open);
                adbFileNotExist.setMessage(R.string.open_no_file);
                adbFileNotExist.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                adbFileNotExist.show();
            }
        } catch (NullPointerException e) {
            Toast.makeText(SLCMain.this, getString(R.string.open_fail), Toast.LENGTH_SHORT).show();
        }
    }

    private void mnSaveFile(final boolean isSaveAs) {
        if (etTextBox.getText().toString().equals("")) {
            Toast.makeText(SLCMain.this, R.string.save_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mLoadedFileName == null || isSaveAs) {
            final EditText etFileName = new EditText(SLCMain.this);
            AlertDialog.Builder adbSaveFileName = new AlertDialog.Builder(SLCMain.this);

            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdfName = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
            final String tempFileName = sdfName.format(date);
            etFileName.setHint(tempFileName);

            adbSaveFileName.setTitle(R.string.save_title);
            adbSaveFileName.setView(etFileName);
            adbSaveFileName.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String fileName = etFileName.getText().toString();
                    if (fileName.equals("")) {
                        saveFile(tempFileName);
                    } else {
                        saveFile(etFileName.getText().toString());
                    }
                }
            });
            adbSaveFileName.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            adbSaveFileName.show();
        } else {
            saveFile(mLoadedFileName);
        }
    }

    private void mnCheckType() {
        final CharSequence[] csMenuItems = {
                getString(R.string.check_option_except_space),
                getString(R.string.check_option_except_enter),
                getString(R.string.check_option_except_trim)};
        final boolean[] bState = {(mCheckType & 0b001) == 0b001, (mCheckType & 0b010) == 0b010, (mCheckType & 0b100) == 0b100};

        AlertDialog.Builder adbCheckType = new AlertDialog.Builder(SLCMain.this);
        adbCheckType.setTitle(R.string.check_option);
        adbCheckType.setMultiChoiceItems(csMenuItems, bState, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialogInterface, int item, boolean state) {
            }
        });
        adbCheckType.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SparseBooleanArray sbaValue = ((AlertDialog) dialog).getListView().getCheckedItemPositions();

                int checkType = 0;
                if (sbaValue.get(0)) checkType += 0b001;
                if (sbaValue.get(1)) checkType += 0b010;
                if (sbaValue.get(2)) checkType += 0b100;

                sharedPreferences.edit().putInt("checkType", checkType).apply();
                mCheckType = checkType;
                checkLengthAndDisplay();
            }
        });
        adbCheckType.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        adbCheckType.show();
    }

    private void openFile(final String fileName) {
        try {
            String fullFilePath = Environment.getExternalStorageDirectory() + "/DigutSLC/" + fileName;

            File file = new File(fullFilePath);
            StringBuilder sbOpeningFileContent = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sbOpeningFileContent.append(line);
                sbOpeningFileContent.append('\n');
            }
            bufferedReader.close();

            mLoadedFileName = fileName;
            tvFileName.setText(getString(R.string.working_local) + mLoadedFileName);
            etTextBox.setText(sbOpeningFileContent);

            if (isFileLoadMode) isFileLoadMode = false;

            checkLengthAndDisplay();
        } catch (IOException e) {
            Toast.makeText(SLCMain.this, getString(R.string.open_fail), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFile(String fileName) {
        try {
            if (!fileName.endsWith(".txt")) {
                fileName = fileName + ".txt";
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/DigutSLC/" + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.append(etTextBox.getText().toString());
            outputStreamWriter.close();
            fileOutputStream.close();

            mLoadedFileName = fileName;
            tvFileName.setText(getString(R.string.working_local) + mLoadedFileName);
            Toast.makeText(SLCMain.this, getString(R.string.save_done) + fileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(SLCMain.this, getString(R.string.save_fail), Toast.LENGTH_SHORT).show();
        }
    }

    public static void loadFile(String fileName) {
        isFileLoadMode = true;
        mLoadedFileName = fileName;
    }
}