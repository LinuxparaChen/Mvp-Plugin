package com.mvp.plugin.tools;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PsiAnnotationTool {

    public static String findAnoTextValue(@NotNull PsiAnnotation annotation, @NotNull String key) {
        if (annotation == null || !annotation.isValid()){
            return null;
        }

        PsiAnnotationMemberValue annotationValue = annotation.findAttributeValue(key);
        if (annotationValue instanceof PsiLiteralExpression) {
            return ((PsiLiteralExpression) annotationValue).getValue().toString();
        }
        return null;
    }

    public static List<PsiClass> findAnoClazzArrayValue(@NotNull PsiAnnotation annotation, @NotNull String key) {
        List<PsiClass> values = new ArrayList<>();
        PsiAnnotationMemberValue annotationValue = annotation.findAttributeValue(key);
        for (PsiElement psiElement : annotationValue.getChildren()) {
            if (psiElement instanceof PsiClassObjectAccessExpression) {
                PsiType psiType = ((PsiClassObjectAccessExpression) psiElement).getOperand().getType();
                if (psiType instanceof PsiClassType){
                    //psitype 转 psiclass
                    values.add(((PsiClassType) psiType).resolve());
                }
            }
        }
        return values;
    }

    public static List<PsiMethod> getAnoMethods(@NotNull PsiClass psiClazz, @NotNull String annotation) {
        List<PsiMethod> anoMethodList = new ArrayList<>();

        if (psiClazz == null || !psiClazz.isValid()) {
            return anoMethodList;
        }

        for (PsiMethod psiMethod : psiClazz.getAllMethods()) {
            if (psiMethod.getAnnotation(annotation) != null) {
                anoMethodList.add(psiMethod);
            }
        }
        return anoMethodList;
    }
}
