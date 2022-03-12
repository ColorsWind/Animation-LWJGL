package net.colors_wind.cglab3.engine


interface IGameLogic {
    @Throws(Exception::class)
    fun init(window: Window)
    fun input(window: Window, mouseInput: MouseInput)
    fun update(window: Window, interval: Float, mouseInput: MouseInput)
    fun render(window: Window)
    fun cleanup()
}