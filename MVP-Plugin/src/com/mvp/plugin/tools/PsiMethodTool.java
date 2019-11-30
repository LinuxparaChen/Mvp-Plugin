package com.mvp.plugin.tools;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PsiMethodTool {
    public static boolean isModifierByStatic(@NotNull List<PsiMethod> psiMethods) {
        for (PsiMethod psiMethod : psiMethods) {
            if (psiMethod.getModifierList().hasModifierProperty(PsiModifier.STATIC)) {
                return true;
            }
        }
        return false;
    }

    public static List<PsiMethod> generateItrPsiMethods(@NotNull List<PsiMethod> psiMethods, @NotNull PsiElementFactory psiElementFactory) {
        List<PsiMethod> _psiMethods = new ArrayList<>();
        for (PsiMethod psiMethod : psiMethods) {
            StringBuilder methodText = new StringBuilder(PsiModifier.PUBLIC + " " + PsiModifier.ABSTRACT + " ");
            methodText.append(psiMethod.getReturnType().getCanonicalText() + " ");
            methodText.append(psiMethod.getName() + "();");
            PsiMethod itrPsiMethod = psiElementFactory.createMethodFromText(methodText.toString(), null);
            //参数
            for (PsiParameter psiParameter : psiMethod.getParameterList().getParameters()) {
                itrPsiMethod.getParameterList().add(psiParameter);
            }
            _psiMethods.add(itrPsiMethod);
        }
        return _psiMethods;
    }

    private static PsiMethod createStaticMethod(@NotNull PsiElementFactory psiElementFactory, @NotNull String methodName, @NotNull PsiType returnType) {
        PsiMethod psiMethod = psiElementFactory.createMethod(methodName, returnType);
        psiMethod.getModifierList().setModifierProperty(PsiModifier.PUBLIC, true);
        psiMethod.getModifierList().setModifierProperty(PsiModifier.STATIC, true);

        PsiParameter psiParameter = psiElementFactory.createParameter("view", psiElementFactory.createTypeByFQClassName("java.lang.Object"));
        psiMethod.getParameterList().add(psiParameter);

        return psiMethod;
    }

    public static PsiMethod createManagerPsiMethodView(@NotNull PsiElementFactory psiElementFactory, @NotNull String methodName, @NotNull PsiType returnType) {
        PsiMethod psiMethod = createStaticMethod(psiElementFactory, methodName, returnType);

        String createDelegateText = "return (%s) Proxy.newProxyInstance(view.getClass().getClassLoader(), new Class[]{%s.class}, new ViewDelegateInvocationHandler(view));";
        PsiStatement psiStatement = psiElementFactory.createStatementFromText(String.format(createDelegateText, returnType.getCanonicalText(), returnType.getCanonicalText()), null);
        psiMethod.getBody().add(psiStatement);

        return psiMethod;
    }

    public static PsiMethod createManagerPsiMethodPresenter(@NotNull PsiElementFactory psiElementFactory, @NotNull String methodName, @NotNull PsiType returnType, @NotNull PsiType viewItrType, @NotNull PsiType presenterType) {
        PsiMethod psiMethod = createStaticMethod(psiElementFactory, methodName, returnType);

        String callMethodText = "%s viewDelegate = createViewDelegate(view);";
        PsiStatement callMethodStatement = psiElementFactory.createStatementFromText(String.format(callMethodText, viewItrType.getCanonicalText()), null);

        String varPresenterText = "%s presenter = new %s(viewDelegate);";
        PsiStatement varPresenterStatement = psiElementFactory.createStatementFromText(String.format(varPresenterText, presenterType.getCanonicalText(), presenterType.getCanonicalText()), null);

        String createDelegateText = "return (%s) Proxy.newProxyInstance(view.getClass().getClassLoader(), new Class[]{%s.class}, new PresenterDelegateInvocationHandler(presenter));";
        PsiStatement createDelegateStatement = psiElementFactory.createStatementFromText(String.format(createDelegateText, returnType.getCanonicalText(), returnType.getCanonicalText()), null);

        psiMethod.getBody().add(callMethodStatement);
        psiMethod.getBody().add(varPresenterStatement);
        psiMethod.getBody().add(createDelegateStatement);

        return psiMethod;
    }
}
