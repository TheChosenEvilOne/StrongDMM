package strongdmm.ui.panel.environment_tree

import imgui.flag.ImGuiTreeNodeFlags
import strongdmm.byond.TYPE_AREA
import strongdmm.byond.TYPE_MOB
import strongdmm.byond.TYPE_OBJ
import strongdmm.byond.TYPE_TURF
import strongdmm.byond.dme.DmeItem
import strongdmm.byond.dmm.GlobalTileItemHolder
import strongdmm.byond.dmm.MapPath
import strongdmm.event.EventHandler
import strongdmm.event.type.service.TriggerEnvironmentService
import strongdmm.event.type.service.TriggerMapHolderService
import strongdmm.event.type.service.TriggerTileItemService
import strongdmm.ui.panel.environment_tree.model.TreeNode
import strongdmm.util.NfdUtil
import strongdmm.util.extension.getOrPut
import java.io.File

class ViewController(
    private val state: State
) : EventHandler {
    companion object {
        private const val TREE_NODES_CREATION_LIMIT_PER_CYCLE: Int = 25
    }

    private var createdTeeNodesInCycle: Int = 0
    private var isRepeatFilteredTreeNodesCollection: Boolean = false

    fun doOpenEnvironment() {
        NfdUtil.selectFile("dme")?.let { file ->
            sendEvent(TriggerEnvironmentService.OpenEnvironment(file))
        }
    }

    fun doOpenEnvironment(environmentPath: String) {
        sendEvent(TriggerEnvironmentService.OpenEnvironment(File(environmentPath)))
    }

    fun doOpenEnvironmentWithMap(environmentPath: String, mapPath: MapPath) {
        sendEvent(TriggerEnvironmentService.OpenEnvironment(File(environmentPath)) {
            state.mapToOpen = mapPath
        })
    }

    fun doCollapseAll() {
        state.isDoCollapseAll = true
    }

    fun doCollectFilteredTreeNodes() {
        state.filteredTreeNodes.clear()

        if (state.typeFilter.length == 0) {
            return
        }

        val initialCreatedNodesIntCycle = createdTeeNodesInCycle

        collectFilteredTreeNodes(state.currentEnvironment!!.getItem(TYPE_AREA)!!)
        collectFilteredTreeNodes(state.currentEnvironment!!.getItem(TYPE_TURF)!!)
        collectFilteredTreeNodes(state.currentEnvironment!!.getItem(TYPE_OBJ)!!)
        collectFilteredTreeNodes(state.currentEnvironment!!.getItem(TYPE_MOB)!!)

        isRepeatFilteredTreeNodesCollection = initialCreatedNodesIntCycle != createdTeeNodesInCycle
    }

    fun getEnvironmentNameFromPath(environmentPath: String): String {
        return environmentPath.replace('\\', '/').substringAfterLast('/')
    }

    fun startCycle() {
        state.isSelectedInCycle = false
        createdTeeNodesInCycle = 0

        if (isRepeatFilteredTreeNodesCollection) {
            doCollectFilteredTreeNodes()
        }
    }

    fun stopCycle() {
        state.isDoCollapseAll = false

        state.mapToOpen?.let {
            sendEvent(TriggerMapHolderService.OpenMap(File(it.absolute)))
            state.mapToOpen = null
        }
    }

    fun getTitle(): String {
        val treeNodesCount = if (state.filteredTreeNodes.isNotEmpty()) state.filteredTreeNodes.size else 0
        val suffix = if (treeNodesCount != 0) " ($treeNodesCount)" else ""
        return "Environment Tree$suffix###environment_tree"
    }

    fun getTreeNodeSelectedFlag(dmeItem: DmeItem): Int {
        return if (dmeItem.type == state.selectedTileItemType) ImGuiTreeNodeFlags.Selected else 0
    }

    fun getOrCreateTreeNode(dmeItem: DmeItem): TreeNode? {
        return when {
            state.treeNodes.containsKey(dmeItem.id) -> {
                state.treeNodes.get(dmeItem.id)
            }
            createdTeeNodesInCycle < TREE_NODES_CREATION_LIMIT_PER_CYCLE -> {
                createdTeeNodesInCycle++
                state.treeNodes.getOrPut(dmeItem.id) { TreeNode(dmeItem) }
            }
            else -> null
        }
    }

    fun selectType(type: String) {
        if (!state.isSelectedInCycle) {
            state.isSelectedInCycle = true
            sendEvent(TriggerTileItemService.ChangeSelectedTileItem(GlobalTileItemHolder.getOrCreate(type)))
        }
    }

    fun isPartOfOpenedSelectedType(type: String): Boolean = state.isDoOpenSelectedType && state.selectedTileItemType.startsWith(type)

    fun isSelectedTypeOpened(currentType: String): Boolean {
        if (state.isDoOpenSelectedType && state.selectedTileItemType == currentType) {
            state.isDoOpenSelectedType = false
            return true
        }

        return false
    }

    private fun isFilteredNode(dmeItem: DmeItem): Boolean {
        return dmeItem.type.contains(state.typeFilter.get())
    }

    private fun collectFilteredTreeNodes(dmeItem: DmeItem) {
        if (isFilteredNode(dmeItem)) {
            val treeNode = getOrCreateTreeNode(dmeItem) ?: return
            state.filteredTreeNodes.add(treeNode)
        }

        dmeItem.children.forEach { child ->
            collectFilteredTreeNodes(state.currentEnvironment!!.getItem(child)!!)
        }
    }
}
