package com.starwin.app.utils;


import com.starwin.app.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;
import java.util.List;

@SuppressWarnings("Duplicates")
public class SelectPathHelper {

    private List<String> mSharedPath;
    private List<File> mSharedFiles;
    private JLabel mProgressLabel;

    public SelectPathHelper(List<String> sharedPath, List<File> sharedFiles) {
        mSharedPath = sharedPath;
        mSharedFiles = sharedFiles;
    }

    public void show(Frame parent) {
        String[] path = chooseFileToImport("选择文件或路径", parent);
        if (path != null) {
            mSharedPath.addAll(Arrays.asList(path));
            JDialog jDialog = new JDialog(parent, true);
            jDialog.setBounds(0, 0, 300, 200);
            jDialog.setLayout(new BorderLayout());
            mProgressLabel = new JLabel("Running ");
            mProgressLabel.setHorizontalAlignment(JLabel.CENTER);
            jDialog.add(mProgressLabel, BorderLayout.CENTER);
            jDialog.setLocationRelativeTo(parent);
            jDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    mProgressLabel = null;
                }
            });
            Main.sExecutors.networkIO().execute(() -> {
                for (String filePath : path) {
                    File f = new File(filePath);
                    if (f.isDirectory()) {
                        List<File> ret = FileUtil.getDirFileList(new ArrayList<>(), f);
                        int index = 0;
                        for (File file : ret) {
                            if (mProgressLabel == null) {
                                break;
                            }
                            if (mSharedFiles.indexOf(file) < 0) {
                                mSharedFiles.add(file);
                            }
                            mProgressLabel.setText("Running " + (int) (((index++) * 1.0f) / (ret.size() * 1.0f) * 100) + "%");
                        }
                    } else {
                        if (mSharedFiles.indexOf(f) < 0) {
                            mSharedFiles.add(f);
                        }
                        mProgressLabel.setText("Running " + "100%");
                    }
                    Map<String, List<String>> map = new LinkedHashMap<>();
                    setupFiles(mSharedFiles, "Movie", map, FileUtil.MOVIE_FORMAT);
                    setupFiles(mSharedFiles, "Music", map, FileUtil.MP3_FORMAT);
                    setupFiles(mSharedFiles, "Photo", map, FileUtil.IMG_FORMAT);
                    setupFiles(mSharedFiles, "Doc", map, FileUtil.DOC_FORMAT);
                    setupFiles(mSharedFiles, "Apk", map, FileUtil.APP_FORMAT);
                    setupFiles(mSharedFiles, "Rar", map, FileUtil.RAR_FORMAT);
                    jDialog.setVisible(false);
                    jDialog.dispose();
                    if (mProgressLabel == null) {
                        return;
                    }
                }
            });
            jDialog.setVisible(true);
        }
    }

    public String[] chooseFileToImport(String dialogTitle, Component parent) {
        JFileChooser jfc = new JFileChooser(JarToolUtil.getJarFilePath());
        jfc.setDialogTitle(dialogTitle);
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jfc.setMultiSelectionEnabled(true);
        int result = jfc.showOpenDialog(parent);
        if (JFileChooser.APPROVE_OPTION == result) {
            File[] file = jfc.getSelectedFiles();
            String[] ret = new String[file.length];
            int index = 0;
            for (File f : file) {
                ret[index++] = f.getAbsolutePath();
            }
            return ret;
        } else {
            return null;
        }
    }

    public void setupFiles(List<File> fileList, String key, Map<String, List<String>> map, String[] format) {
        List<File> movieList = FileUtil.getMediaFileListByName(format, fileList);
        List<String> movieStrPath = new ArrayList<>();
        for (File movie : movieList) {
            movieStrPath.add(movie.getAbsolutePath().replace("\\", "/"));
        }
        map.put(key, movieStrPath);
    }
}
