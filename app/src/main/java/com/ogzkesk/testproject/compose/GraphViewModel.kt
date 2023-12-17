package com.ogzkesk.testproject.compose

import android.text.Html
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

private const val HTMLCODE = """
    <p data-sourcepos="1:1-1:140">Unfortunately, "sa" is not specific enough for me to understand what you need. To help you effectively, I need more context or information.</p>
    <p data-sourcepos="3:1-3:53">Here are some possibilities for what "sa" could mean:</p>
    <ul data-sourcepos="5:1-9:0">
        <li data-sourcepos="5:1-5:176"><strong>It could be a short form of a greeting like "sağolun" (thank you) or "selam" (hello).</strong> If so, please provide more context so I can understand how to respond appropriately.</li>
        <li data-sourcepos="6:1-6:221"><strong>It could be an abbreviation for something.</strong> There are many abbreviations that include the letters "sa," such as "South Africa," "Salvation Army," and "sex appeal." Please clarify what "sa" stands for in your context.</li>
        <li data-sourcepos="7:1-7:193"><strong>It could be a Turkish word.</strong> In Turkish, "sa" can mean "even though," "whether or not," or "if." Please provide a sentence or additional context so I can understand how "sa" is being used.</li>
        <li data-sourcepos="8:1-9:0"><strong>It could be a typo or abbreviation for something else entirely.</strong> If you're unsure of what "sa" means, please provide me with the full word or phrase you intended.</li>
    </ul>
    <p data-sourcepos="10:1-10:90">The more information you provide, the better I can understand your request and assist you.</p>
"""

class GraphViewModel : ViewModel() {

    val deneme: MutableStateFlow<String> = MutableStateFlow("initial()")
    var state by mutableStateOf(AnimContent.FIRST)
    var secondValue by mutableStateOf("")
    var thirdValue by mutableStateOf(Html.fromHtml(HTMLCODE, HtmlCompat.FROM_HTML_MODE_COMPACT).trim().toString())

    fun onSecondValueChanged(s: String) {
        secondValue = s
    }

    fun onThirdValueChanged(s: String) {
        thirdValue = s
    }

    fun onStateChanged(state: AnimContent) {
        this.state = state
    }
}