package com.digutsoft.slc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class SLCFileList extends ActionBarActivity {

    ArrayList<String> alFileList;
    ListView lvFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filelist);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        alFileList = new ArrayList<>();

        lvFileList = (ListView) findViewById(R.id.lvFileList);
        lvFileList.setEmptyView(findViewById(R.id.tvEmptyList));

        lvFileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder adbOpenFile = new AlertDialog.Builder(SLCFileList.this);
                adbOpenFile.setTitle(R.string.file_list_menu_open);
                adbOpenFile.setMessage(String.format(getString(R.string.file_list_open_file_ask), alFileList.get(i)));
                adbOpenFile.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i2) {
                        SLCMain.loadFile(alFileList.get(i));
                        finish();
                    }
                });
                adbOpenFile.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i2) {
                    }
                });
                adbOpenFile.show();
            }
        });

        lvFileList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String filePath = Environment.getExternalStorageDirectory() + "/DigutSLC/";
                final String fileName = alFileList.get(i);

                AlertDialog.Builder adbFileOptions = new AlertDialog.Builder(SLCFileList.this);
                adbFileOptions.setTitle(fileName);
                adbFileOptions.setItems(new String[]{
                        getString(R.string.file_list_menu_open),
                        getString(R.string.file_list_menu_rename),
                        getString(R.string.file_list_menu_delete)
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                AlertDialog.Builder adbOpenFile = new AlertDialog.Builder(SLCFileList.this);
                                adbOpenFile.setTitle(R.string.file_list_menu_open);
                                adbOpenFile.setMessage(String.format(getString(R.string.file_list_open_file_ask), fileName));
                                adbOpenFile.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SLCMain.loadFile(fileName);
                                        finish();
                                    }
                                });
                                adbOpenFile.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                                adbOpenFile.show();
                                break;
                            case 1:
                                final EditText etFileName = new EditText(SLCFileList.this);
                                AlertDialog.Builder adbSaveFileName = new AlertDialog.Builder(SLCFileList.this);

                                final String originalFileName = fileName.substring(0, fileName.length() - 4);
                                etFileName.setHint(originalFileName);

                                adbSaveFileName.setTitle(R.string.save_title);
                                adbSaveFileName.setView(etFileName);
                                adbSaveFileName.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String newFileName = etFileName.getText().toString();
                                        if (newFileName.equals("") || (originalFileName + ".txt").equals(newFileName.endsWith(".txt") ? newFileName : newFileName + ".txt")) {
                                            return;
                                        } else {
                                            if (!newFileName.endsWith(".txt")) {
                                                newFileName = newFileName + ".txt";
                                            }
                                        }

                                        final File file = new File(filePath + fileName);
                                        final File renamedFile = new File(filePath + newFileName);

                                        if (renamedFile.exists()) {
                                            AlertDialog.Builder adbOverwrite = new AlertDialog.Builder(SLCFileList.this);
                                            adbOverwrite.setTitle(R.string.file_list_menu_rename);
                                            adbOverwrite.setMessage(String.format(getString(R.string.file_list_menu_rename_overwrite), newFileName));
                                            adbOverwrite.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    if (file.renameTo(renamedFile)) {
                                                        Toast.makeText(SLCFileList.this, R.string.file_list_menu_rename_success, Toast.LENGTH_SHORT).show();
                                                        loadFileList();
                                                    } else {
                                                        Toast.makeText(SLCFileList.this, R.string.file_list_menu_rename_fail, Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                            adbOverwrite.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                }
                                            });
                                            adbOverwrite.show();
                                        } else {
                                            if (file.renameTo(renamedFile)) {
                                                Toast.makeText(SLCFileList.this, R.string.file_list_menu_rename_success, Toast.LENGTH_SHORT).show();
                                                loadFileList();
                                            } else {
                                                Toast.makeText(SLCFileList.this, R.string.file_list_menu_rename_fail, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                });
                                adbSaveFileName.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                                adbSaveFileName.show();
                                break;
                            case 2:
                                AlertDialog.Builder adbDelete = new AlertDialog.Builder(SLCFileList.this);
                                adbDelete.setTitle(fileName);
                                adbDelete.setTitle(String.format(getString(R.string.file_list_menu_delete_ask), fileName));
                                adbDelete.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        File file = new File(filePath + fileName);
                                        if (file.delete()) {
                                            Toast.makeText(SLCFileList.this, R.string.file_list_menu_delete_success, Toast.LENGTH_SHORT).show();
                                            loadFileList();
                                        } else {
                                            Toast.makeText(SLCFileList.this, R.string.file_list_menu_delete_fail, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                adbDelete.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                                adbDelete.show();
                                break;
                        }
                    }
                });
                adbFileOptions.show();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFileList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadFileList() {
        try {
            alFileList.clear();
            File flDirectory = new File(Environment.getExternalStorageDirectory() + "/DigutSLC");
            if (flDirectory.listFiles().length > 0) {
                for (File file : flDirectory.listFiles()) {
                    String name = file.getName();
                    alFileList.add(name);
                }
                ArrayAdapter<String> aaFileList = new ArrayAdapter<>(SLCFileList.this, android.R.layout.simple_list_item_1, alFileList);
                lvFileList.setAdapter(aaFileList);
            }
        } catch (NullPointerException e) {
            AlertDialog.Builder adbFileOpenFail = new AlertDialog.Builder(SLCFileList.this);
            adbFileOpenFail.setTitle(R.string.file_list_load_fail_title);
            adbFileOpenFail.setMessage(R.string.file_list_load_fail_content);
            adbFileOpenFail.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            adbFileOpenFail.show();
        }
    }
}
