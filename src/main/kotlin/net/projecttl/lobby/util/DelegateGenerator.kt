package net.projecttl.lobby.util

import java.util.*
import kotlin.reflect.KProperty

interface DelegateGenerator<T> {
	val props: Properties
	operator fun getValue(thisRef: Any, property: KProperty<*>): T
}