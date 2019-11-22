package com.mvp.plugin.tools;

import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PsiTool {

    public static PsiClass getPsiClazz(PsiFile psiFile) {
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

//    public static PsiPackage getPsiPackage(PsiFile psiFile) {
//        for (PsiElement psiElement : psiFile.getChildren()) {
//            if (psiElement instanceof PsiPackage) {
//                return (PsiPackage) psiElement;
//            }
//        }
//        return null;
//    }

    public static PsiClass createItrJavaFile(@NotNull PsiDirectory parentPsiDir, @NotNull String itrName, @NotNull List<PsiMethod> methods) {
        if (parentPsiDir == null || !parentPsiDir.isValid()) {
            return null;
        }
        if (TextUtils.isEmpty(PsiFileTool.ensureJavaSuffix(itrName))) {
            return null;
        }
        if (methods == null || methods.isEmpty()) {
            return null;
        }
        PsiFileTool.deleteFileIfExist(parentPsiDir, PsiFileTool.ensureJavaSuffix(itrName));

        PsiClass itrPsiClazz = JavaDirectoryService.getInstance().createInterface(parentPsiDir, itrName);
        itrPsiClazz.getModifierList().setModifierProperty(PsiModifier.PUBLIC, true);
        PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(parentPsiDir.getProject());
        for (PsiMethod psiMethod : methods) {
            StringBuilder methodText = new StringBuilder(PsiModifier.PUBLIC + " " + PsiModifier.ABSTRACT + " ");
            String returnType = psiMethod.getReturnType().getCanonicalText();
            methodText.append(returnType + " ");
            methodText.append(psiMethod.getName());
            methodText.append("();");
            PsiMethod itrPsiMethod = psiElementFactory.createMethodFromText(methodText.toString(), null);
            //参数
            for (PsiParameter psiParameter : psiMethod.getParameterList().getParameters()) {
                itrPsiMethod.getParameterList().add(psiParameter);
            }
            itrPsiClazz.add(itrPsiMethod);
        }
        CodeStyleManager.getInstance(parentPsiDir.getProject()).reformat(itrPsiClazz);
        return itrPsiClazz;
    }

    private static final String CREATE_DELEGATE_TEMPLATE = "return (%s) java.lang.reflect.Proxy.newProxyInstance(view.getClass().getClassLoader(), new Class[]{%s.class}, new com.mvp.delegate.DelegateInvocationHandler(%s));";

    public static PsiClass createMvpManagerJavaFile(@NotNull PsiDirectory parentPsiDir, @NotNull String name, @NotNull PsiClass viewItrClazz, @NotNull List<PsiMethod> methods) {
        if (parentPsiDir == null || !parentPsiDir.isValid()) {
            return null;
        }
        if (TextUtils.isEmpty(PsiFileTool.ensureJavaSuffix(name))) {
            return null;
        }
        if (viewItrClazz == null || !viewItrClazz.isValid()) {
            return null;
        }
        if (methods == null || methods.isEmpty()) {
            return null;
        }
        PsiFileTool.deleteFileIfExist(parentPsiDir, PsiFileTool.ensureJavaSuffix(name));

        PsiClass mvpManagerClazz = JavaDirectoryService.getInstance().createClass(parentPsiDir, name);
        mvpManagerClazz.getModifierList().setModifierProperty(PsiModifier.PUBLIC, true);
        PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(parentPsiDir.getProject());

        //创建createViewDelegate函数
        PsiClassType viewItrType = psiElementFactory.createType(viewItrClazz);
        PsiMethod createViewDelegatePsiMehtod = psiElementFactory.createMethod("createViewDelegate", viewItrType);
        //参数
        createViewDelegatePsiMehtod.getModifierList().setModifierProperty(PsiModifier.PRIVATE, true);
        createViewDelegatePsiMehtod.getModifierList().setModifierProperty(PsiModifier.STATIC, true);
        PsiClassType objectPsiType = psiElementFactory.createTypeByFQClassName("java.lang.Object");
        createViewDelegatePsiMehtod.getParameterList().add(psiElementFactory.createParameter("view", objectPsiType));
        //函数体
        PsiStatement delegateStatement = psiElementFactory.createStatementFromText(String.format(CREATE_DELEGATE_TEMPLATE, viewItrClazz.getQualifiedName(), viewItrClazz.getQualifiedName(), "view"), null);
        createViewDelegatePsiMehtod.getBody().add(delegateStatement);

        mvpManagerClazz.add(createViewDelegatePsiMehtod);

        for (PsiMethod method : methods) {
            mvpManagerClazz.add(method);
        }

        return mvpManagerClazz;
    }

    public static PsiMethod createPresenterDelegateMethod(@NotNull PsiClass pPsiClazz, @NotNull PsiClass pItrPsiClazz, @NotNull PsiClass viewItrPsiClazzz) {
        PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(pPsiClazz.getProject());

        PsiClassType pItrType = psiElementFactory.createType(pItrPsiClazz);
        PsiMethod method = psiElementFactory.createMethod("create" + pPsiClazz.getName() + "Delegate", pItrType);
        method.getModifierList().setModifierProperty(PsiModifier.PUBLIC, true);
        method.getModifierList().setModifierProperty(PsiModifier.STATIC, true);

        PsiParameter viewPsiParameter = psiElementFactory.createParameter("view", psiElementFactory.createTypeByFQClassName("java.lang.Object"));
        method.getParameterList().add(viewPsiParameter);

        PsiStatement statement_var = psiElementFactory.createStatementFromText(viewItrPsiClazzz.getQualifiedName() + " viewDelegate = createViewDelegate(view);", null);
        method.getBody().add(statement_var);

        PsiStatement statement_var_p = psiElementFactory.createStatementFromText(pPsiClazz.getQualifiedName() + " presenter = new " + pPsiClazz.getQualifiedName() + "(viewDelegate);", null);
        method.getBody().add(statement_var_p);

        PsiStatement p_delegate_statement = psiElementFactory.createStatementFromText(String.format(CREATE_DELEGATE_TEMPLATE, pItrPsiClazz.getQualifiedName(), pItrPsiClazz.getQualifiedName(), "presenter"), null);
        method.getBody().add(p_delegate_statement);
        return method;
    }

    public static PsiMethod createConstrotMethodIfExist(@NotNull PsiClass targetPsiClazz, @NotNull PsiClass argPsiClazz, @NotNull String argName) {
        PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(targetPsiClazz.getProject());

        PsiMethod constructorPsiMethod = psiElementFactory.createConstructor(targetPsiClazz.getName());
        PsiParameter argPsi = psiElementFactory.createParameter(argName, psiElementFactory.createType(argPsiClazz));
        constructorPsiMethod.getParameterList().add(argPsi);

        PsiMethod _constructorPsiMethod = targetPsiClazz.findMethodBySignature(constructorPsiMethod, true);
        if (_constructorPsiMethod != null) {
            return _constructorPsiMethod;
        }
        targetPsiClazz.add(constructorPsiMethod);
        return constructorPsiMethod;
    }
}
