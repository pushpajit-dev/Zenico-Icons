package com.runeicons.app.components

import androidx.compose.ui.geometry.Offset
import java.util.regex.Pattern

data class EditorNode(
    var x: Float,
    var y: Float,
    val command: Char,
    val isControl: Boolean = false,
    val isMoveTo: Boolean = false
)

object SvgPathEditorUtils {

    fun parsePathToNodes(pathData: String): List<EditorNode> {
        if (pathData.isBlank()) return emptyList()

        val nodes = mutableListOf<EditorNode>()
        val p = Pattern.compile("([a-zA-Z])([^a-zA-Z]*)")
        val m = p.matcher(pathData)

        var curX = 0f
        var curY = 0f

        while (m.find()) {
            val cmdStr = m.group(1) ?: continue
            val cmd = cmdStr[0]
            val isRelative = cmd.isLowerCase()
            val upper = cmd.uppercaseChar()
            val argsStr = m.group(2)?.trim() ?: ""

            val numPattern = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+(?:[eE][-+]?[0-9]+)?")
            val numMatcher = numPattern.matcher(argsStr)
            val nums = mutableListOf<Float>()
            while (numMatcher.find()) {
                nums.add(numMatcher.group().toFloatOrNull() ?: 0f)
            }

            when (upper) {
                'M' -> {
                    var i = 0
                    while (i + 1 < nums.size) {
                        val x = if (isRelative) curX + nums[i] else nums[i]
                        val y = if (isRelative) curY + nums[i + 1] else nums[i + 1]
                        nodes.add(EditorNode(x, y, cmd, isControl = false, isMoveTo = (i == 0)))
                        curX = x
                        curY = y
                        i += 2
                    }
                }
                'L' -> {
                    var i = 0
                    while (i + 1 < nums.size) {
                        val x = if (isRelative) curX + nums[i] else nums[i]
                        val y = if (isRelative) curY + nums[i + 1] else nums[i + 1]
                        nodes.add(EditorNode(x, y, cmd, isControl = false))
                        curX = x
                        curY = y
                        i += 2
                    }
                }
                'H' -> {
                    for (num in nums) {
                        val x = if (isRelative) curX + num else num
                        nodes.add(EditorNode(x, curY, cmd, isControl = false))
                        curX = x
                    }
                }
                'V' -> {
                    for (num in nums) {
                        val y = if (isRelative) curY + num else num
                        nodes.add(EditorNode(curX, y, cmd, isControl = false))
                        curY = y
                    }
                }
                'C' -> {
                    var i = 0
                    while (i + 5 < nums.size) {
                        val x1 = if (isRelative) curX + nums[i] else nums[i]
                        val y1 = if (isRelative) curY + nums[i + 1] else nums[i + 1]
                        val x2 = if (isRelative) curX + nums[i + 2] else nums[i + 2]
                        val y2 = if (isRelative) curY + nums[i + 3] else nums[i + 3]
                        val x = if (isRelative) curX + nums[i + 4] else nums[i + 4]
                        val y = if (isRelative) curY + nums[i + 5] else nums[i + 5]

                        nodes.add(EditorNode(x1, y1, cmd, isControl = true))
                        nodes.add(EditorNode(x2, y2, cmd, isControl = true))
                        nodes.add(EditorNode(x, y, cmd, isControl = false))

                        curX = x
                        curY = y
                        i += 6
                    }
                }
                'Q' -> {
                    var i = 0
                    while (i + 3 < nums.size) {
                        val x1 = if (isRelative) curX + nums[i] else nums[i]
                        val y1 = if (isRelative) curY + nums[i + 1] else nums[i + 1]
                        val x = if (isRelative) curX + nums[i + 2] else nums[i + 2]
                        val y = if (isRelative) curY + nums[i + 3] else nums[i + 3]

                        nodes.add(EditorNode(x1, y1, cmd, isControl = true))
                        nodes.add(EditorNode(x, y, cmd, isControl = false))

                        curX = x
                        curY = y
                        i += 4
                    }
                }
                'Z' -> {
                    // Close path command
                }
            }
        }
        return nodes
    }

    fun nodesToSvgPath(nodes: List<EditorNode>): String {
        if (nodes.isEmpty()) return ""
        val sb = StringBuilder()
        var lastCmd = ' '

        for (node in nodes) {
            val cmd = node.command.uppercaseChar()
            if (cmd != lastCmd || node.isMoveTo) {
                sb.append(cmd)
                lastCmd = cmd
            } else {
                sb.append(" ")
            }
            sb.append(String.format(java.util.Locale.US, "%.1f %.1f ", node.x, node.y))
        }
        return sb.toString().trim()
    }

    fun generateMidpointDots(nodes: List<EditorNode>): List<Offset> {
        val midpoints = mutableListOf<Offset>()
        for (i in 0 until nodes.size - 1) {
            val a = nodes[i]
            val b = nodes[i + 1]
            if (!b.isMoveTo) {
                midpoints.add(Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f))
            }
        }
        return midpoints
    }
}
