package com.github.kkolyan.fyroxliteriderplugin

import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.actions.CreateFromTemplateAction
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import java.util.*

class CreateGlobalScriptAction : CreateFromTemplateAction<PsiElement>(
    "Fyrox C# Global Script",
    "Creates a new Fyrox C# Global Script",
    MyIcons.Fyrox
) {

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle("New Fyrox C# Global Script")
            .addKind("Fyrox C# Global Script", MyIcons.Fyrox, "GlobalScript.cs")
    }

    override fun getActionName(directory: PsiDirectory, newName: String, templateName: String): String =
        "Create Global Script $newName"

    override fun createFile(name: String, templateName: String, dir: PsiDirectory): PsiElement {
        val manager = FileTemplateManager.getInstance(dir.project)
        val template = manager.getInternalTemplate(templateName)
        val props = FileTemplateManager.getInstance(dir.project).defaultProperties
        props["NAME"] = name
        return FileTemplateUtil.createFromTemplate(template, name, props, dir)
    }
}