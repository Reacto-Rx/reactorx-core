package cz.filipproch.reactor.extras.sharedprefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import io.reactivex.Completable
import io.reactivex.Observable

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class RxSharedPreferences {

    private val sharedPrefs: SharedPreferences

    private constructor(context: Context, prefFile: String) {
        this.sharedPrefs = context.getSharedPreferences(prefFile, 0)
    }

    private constructor(sharedPreferences: SharedPreferences) {
        this.sharedPrefs = sharedPreferences
    }

    fun setString(key: String, value: String, commit: Boolean = false): Completable {
        return sharedPrefs.editInline(commit) {
            it.putString(key, value)
        }
    }

    fun setBoolean(key: String, value: Boolean, commit: Boolean = false): Completable {
        return sharedPrefs.editInline(commit) {
            it.putBoolean(key, value)
        }
    }

    fun setInt(key: String, value: Int, commit: Boolean = false): Completable {
        return sharedPrefs.editInline(commit) {
            it.putInt(key, value)
        }
    }

    fun setLong(key: String, value: Long, commit: Boolean = false): Completable {
        return sharedPrefs.editInline(commit) {
            it.putLong(key, value)
        }
    }

    fun setFloat(key: String, value: Float, commit: Boolean = false): Completable {
        return sharedPrefs.editInline(commit) {
            it.putFloat(key, value)
        }
    }

    fun setStringSet(key: String, value: MutableSet<String>, commit: Boolean = false): Completable {
        return sharedPrefs.editInline(commit) {
            it.putStringSet(key, value)
        }
    }

    fun observeString(key: String, defaultValue: String? = null): Observable<PreferenceValue<String>> {
        return observePreference(key)
                .map { PreferenceValue(sharedPrefs.getString(key, defaultValue)) }
    }

    fun observeBoolean(key: String, defaultValue: Boolean = false): Observable<Boolean> {
        return observePreference(key)
                .map { sharedPrefs.getBoolean(key, defaultValue) }
    }

    fun observeInt(key: String, defaultValue: Int = 0): Observable<Int> {
        return observePreference(key)
                .map { sharedPrefs.getInt(key, defaultValue) }
    }

    fun observeLong(key: String, defaultValue: Long = 0): Observable<Long> {
        return observePreference(key)
                .map { sharedPrefs.getLong(key, defaultValue) }
    }

    fun observeFloat(key: String, defaultValue: Float = 0F): Observable<Float> {
        return observePreference(key)
                .map { sharedPrefs.getFloat(key, defaultValue) }
    }

    fun observeStringSet(key: String, defaultValue: MutableSet<String>? = null): Observable<PreferenceValue<MutableSet<String>>> {
        return observePreference(key)
                .map { PreferenceValue(sharedPrefs.getStringSet(key, defaultValue)) }
    }

    fun observePreference(key: String): Observable<Unit> {
        return observePreferenceChanges()
                .filter { it.key == key }
                .map { Unit }
                .startWith(Unit)
    }

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
    private inline fun SharedPreferences.editInline(commit: Boolean, crossinline body: (SharedPreferences.Editor) -> SharedPreferences.Editor): Completable {
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
        fun create(context: Context, prefFile: String): RxSharedPreferences {
            return RxSharedPreferences(context, prefFile)
        }

        fun fromPreferences(sharedPreferences: SharedPreferences): RxSharedPreferences {
            return RxSharedPreferences(sharedPreferences)
        }
    }

    data class PreferenceValue<out T>(val value: T)

    data class PreferenceChangeEvent(val key: String)

}