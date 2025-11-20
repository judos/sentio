package ch.judos.sentio.extensions

fun <K, V> MutableMap<K, V>.update(key: K, default: V, updateFunc: (V) -> V) {
	this[key] = updateFunc(this[key] ?: default)
}
