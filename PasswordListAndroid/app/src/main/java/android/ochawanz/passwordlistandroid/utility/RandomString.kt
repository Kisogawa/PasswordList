package android.ochawanz.passwordlistandroid.utility

import java.util.*

class RandomString {
	companion object {
		/**
		 * ランダム対象文字
		 */
		private val chrarctors = arrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')
		// 乱数インスタンス
		private val rand = Random()

		fun generateRandomString(length: Int): String {
			val randomString = StringBuilder(32)
			for (i in 0 until length) {
				randomString.append(chrarctors[rand.nextInt(chrarctors.size)])
			}
			return randomString.toString()
		}
	}
}