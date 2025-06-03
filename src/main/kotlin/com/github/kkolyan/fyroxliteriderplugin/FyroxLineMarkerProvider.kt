package com.github.kkolyan.fyroxliteriderplugin

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.elementType
import com.jetbrains.rider.languages.fileTypes.csharp.psi.impl.CSharpDummyDeclaration
import com.jetbrains.rider.languages.fileTypes.csharp.psi.impl.CSharpFieldDeclaration


class FyroxLineMarkerProvider : LineMarkerProvider {

    enum class ScriptType {
        Node,
        Global,
    }

    data class Script(
        val type: ScriptType,
        val element: PsiElement,
    )

    fun resolveScriptType(element: PsiElement) : Script? {

        if (element is CSharpDummyDeclaration && element.elementType.toString() == "cs:class-declaration") {
            val children = ArrayDeque(element.children
                .map { it.elementType to it }
                .filter { it.second !is PsiWhiteSpace })
            val first = children.first()
            if (first.second is CSharpDummyDeclaration && first.second.text.startsWith("[Uuid(") ) {
                children.removeFirst()
            }
            if (children.removeFirst().second.text != "public") {
                return null
            }
            if (children.removeFirst().second.text != "class") {
                return null
            }
            val name = children.removeFirst() // class name
            if (children.removeFirst().second.text != ":") {
                return null
            }
            val parent = children.removeFirst()
            if (parent.first.toString() == "cs:type-usage-role" && parent.second.text == "GlobalScript") {
                return Script(ScriptType.Global, name.second)
            }
            if (parent.first.toString() == "cs:type-usage-role" && parent.second.text == "NodeScript") {
                return Script(ScriptType.Node, name.second)
            }
        }
        return null
    }

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
//        println("element: $element / ${element.elementType}: ${element.text}")
        val scriptType = resolveScriptType(element)
        val scriptInstance = scriptType?.element
        when (scriptType?.type) {
            ScriptType.Node -> return LineMarkerInfo(
                scriptInstance!!,
                scriptInstance!!.textRange,
                MyIcons.Fyrox,
                { "Node script" },
                null,
                GutterIconRenderer.Alignment.LEFT
            )
            ScriptType.Global -> return LineMarkerInfo(
                scriptInstance!!,
                scriptInstance!!.textRange,
                MyIcons.Fyrox,
                { "Global script" },
                null,
                GutterIconRenderer.Alignment.LEFT
            )
            null -> {}
        }
        if (element is CSharpFieldDeclaration) {
            val isNotTransient =
                element.children.none { it is CSharpDummyDeclaration && it.text == "[Transient]" && it.elementType.toString() == "cs:attribute-declaration" }
            if (isNotTransient && resolveScriptType(element.parent.parent) != null) {
                return LineMarkerInfo(
                    element,
                    element.textRange,
                    MyIcons.Fyrox,
                    { "Field is persisted by the engine" },
                    null,
                    GutterIconRenderer.Alignment.LEFT
                )
            }
        }
//        if (!element.hasAttribute("Persisted")) return null

        /*
        0 = {Pair@51703} (cs:attribute-declaration, [Transient])
        1 = {Pair@51704} (WHITE_SPACE,  )
        2 = {Pair@51705} (PUBLIC_KEYWORD, public)
        3 = {Pair@51706} (WHITE_SPACE,  )
        4 = {Pair@51707} (cs:type-usage-role, List<Vector3>)
        5 = {Pair@51708} (WHITE_SPACE,  )
        6 = {Pair@51709} (cs:id-role, beacons)
        7 = {Pair@51710} (SEMICOLON, ;)
         */
        return null
    }
}