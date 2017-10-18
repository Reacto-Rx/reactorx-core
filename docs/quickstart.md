## Quickstart

### Gradle

Until stable version (1.0.0) is released, you need to add custom maven repository. You also need to make sure you have Google's Maven repository as this library is using it's Architecture components.

```groovy
repositories {
    // Library specific repo, required for version < 1.0.0
    maven { url "https://dl.bintray.com/reacto-rx/maven" }
    
    // Google repository, required for Architecture components
    maven { url "https://maven.google.com" }
}
```

After that, just add dependency to the latest version of the library.

```groovy
dependencies {
    implementation "org.reactorx:reactorx:<VERSION>"
}
```

### Example use with an Activity

TODO - demo gif animation (button clicking, count incrementing in textview)

As an example, we have a simple Activity with a single Button and a TextView. Whenever you click the button, a click count increments and is displayed in the text view.

__DemoUiEvents.kt__

```kotlin
class PrimaryActionClicked : UiEvent
```

This is a Kotlin file where we declare all events that are dispatched from the view (Activity in our case) to the Presenter.

Here we declare `PrimaryActionClicked`, which will be dispatched whenever we click a button.

__DemoActions.kt__

```kotlin
class IncrementClickCount : Action
```

This is a Kotlin file where we declare all actions that modify the internal __Presenter__ state.
 
`IncrementClickCount` is an Action that is created whenever `PrimaryActionClicked` and modifies the state by incrementing the click count. This logic is implemented below.

__DemoViewModel.kt__

```kotlin
data class DemoViewModel(
    val count: Long = 0
)
```

A ViewModel declaration. The single source of truth for the state of the view (Activity in our case). We have a `count` field there, which represents the current click count.

__DemoPresenter.kt__
```kotlin
class DemoPresenter : Presenter<DemoViewModel>() {
    
    override val initialState = DemoViewModel()
    
    override val transformers: Array<StateStoreTransformer<Action, Action>>
            get() = arrayOf(
                    reactToClicks
            )
            
    private val reactToClicks = transformer<PrimaryActionClicked> { inputStream, _ ->
        inputStream.map {
            IncrementClickCount()
        }
    }
    
    override fun reduceState(
            previousState: DemoViewModel,
            action: Action
    ) = when (action) {
        is IncrementClickCount -> {
            previousState.copy(
                    count = previousState.count + 1
            )
        }
        else -> previousState
    }
    
    class Factory : PresenterFactory<DemoPresenter>() {
        override fun newInstance() = DemoPresenter()  
    }
    
}
```

Here is the implementation of the Presenter for our Activity. First, there is a declaration of what our initial state is.

```kotlin
override val initialState = DemoViewModel()
```

Then, we define our transformers. Those are functions that transform the incoming UiEvents from our Activity to Actions which modify the state.

```kotlin
override val transformers: Array<StateStoreTransformer<Action, Action>>
            get() = arrayOf(
                    reactToClicks
            )
```

Then, we define the `reactToClicks` tranformer. It takes stream of `PrimaryActionClicked` UiEvents as input, and maps each emission to an `IncrementClickCount` Action.

```kotlin
private val reactToClicks = transformer<PrimaryActionClicked> { inputStream, _ ->
    inputStream.map {
        IncrementClickCount()
    }
}
```

Finally, we define the logic of how we modify the state with given Actions.

__DemoActivity.kt__

```kotlin
class DemoActivity : AppCompatActivity(),
        PresenterView<DemoViewModel, DemoPresenter> {
    
    override val viewHelper = ViewHelper<DemoViewModel, DemoPresenter>()
    
    private lateinit var vClickCountText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // create / restore Presenter instance
        viewHelper.restorePresenterInstance(this, 
            DemoPresenter::class.java,
            DemoPresenter.Factory()
        )
        
        // set Activity layout
        setContentView(R.layout.activity_demo)
        
        // get instance of Views
        vClickCountText = findViewById(R.id.vClickCountText) as TextView
        val myButton = findViewById(R.id.vMyButton) as Button
        
        // listen for clicks and dispatch them to Presenter
        myButton.clicks()
            .map { PrimaryActionClicked() }
            .subscribeByDispatch()
    }

    override fun onStart() {
        super.onStart()
        // connect the streams between Presenter and this Activity
        connectPresenter()
    }

    override fun onStop() {
        super.onStop()
        // disconnect the streams between Presenter and this Activity
        disconnectPresenter()
    }
    
    override fun onPresenterConnected() {
        super.onPresenterConnected()
        // Presenter is connected to this Activity
        
        observeViewModelChanges { it.count }
            .map { it.toString() }
            .subscribeWithView(vClickCountText.text())
    }
    
}
```