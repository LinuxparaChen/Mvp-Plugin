package com.mvp.plugin.tools;

import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiParameter;

public class GenerateTool {

    public static String generateParameterTypesText(PsiParameter[] parameters) {
        StringBuffer sb = new StringBuffer();
        for (PsiParameter psiParameter : parameters) {
            if (psiParameter.getType() instanceof PsiClassType){
                String clazzName = ((PsiClassType) psiParameter.getType()).getClassName();
                sb.append(", "+clazzName+".class");
            }
        }
        return sb.toString();
    }

    public static String generateParameterNamesText(PsiParameter[] parameters) {
        StringBuffer sb = new StringBuffer();
        for (PsiParameter psiParameter : parameters) {
            sb.append(", "+psiParameter.getName());
        }
        return sb.toString();
    }

}
