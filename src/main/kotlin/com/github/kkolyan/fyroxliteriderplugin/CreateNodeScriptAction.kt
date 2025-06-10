package com.github.kkolyan.fyroxliteriderplugin

import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.actions.CreateFromTemplateAction
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import java.util.*

class CreateNodeScriptAction : CreateFromTemplateAction<PsiElement>(
    "Fyrox C# Node Script",
    "Creates a new Fyrox C# Node Script",
    MyIcons.Fyrox
) {

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle("New Fyrox C# Node Script")
            .addKind("Fyrox C# Node Script", MyIcons.Fyrox, "NodeScript.cs")
    }

    override fun getActionName(directory: PsiDirectory, newName: String, templateName: String): String =
        "Create NodeScript Class $newName"

    override fun createFile(name: String, templateName: String, dir: PsiDirectory): PsiElement {
        val manager = FileTemplateManager.getInstance(dir.project)
        val template = manager.getInternalTemplate(templateName)
        val props = FileTemplateManager.getInstance(dir.project).defaultProperties
        props["NAME"] = name
        props["GUID"] = UUID.randomUUID().toString()
        return FileTemplateUtil.createFromTemplate(template, name, props, dir)
    }
}