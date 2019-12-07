package com.mvp.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.mvp.plugin.bean.MvpClazz;
import com.mvp.plugin.tools.PsiAnnotationTool;
import com.mvp.plugin.tools.PsiFileTool;
import com.mvp.plugin.tools.PsiMethodTool;
import org.apache.http.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class MvpCodeAction extends AnAction {

    public static final String MVP_V = "com.mvp.plugin.dependent.annotation.MVP_V";
    public static final String MVP_V_Packaged = "packaged";
    public static final String MVP_V_Key = "key";
    public static final String MVP_V_Presenters = "presenters";
    public static final String MVP_Itr = "com.mvp.plugin.dependent.annotation.MVP_Itr";
    public static final String Execute_On = "com.mvp.plugin.dependent.annotation.ExecuteOn";

    public static final String MVP_DIALOG_TITLE = "Mvp-Code";

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        //=============================================文件必须为java文件==============================================
        if (psiFile.getFileType() != StdFileTypes.JAVA) {
            //获取到的文件不是java文件，则不作任何操作。
            Messages.showMessageDialog("当前Mvp-Code插件操作的文件非Java文件！", MVP_DIALOG_TITLE, null);
            return;
        }
        //=============================================文件中必须定义了类==============================================
        PsiClass psiClazz = PsiFileTool.getPsiClazz(psiFile);
        if (psiClazz == null) {
            //包名为空，包名可能没内容，回去没有包名。
            Messages.showMessageDialog("未在Java文件发现定义的类！", MVP_DIALOG_TITLE, null);
            return;
        }
        //=============================================类必须含有@MVP_V注解==============================================
        PsiAnnotation MVP_V_Ano = psiClazz.getAnnotation(MVP_V);
        if (MVP_V_Ano == null) {
            Messages.showMessageDialog(String.format("当前Mvp-Code插件操作的类：%s 未使用@MVP_V注解！", psiClazz.getName()), MVP_DIALOG_TITLE, null);
            return;
        }
        //=============================================获取@MVP_V注解中pacakged的值==============================================
        String _Mvp_V_Packaged = PsiAnnotationTool.findAnoTextValue(MVP_V_Ano, MVP_V_Packaged);
        if (TextUtils.isEmpty(_Mvp_V_Packaged)) {
            _Mvp_V_Packaged = PsiFileTool.getTextPackage(psiClazz);
            if (TextUtils.isEmpty(_Mvp_V_Packaged)) {
                _Mvp_V_Packaged = "_generate";
            } else {
                _Mvp_V_Packaged += "._generate";
            }
        }
        //=============================================获取@MVP_V注解中key的值==============================================
        String _Mvp_V_Key = PsiAnnotationTool.findAnoTextValue(MVP_V_Ano, MVP_V_Key);
        if (TextUtils.isEmpty(_Mvp_V_Key)) {
            Messages.showMessageDialog("@MVP_V注解的属性key未指定 或 为空！", MVP_DIALOG_TITLE, null);
            return;
        }
        //=============================================获取@MVP_V注解中presenters的数组==============================================
        List<PsiClass> _Mvp_V_Presenters = PsiAnnotationTool.findAnoClazzArrayValue(MVP_V_Ano, MVP_V_Presenters);
        if (_Mvp_V_Presenters == null || _Mvp_V_Presenters.isEmpty()) {
            Messages.showMessageDialog("@MVP_V注解的属性presenters未指定 或 为空！", "Mvp-Code", null);
        }


        //=============================================获取类中被@MVP_Itr修饰的方法==============================================
        List<PsiMethod> extractViewPsiMethods = PsiAnnotationTool.getAnoMethods(psiClazz, MVP_Itr);
        if (extractViewPsiMethods == null || extractViewPsiMethods.isEmpty()) {
            Messages.showMessageDialog(psiClazz.getName() + " 类中未找到MVP_Itr注解修饰的函数！", MVP_DIALOG_TITLE, null);
            return;
        }
        if (PsiMethodTool.isModifierByStatic(extractViewPsiMethods)) {
            Messages.showMessageDialog(psiClazz.getName() + " 类中被@MVP_Itr注解的函数不能被static修饰！", MVP_DIALOG_TITLE, null);
            return;
        }
        if (PsiMethodTool.isNotReturnVoidByExecuteOn(extractViewPsiMethods)) {
            Messages.showMessageDialog(psiClazz.getName() + " 类中被@ExecuteOn注解的函数返回值必须是void！", MVP_DIALOG_TITLE, null);
            return;
        }
        //=============================================生成View接口==============================================
        PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(psiClazz.getProject());
        List<PsiMethod> viewItrMethods = PsiMethodTool.generateItrPsiMethods(extractViewPsiMethods, psiElementFactory);
        MvpClazz viewItrClazz = new MvpClazz(_Mvp_V_Packaged + ".view", "I" + _Mvp_V_Key + "View", viewItrMethods);
        PsiType viewItrType = psiElementFactory.createTypeFromText(viewItrClazz.getQName(), null);

        MvpClazz mvpManagerClazz = new MvpClazz(_Mvp_V_Packaged + ".manager", _Mvp_V_Key + "MvpManager");
        PsiImportStatement proxyPsiImport = psiElementFactory.createImportStatement(psiElementFactory.createTypeByFQClassName("java.lang.reflect.Proxy").resolve());
        PsiImportStatement invocationHandlerPsiImport = psiElementFactory.createImportStatement(psiElementFactory.createTypeByFQClassName("com.mvp.plugin.dependent.delegate.DelegateInvocationHandler").resolve());
//        PsiImportStatement viewInvocationHandlerPsiImport = psiElementFactory.createImportStatement(psiElementFactory.createTypeByFQClassName("com.mvp.plugin.dependent.delegate.ViewDelegateInvocationHandler").resolve());
//        PsiImportStatement presenterInvocationHandlerPsiImport = psiElementFactory.createImportStatement(psiElementFactory.createTypeByFQClassName("com.mvp.plugin.dependent.delegate.PresenterDelegateInvocationHandler").resolve());
        mvpManagerClazz.addPsiImort(proxyPsiImport);
        mvpManagerClazz.addPsiImort(invocationHandlerPsiImport);
//        mvpManagerClazz.addPsiImort(viewInvocationHandlerPsiImport);
//        mvpManagerClazz.addPsiImort(presenterInvocationHandlerPsiImport);
        mvpManagerClazz.addPsiMethod(PsiMethodTool.createManagerPsiMethodView(psiElementFactory, "createViewDelegate", viewItrType));

        List<MvpClazz> presenterItrClazzList = new ArrayList<>();
        for (PsiClass mvp_v_presenter : _Mvp_V_Presenters) {
            //===========================================P层需要抽取的接口========================================
            List<PsiMethod> extractPresenterPsiMethods = PsiAnnotationTool.getAnoMethods(mvp_v_presenter, MVP_Itr);
            if (extractPresenterPsiMethods == null || extractPresenterPsiMethods.isEmpty()) {
                Messages.showMessageDialog(mvp_v_presenter.getName() + " 类中未找到MVP_Itr注解修饰的函数！", MVP_DIALOG_TITLE, null);
                return;
            }
            if (PsiMethodTool.isModifierByStatic(extractPresenterPsiMethods)) {
                Messages.showMessageDialog(mvp_v_presenter.getName() + "中被@MVP_Itr注解的函数不能被static修饰！", MVP_DIALOG_TITLE, null);
                return;
            }
            if (PsiMethodTool.isNotReturnVoidByExecuteOn(extractPresenterPsiMethods)) {
                Messages.showMessageDialog(psiClazz.getName() + " 类中被@ExecuteOn注解的函数返回值必须是void！", MVP_DIALOG_TITLE, null);
                return;
            }

            mvpManagerClazz.addPsiImort(psiElementFactory.createImportStatement(mvp_v_presenter));
            //=============================================生成Presenter接口==============================================
            List<PsiMethod> presenterItrMethods = PsiMethodTool.generateItrPsiMethods(extractPresenterPsiMethods, psiElementFactory);
            MvpClazz presenterItrClazz = new MvpClazz(_Mvp_V_Packaged + ".presenter", "I" + mvp_v_presenter.getName(), presenterItrMethods);
            presenterItrClazzList.add(presenterItrClazz);
            PsiType presenterItrType = psiElementFactory.createTypeFromText(presenterItrClazz.getQName(), null);

            mvpManagerClazz.addPsiMethod(PsiMethodTool.createManagerPsiMethodPresenter(psiElementFactory, "create" + mvp_v_presenter.getName() + "Delegate", presenterItrType, viewItrType, psiElementFactory.createType(mvp_v_presenter)));

        }

        WriteCommandAction.runWriteCommandAction(psiClazz.getProject(), () -> {
            PsiDirectory javaDir = PsiFileTool.findSupDirByName(psiFile.getParent(), "java");

            PsiDirectory viewDir = PsiFileTool.createPackageDir(javaDir, viewItrClazz.getPackageName());
            PsiFileTool.createJavaFile(viewDir, viewItrClazz, true);

            for (MvpClazz presenterItrClazz : presenterItrClazzList) {
                PsiDirectory presenterDir = PsiFileTool.createPackageDir(javaDir, presenterItrClazz.getPackageName());
                PsiFileTool.createJavaFile(presenterDir, presenterItrClazz, true);
            }

            for (PsiClass mvp_v_presenter : _Mvp_V_Presenters) {
                PsiFileTool.createControtMethodIfNotExist(mvp_v_presenter, viewItrType, "viewDelegate");
            }

            PsiDirectory managerDir = PsiFileTool.createPackageDir(javaDir, mvpManagerClazz.getPackageName());
            PsiFileTool.createJavaFile(managerDir, mvpManagerClazz, false);
        });
    }
}
