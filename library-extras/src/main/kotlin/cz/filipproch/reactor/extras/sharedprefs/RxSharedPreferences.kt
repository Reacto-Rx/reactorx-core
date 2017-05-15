package cz.filipproch.reactor.extras.sharedprefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.NonNull
import io.reactivex.Completable
import io.reactivex.Observable

/**
 * Reactive [SharedPreferences] wrapper. Provides you
 * with methods that return [Completable], [io.reactivex.Single] and [Observable]
 * so you can use them in your app in the Reactive way
 */
class RxSharedPreferences {

    private val sharedPrefs: SharedPreferences

    private constructor(context: Context, prefFile: String) {
        this.sharedPrefs = context.getSharedPreferences(prefFile, 0)
    }

    private constructor(sharedPreferences: SharedPreferences) {
        this.sharedPrefs = sharedPreferences
    }

    /**
     * TODO
     */
    fun setString(key: String, value: String, commit: Boolean = false): Completable {
        return sharedPrefs.editInline(commit) {
            it.putString(key, value)
        }
    }

    /**
     * TODO
     */
    fun setBoolean(key: String, value: Boolean, commit: Boolean = false): Completable {
        return sharedPrefs.editInline(commit) {
            it.putBoolean(key, value)
        }
    }

    /**
     * TODO
     */
    fun setInt(key: String, value: Int, commit: Boolean = false): Completable {
        return sharedPrefs.editInline(commit) {
            it.putInt(key, value)
        }
    }

    /**
     * TODO
     */
    fun setLong(key: String, value: Long, commit: Boolean = false): Completable {
        return sharedPrefs.editInline(commit) {
            it.putLong(key, value)
        }
    }

    /**
     * TODO
     */
    fun setFloat(key: String, value: Float, commit: Boolean = false): Completable {
        return sharedPrefs.editInline(commit) {
            it.putFloat(key, value)
        }
    }

    /**
     * TODO
     */
    fun setStringSet(key: String, value: MutableSet<String>, commit: Boolean = false): Completable {
        return sharedPrefs.editInline(commit) {
            it.putStringSet(key, value)
        }
    }

    /**
     * TODO
     */
    fun removeKey(key: String, commit: Boolean = false): Completable {
        return sharedPrefs.editInline(commit) {
            it.remove(key)
        }
    }

    /**
     * TODO
     */
    fun clear(commit: Boolean = false): Completable {
        return sharedPrefs.editInline(commit) {
            it.clear()
        }
    }

    /**
     * TODO
     */
    fun observeString(key: String, defaultValue: String? = null): Observable<PreferenceValue<String?>> {
        return observePreference(key)
                .map { PreferenceValue(sharedPrefs.getString(key, defaultValue)) }
    }

    /**
     * TODO
     */
    fun observeBoolean(key: String, defaultValue: Boolean = false): Observable<Boolean> {
        return observePreference(key)
                .map { sharedPrefs.getBoolean(key, defaultValue) }
    }

    /**
     * TODO
     */
    fun observeInt(key: String, defaultValue: Int = 0): Observable<Int> {
        return observePreference(key)
                .map { sharedPrefs.getInt(key, defaultValue) }
    }

    /**
     * TODO
     */
    fun observeLong(key: String, defaultValue: Long = 0): Observable<Long> {
        return observePreference(key)
                .map { sharedPrefs.getLong(key, defaultValue) }
    }

    /**
     * TODO
     */
    fun observeFloat(key: String, defaultValue: Float = 0F): Observable<Float> {
        return observePreference(key)
                .map { sharedPrefs.getFloat(key, defaultValue) }
    }

    /**
     * TODO
     */
    fun observeStringSet(key: String, defaultValue: MutableSet<String>? = null): Observable<PreferenceValue<MutableSet<String>>> {
        return observePreference(key)
                .map { PreferenceValue(sharedPrefs.getStringSet(key, defaultValue)) }
    }

    /**
     * TODO
     */
    fun observePreference(key: String): Observable<Unit> {
        return observePreferenceChanges()
                .filter { it.key == key }
                .map { Unit }
                .startWith(Unit)
    }

    /**
     * TODO
     */
    fun observePreferenceChanges(): Observable<PreferenceChangeEvent> {
        return Observable.create<PreferenceChangeEvent> {
            val listener = { _: SharedPreferences, key: String ->
                it.onNext(PreferenceChangeEvent(key))
            }

            sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
            it.setCancellable {
                sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private inline fun SharedPreferences.editInline(
            commit: Boolean,
            crossinline body: (SharedPreferences.Editor) -> SharedPreferences.Editor
    ): Completable {
        return Completable.create {
            val editor = body.invoke(this.edit())
            if (commit) {
                editor.commit()
            } else {
                editor.apply()
            }
            it.onComplete()
        }
    }

    companion object {
        /**
         * TODO
         */
        @NonNull
        fun create(@NonNull context: Context, @NonNull prefFile: String): RxSharedPreferences {
            return RxSharedPreferences(context, prefFile)
        }

        /**
         * TODO
         */
        @NonNull
        fun fromPreferences(@NonNull sharedPreferences: SharedPreferences): RxSharedPreferences {
            return RxSharedPreferences(sharedPreferences)
        }
    }

    /**
     * TODO
     */
    data class PreferenceValue<out T>(val value: T?)

    /**
     * TODO
     */
    data class PreferenceChangeEvent(val key: String)

}