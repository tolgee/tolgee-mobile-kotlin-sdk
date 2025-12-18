package io.tolgee

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.tolgee.mobilekotlinsdk.core.R

/**
 * A custom [LayoutInflater.Factory2] that automatically translates text attributes
 * during view inflation.
 *
 * This factory intercepts view creation and applies Tolgee translations to text-related
 * attributes (text, hint, contentDescription) automatically. It delegates to an existing
 * factory (typically AppCompat's) to preserve compatibility with material components.
 * Due to this, this factory behaves a little bit like `FactoryMerger`, as it has to sit
 * in between existing factory and layout inflater.
 *
 * Resource IDs are stored in View tags for dynamic re-translation when the language changes.
 *
 * @param tolgee The Tolgee Android instance for translation lookups
 * @param delegate The existing Factory to delegate view creation to (e.g., AppCompat)
 * @param delegate2 The existing Factory2 to delegate view creation to (e.g., AppCompat)
 */
internal class TolgeeLayoutInflaterFactory(
    private val tolgee: TolgeeAndroid,
    private val delegate: LayoutInflater.Factory?,
    private val delegate2: LayoutInflater.Factory2?
) : LayoutInflater.Factory2 {

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        // Delegate to existing factory first (AppCompat, etc.)
        val view = delegate2?.onCreateView(parent, name, context, attrs)
            ?: delegate2?.onCreateView(name, context, attrs)
            ?: delegate?.onCreateView(name, context, attrs)
            ?: createViewWithFallback(name, context, attrs)  // Fallback for bare Activity and custom views

        // Apply Tolgee translations to the created view
        view?.let { applyTranslations(it, context, attrs) }

        return view
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        // Delegate to existing factory first (AppCompat, etc.)
        val view = delegate2?.onCreateView(name, context, attrs)
            ?: delegate?.onCreateView(name, context, attrs)
            ?: createViewWithFallback(name, context, attrs)  // Fallback for bare Activity and custom views

        // Apply Tolgee translations to the created view
        view?.let { applyTranslations(it, context, attrs) }

        return view
    }

    /**
     * Creates a view when all delegates return null.
     *
     * This handles edge cases like:
     * - Bare Activity (no AppCompat)
     * - Custom views with translatable attributes
     * - Any other scenario where existing factories don't create the view
     */
    private fun createViewWithFallback(
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        return try {
            if (name.contains('.')) {
                // Fully qualified name: com.example.CustomView
                createViewByClassName(name, context, attrs)
            } else {
                // Simple name: TextView, Button, etc.
                createViewWithPrefix(name, context, attrs)
            }
        } catch (e: Exception) {
            // Let LayoutInflater's default mechanism handle it
            null
        }
    }

    /**
     * Creates a view using its fully qualified class name.
     */
    private fun createViewByClassName(
        className: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        return try {
            val clazz = context.classLoader.loadClass(className).asSubclass(View::class.java)
            val constructor = clazz.getConstructor(Context::class.java, AttributeSet::class.java)
            constructor.newInstance(context, attrs)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Creates a view by trying standard Android prefixes.
     *
     * Tries in order:
     * 1. android.widget.* (TextView, Button, EditText, etc.)
     * 2. android.webkit.* (WebView)
     * 3. android.app.* (Fragment)
     */
    private fun createViewWithPrefix(
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        for (prefix in CLASS_PREFIXES) {
            try {
                val clazz = context.classLoader.loadClass(prefix + name).asSubclass(View::class.java)
                val constructor = clazz.getConstructor(Context::class.java, AttributeSet::class.java)
                return constructor.newInstance(context, attrs)
            } catch (e: ClassNotFoundException) {
                // Try next prefix
                continue
            }
        }
        return null
    }

    /**
     * Extracts string resource IDs from view attributes and applies translations.
     *
     * This method:
     * 1. Extracts resource IDs from text-related attributes
     * 2. Stores them in View tags for later re-translation
     * 3. Applies immediate translations using tolgee.t()
     * 4. Skips parameterized strings (with format placeholders)
     */
    private fun applyTranslations(view: View, context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, TRANSLATABLE_ATTRS)
        try {
            // Extract resource IDs from attributes
            val textResId = typedArray.getResourceId(ATTR_TEXT_INDEX, 0)
            val hintResId = typedArray.getResourceId(ATTR_HINT_INDEX, 0)
            val contentDescResId = typedArray.getResourceId(ATTR_CONTENT_DESC_INDEX, 0)

            // Apply text translation
            if (textResId != 0 && !hasFormatPlaceholders(context, textResId)) {
                view.setTag(R.id.tolgee_text_res_id, textResId)
                applyTextTranslation(view, textResId, tolgee)
            }

            // Apply hint translation
            if (hintResId != 0 && !hasFormatPlaceholders(context, hintResId)) {
                view.setTag(R.id.tolgee_hint_res_id, hintResId)
                applyHintTranslation(view, hintResId, tolgee)
            }

            // Apply content description translation
            if (contentDescResId != 0 && !hasFormatPlaceholders(context, contentDescResId)) {
                view.setTag(R.id.tolgee_content_desc_res_id, contentDescResId)
                applyContentDescriptionTranslation(view, contentDescResId, tolgee)
            }
        } finally {
            typedArray.recycle()
        }
    }

    /**
     * Detects if a string resource contains format placeholders.
     *
     * Parameterized strings (with %1$s, %d, etc.) cannot be auto-translated
     * because the format arguments are only available at runtime.
     *
     * @return true if the string contains format placeholders, false otherwise
     */
    private fun hasFormatPlaceholders(context: Context, resId: Int): Boolean {
        return try {
            val text = context.resources.getString(resId)
            FORMAT_PLACEHOLDER_REGEX.containsMatchIn(text)
        } catch (e: Exception) {
            // If we can't read the string, skip auto-translation
            true
        }
    }

    companion object {
        /**
         * Standard Android class prefixes for view resolution.
         * Used when creating views with simple names (TextView, Button, etc.)
         */
        private val CLASS_PREFIXES = arrayOf(
            "android.widget.",
            "android.webkit.",
            "android.app."
        )

        /**
         * Array of translatable attribute IDs we want to intercept.
         */
        private val TRANSLATABLE_ATTRS = intArrayOf(
            android.R.attr.text,
            android.R.attr.hint,
            android.R.attr.contentDescription
        )

        /**
         * Indices into TRANSLATABLE_ATTRS array
         */
        private const val ATTR_TEXT_INDEX = 0
        private const val ATTR_HINT_INDEX = 1
        private const val ATTR_CONTENT_DESC_INDEX = 2

        /**
         * Regex pattern to detect format placeholders like %1$s, %d, %s, etc.
         */
        private val FORMAT_PLACEHOLDER_REGEX = Regex("""%(\d+\$)?[sdxXfegGc]""")

        /**
         * Re-translates all views in the given view hierarchy.
         *
         * This method recursively walks the view hierarchy and re-translates
         * any views that were automatically translated during inflation.
         *
         * @param rootView The root view to start re-translation from
         * @param tolgee The Tolgee Android instance for translation lookups
         */
        fun retranslateViewHierarchy(rootView: View, tolgee: TolgeeAndroid) {
            retranslateView(rootView, tolgee)

            if (rootView is ViewGroup) {
                for (i in 0 until rootView.childCount) {
                    retranslateViewHierarchy(rootView.getChildAt(i), tolgee)
                }
            }
        }

        /**
         * Re-translates a single view using its stored resource IDs.
         */
        private fun retranslateView(view: View, tolgee: TolgeeAndroid) {
            // Re-translate text
            (view.getTag(R.id.tolgee_text_res_id) as? Int)?.let { resId ->
                applyTextTranslation(view, resId, tolgee)
            }

            // Re-translate hint
            (view.getTag(R.id.tolgee_hint_res_id) as? Int)?.let { resId ->
                applyHintTranslation(view, resId, tolgee)
            }

            // Re-translate content description
            (view.getTag(R.id.tolgee_content_desc_res_id) as? Int)?.let { resId ->
                applyContentDescriptionTranslation(view, resId, tolgee)
            }
        }

        /**
         * Applies text translation to a view.
         */
        private fun applyTextTranslation(view: View, resId: Int, tolgee: TolgeeAndroid) {
            when (view) {
                is TextView -> view.text = tolgee.t(view.resources, resId)
            }
        }

        /**
         * Applies hint translation to a view.
         */
        private fun applyHintTranslation(view: View, resId: Int, tolgee: TolgeeAndroid) {
            when (view) {
                is TextView -> view.hint = tolgee.t(view.resources, resId)
            }
        }

        /**
         * Applies content description translation to a view.
         */
        private fun applyContentDescriptionTranslation(view: View, resId: Int, tolgee: TolgeeAndroid) {
            view.contentDescription = tolgee.t(view.resources, resId)
        }
    }
}
