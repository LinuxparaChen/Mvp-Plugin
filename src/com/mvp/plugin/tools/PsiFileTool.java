package com.mvp.plugin.tools;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PsiFileTool {
    private static final String JAVA_SUFFIX = ".java";

    public static PsiDirectory createDirIfNotExist(@NotNull PsiDirectory parentDir, @NotNull String dirName) {
        PsiDirectory subDir = parentDir.findSubdirectory(dirName);
        if (subDir == null || !subDir.getVirtualFile().exists()) {
            subDir = parentDir.createSubdirectory(dirName);
        }
        return subDir;
    }

    public static String ensureJavaSuffix(String clazzName) {
        StringBuffer _clazzName = new StringBuffer(clazzName);
        if (!clazzName.endsWith(JAVA_SUFFIX)) {
            _clazzName.append(JAVA_SUFFIX);
        }
        return _clazzName.toString();
    }

    public static void deleteFileIfExist(@NotNull PsiDirectory parentPsiDir, @NotNull String fileName) {
        PsiFile psiFile = parentPsiDir.findFile(fileName);
        if (psiFile == null || !psiFile.isValid()) {
            return;
        }
        psiFile.delete();
    }

    public static PsiDirectory findSupDirByName(@NotNull PsiDirectory psiDir, @NotNull String parentDirName) {
        if (psiDir.getName().equals(parentDirName)) {
            return psiDir;
        }
        return findSupDirByName(psiDir.getParent(), parentDirName);
    }

    public static PsiDirectory createPackageDir(@NotNull PsiDirectory psiDir, @NotNull String packageName) {
        if (TextUtils.isEmpty(packageName)){
            return psiDir;
        }
        String[] dirs = packageName.split("\\.");
        for (int i = 0; i < dirs.length; i++) {
            psiDir = createDirIfNotExist(psiDir, dirs[i]);
        }
        return psiDir;
    }
}
