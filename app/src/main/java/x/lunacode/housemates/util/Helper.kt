package x.lunacode.housemates.util

import kotlin.random.Random

fun generateGroupId(): String {

    // 68 characters randomly chosen to form a 8-characters long string will give us a possibility of 4.57x10^14 : 1  of collision
    // if an estimate of 3.380.000 households join the app, chance of collision would be of the 0.000000739%
    // calculations here:  https://math.stackexchange.com/questions/109677/what-is-the-chance-of-repeating-a-random-19-digit-alphanumeric-string
    val charPool: List<Char> =
        ('a'..'z') + ('A'..'Z') + ('0'..'9') + '#' + '@' + '?' + '!' + '$' + '-'
    var generatedId = ""
    for (i in 1..8) {
        generatedId += charPool[Random.nextInt(0, charPool.size)]
    }
    return generatedId
}