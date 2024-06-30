package net.projecttl.util

import java.util.Properties
import kotlin.reflect.KProperty

interface DelegateGenerator<T> {
	val props: Properties
	operator fun getValue(thisRef: Any, property: KProperty<*>): T
}