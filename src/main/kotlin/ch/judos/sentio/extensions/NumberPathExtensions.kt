package ch.judos.sentio.extensions

import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.NumberPath

fun <T> NumberPath<T>.eqOrNull(value: T?): Predicate where T : Number, T : Comparable<T> {
	return if (value == null) {
		this.isNull
	}
	else {
		this.eq(value)
	}
}
