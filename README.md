# bridge

https://maestro.mobile.dev/

https://accsell.ir/product/Spotify-Premium

```
protected fun <T> onAsyncResult(
        asyncProp: KProperty1<S, AsyncResult<T>>,
        onFail: (suspend (Throwable) -> Unit)? = null,
        onSuccess: (suspend (T) -> Unit)? = null,
    ): Job = uiState
        .map { asyncProp.get(it) }
        .distinctUntilChanged()
        .resolveSubscription { asyncValue ->
            if (onSuccess != null && asyncValue is Success) {
                onSuccess(asyncValue())
            } else if (onFail != null && asyncValue is Fail) {
                onFail(asyncValue.error)
            }
        }

    private fun <T> Flow<T>.resolveSubscription(action: suspend (T) -> Unit): Job {
        return viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            // Use yield to ensure flow collect coroutine is dispatched rather than invoked immediately.
            // This is necessary when Dispatchers.Main.immediate is used in scope.
            // Coroutine is launched with start = CoroutineStart.DISPATCHER to perform dispatch only once.
            yield()
            this@resolveSubscription.collectLatest(action)
        }
    }
```
