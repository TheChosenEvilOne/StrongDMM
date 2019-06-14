package io.github.spair.strongdmm.gui.map.select

import io.github.spair.strongdmm.gui.map.Frame
import io.github.spair.strongdmm.gui.menubar.MenuBarView
import io.github.spair.strongdmm.logic.map.CoordArea

object SelectOperation : TileSelect {

    private var tileSelect: TileSelect = AddTileSelect()

    override fun onStart(x: Int, y: Int) = tileSelect.onStart(x, y)
    override fun onAdd(x: Int, y: Int) = tileSelect.onAdd(x, y)
    override fun onStop() = tileSelect.onStop()
    override fun isEmpty() = tileSelect.isEmpty()
    override fun render(iconSize: Int) = tileSelect.render(iconSize)

    fun switchSelectType(selectType: SelectType) {
        when (selectType) {
            SelectType.ADD -> tileSelect = AddTileSelect()
            SelectType.FILL -> tileSelect = FillTileSelect()
            SelectType.PICK -> tileSelect = PickTileSelect()
        }
        Frame.update()
    }

    fun pickArea(coordArea: CoordArea) {
        tileSelect = PickTileSelect().apply {
            selectArea(coordArea.x1, coordArea.y1, coordArea.x2, coordArea.y2)
        }
        MenuBarView.switchSelectType(SelectType.PICK)
    }

    fun depickArea() {
        if (isPickType()) {
            tileSelect = PickTileSelect()
            Frame.update()
        }
    }

    fun depickAreaIfNotInBounds(x: Int, y: Int) {
        if (isPickType()) {
            var inBounds = false

            for (tile in getPickedTiles()!!) {
                if (tile.x == x && tile.y == y) {
                    inBounds = true
                    break
                }
            }

            if (!inBounds) {
                depickArea()
                Frame.update()
            }
        }
    }

    fun isPickType() = tileSelect is PickTileSelect
    fun getPickedTiles() = (tileSelect as? PickTileSelect)?.getSelectedTiles()
}
