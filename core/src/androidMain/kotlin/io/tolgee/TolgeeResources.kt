package io.tolgee

import android.annotation.SuppressLint
import android.content.res.AssetFileDescriptor
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.TypedArray
import android.content.res.XmlResourceParser
import android.content.res.loader.ResourcesLoader
import android.graphics.Movie
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.AnyRes
import androidx.annotation.ArrayRes
import androidx.annotation.BoolRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Discouraged
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.FractionRes
import androidx.annotation.IntegerRes
import androidx.annotation.LayoutRes
import androidx.annotation.PluralsRes
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.annotation.StyleableRes
import androidx.annotation.XmlRes
import io.tolgee.common.getQuantityStringT
import io.tolgee.common.getQuantityTextT
import io.tolgee.common.getStringArrayT
import io.tolgee.common.getStringT
import io.tolgee.common.getTextArrayT
import io.tolgee.common.getTextT
import java.io.InputStream

/**
 * Ignore Deprecation: Resources constructor is not really deprecated, apps should just not create
 * an instance like this.
 * Since we are extending the resources class, we are not affected.
 */
@Suppress("DEPRECATION")
internal class TolgeeResources(
    val base: Resources,
    val tolgee: Tolgee,
    val interceptGetString: Boolean = true,
    val interceptGetText: Boolean = true
) : Resources(base.assets, base.displayMetrics, base.configuration) {

    /**
     * Following methods are intercepted by Tolgee.
     */

    override fun getString(@StringRes id: Int): String {
        if (!interceptGetString) return base.getString(id)
        return base.getStringT(tolgee, id)
    }

    override fun getString(@StringRes id: Int, vararg formatArgs: Any?): String {
        if (!interceptGetString) return base.getString(id, *formatArgs)
        return base.getStringT(tolgee, id, *formatArgs.filterNotNull().toTypedArray())
    }

    override fun getQuantityString(@PluralsRes id: Int, quantity: Int): String {
        if (!interceptGetString) return base.getQuantityString(id, quantity)
        return base.getQuantityStringT(tolgee, id, quantity)
    }

    override fun getQuantityString(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any?): String {
        if (!interceptGetString) return base.getQuantityString(id, quantity, *formatArgs)
        return base.getQuantityStringT(tolgee, id, quantity, *formatArgs.filterNotNull().toTypedArray())
    }

    override fun getStringArray(@ArrayRes id: Int): Array<out String?> {
        if (!interceptGetString) return base.getStringArray(id)
        return base.getStringArrayT(tolgee, id)
    }

    override fun getText(@StringRes id: Int): CharSequence {
        if (!interceptGetText) return base.getText(id)
        return base.getTextT(tolgee, id)
    }

    override fun getQuantityText(@PluralsRes id: Int, quantity: Int): CharSequence {
        if (!interceptGetText) return base.getQuantityText(id, quantity)
        return base.getQuantityTextT(tolgee, id, quantity)
    }

    override fun getTextArray(id: Int): Array<out CharSequence?> {
        if (!interceptGetText) return base.getTextArray(id)
        return base.getTextArrayT(tolgee, id)
    }

    override fun getText(@StringRes id: Int, def: CharSequence?): CharSequence? {
        if (!interceptGetText) return base.getText(id, def)
        return base.getTextT(tolgee, id, def)
    }

    /**
     * Following methods are proxied to preserve original behavior.
     */

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getFont(@FontRes id: Int): Typeface {
        return base.getFont(id)
    }

    override fun getIntArray(@ArrayRes id: Int): IntArray {
        return base.getIntArray(id)
    }

    override fun obtainTypedArray(@ArrayRes id: Int): TypedArray {
        return base.obtainTypedArray(id)
    }

    override fun getDimension(@DimenRes id: Int): Float {
        return base.getDimension(id)
    }

    override fun getDimensionPixelOffset(@DimenRes id: Int): Int {
        return base.getDimensionPixelOffset(id)
    }

    override fun getDimensionPixelSize(@DimenRes id: Int): Int {
        return base.getDimensionPixelSize(id)
    }

    override fun getFraction(@FractionRes id: Int, base: Int, pbase: Int): Float {
        return this.base.getFraction(id, base, pbase)
    }

    @Deprecated("Proxied Deprecation")
    override fun getDrawable(@DrawableRes id: Int): Drawable? {
        return base.getDrawable(id)
    }

    override fun getDrawable(@DrawableRes id: Int, theme: Theme?): Drawable? {
        return base.getDrawable(id, theme)
    }

    @Deprecated("Proxied Deprecation")
    override fun getDrawableForDensity(@DrawableRes id: Int, density: Int): Drawable? {
        return base.getDrawableForDensity(id, density)
    }

    override fun getDrawableForDensity(@DrawableRes id: Int, density: Int, theme: Theme?): Drawable? {
        return base.getDrawableForDensity(id, density, theme)
    }

    @Deprecated("Proxied Deprecation")
    override fun getMovie(@RawRes id: Int): Movie? {
        return base.getMovie(id)
    }

    @ColorInt
    @Deprecated("Proxied Deprecation")
    override fun getColor(@ColorRes id: Int): Int {
        return base.getColor(id)
    }

    @ColorInt
    @RequiresApi(Build.VERSION_CODES.M)
    override fun getColor(@ColorRes id: Int, theme: Theme?): Int {
        return base.getColor(id, theme)
    }

    @Deprecated("Proxied Deprecation")
    override fun getColorStateList(@ColorRes id: Int): ColorStateList {
        return base.getColorStateList(id)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getColorStateList(@ColorRes id: Int, theme: Theme?): ColorStateList {
        return base.getColorStateList(id, theme)
    }

    override fun getBoolean(@BoolRes id: Int): Boolean {
        return base.getBoolean(id)
    }

    override fun getInteger(@IntegerRes id: Int): Int {
        return base.getInteger(id)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun getFloat(@DimenRes id: Int): Float {
        return base.getFloat(id)
    }

    override fun getLayout(@LayoutRes id: Int): XmlResourceParser {
        return base.getLayout(id)
    }

    override fun getAnimation(@AnimatorRes @AnimRes id: Int): XmlResourceParser {
        return base.getAnimation(id)
    }

    override fun getXml(@XmlRes id: Int): XmlResourceParser {
        return base.getXml(id)
    }

    override fun openRawResource(@RawRes id: Int): InputStream {
        return base.openRawResource(id)
    }

    override fun openRawResource(@RawRes id: Int, value: TypedValue?): InputStream {
        return base.openRawResource(id, value)
    }

    override fun openRawResourceFd(@RawRes id: Int): AssetFileDescriptor? {
        return base.openRawResourceFd(id)
    }

    override fun getValue(@AnyRes id: Int, outValue: TypedValue?, resolveRefs: Boolean) {
        return base.getValue(id, outValue, resolveRefs)
    }

    override fun getValueForDensity(
        @AnyRes id: Int,
        density: Int,
        outValue: TypedValue?,
        resolveRefs: Boolean
    ) {
        return base.getValueForDensity(id, density, outValue, resolveRefs)
    }

    @SuppressLint("DiscouragedApi")
    @Discouraged("Proxied Discourage")
    override fun getValue(name: String?, outValue: TypedValue?, resolveRefs: Boolean) {
        return base.getValue(name, outValue, resolveRefs)
    }

    override fun obtainAttributes(set: AttributeSet?, @StyleableRes attrs: IntArray?): TypedArray? {
        return base.obtainAttributes(set, attrs)
    }

    @Deprecated("Proxied Deprecation")
    override fun updateConfiguration(config: Configuration?, metrics: DisplayMetrics?) {
        return base.updateConfiguration(config, metrics)
    }

    override fun getDisplayMetrics(): DisplayMetrics? {
        return base.displayMetrics
    }

    override fun getConfiguration(): Configuration? {
        return base.configuration
    }

    @SuppressLint("DiscouragedApi")
    @Discouraged("Proxied Discourage")
    override fun getIdentifier(name: String?, defType: String?, defPackage: String?): Int {
        return base.getIdentifier(name, defType, defPackage)
    }

    override fun getResourceName(@AnyRes resid: Int): String? {
        return base.getResourceName(resid)
    }

    override fun getResourcePackageName(@AnyRes resid: Int): String? {
        return base.getResourcePackageName(resid)
    }

    override fun getResourceEntryName(@AnyRes resid: Int): String? {
        return base.getResourceEntryName(resid)
    }

    override fun parseBundleExtras(parser: XmlResourceParser?, outBundle: Bundle?) {
        return base.parseBundleExtras(parser, outBundle)
    }

    override fun parseBundleExtra(tagName: String?, attrs: AttributeSet?, outBundle: Bundle?) {
        return base.parseBundleExtra(tagName, attrs, outBundle)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun addLoaders(vararg loaders: ResourcesLoader?) {
        return base.addLoaders(*loaders)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun removeLoaders(vararg loaders: ResourcesLoader?) {
        return base.removeLoaders(*loaders)
    }

    override fun getResourceTypeName(@AnyRes resid: Int): String? {
        return base.getResourceTypeName(resid)
    }
}