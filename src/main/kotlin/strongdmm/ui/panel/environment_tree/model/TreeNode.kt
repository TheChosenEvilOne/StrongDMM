package strongdmm.ui.panel.environment_tree.model

import strongdmm.byond.VAR_ICON
import strongdmm.byond.VAR_ICON_STATE
import strongdmm.byond.dme.DmeItem
import strongdmm.byond.dmi.GlobalDmiHolder
import strongdmm.byond.dmi.IconSprite

class TreeNode(
    val dmeItem: DmeItem
) {
    val name: String = dmeItem.type.substringAfterLast('/')
    val sprite: IconSprite

    init {
        val icon = dmeItem.getVarText(VAR_ICON) ?: ""
        val iconState = dmeItem.getVarText(VAR_ICON_STATE) ?: ""
        sprite = GlobalDmiHolder.getIconSpriteOrPlaceholder(icon, iconState)
    }
}
