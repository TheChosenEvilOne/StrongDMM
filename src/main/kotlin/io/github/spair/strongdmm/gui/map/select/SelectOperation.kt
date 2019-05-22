package io.github.spair.strongdmm.gui.map.select

import io.github.spair.strongdmm.gui.map.Frame

object SelectOperation : TileSelect {

    private var tileSelect: TileSelect = AddTileSelect()

    override fun onStart(x: Int, y: Int) = tileSelect.onStart(x, y)
    override fun onAdd(x: Int, y: Int) = tileSelect.onAdd(x, y)
    override fun onStop() = tileSelect.onStop()
    override fun isEmpty() = tileSelect.isEmpty()
    override fun render(iconSize: Int) = tileSelect.render(iconSize)

    fun switchSelectMode(selectType: SelectType) {
        when (selectType) {
            SelectType.ADD -> tileSelect = AddTileSelect()
            SelectType.FILL -> tileSelect = FillTileSelect()
            SelectType.PICK -> tileSelect = PickTileSelect()
        }
        Frame.update()
    }
}