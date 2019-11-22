package com.mvp.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.mvp.plugin.tools.PsiAnnotationTool;
import com.mvp.plugin.tools.PsiFileTool;
import com.mvp.plugin.tools.PsiTool;
import org.apache.http.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class MvpCodeAction extends AnAction {

    public static final String MVP_V = "com.mvp.annotation.MVP_V";
    public static final String MVP_V_Packaged = "packaged";
    public static final String MVP_V_Key = "key";
    public static final String MVP_V_Presenters = "presenters";
    public static final String MVP_Itr = "com.mvp.annotation.MVP_Itr";

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

        if (psiFile.getFileType() != StdFileTypes.JAVA) {
            //获取到的文件不是java文件，则不作任何操作。
            Messages.showMessageDialog("当前Mvp-Code插件操作的文件非Java文件！", "Mvp-Code", null);
            return;
        }

        PsiClass psiClazz = PsiTool.getPsiClazz(psiFile);
        if (psiClazz == null) {
            //包名为空，包名可能没内容，回去没有包名。
            Messages.showMessageDialog("未在Java文件发现定义的类！", "Mvp-Code", null);
            return;
        }

        PsiAnnotation MVP_V_Ano = psiClazz.getAnnotation(MVP_V);
        if (MVP_V_Ano == null) {
            Messages.showMessageDialog(String.format("当前Mvp-Code插件操作的类：%s 未使用@MVP_V注解！", psiClazz.getName()), "Mvp-Code", null);
            return;
        }

        String _Mvp_V_Packaged = PsiAnnotationTool.findAnoTextValue(MVP_V_Ano, MVP_V_Packaged);
        if (TextUtils.isEmpty(_Mvp_V_Packaged)) {
            _Mvp_V_Packaged = PsiTool.getTextPackage(psiClazz);
            if (TextUtils.isEmpty(_Mvp_V_Packaged)) {
                _Mvp_V_Packaged = "_generate";
            } else {
                _Mvp_V_Packaged += "._generate";
            }
        }

        String _Mvp_V_Key = PsiAnnotationTool.findAnoTextValue(MVP_V_Ano, MVP_V_Key);
        if (TextUtils.isEmpty(_Mvp_V_Key)) {
            Messages.showMessageDialog("@MVP_V注解的属性key未指定 或 为空！", "Mvp-Code", null);
            return;
        }

        List<PsiClass> _Mvp_V_Presenters = PsiAnnotationTool.findAnoClazzArrayValue(MVP_V_Ano, MVP_V_Presenters);
        if (_Mvp_V_Presenters == null || _Mvp_V_Presenters.isEmpty()) {
            Messages.showMessageDialog("@MVP_V注解的属性presenters未指定 或 为空！", "Mvp-Code", null);
        }

        String final_Mvp_V_Packaged = _Mvp_V_Packaged;
        WriteCommandAction.runWriteCommandAction(psiClazz.getProject(), () -> {
            PsiDirectory javaDir = PsiFileTool.findSupDirByName(psiFile.getParent(), "java");
            PsiDirectory generatedDir = PsiFileTool.createPackageDir(javaDir, final_Mvp_V_Packaged);

            //===========================================view层需要抽取的接口========================================
            List<PsiMethod> extractViewPsiMethods = PsiAnnotationTool.getAnoMethods(psiClazz, MVP_Itr);
            if (extractViewPsiMethods == null || extractViewPsiMethods.isEmpty()) {
                return;
            }

            //===========================================生成V层接口代码========================================
            PsiDirectory viewGenerateDir = PsiFileTool.createDirIfNotExist(generatedDir, "view");
            //生成View接口
            PsiClass viewItrPsiClazz = PsiTool.createItrJavaFile(viewGenerateDir, "I" + _Mvp_V_Key + "View", extractViewPsiMethods);
            if (viewItrPsiClazz == null || !viewItrPsiClazz.isValid()) {
                return;
            }

            //================================================================================================
            PsiDirectory presenterGenerateDir = PsiFileTool.createDirIfNotExist(generatedDir, "presenter");
            List<PsiMethod> createPresenterDelegateMethodList = new ArrayList<>();
            List<PsiClass> presenterItrPsiClazzList = new ArrayList<>();
            for (PsiClass presenterPsiClazz : _Mvp_V_Presenters) {
                //===========================================presenter层需要抽取的接口========================================
                List<PsiMethod> extractPresenterPsiMethods = PsiAnnotationTool.getAnoMethods(presenterPsiClazz, MVP_Itr);
                if (extractPresenterPsiMethods == null || extractPresenterPsiMethods.isEmpty()) {
                    continue;
                }
                //===========================================生成P层接口代码========================================
                PsiClass presenterItrPsiClazz = PsiTool.createItrJavaFile(presenterGenerateDir, "I" + presenterPsiClazz.getName(), extractPresenterPsiMethods);
                if (presenterItrPsiClazz == null || !presenterItrPsiClazz.isValid()) {
                    continue;
                }
                presenterItrPsiClazzList.add(presenterItrPsiClazz);
                //===========================================presenter层需要抽取的接口========================================
                PsiMethod createPresenterDelegateMethod = PsiTool.createPresenterDelegateMethod(presenterPsiClazz, presenterItrPsiClazz, viewItrPsiClazz);
                createPresenterDelegateMethodList.add(createPresenterDelegateMethod);

                PsiTool.createConstrotMethodIfExist(presenterPsiClazz, viewItrPsiClazz, "viewDelegate");

            }

            //===========================================创建MVPManager类========================================
            PsiDirectory managerGenerateDir = PsiFileTool.createDirIfNotExist(generatedDir, "manager");
            PsiClass mvpManagerClazz = PsiTool.createMvpManagerJavaFile(managerGenerateDir, _Mvp_V_Key + "MvpManager", viewItrPsiClazz, createPresenterDelegateMethodList);
            if (mvpManagerClazz == null || !mvpManagerClazz.isValid()) {
                return;
            }

        });
    }
}
