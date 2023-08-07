package ge.spoli.messagingapp.presentation.main.views.home.search

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import ge.spoli.messagingapp.databinding.SearchLayoutBinding
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(FlowPreview::class)
class Search @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding = SearchLayoutBinding.inflate(LayoutInflater.from(context), this)
    private var requestListener: RequestListener? = null
    private var searchParam = ""

    init {
        val scope = MainScope()
        searchParam = if ((binding.input.text?.length ?: 0) > 2) {
            binding.input.text.toString()
        } else {
            ""
        }
        binding
            .input
            .onChange()
            .debounce(1500)
            .onEach {
                requestListener?.onSearchRequest(it)
            }.launchIn(scope)

    }

    private fun AppCompatEditText.onChange(): Flow<String> = callbackFlow {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val length = s?.length
                if (length != null && length > 2) {
                    trySend(s.toString()).isSuccess
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        addTextChangedListener(textWatcher)
        awaitClose { removeTextChangedListener(null) }
    }

    fun setRequestListener(listener: RequestListener) {
        requestListener = listener
    }

}