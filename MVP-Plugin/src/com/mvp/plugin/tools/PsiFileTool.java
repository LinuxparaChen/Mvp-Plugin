package com.mvp.plugin.tools;

import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.mvp.plugin.bean.MvpClazz;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PsiFileTool {
    private static final String JAVA_SUFFIX = ".java";

    public static PsiClass getPsiClazz(@NotNull PsiFile psiFile) {
        for (PsiElement psiElement : psiFile.getChildren()) {
            if (psiElement instanceof PsiClass) {
                return ((PsiClass) psiElement);
            }
        }
        return null;
    }

    public static String getTextPackage(PsiClass psiClazz) {
        String qName = psiClazz.getQualifiedName();
        return qName.substring(0, qName.length() - psiClazz.getName().length() - 1);
    }
//    public static boolean isExist(@NotNull PsiDirectory parentDir, @NotNull String fileName) {
//        PsiFile psiFile = parentDir.findFile(ensureJavaSuffix(fileName));
//        return psiFile != null && psiFile.getVirtualFile().exists();
//    }

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
        if (TextUtils.isEmpty(packageName)) {
            return psiDir;
        }
        String[] dirs = packageName.split("\\.");
        for (int i = 0; i < dirs.length; i++) {
            psiDir = createDirIfNotExist(psiDir, dirs[i]);
        }
        return psiDir;
    }

    public static PsiClass createJavaFile(@NotNull PsiDirectory parentDir, @NotNull MvpClazz Clazz, boolean isInterface) {
        PsiClass psiClazz = null;
        if (isInterface) {
            deleteFileIfExist(parentDir,ensureJavaSuffix(Clazz.getName()));
            psiClazz = JavaDirectoryService.getInstance().createInterface(parentDir, Clazz.getName());
        } else {
            deleteFileIfExist(parentDir,ensureJavaSuffix(Clazz.getName()));
            psiClazz = JavaDirectoryService.getInstance().createClass(parentDir, Clazz.getName());
        }

        psiClazz.getModifierList().setModifierProperty(PsiModifier.PUBLIC,true);

        for (PsiImportStatement clazzImport : Clazz.getImports()) {
            ((PsiJavaFile) psiClazz.getContainingFile()).getImportList().add(clazzImport);
        }
        for (PsiMethod psiMethod : Clazz.getMethods()) {
            psiClazz.add(psiMethod);
        }
        CodeStyleManager.getInstance(parentDir.getProject()).reformat(psiClazz);
        return psiClazz;
    }

    public static PsiMethod createControtMethodIfNotExist(@NotNull PsiClass targetClazz, @NotNull PsiType argType, @NotNull String argName) {
        PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(targetClazz.getProject());

        PsiMethod constructorPsiMethod = psiElementFactory.createConstructor(targetClazz.getName());
        PsiParameter argPsi = psiElementFactory.createParameter(argName, argType);
        constructorPsiMethod.getParameterList().add(argPsi);

        PsiMethod _constructorPsiMethod = targetClazz.findMethodBySignature(constructorPsiMethod, true);
        if (_constructorPsiMethod != null) {
            return _constructorPsiMethod;
        }
        targetClazz.add(constructorPsiMethod);
        return _constructorPsiMethod;
    }

}
