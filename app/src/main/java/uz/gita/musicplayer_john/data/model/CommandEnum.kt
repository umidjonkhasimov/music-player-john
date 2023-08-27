package uz.gita.musicplayer_john.data.model

enum class CommandEnum(val amount: Int) {
    MANAGE(1),
    PREV(2),
    NEXT(3),
    PLAY(4),
    PAUSE(5),
    CLOSE(6),
    CONTINUE(7),
    UPDATE_SEEKBAR(8)
}