package com.mvp.plugin.bean;

import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class MvpClazz {

    private String mPackageName;
    private String mClazzName;
    private List<PsiMethod> mMethods;
    private List<PsiImportStatement> mImports;

    public MvpClazz(String packageName, String clazzName, List<PsiMethod> methods) {
        this.mPackageName = packageName;
        this.mClazzName = clazzName;
        this.mMethods = methods;
        this.mImports = new ArrayList<>();
    }

    public MvpClazz(String packageName, String clazzName) {
        this.mPackageName = packageName;
        this.mClazzName = clazzName;
        this.mMethods = new ArrayList<>();
        this.mImports = new ArrayList<>();
    }

    public void addPsiMethod(PsiMethod method) {
        mMethods.add(method);
    }

    public void addPsiImort(PsiImportStatement psiImport) {
        mImports.add(psiImport);
    }

    public String getQName() {
        return mPackageName + "." + mClazzName;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getName() {
        return mClazzName;
    }

    public List<PsiMethod> getMethods(){
        return mMethods;
    }

    public List<PsiImportStatement> getImports(){
        return mImports;
    }
}
