package strongdmm.controller.shortcut

import imgui.ImGui
import strongdmm.event.Event
import strongdmm.event.EventSender

class ShortcutController : EventSender, ShortcutHandler() {
    fun process() {
        var shortcutToTrigger: Shortcut? = null

        for (shortcut in globalShortcuts) {
            val (firstKey, secondKey, thirdKey) = shortcut

            if (ImGui.isKeyDown(firstKey)) {
                if (thirdKey != -1) {
                    if (ImGui.isKeyDown(secondKey) && ImGui.isKeyPressed(thirdKey)) {
                        shortcutToTrigger = shortcut
                        break
                    }
                } else if (secondKey != -1) {
                    if (ImGui.isKeyPressed(secondKey)) {
                        shortcutToTrigger = shortcut
                    }
                } else {
                    shortcutToTrigger = shortcut
                }
            }
        }

        shortcutToTrigger?.let {
            sendEvent(Event.Global.TriggerShortcut(it))
        }
    }
}
