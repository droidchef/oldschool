package co.droidchef.oldschool.images

/**
 * Created this simple LruCache interface so that I could mock it and test it in the unit tests as
 * I wanted to use the LruCache Implementation from Android SDK itself. This interface allows me to
 * abstract out the functionality that I cared about and needed for my feature.
 * */
interface LruCache<K,V> {

    fun get(k: K): V

    fun put(k: K, v: V)

}